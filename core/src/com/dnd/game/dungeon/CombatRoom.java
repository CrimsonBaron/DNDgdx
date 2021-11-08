package com.dnd.game.dungeon;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.dnd.game.utils.SceneBuilder;

import java.util.ArrayList;

public class CombatRoom {
    private World world;
    private ArrayList<Body> layout;

    public CombatRoom(World world) {
        this.world = world;
        this.layout = new ArrayList<Body>();

        initRoom();
    }

    public void initRoom(){
        layout.add(SceneBuilder.createBox(world,0,-360,1300,400,true,true));
        layout.add(SceneBuilder.createBox(world, 450, 338, 600, 64, true, true));
        layout.add(SceneBuilder.createBox(world,-450, 338, 600, 64, true, true));
    }
}
