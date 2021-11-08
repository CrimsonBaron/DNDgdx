package com.dnd.game.utils;

public class MathUtils {

    public static boolean randomChance(int percentage) {
        int random = com.badlogic.gdx.math.MathUtils.random(100);
        return random < percentage;
    }

}
