package com.dnd.game.entities;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.dnd.game.Globals;
import com.dnd.game.dungeon.Room;
import com.dnd.game.interfaces.ICombatInter;
import com.dnd.game.utils.LightBuilder;
import com.dnd.game.utils.VisionData;
import com.dnd.game.weapons.WeaponType;


import java.util.HashMap;

import static com.dnd.game.Globals.*;

public class Player extends MapEntity implements ICombatInter {

    private OrthographicCamera cam;
    private Pixmap crosshair;


    private final World world;
    private Room currentPlayersRoom;
    private final float SPEED;

    private Vector3 mouseLoc;
    private Vector2 rayStart;
    private Vector2 rayEnd;

    private Boolean isDead;
    private float hp;

    private static final float vissionDistance = 1280;
    private static final int rayCount = 1500;
    private RayCastCallback vissionCallback;
    private HashMap<Integer, VisionData> visionRays;
    private int currentI = 0;

    private RayHandler rayHandler;
    private PointLight pointLight;
    private ConeLight flashLight;

    private boolean updateLightAttackAnim = true;
    private boolean updateHeavyAttackAnim = true;
    private boolean updateShootAnim = true;


    private float LightAttacktimeSeconds = 0f;
    private float LightAttackperiod = 1f;
    private float HeavyAttacktimeSeconds = 0f;
    private float HeavyAttackperiod = 2f;
    private float ShottimeSeconds = 0f;
    private float Shotperiod = 0.25f;

    private float lightTimer = 0f;
    private float lightPeriod = 0.05f;

    private WeaponType weaponType = WeaponType.SWORD;


