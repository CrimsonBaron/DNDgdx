package com.dnd.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.dnd.game.components.GameStateManager;
import com.dnd.game.dungeon.CombatRoom;

import static com.dnd.game.Globals.PPM;

public class CombatState extends  GameState{

    private final Box2DDebugRenderer b2dr;
    private final World world;
    private final CombatRoom room;

    public CombatState(GameStateManager gsm) {
        super(gsm);
        world = new World(new Vector2(0f, 0f), false);
        b2dr = new Box2DDebugRenderer();
        room = new CombatRoom(world);
    }

    @Override
    public void update(float delta) {
        world.step(1 / 60f, 6, 2);
        cameraUpdate(delta);
    }

    public void cameraUpdate(float delta){
        Vector3 pos = camera.position;
        pos.x=0;
        pos.y=0;
        camera.position.set(pos);
        camera.update();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        b2dr.render(world, camera.combined.cpy().scl(PPM));
    }

    @Override
    public void dispose() {
        b2dr.dispose();
        world.dispose();
    }
}
