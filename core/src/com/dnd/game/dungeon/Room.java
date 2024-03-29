package com.dnd.game.dungeon;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.dnd.game.entities.Enemy;
import com.dnd.game.entities.InteractableEntity;
import com.dnd.game.entities.LootPile;
import com.dnd.game.entities.Player;
import com.dnd.game.utils.SceneBuilder;


import java.util.ArrayList;

import static com.dnd.game.utils.MathUtils.randomChance;

public class Room {
    private World world;

    private int x, y;
    private Vector2 center;
    private ArrayList<Body> layout;
    private ArrayList<Body> pillarsLayout;
    private ArrayList<Enemy> enemies;
    private LootPile chest;

    public Room(World world, int x, int y, Vector2 center) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.center = center;
        this.layout = new ArrayList<Body>();
        this.pillarsLayout = new ArrayList<Body>();
        this.enemies = new ArrayList<Enemy>();
        initRoom();
    }

    private Body left, right;

    public void initRoom() {
        // Bottom Wall
        layout.add(right = SceneBuilder.createBox(world, center.x + 352, center.y - 338, 570, 32, true, true));
        layout.add(left = SceneBuilder.createBox(world, center.x - 352, center.y - 338, 570, 32, true, true));

        // Top Wall
        layout.add(SceneBuilder.createBox(world, center.x + 352, center.y + 338, 570, 32, true, true));
        layout.add(SceneBuilder.createBox(world, center.x - 352, center.y + 338, 570, 32, true, true));

        // Right wall
        layout.add(SceneBuilder.createBox(world, center.x + 620, center.y + 190, 32, 260, true, true));
        layout.add(SceneBuilder.createBox(world, center.x + 620, center.y - 190, 32, 260, true, true));

        // Left wall
        layout.add(SceneBuilder.createBox(world, center.x - 620, center.y + 190, 32, 260, true, true));
        layout.add(SceneBuilder.createBox(world, center.x - 620, center.y - 190, 32, 260, true, true));


    }

    public void createPillars(boolean pillars) {
        pillarsLayout.add(SceneBuilder.createBox(world, center.x + 176, center.y + 169, 64, 64, true, true));
        pillarsLayout.add(SceneBuilder.createBox(world, center.x - 176, center.y + 169, 64, 64, true, true));
        pillarsLayout.add(SceneBuilder.createBox(world, center.x + 176, center.y - 169, 64, 64, true, true));
        pillarsLayout.add(SceneBuilder.createBox(world, center.x - 176, center.y - 169, 64, 64, true, true));

        if (pillars) {
            pillarsLayout.add(SceneBuilder.createBox(world, center.x + 376, center.y + 169, 64, 64, true, true));
            pillarsLayout.add(SceneBuilder.createBox(world, center.x - 376, center.y + 169, 64, 64, true, true));
            pillarsLayout.add(SceneBuilder.createBox(world, center.x + 376, center.y - 169, 64, 64, true, true));
            pillarsLayout.add(SceneBuilder.createBox(world, center.x - 376, center.y - 169, 64, 64, true, true));
        }
    }

    public void spawnChest(Player p) {
        chest = new LootPile(world, center.x + MathUtils.random(-376, 376), center.y + MathUtils.random(-169, 169),p);
        world.setContactListener(chest);
    }

    public void spawnEnemies(){
        enemies.add(new Enemy(world, center.x + MathUtils.random(-476, 476),center.y + MathUtils.random(-269, 269), randomChance(30),false));
        if (enemies.get(0).getType()!= Enemy.EmapEnemyType.MINI_BOSS){
            for (int i = 0; i <3 ; i++) {
                enemies.add(new Enemy(world, center.x + MathUtils.random(-476, 476),center.y + MathUtils.random(-269, 269),false,false));
            }
        }
    }

    public void spawnmEn(){
        enemies.add(new Enemy(world, center.x + MathUtils.random(-476, 476),center.y + MathUtils.random(-269, 269), randomChance(30),false));
    }

    public void spawnBoss(){
        enemies.add(new Enemy(world, center.x ,center.y,false,true));
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public LootPile getChest() {
        return chest;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void removeEnemy(Enemy e){
        enemies.remove(e);
    }

    public void dispose() {
        if (chest != null) {
            chest.dispose();
        }
        if (!enemies.isEmpty()){
            for (Enemy e:enemies){
                e.dispose();
            }
        }
    }


}
