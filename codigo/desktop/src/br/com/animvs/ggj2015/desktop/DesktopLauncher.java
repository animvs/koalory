package br.com.animvs.ggj2015.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import br.com.animvs.ggj2015.GGJ2015Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.foregroundFPS = 60;
        config.backgroundFPS = 60;

        config.width = 1280;
        config.height = 768;

		new LwjglApplication(new GGJ2015Game(), config);
	}
}
