package com.dnd.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.dnd.game.utils.SceneBuilder;

public class LootPile extends InteractableEntity {
    private boolean interactable;
    private boolean openned;

    public LootPile(World world, float x, float y) {
        this.width = 128;
        this.height = 128;
        this.body = SceneBuilder.createBox(world, x, y, width, height, true, true);
        this.world = world;
        this.interactable = false;
        this.openned = false;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.E) && !openned){
            System.out.println("chest openned");
            openned = true;
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void beginContact(Contact contact) {
        interactable = !openned;
    }

    @Override
    public void endContact(Contact contact) {
        interactable= false;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        super.preSolve(contact, oldManifold);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        super.postSolve(contact, impulse);
    }
}
