package br.com.animvs.koalory;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import br.com.animvs.koalory.controller.GameController;

public class KoaloryGame extends ApplicationAdapter {

    private GameController controller;

    public GameController getController() {
        return controller;
    }

    @Override
    public void create() {
        controller.load();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0.502f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        controller.update();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        controller.resize(width, height);
    }

    public KoaloryGame() {
        controller = new GameController();
    }
}
