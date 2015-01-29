package br.com.animvs.koalory.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import br.com.animvs.koalory.KoaloryGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.foregroundFPS = 60;
        config.backgroundFPS = 60;

        config.width = 1280;
        config.height = 768;

		new LwjglApplication(new KoaloryGame(), config);
	}
}
