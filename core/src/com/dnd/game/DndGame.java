package com.dnd.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.dnd.game.components.GameStateManager;

public class DndGame extends ApplicationAdapter {

	private boolean test = true;

	private OrthographicCamera camera;
	private SpriteBatch batch;

	private GameStateManager stateManager;
	
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);

		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);

		stateManager = new GameStateManager(this);

	}



	@Override
	public void render () {
		stateManager.update(Gdx.graphics.getDeltaTime());
		stateManager.render();

		if (test){stateManager.setState(GameStateManager.State.DUNGEON); test=!test;}
		//if (test){stateManager.setState(GameStateManager.State.COMBAT); test=!test;}


	}

	@Override
	public void resize(int width, int height) {
		stateManager.resize(1280, 720);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		stateManager.dispose();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}
	public SpriteBatch getBatch() {
		return batch;
	}

}
