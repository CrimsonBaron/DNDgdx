package com.dnd.game.entities;

import com.dnd.game.utils.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.dnd.game.utils.SceneBuilder;

public class Enemy extends KillAbleEntity{
    private World world;
    private float x,y;
    private Body body;

    public enum EmapEnemyType{
        NORMAL(64),
        MINI_BOSS(128),
        BOSS(256);

        private int size;

        EmapEnemyType(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    public Enemy(World world, float x, float y ,boolean isBoss) {
        this.world = world;
        this.x = x;
        this.y = y;
        EmapEnemyType mapEnemy = getRandomEnemyType(isBoss);
        body = SceneBuilder.createBox(world,x,y, mapEnemy.getSize(), mapEnemy.getSize(), false,true);
    }

    public EmapEnemyType getRandomEnemyType(boolean isBoss){
        if (MathUtils.randomChance(80) && !isBoss){
            return EmapEnemyType.NORMAL;
        }
        if (MathUtils.randomChance(20) && !isBoss){
            return EmapEnemyType.MINI_BOSS;
        }
        return EmapEnemyType.BOSS;
    }
}
