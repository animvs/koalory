package br.com.animvs.koalory.entities.engine.graphics.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import br.com.animvs.engine2.graficos.shaders.Shader;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;

public final class ShaderColor extends Shader {
    private GameController controller;

    public ShaderColor(GameController controller) {
        this.controller = controller;
    }

    @Override
    protected FileHandle getFragmentShaderFile() {
        return Gdx.files.internal(Configurations.GRAPHICS_SHADER_COLOR_FRAG);
    }

    @Override
    protected FileHandle getVertexShaderFile() {
        return Gdx.files.internal(Configurations.GRAPHICS_SHADER_DEFAULT_VERT);
    }

    @Override
    public void update() {
        // Texture t = new
        // Texture(Gdx.files.internal("data/graphics/background/grass256.png"));
        // t.bind(0);
        //setUniformf("u_power", intensidade);
        // setUniformf("u_intensidade2", intensidade);
        // setUniformf("u_intensidade3", intensidade);
        // Engine.loga("TEST SHADER", String.valueOf(intensidade));

        setUniformf("v_satIntensity", controller.getColorRecovered());
    }
}
