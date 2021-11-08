package com.dnd.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.nio.file.attribute.UserPrincipalLookupService;

import static com.dnd.game.Globals.PPM;

public class Player extends KillAbleEntity {

    public Player(World world) {
        this.body = createPlayerHitBox(world, 0, 0, 32, 32 );
        this.body.setLinearDamping(20f);
        this.body.setAngularDamping(1.3f);
        this.SPEED = 725;
    }

    private Body createPlayerHitBox(World world, int x, int y, int hx, int hy) {
        Body pBody;
        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.DynamicBody;

        def.position.set(x/PPM, y/PPM);
        def.fixedRotation = true;
        pBody = world.createBody(def);

        PolygonShape polyShape = new PolygonShape();
        polyShape.setAsBox(hx/PPM, hy/PPM);

        FixtureDef fd = new FixtureDef();
        fd.shape = polyShape;
        fd.density = 1.f;

        pBody.createFixture(fd).setUserData(this);
        polyShape.dispose();

        return pBody;

    }

    public void render(Batch batch) {

    }

    public void controller(float delta){

        float x=0,y=0;

        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            x+=1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            x-=1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            y+=1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            y-=1f;
        }

        if(x != 0) {
            body.setLinearVelocity(x * SPEED * delta, body.getLinearVelocity().y);

        }
        if(y != 0) {
            body.setLinearVelocity(body.getLinearVelocity().x, y * SPEED * delta);
        }
    }



    public Vector2 getPosition() {
        return body.getPosition();
    }

    public void dispose() {

    }

}
