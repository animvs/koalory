package br.com.animvs.koalory.controller;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import br.com.animvs.koalory.Configurations;

/**
 * Created by DALDEGAN on 25/01/2015.
 */
public final class BackgroundController extends BaseController {
    private Stage stage;

    public enum Type {
        JUNGLE, DESERT, CASTLE
    }

    private Type type;

    public void setType(Type type) {
        if (this.type == type)
            return;

        this.type = type;

        Image newBackground;

        switch (type) {
            case JUNGLE:
                newBackground = new Image(getController().getLoad().get(LoadController.TEXTURE_BACKGROUND, Texture.class));
                break;
            case CASTLE:
                newBackground = new Image(getController().getLoad().get(LoadController.TEXTURE_BACKGROUND_CASTLE, Texture.class));
                break;
            case DESERT:
                newBackground = new Image(getController().getLoad().get(LoadController.TEXTURE_BACKGROUND_DESERT, Texture.class));
                break;
            default:
                throw new RuntimeException("Unknown background type: " + type.name());
        }

        stage.clear();
        stage.addActor(newBackground);

        //resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public Viewport getViewport() {
        return stage.getViewport();
    }

    public BackgroundController(GameController controller) {
        super(controller);
        SpriteBatch batch = new SpriteBatch();
        batch.setShader(controller.getShaderColor().getGDXShader());

        //TODO: The right viewport should be FitViewport but for some reason, it's not working:
        stage = new Stage(new FitViewport(Configurations.RESOLUTION_REAL.x, Configurations.RESOLUTION_REAL.y), batch);
        setType(Type.JUNGLE);
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
