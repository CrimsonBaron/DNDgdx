package com.dnd.game.entities;


import com.badlogic.gdx.physics.box2d.Body;

public abstract class KillAbleEntity extends Entity{

    public float SPEED = 425;

    public Body body;


    public KillAbleEntity() {
    }

    public float getSPEED() {
        return SPEED;
    }

    public void setSPEED(float SPEED) {
        this.SPEED = SPEED;
    }



}
