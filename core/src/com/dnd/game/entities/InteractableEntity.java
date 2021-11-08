package com.dnd.game.entities;

import com.badlogic.gdx.physics.box2d.*;
import com.dnd.game.utils.SceneBuilder;

public abstract class InteractableEntity extends Entity implements ContactListener{

    public Body body;
    public World world;
    public int width = 15;
    public int height = 15;


    public InteractableEntity() {
    }

    public void render(){

    }

    public void update(float delta){

    }

    public void dispose(){

    }

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