    public Player(World world, OrthographicCamera cam) {
        this.cam = cam;


        this.crosshair = new Pixmap(Gdx.files.internal("../data/textures/croshair.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(crosshair, 0, 0));
        this.crosshair.dispose();


        this.body = createPlayerHitBox(world, 0, 0, 32, 32);
        this.body.setLinearDamping(20f);
        this.body.setAngularDamping(1.3f);
        this.SPEED = 725;
        this.world = world;
        this.currentPlayersRoom = null;
        this.rayEnd = new Vector2(0, 0);
        this.rayStart = new Vector2(0, 0);
        this.mouseLoc = new Vector3(0, 0, 0);
        this.isDead = false;
        this.hp = 100f;
        this.rayHandler = new RayHandler(world);
        this.flashLight = LightBuilder.createConeLight(rayHandler, this.body, Color.GRAY, 50, -90, 15);
        this.pointLight = LightBuilder.createPointLightAtBodyLoc(rayHandler, this.body, Color.SCARLET, 10);
       /* this.vissionCallback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (!visionRays.containsKey(currentI)) {
                    visionRays.put(currentI, new VisionData(point.cpy(), fixture));
                } else if (visionRays.get(currentI).point.dst(getPosition()) > point.dst(getPosition())) {
                    visionRays.put(currentI, new VisionData(point.cpy(), fixture));
                }

                return 0;
            }
        };
        this.visionRays = new HashMap<Integer, VisionData>();*/
    }

    private Body createPlayerHitBox(World world, int x, int y, int hx, int hy) {
        Body pBody;
        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.DynamicBody;

        def.position.set(x / PPM, y / PPM);
        def.fixedRotation = true;
        pBody = world.createBody(def);

        // PolygonShape polyShape = new PolygonShape();
        // polyShape.setAsBox(hx / PPM, hy / PPM);

        CircleShape polyShape = new CircleShape();
        polyShape.setRadius(hx / PPM);


        FixtureDef fd = new FixtureDef();
        fd.shape = polyShape;
        fd.density = 1.f;
        fd.filter.categoryBits = Globals.BIT_PLAYER;
        fd.filter.maskBits = Globals.BIT_WALL;
        fd.filter.groupIndex = 0;


        pBody.createFixture(fd).setUserData(this);
        polyShape.dispose();

        return pBody;

    }


    public void render(Batch batch) {

        if (flashLight.getColor() != Color.GRAY) {
            lightTimer += Gdx.graphics.getDeltaTime();
            if (lightTimer > lightPeriod){
                lightTimer -= lightPeriod;
                flashLight.setColor(Color.GRAY);
            }
        }


        if (!updateLightAttackAnim) {
            LightAttacktimeSeconds += Gdx.graphics.getDeltaTime();
            if (LightAttacktimeSeconds > LightAttackperiod) {
                LightAttacktimeSeconds -= LightAttackperiod;
                updateLightAttackAnim = true;
            }
        }

        if (!updateHeavyAttackAnim) {
            HeavyAttacktimeSeconds += Gdx.graphics.getDeltaTime();
            if (HeavyAttacktimeSeconds > HeavyAttackperiod) {
                HeavyAttacktimeSeconds -= HeavyAttackperiod;
                updateHeavyAttackAnim = true;
            }
        }

        if (!updateShootAnim) {
            ShottimeSeconds += Gdx.graphics.getDeltaTime();
            if (ShottimeSeconds > Shotperiod) {
                ShottimeSeconds -= Shotperiod;
                updateShootAnim = true;
            }
        }

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);

        rayHandler.render();

       /*- if (!visionRays.isEmpty()) {
            Vector2 pos = this.body.getPosition();
            Gdx.gl20.glEnable(GL20.GL_BLEND);
            Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            //shapeRenderer.setColor(1, 0, 0, 1); // Red line
            //shapeRenderer.line(new Vector2(this.body.getPosition().x * PPM, this.body.getPosition().y * PPM), new Vector2(mouseLoc.x, mouseLoc.y));
            shapeRenderer.identity();
            shapeRenderer.setColor(.25f, .25f, .25f, 0.5f);
            Gdx.gl20.glLineWidth(10);
            for (Integer i : visionRays.keySet()) {
                shapeRenderer.line(new Vector2(pos.x * PPM, pos.y * PPM), new Vector2(visionRays.get(i).point.x * PPM, visionRays.get(i).point.y * PPM));
            }

            double angle = (Math.PI * 2) / rayCount;
            for (int i = 0; i < rayCount; i++) {
                if (!visionRays.containsKey(i)) {
                    double rotAmmount = angle * i;
                    Vector2 v = new Vector2(0, 1).scl(vissionDistance);
                    v.rotateRad((float) rotAmmount);
                    v.add(getPosition());
                    shapeRenderer.line(new Vector2(pos.x * PPM, pos.y * PPM), new Vector2(v.x * PPM, v.y * PPM));
                }
            }
            shapeRenderer.end();

            visionRays.clear();
        }

        Gdx.gl20.glLineWidth(1);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.identity();
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(getPosition().x * PPM, getPosition().y * PPM, 32, 10);
        shapeRenderer.end();*/

    }

    public void controller(float delta) {

        //visionRays.clear();

        float x = 0, y = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += 1f;
            //swordBody.setTransform(swordBody.getPosition(), (float) (-90*DEGREES_TO_RADIANS));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= 1f;
            //swordBody.setTransform(swordBody.getPosition(), (float) (90*DEGREES_TO_RADIANS));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += 1f;
            // swordBody.setTransform(swordBody.getPosition(), (float) (180*DEGREES_TO_RADIANS));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= 1f;
            //swordBody.setTransform(swordBody.getPosition(), (float) (-180*DEGREES_TO_RADIANS));
        }

        if (x != 0) {
            body.setLinearVelocity(x * SPEED * delta, body.getLinearVelocity().y);

        }
        if (y != 0) {
            body.setLinearVelocity(body.getLinearVelocity().x, y * SPEED * delta);
        }

        Gdx.input.setInputProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.Q) {
                    weaponType = WeaponType.SWORD;
                    System.out.println(weaponType.toString());
                    return true;
                }
                if (keycode == Input.Keys.E) {
                    weaponType = WeaponType.BIGSWORD;
                    System.out.println(weaponType.toString());
                    return true;
                }
                if (keycode == Input.Keys.R) {
                    weaponType = WeaponType.GUN;
                    System.out.println(weaponType.toString());
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    switch (weaponType) {
                        case SWORD:
                            if (updateLightAttackAnim) {
                                lightAttack();
                                updateLightAttackAnim = false;
                            }
                            break;
                        case BIGSWORD:
                            if (updateHeavyAttackAnim) {
                                chargedAttack();
                                ;
                                updateHeavyAttackAnim = false;
                            }
                            break;
                    }
                    return true;
                }

                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        });

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && weaponType == WeaponType.GUN) {
            if (updateShootAnim) {
                gunShot();
                updateShootAnim = false;
            }
        }


        this.mouseLoc = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.unproject(mouseLoc);

        Vector2 direction = new Vector2((mouseLoc.x / PPM) - getPosition().x, (mouseLoc.y / PPM) - getPosition().y);

        float angle = (float) (Math.atan2(direction.y, direction.x));
        angle = (float) (Math.toDegrees(angle));
        angle -= Math.PI / 2;

        flashLight.setDirection(angle);
        flashLight.setPosition(getPosition());

        rayHandler.update();
        rayHandler.setCombinedMatrix(cam.combined.cpy().scl(PPM));
        // castRays();

    }

    public void castRays() {
        double angle = (Math.PI * 2) / rayCount;
        for (int i = 0; i < rayCount; i++) {
            double ammountRot = angle * i;
            Vector2 v = new Vector2(0, 1).scl(vissionDistance);
            v.rotateRad((float) ammountRot);
            v.add(getPosition());
            currentI = i;
            world.rayCast(vissionCallback, getPosition(), v);
        }

        for (int i = 0; i < rayCount; ++i) {
            if (visionRays.containsKey(i)) {
                Fixture f = visionRays.get(i).fixture;
                //implemetn visibility logic with visible interface
            }
        }


    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Room getCurrentPlayersRoom() {
        return currentPlayersRoom;
    }


    public void setCurrentPlayersRoom(Room currentPlayersRoom) {
        this.currentPlayersRoom = currentPlayersRoom;
        //visionRays.clear();

    }

    public void dispose() {
        rayHandler.dispose();
    }

    @Override
    public void lightAttack() {
      /* // createSwordtestShit(world,getPosition().x,getPosition().y,32,72);
        if (!currentPlayersRoom.getEnemies().isEmpty()){
            Vector2 playerPosLeft = new Vector2((getPosition().x+32/PPM),(getPosition().y+72/PPM));
            Vector2 playerPosRight = new Vector2((getPosition().x-32/PPM),(getPosition().y+72/PPM));
            for (Enemy e:currentPlayersRoom.getEnemies()) {
                Vector2 enemyPos = e.getPosition();


                if (((enemyPos.x >= getPosition().x && enemyPos.x <= playerPosLeft.x) || (enemyPos.x >= getPosition().x && enemyPos.x <= playerPosRight.x)) &&
                    ((enemyPos.y >= getPosition().y && enemyPos.y <= playerPosLeft.y) || (enemyPos.y >= getPosition().y && enemyPos.y <= playerPosRight.y))){
                    System.out.println("BOOOm");
                }

            }
        }*/

        RayCastCallback callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (!currentPlayersRoom.getEnemies().isEmpty()) {
                    for (Enemy e : currentPlayersRoom.getEnemies()) {
                        if (fixture.getBody().getPosition() == e.getPosition()) {
                            e.damage(10);
                            return 1;
                        }
                    }
                }

                return -1;
            }
        };

        setRayCastLocationAndCastRay(callback);
        flashLight.setColor(Color.CYAN);

    }


    @Override
    public void chargedAttack() {

        RayCastCallback callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (!currentPlayersRoom.getEnemies().isEmpty()) {
                    for (Enemy e : currentPlayersRoom.getEnemies()) {
                        if (fixture.getBody().getPosition() == e.getPosition()) {
                            e.damage(50);
                            return 1;
                        }
                    }
                }

                return -1;
            }
        };

        setRayCastLocationAndCastRay(callback);
        flashLight.setColor(Color.RED);
    }

    private void setRayCastLocationAndCastRay(RayCastCallback callback) {
        rayStart = getPosition();
        rayEnd = new Vector2((mouseLoc.x / PPM), (mouseLoc.y / PPM));

        System.out.println("body pos" + this.body.getPosition().toString());
        System.out.println("rayStart: " + rayStart.toString());
        System.out.println("rayEmd: " + rayEnd.toString());


        world.rayCast(callback, rayStart, rayEnd);
    }

    @Override
    public void gunShot() {
        RayCastCallback callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (!currentPlayersRoom.getEnemies().isEmpty()) {
                    for (Enemy e : currentPlayersRoom.getEnemies()) {
                        if (fixture.getBody().getPosition() == e.getPosition()) {
                            e.damage(5f);
                            return 1;
                        }
                    }
                }

                return -1;
            }
        };

        setRayCastLocationAndCastRay(callback);
        flashLight.setColor(Color.GOLD);
    }

    @Override
    public void damage(float dmg) {
        if (!isDead) {
            this.hp -= dmg;
        }
        if (hp < 0) {
            isDead = true;
        }
        System.out.println("player hp: " + this.hp);
    }

    public Boolean getDead() {
        return isDead;
    }
}
