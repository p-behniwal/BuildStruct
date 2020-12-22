package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.ScienceSorcerySiege;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Science Sorcery Siege!";
		config.height = 960;
		config.width = 960;
		new LwjglApplication(new ScienceSorcerySiege(), config);
		
		
	}
}
