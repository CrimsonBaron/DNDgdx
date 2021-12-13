package com.dnd.game.states;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.dnd.game.Globals;
import com.dnd.game.components.GameStateManager;
import com.dnd.game.dungeon.Room;
import com.dnd.game.entities.Enemy;
import com.dnd.game.entities.Player;
import com.dnd.game.utils.SceneBuilder;
import com.dnd.game.weapons.Bullet;

import static com.dnd.game.Globals.PPM;
import static com.dnd.game.utils.MathUtils.randomChance;

public class DungeonState extends GameState {

    private final Player player;
    private final Vector2 target;
    private final Box2DDebugRenderer b2dr;
    private final World world;

    private Room[][] rooms;
    private Vector2 pos;
    private int roomCount = 18;
    private int maxRooms = 20;

    private RayHandler rayHandler;
    private int state = 0;


    public DungeonState(GameStateManager gsm) {
        super(gsm);
        world = new World(new Vector2(0f, 0f), false);
        b2dr = new Box2DDebugRenderer();
        target = new Vector2(0, 0);
        rooms = new Room[32][32];
        rayHandler = new RayHandler(world);
        pos = new Vector2(MathUtils.random(28) + 2, MathUtils.random(28) + 2);
        player = new Player(world, camera,rayHandler);
        CreateRoom(0);
        player.setCurrentPlayersRoom(rooms[(int) pos.x][(int) pos.y]);
       //rooms[(int) pos.x][(int) pos.y].spawnmEn();
       // rooms[(int) pos.x][(int) pos.y].spawnmEn();
       // rooms[(int) pos.x][(int) pos.y].spawnmEn();


    }

