package com.dnd.game.utils;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;

import static com.dnd.game.Globals.PPM;

public class LightBuilder {
    private static final int numberOfRays = 500;

    public static PointLight createPointLightAtBodyLoc(RayHandler rayHandler, Body body, Color color, float lightDistance) {
        PointLight p = new PointLight(rayHandler, numberOfRays, color, lightDistance, 0, 0);
        p.attachToBody(body);
        return p;
    }

    public static PointLight createPointLightAtLoc(RayHandler rayHandler, float x, float y, Color color, float lightDistance) {
        PointLight p = new PointLight(rayHandler, numberOfRays, color, lightDistance, x / PPM, y / PPM);
        return p;
    }

    public static ConeLight createConeLight(RayHandler rayHandler, Body body, Color color,float lightDistance,float dir,float degrees){
        ConeLight c = new ConeLight(rayHandler,numberOfRays,color,lightDistance,0,0,dir,degrees);
       // c.attachToBody(body);
        return c;

    }
}
