package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import br.com.animvs.engine2.graficos.shaders.ShaderFullQuad;
import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.entities.engine.graphics.ParallaxCamera;

/**
 * Created by DALDEGAN on 25/01/2015.
 */
public final class BackgroundController implements Disposable {
    private GameController controller;

    private SpriteBatch batch;
    private ParallaxCamera cameraCache;

    /*private ShaderFullQuad shader;
    private Vector2 resolutionCache;*/

    public BackgroundController(GameController controller) {
        this.controller = controller;
        batch = new SpriteBatch();
        batch.setShader(controller.getShaderColor().getGDXShader());

        /*resolutionCache = new Vector2();

        shader = new ShaderFullQuad() {
            @Override
            protected Vector2 getResolution() {
                resolutionCache.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                return resolutionCache;
            }

            @Override
            protected void eventUpdateParameters() {

            }

            @Override
            protected FileHandle getVertexShaderFile() {
                return Gdx.files.internal("data/graphics/shaders/vert/fullquad.vert");
            }

            @Override
            protected FileHandle getFragmentShaderFile() {
                return Gdx.files.internal("data/graphics/shaders/frag/default.frag");
            }
        };

        batch.setShader(shader.getGDXShader());*/
    }

    public final void render() {
        if (cameraCache == null)
            cameraCache = (ParallaxCamera) controller.getStage().getCamera();

        batch.setProjectionMatrix(cameraCache.calculateParallaxMatrix(1f, 1));

        float x = controller.getStage().getCamera().position.x - Configurations.RESOLUTION_REAL.x / 2f;
        float y = controller.getStage().getCamera().position.y - Configurations.RESOLUTION_REAL.y / 2f;

        batch.begin();
        batch.draw(controller.getLoad().get(LoadController.TEXTURE_BACKGROUND, Texture.class), x, y);
        //for (int i = 0; i < 9; i++)
        //batch.draw(layers[2], i * layers[2].getRegionWidth() - 1024, -160);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