    private void CreateRoom(int i) {
        rooms[(int) pos.x][(int) pos.y] = new Room(world, (int) pos.x, (int) pos.y, target);
        roomCount++;
        System.out.println("Room count"+roomCount);

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
                if (!checkIfBodyExits(target.x, target.y - 720 + 338)) {
                    SceneBuilder.createBox(world, target.x, target.y - 720 + 338, 130, 32, true, true);
                }
                if (!checkIfBodyExits(target.x, target.y - 338)){
                    SceneBuilder.createBox(world, target.x, target.y - 338, 130, 32, true, true);
                }

            }
        }
        if (enter != 1 && !W) {
            if (randomChance(30)) {
                if (!checkIfBodyExits(target.x - 1280 + 620, target.y)) {
                    SceneBuilder.createBox(world, target.x - 1280 + 620, target.y, 32, 120, true, true);
                }
                if (!checkIfBodyExits(target.x - 620, target.y)){
                    SceneBuilder.createBox(world, target.x - 620, target.y, 32, 120, true, true);
                }


            }
        }
        if (enter != 2 && !N) {
            if (randomChance(30)) {
                if (!checkIfBodyExits(target.x, target.y + 720 - 338)) {
                    SceneBuilder.createBox(world, target.x, target.y + 720 - 338, 130, 32, true, true);
                }
                if (!checkIfBodyExits(target.x, target.y + 338)){
                    SceneBuilder.createBox(world, target.x, target.y + 338, 130, 32, true, true);
                }

            }
        }
        if (enter != 3 && !E) {
            if (randomChance(30)) {
                if (!checkIfBodyExits(target.x + 1280 - 620, target.y)) {
                    SceneBuilder.createBox(world, target.x + 1280 - 620, target.y, 32, 120, true, true);
                }
                if (!checkIfBodyExits(target.x + 620, target.y)) {
                    SceneBuilder.createBox(world, target.x + 620, target.y, 32, 120, true, true);
                }

            }
        }

        if (roomCount % 2 == 0 && randomChance(70)) {
            rooms[(int) pos.x][(int) pos.y].createPillars(randomChance(60));
        }

        if (roomCount%2==1 && randomChance(30) && roomCount > 1){
            rooms[(int) pos.x][(int) pos.y].spawnChest(player);
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

       if (!rooms[(int) pos.x][(int) pos.y].getEnemies().isEmpty()){
           for (Enemy e: rooms[(int) pos.x][(int) pos.y].getEnemies()) {
               e.AiControler(delta,player);
           }
       }

        if (player.getPosition().x > (target.x + 640) / PPM) {
            target.x += 1280;
            pos.x += 1;
            if (rooms[(int) pos.x][(int) pos.y] == null) {
                CreateRoom(1);
            }
            player.setCurrentPlayersRoom(rooms[(int) pos.x][(int) pos.y]);
        }

        if (player.getPosition().x < (target.x - 640) / PPM) {
            target.x -= 1280;
            pos.x -= 1;
            if (rooms[(int) pos.x][(int) pos.y] == null) {
                CreateRoom(3);
            }
            player.setCurrentPlayersRoom(rooms[(int) pos.x][(int) pos.y]);
        }

        if (player.getPosition().y > (target.y + 360) / PPM) {
            target.y += 720;
            pos.y += 1;
            if (rooms[(int) pos.x][(int) pos.y] == null) {
                CreateRoom(0);
            }
            player.setCurrentPlayersRoom(rooms[(int) pos.x][(int) pos.y]);
        }

        if (player.getPosition().y < (target.y - 360) / PPM) {
            target.y -= 720;
            pos.y -= 1;
            if (rooms[(int) pos.x][(int) pos.y] == null) {
                CreateRoom(2);
            }
            player.setCurrentPlayersRoom(rooms[(int) pos.x][(int) pos.y]);
        }

        for (int i = 0; i <rooms.length ; i++) {
            for (Room r:rooms[i]) {
                if (r!=null){
                    if (r.getChest() !=null){
                        r.getChest().update(delta);
                    }
                    if (!r.getEnemies().isEmpty()){
                        for (Enemy e: r.getEnemies()) {
                            e.setPlayer(this.player);
                        }
                    }
                }
            }
        }

        if (player.getDead() ){
            gsm.setState(GameStateManager.State.DUNGEON);
        }
      /*  if (!rooms[(int) pos.x][(int) pos.y].getEnemies().isEmpty()&&rooms[(int) pos.x][(int) pos.y].getEnemies().get(0).getType() == Enemy.EmapEnemyType.BOSS && rooms[(int) pos.x][(int) pos.y] .getEnemies().get(0).isDead()){
           /* if (state == 0){
                gsm.setState(GameStateManager.State.TITLE);
                state++;
            }else{
                gsm.setState(GameStateManager.State.DUNGEON);
                state--;
            }
            System.exit(0);
        }*/
        rayHandler.update();
        rayHandler.setCombinedMatrix(camera.combined.cpy().scl(PPM));
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

    private BitmapFont font = new BitmapFont();
    @Override
    public void render() {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        b2dr.render(world, camera.combined.cpy().scl(PPM));
        player.render(batch);


       if (!player.getCurrentPlayersRoom().getEnemies().isEmpty()){
           for (Enemy e: player.getCurrentPlayersRoom().getEnemies()) {
               e.render(camera, batch);
           }
       }

        if (!rooms[(int) pos.x][(int) pos.y].getEnemies().isEmpty()&&rooms[(int) pos.x][(int) pos.y].getEnemies().get(0).getType() == Enemy.EmapEnemyType.BOSS && rooms[(int) pos.x][(int) pos.y] .getEnemies().get(0).isDead()){

           // gsm.setState(GameStateManager.State.DUNGEON);
            System.exit(0);
        }
       rayHandler.render();
        batch.begin();
            font.setColor(Color.FOREST);
            font.draw(batch,"HP: "+player.getHp()+"/100", target.x-570, target.y+300);
            font.draw(batch,"POTIONS: "+player.getPotion(), target.x-570, target.y+275);
        int x=450,y=300;
        for (Enemy e: player.getCurrentPlayersRoom().getEnemies()) {
            font.draw(batch,e.getType().toString()+": HP: "+e.getHp()+"/"+e.getType().getHp(),target.x+x,target.y+y);
            y -= 25;
        }
            font.draw(batch,"ROOMS TILL BOSS: "+(maxRooms-roomCount),target.x-570,target.y-300);
        batch.end();
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
