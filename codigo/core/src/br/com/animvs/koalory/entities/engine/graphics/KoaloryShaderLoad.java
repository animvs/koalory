package br.com.animvs.koalory.entities.engine.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import br.com.animvs.engine2.graficos.shaders.ShaderLoad;

public final class KoaloryShaderLoad extends ShaderLoad {

    @Override
    protected FileHandle getFragmentShaderFile() {
        return Gdx.files.internal("data/graphics/shaders/frag/animvs-load2.frag");
    }

    @Override
    protected FileHandle getVertexShaderFile() {
        return Gdx.files.internal("data/graphics/shaders/vert/fullquad.vert");
    }
}
