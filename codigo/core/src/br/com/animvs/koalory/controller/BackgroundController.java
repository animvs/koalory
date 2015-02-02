package br.com.animvs.koalory.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import br.com.animvs.koalory.Configurations;

/**
 * Created by DALDEGAN on 25/01/2015.
 */
public final class BackgroundController extends BaseController {
    private Stage stage;

    public BackgroundController(GameController controller) {
        super(controller);
        SpriteBatch batch = new SpriteBatch();
        batch.setShader(controller.getShaderColor().getGDXShader());

        //TODO: The right viewport shoukd be FitViewport but for some reason, it's not working:
        stage = new Stage(new ExtendViewport(Configurations.RESOLUTION_REAL.x, Configurations.RESOLUTION_REAL.y), batch);

        Image background = new Image(getController().getLoad().get(LoadController.TEXTURE_BACKGROUND, Texture.class));

        stage.addActor(background);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void initialize() {
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public final void render() {
        //No need to call act() since there is only one (not animated) image in theis stage:
        //stage.act(Gdx.graphics.getDeltaTime());

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
