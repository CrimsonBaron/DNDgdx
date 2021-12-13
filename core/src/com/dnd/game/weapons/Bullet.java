package com.dnd.game.weapons;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dnd.game.Globals;
import com.dnd.game.entities.Player;
import com.dnd.game.utils.LightBuilder;

import static com.dnd.game.Globals.PPM;

public class Bullet {

    private Body body;
    private RayHandler rayHandler;
    private World world;
    private Player player;

    private final Vector2 rayEnd;
    private final Vector2 rayStart;
    private Vector2 prevDir;

    private float speed;
    private PointLight pointLight;

    public Bullet(RayHandler rayHandler,Player player, World world, float radius, Vector2 rayEnd, Vector2 rayStart,float speed) {
        this.rayHandler = rayHandler;
        this.world = world;
        this.player = player;
        this.rayEnd = rayEnd;
        this.rayStart = rayStart;
        this.speed = speed;
        this.body = createBullet(world,radius);
        //this.pointLight = LightBuilder.createPointLightAtBodyLoc(rayHandler,body, Color.BLUE,1);
        this.prevDir = new Vector2(0,0);
    }


    private boolean isActie = true;

    public void fire(){
        Vector2 dir = calcDir(rayEnd,rayStart);
        //System.out.println(dir);

       // body.setLinearVelocity(dir.x*speed* Gdx.graphics.getDeltaTime(), dir.y*speed* Gdx.graphics.getDeltaTime());
        body.setTransform(new Vector2(body.getPosition().x+dir.x,body.getPosition().y+dir.y),body.getAngle());
        if (body.getPosition().x+dir.x >= rayEnd.x && body.getPosition().y+dir.y >= rayEnd.y){
            world.destroyBody(body);
            //pointLight.setColor(Color.CLEAR);
            isActie = false;
        }

    }

    private Vector2 calcDir(Vector2 rayEnd, Vector2 rayStart) {
        Vector2 dir = new Vector2(0,0);
        dir.x = rayEnd.add(prevDir).x - body.getPosition().x;
        dir.y = rayEnd.add(prevDir).y -  body.getPosition().y;
        double pre = Math.sqrt(dir.x * dir.x + dir.y * dir.y);
        dir.x /= pre;
        dir.y /= pre;
        prevDir = dir;
        return dir;
    }

    private Body createBullet(World world, float radius ){
        Body pBody;
        BodyDef def = new BodyDef();


        def.type = BodyDef.BodyType.DynamicBody;
        def.fixedRotation = true;
        def.position.set(player.getPosition());
        pBody = world.createBody(def);
        pBody.setUserData("bullet");

        CircleShape shape = new CircleShape();
        shape.setRadius(radius/PPM);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.filter.categoryBits = Globals.BIT_SWORD;
        fd.filter.maskBits = Globals.BIT_ENEMY | Globals.BIT_WALL;
        fd.filter.groupIndex = 0;
        fd.density = 1.0f;
        pBody.createFixture(fd);
        pBody.setBullet(true);
        shape.dispose();

        pBody.setLinearDamping(1);
        return pBody;
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getRayEnd() {
        return rayEnd;
    }

    public boolean isActie() {
        return isActie;
    }
}
