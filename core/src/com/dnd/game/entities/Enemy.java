package com.dnd.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dnd.game.Globals;
import com.dnd.game.interfaces.ICombatInter;
import com.dnd.game.utils.MathUtils;
import com.dnd.game.utils.SceneBuilder;
import com.sun.imageio.plugins.gif.GIFImageMetadataFormat;

import java.util.ArrayList;

import static com.dnd.game.Globals.PPM;

public class Enemy extends MapEntity implements ICombatInter {
    private World world;
    private float x, y, hp;
    private Body body;
    private EmapEnemyType type;
    private boolean isDead;
    private Player player;
    private boolean shouldMove;
    private boolean attack;
    private Vector2 rayEnd = new Vector2(0, 0);
    private ShapeRenderer shapeRenderer;
    private ArrayList<Fixture> hitFixtures;

    @Override
    public void lightAttack() {
        RayCastCallback callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getFilterData().categoryBits == Globals.BIT_PLAYER && hitFixtures.isEmpty()) {
                    if (player != null && player.getPosition() == fixture.getBody().getPosition()) {
                        player.damage(type.getDmg());
                        hitFixtures.add(fixture);
                        return 0;
                    }
                }

                return -1;
            }
        };
        float angle = 0;
        for (int i = 0; i < 50; i++) {
            float x = (float) Math.sin((double) angle);
            float y = (float) Math.cos((double) angle);
            angle += 2 * Math.PI / 50;
            rayEnd = new Vector2(getPosition().x + x, getPosition().y + y).sub(getPosition().x, getPosition().y).nor().scl(5).add(getPosition().x, getPosition().y);
            world.rayCast(callback, getPosition(), rayEnd);

        }
        attack = false;

    }

    @Override
    public void chargedAttack() {
        if (player != null) {
            player.damage(50);
        }
    }

    @Override
    public void gunShot() {
        if (player != null) {
            player.damage(5);
        }
    }

    @Override
    public void damage(float dmg) {
        if (!isDead) {
            this.hp -= dmg;
        }
        if (this.hp < 0) {
            isDead = true;
        }
        System.out.println("Enemy hp: " + this.hp);
    }


    public enum EmapEnemyType {
        NORMAL(64, 50f, 10f,1f ),
        MINI_BOSS(128,100f,25f,3f),
        BOSS(256,250f,30f,4f);

        private int size;
        private float dmg, hp, attackInterval;

        EmapEnemyType(int size, float hp, float dmg, float attackInterval) {
            this.size = size;
            this.hp = hp;
            this.dmg = dmg;
            this.attackInterval = attackInterval;
        }

        public int getSize() {
            return size;
        }

        public float getDmg() {
            return dmg;
        }

        public float getHp() {
            return hp;
        }

        public float getAttackInterval() {
            return attackInterval;
        }
    }

    public Enemy(World world, float x, float y, boolean isMiniBoss, boolean isBoss) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.isDead = false;
        EmapEnemyType mapEnemy = getRandomEnemyType(isMiniBoss, isBoss);
        body = createEnemy(world, x, y, mapEnemy.getSize(), mapEnemy.getSize(), false, true);
        this.type = mapEnemy;
        this.hp = type.getHp();
        this.shouldMove = true;
        this.attack = true;
        this.hitFixtures = new ArrayList<Fixture>();
        this.attackPeriod = type.getAttackInterval();
    }

    private Vector2 dir = new Vector2(0, 0);
    private float attackPeriod;
    private float attackTime = 0f;


    public void AiControler(float delta, Player player) {
        if (shouldMove && !isDead) {
            dir.x = player.getPosition().x - getPosition().x;
            dir.y = player.getPosition().y - getPosition().y;
            double pre = Math.sqrt(dir.x * dir.x + dir.y * dir.y);
            dir.x /= pre;
            dir.y /= pre;
            //body.setLinearVelocity(getPosition().x+dir.x/PPM,getPosition().y+dir.y/PPM);
            body.setTransform(new Vector2(getPosition().x + dir.x / PPM, getPosition().y + dir.y / PPM), body.getAngle());
            if (attack) {
                lightAttack();
            }
        }
    }


    private Body createEnemy(World world, float x, float y, int width, int height, boolean isStatic, boolean fixedRotation) {
        Body pBody;
        BodyDef def = new BodyDef();

        if (isStatic)
            def.type = BodyDef.BodyType.StaticBody;
        else
            def.type = BodyDef.BodyType.DynamicBody;

        def.position.set(x / PPM, y / PPM);
        def.fixedRotation = fixedRotation;
        pBody = world.createBody(def);
        pBody.setUserData("wall");

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.filter.categoryBits = Globals.BIT_ENEMY;
        fd.filter.maskBits = Globals.BIT_ENEMY | Globals.BIT_WALL;
        fd.filter.groupIndex = 0;
        fd.density = 1.0f;
        pBody.createFixture(fd).setUserData(this);
        shape.dispose();

        pBody.setLinearDamping(100f);
        return pBody;
    }

    public void render(Camera cam) {
        if (!isDead) {
            if (!attack) {
                attackTime += Gdx.graphics.getDeltaTime();
                if (attackTime > attackPeriod) {
                    attackTime -= attackPeriod;
                    attack = true;
                    hitFixtures.clear();
                }
            }


            shapeRenderer = new ShapeRenderer();
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.line(new Vector2(getPosition().x * PPM, getPosition().y * PPM), new Vector2(this.rayEnd.x * PPM, this.rayEnd.y * PPM));
            shapeRenderer.end();
        }
    }

    public EmapEnemyType getRandomEnemyType(boolean isMiniBoss, boolean isBoss) {
        if (!isMiniBoss && !isBoss) {
            return EmapEnemyType.NORMAL;
        }
        if (isMiniBoss && !isBoss) {
            return EmapEnemyType.MINI_BOSS;
        }
        return EmapEnemyType.BOSS;
    }

    public EmapEnemyType getType() {
        return type;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setShouldMove(boolean shouldMove) {
        this.shouldMove = shouldMove;
    }

    public boolean isShouldMove() {
        return shouldMove;
    }

    public void setAttack(boolean attack) {
        this.attack = attack;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
