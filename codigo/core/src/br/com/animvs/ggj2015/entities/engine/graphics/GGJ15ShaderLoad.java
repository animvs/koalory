package br.com.animvs.ggj2015.entities.engine.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import br.com.animvs.engine2.graficos.shaders.ShaderLoad;

public final class GGJ15ShaderLoad extends ShaderLoad {

    @Override
    protected FileHandle getFragmentShaderFile() {
        return Gdx.files.internal("data/graphics/shaders/frag/animvs-load2.frag");
    }

    @Override
    protected FileHandle getVertexShaderFile() {
        return Gdx.files.internal("data/graphics/shaders/vert/fullquad.vert");
    }
}
