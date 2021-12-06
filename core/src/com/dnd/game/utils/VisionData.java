package com.dnd.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class VisionData {

    public Vector2 point;
    public Fixture fixture;

    public VisionData(Vector2 point, Fixture fixture) {
        this.point = point;
        this.fixture = fixture;
    }
}
