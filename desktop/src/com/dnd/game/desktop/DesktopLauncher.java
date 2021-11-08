package com.dnd.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dnd.game.DndGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "DND";
		config.width = 1280;
		config.height = 720;
		config.resizable=false;
		config.backgroundFPS = 60;
		config.foregroundFPS = 60;
		new LwjglApplication(new DndGame(), config);
	}
}
