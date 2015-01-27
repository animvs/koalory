package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import br.com.animvs.ggj2015.Configurations;

/**
 * Created by DALDEGAN on 25/01/2015.
 */
public final class BackgroundController extends BaseController {
    private SpriteBatch batch;

    public BackgroundController(GameController controller) {
        super(controller);
        batch = new SpriteBatch();
        batch.setShader(controller.getShaderColor().getGDXShader());
    }

    @Override
    public void initialize() {
    }

    public final void render() {
        batch.setProjectionMatrix(getController().getCamera().calculateParallaxMatrix(1f, 1f));

        float width = Configurations.RESOLUTION_REAL.x * getController().getCamera().getZoom();
        float height = Configurations.RESOLUTION_REAL.y * getController().getCamera().getZoom();

        float x = getController().getStage().getCamera().position.x - width / 2f;
        float y = getController().getStage().getCamera().position.y - height / 2f;

        batch.begin();
        batch.draw(getController().getLoad().get(LoadController.TEXTURE_BACKGROUND, Texture.class), x, y, width, height);
        //for (int i = 0; i < 9; i++)
        //batch.draw(layers[2], i * layers[2].getRegionWidth() - 1024, -160);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
