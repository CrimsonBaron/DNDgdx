package com.dnd.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dnd.game.Globals;

import static com.dnd.game.Globals.PPM;

public class SceneBuilder {
    public static Body createBox(World world, float x, float y, int width, int height, boolean isStatic, boolean fixedRotation) {
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
        fd.filter.categoryBits = Globals.BIT_WALL;
        fd.filter.maskBits = Globals.BIT_ENEMY | Globals.BIT_PLAYER;
        fd.filter.groupIndex = 0;
        fd.density = 1.0f;
        pBody.createFixture(fd);
        shape.dispose();

        pBody.setLinearDamping(20);
        return pBody;
    }




}
