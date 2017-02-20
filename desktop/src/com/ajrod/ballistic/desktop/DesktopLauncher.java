package com.ajrod.ballistic.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ajrod.ballistic.Ballistic;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Ballistic.WIDTH;
		config.height = Ballistic.HEIGHT;
		config.title = Ballistic.TITLE;
		new LwjglApplication(new Ballistic(new DesktopAds()), config);
	}
}
