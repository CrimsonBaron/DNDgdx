package com.dnd.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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
import com.dnd.game.weapons.WeaponType;


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



    private boolean updateLightAttackAnim = true;
    private boolean updateHeavyAttackAnim = true;
    private boolean updateShootAnim = true;


    private float LightAttacktimeSeconds = 0f;
    private float LightAttackperiod = 1f;
    private float HeavyAttacktimeSeconds = 0f;
    private float HeavyAttackperiod = 2f;
    private float ShottimeSeconds = 0f;
    private float Shotperiod = 0.25f;

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
    }

    private Body createPlayerHitBox(World world, int x, int y, int hx, int hy) {
        Body pBody;
        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.DynamicBody;

        def.position.set(x / PPM, y / PPM);
        def.fixedRotation = true;
        pBody = world.createBody(def);

        PolygonShape polyShape = new PolygonShape();
        polyShape.setAsBox(hx / PPM, hy / PPM);

        FixtureDef fd = new FixtureDef();
        fd.shape = polyShape;
        fd.density = 1.f;
        fd.filter.categoryBits = Globals.BIT_PLAYER;
        fd.filter.maskBits = Globals.BIT_ENEMY;
        fd.filter.groupIndex = 0;


        pBody.createFixture(fd).setUserData(this);
        polyShape.dispose();

        return pBody;

    }


    public void render(Batch batch) {

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
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1); // Red line
        shapeRenderer.line(new Vector2(this.body.getPosition().x * PPM, this.body.getPosition().y * PPM), new Vector2(mouseLoc.x, mouseLoc.y));
        shapeRenderer.end();

    }

    public void controller(float delta) {

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

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && weaponType == WeaponType.GUN){
            if (updateShootAnim) {
                gunShot();
                updateShootAnim = false;
            }
        }


        this.mouseLoc = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.unproject(mouseLoc);


    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Room getCurrentPlayersRoom() {
        return currentPlayersRoom;
    }


    public void setCurrentPlayersRoom(Room currentPlayersRoom) {
        this.currentPlayersRoom = currentPlayersRoom;
    }

    public void dispose() {

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
    }

    @Override
    public void damage(float dmg) {

    }
}
