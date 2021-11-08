package com.dnd.game.states;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dnd.game.DndGame;
import com.dnd.game.components.GameStateManager;

public abstract  class GameState {

    protected  GameStateManager gsm;
    protected  DndGame app;
    protected  SpriteBatch batch;
    protected  OrthographicCamera camera;

    public GameState(GameStateManager gsm) {
        this.gsm = gsm;
        this.app = gsm.getApp();
        batch = app.getBatch();
        camera = app.getCamera();
    }

    public void resize(int w, int h) {
        camera.setToOrtho(false, w, h);
    }

    public abstract void update(float delta);
    public abstract void render();
    public abstract void dispose();

}
