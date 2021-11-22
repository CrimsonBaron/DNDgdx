package com.dnd.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.dnd.game.components.GameStateManager;
import com.dnd.game.dungeon.Room;
import com.dnd.game.entities.Player;
import com.dnd.game.utils.SceneBuilder;

import static com.dnd.game.Globals.PPM;
import static com.dnd.game.utils.MathUtils.randomChance;

public class DungeonState extends GameState {

    private final Player player;
    private final Vector2 target;
    private final Box2DDebugRenderer b2dr;
    private final World world;

    private Room[][] rooms;
    private Vector2 pos;
    private int roomCount = 0;


    public DungeonState(GameStateManager gsm) {
        super(gsm);
        world = new World(new Vector2(0f, 0f), false);
        b2dr = new Box2DDebugRenderer();
        target = new Vector2(0, 0);
        rooms = new Room[32][32];
        pos = new Vector2(MathUtils.random(28) + 2, MathUtils.random(28) + 2);
        player = new Player(world, camera);
        CreateRoom(0);

    }

    private void CreateRoom(int i) {
        rooms[(int) pos.x][(int) pos.y] = new Room(world, (int) pos.x, (int) pos.y, target);
        roomCount++;
        System.out.println("Room count"+roomCount);
        player.setCurrentPlayersRoom(rooms[(int) pos.x][(int) pos.y]);
        generateRoomAdditions(i);
    }

    public void generateRoomAdditions(int enter) {
        boolean N = false, E = false, S = false, W = false;

        /*arrray end walls*/
        if (pos.x + 1 > rooms.length - 1) {
            E = true;
            SceneBuilder.createBox(world, target.x + 620, target.y, 32, 120, true, true);
        }
        if (pos.x - 1 < 0) {
            W = true;
            SceneBuilder.createBox(world, target.x - 620, target.y, 32, 120, true, true);
        }
        if (pos.y + 1 > rooms[0].length - 1) {
            N = true;
            SceneBuilder.createBox(world, target.x, target.y + 338, 130, 32, true, true);
        }
        if (pos.y - 1 < 0) {
            S = true;
            SceneBuilder.createBox(world, target.x, target.y - 338, 130, 32, true, true);
        }

        if (enter != 0) {
            if (randomChance(70)) {
                if (!checkIfBodyExits(target.x, target.y - 338)){
                    SceneBuilder.createBox(world, target.x, target.y - 338, 130, 32, true, true);
                }
                if (!checkIfBodyExits(target.x, target.y - 720 + 338)) {
                    SceneBuilder.createBox(world, target.x, target.y - 720 + 338, 130, 32, true, true);
                }
            }
        }
        if (enter != 1 && !W) {
            if (randomChance(30)) {
                if (!checkIfBodyExits(target.x - 620, target.y)){
                    SceneBuilder.createBox(world, target.x - 620, target.y, 32, 120, true, true);
                }
                if (!checkIfBodyExits(target.x - 1280 + 620, target.y)) {
                    SceneBuilder.createBox(world, target.x - 1280 + 620, target.y, 32, 120, true, true);
                }

            }
        }
        if (enter != 2 && !N) {
            if (randomChance(30)) {
                if (!checkIfBodyExits(target.x, target.y + 338)){
                    SceneBuilder.createBox(world, target.x, target.y + 338, 130, 32, true, true);
                }
                if (!checkIfBodyExits(target.x, target.y + 720 - 338)) {
                    SceneBuilder.createBox(world, target.x, target.y + 720 - 338, 130, 32, true, true);
                }
            }
        }
        if (enter != 3 && !E) {
            if (randomChance(30)) {
                if (!checkIfBodyExits(target.x + 620, target.y)) {
                    SceneBuilder.createBox(world, target.x + 620, target.y, 32, 120, true, true);
                }
                if (!checkIfBodyExits(target.x + 1280 - 620, target.y)) {
                    SceneBuilder.createBox(world, target.x + 1280 - 620, target.y, 32, 120, true, true);
                }
            }
        }

        if (roomCount % 2 == 0 && randomChance(70)) {
            rooms[(int) pos.x][(int) pos.y].createPillars(randomChance(60));
        }

        if (roomCount%2==1 && randomChance(30) && roomCount > 1){
            rooms[(int) pos.x][(int) pos.y].spawnChest();
        }

        if (roomCount%2==1 && randomChance(70) && roomCount > 1){
            if (rooms[(int) pos.x][(int) pos.y].getChest() == null){
                rooms[(int) pos.x][(int) pos.y].spawnEnemies();
            }
        }

        if (roomCount >= 20){
            if (rooms[(int) pos.x][(int) pos.y].getChest() == null){
                rooms[(int) pos.x][(int) pos.y].spawnBoss();
                rooms[(int) pos.x][(int) pos.y].createPillars(true);
            }
        }


    }

    private boolean checkIfBodyExits(float x, float y) {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Body b : bodies) {
            Vector2 pos = b.getPosition();
            if (pos.x == x && pos.y == y) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void update(float delta) {
        world.step(1 / 60f, 6, 2);
        player.controller(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            if (camera.zoom < 5) {
                camera.zoom += .1f;
            } else if (camera.zoom > 5) {
                camera.zoom =1f;
            }
        }


        if (player.getPosition().x > (target.x + 640) / PPM) {
            target.x += 1280;
            pos.x += 1;
            if (rooms[(int) pos.x][(int) pos.y] == null) {
                CreateRoom(1);
            }
        }

        if (player.getPosition().x < (target.x - 640) / PPM) {
            target.x -= 1280;
            pos.x -= 1;
            if (rooms[(int) pos.x][(int) pos.y] == null) {
                CreateRoom(3);
            }
        }

        if (player.getPosition().y > (target.y + 360) / PPM) {
            target.y += 720;
            pos.y += 1;
            if (rooms[(int) pos.x][(int) pos.y] == null) {
                CreateRoom(0);
            }
        }

        if (player.getPosition().y < (target.y - 360) / PPM) {
            target.y -= 720;
            pos.y -= 1;
            if (rooms[(int) pos.x][(int) pos.y] == null) {
                CreateRoom(2);
            }
        }

        for (int i = 0; i <rooms.length ; i++) {
            for (Room r:rooms[i]) {
                if (r!=null){
                    if (r.getChest() !=null){
                        r.getChest().update(delta);
                    }
                }
            }
        }

        cameraUpdate();
        batch.setProjectionMatrix(camera.combined);
    }

    public void cameraUpdate() {
        Vector3 position = camera.position;
        position.x = camera.position.x + (target.x - camera.position.x) * .1f;
        position.y = camera.position.y + (target.y - camera.position.y) * .1f;
        camera.position.set(position);
        camera.update();
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        b2dr.render(world, camera.combined.cpy().scl(PPM));
        player.render(batch);
    }

    @Override
    public void dispose() {
        b2dr.dispose();
        world.dispose();
        player.dispose();

        for (int i = 0; i <rooms.length ; i++) {
            for (Room r:rooms[i]) {
                if (r!=null){
                    r.dispose();
                }
            }
        }

    }
}
