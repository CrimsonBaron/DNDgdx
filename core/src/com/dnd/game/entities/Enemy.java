package com.dnd.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dnd.game.Globals;
import com.dnd.game.interfaces.ICombatInter;
import com.dnd.game.utils.MathUtils;
import com.dnd.game.utils.SceneBuilder;

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

    @Override
    public void lightAttack() {
        if (player != null) {
            player.damage(10);
        }
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
        NORMAL(64),
        MINI_BOSS(128),
        BOSS(256);

        private int size;

        EmapEnemyType(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    public Enemy(World world, float x, float y, boolean isMiniBoss, boolean isBoss) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.hp = 100f;
        this.isDead = false;
        EmapEnemyType mapEnemy = getRandomEnemyType(isMiniBoss, isBoss);
        body = createEnemy(world, x, y, mapEnemy.getSize(), mapEnemy.getSize(), false, true);
        this.type = mapEnemy;
        this.shouldMove = true;
        this.attack = false;

    }

    private Vector2 dir = new Vector2(0, 0);
    private float attackPeriod = 1f;
    private float attackTime = 0f;

    public void AiControler(float delta, Player player) {
        if (shouldMove) {
            dir.x = player.getPosition().x - getPosition().x;
            dir.y = player.getPosition().y - getPosition().y;
            double pre = Math.sqrt(dir.x * dir.x + dir.y * dir.y);
            dir.x /= pre;
            dir.y /= pre;
            //body.setLinearVelocity(getPosition().x+dir.x/PPM,getPosition().y+dir.y/PPM);
            body.setTransform(new Vector2(getPosition().x + dir.x / PPM, getPosition().y + dir.y / PPM), body.getAngle());

            if (attack) {
                attackTime += Gdx.graphics.getDeltaTime();
                if (attackTime > attackPeriod) {
                    attackTime -= attackPeriod;
                    lightAttack();
                    attack = false;
                }
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
}
