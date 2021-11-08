package com.dnd.game.utils;

import com.badlogic.gdx.physics.box2d.*;

import static com.dnd.game.Globals.PPM;

public class SceneBuilder {
    public static Body createBox(World world, float x, float y, int width, int height, boolean isStatic, boolean fixedRotation) {
        Body pBody;
        BodyDef def = new BodyDef();

        if(isStatic)
            def.type = BodyDef.BodyType.StaticBody;
        else
            def.type = BodyDef.BodyType.DynamicBody;

        def.position.set(x /PPM, y /PPM);
        def.fixedRotation = fixedRotation;
        pBody = world.createBody(def);
        pBody.setUserData("wall");

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2/PPM  , height/2/PPM  );

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 1.0f;
        pBody.createFixture(fd);
        shape.dispose();
        return pBody;
    }
}
