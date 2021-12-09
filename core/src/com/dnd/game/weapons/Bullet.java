package com.dnd.game.weapons;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dnd.game.Globals;
import com.dnd.game.entities.Player;

import static com.dnd.game.Globals.PPM;

public class Bullet {

    private Body body;
    private RayHandler rayHandler;
    private World world;
    private Player player;

    private Vector2 rayEnd;
    private Vector2 rayStart;

    private float speed;

    public Bullet(RayHandler rayHandler,Player player, World world, float radius, Vector2 rayEnd, Vector2 rayStart,float speed) {
        this.rayHandler = rayHandler;
        this.world = world;
        this.player = player;
        this.rayEnd = rayEnd;
        this.rayStart = rayStart;
        this.speed = speed;
        this.body = createBullet(world,radius);

    }

    public void fire(){
        Vector2 dir = calcDir(rayEnd,rayStart);
        System.out.println(dir);
        body.setLinearVelocity(dir.x*speed* Gdx.graphics.getDeltaTime(), dir.y*speed* Gdx.graphics.getDeltaTime());
    }

    private Vector2 calcDir(Vector2 rayEnd, Vector2 rayStart) {
        Vector2 dir = new Vector2(0,0);
        dir.x = player.getPosition().x - body.getPosition().x;
        dir.y = player.getPosition().y -  body.getPosition().y;
        double pre = Math.sqrt(dir.x * dir.x + dir.y * dir.y);
        dir.x /= pre;
        dir.y /= pre;
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
}
