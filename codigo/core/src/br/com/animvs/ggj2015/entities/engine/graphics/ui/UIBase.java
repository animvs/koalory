package br.com.animvs.ggj2015.entities.engine.graphics.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ArrayMap;

import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.controller.UIController;
import br.com.animvs.ui.AnimvsUI2;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public abstract class UIBase extends AnimvsUI2 {

    public UIBase(UIController controller, AssetManager assetManager, String caminhoUISkin, ArrayMap<String, BitmapFont> fontes) {
        super(controller, assetManager, caminhoUISkin, fontes);
    }

    @Override
    public void render() {
        super.render();

        //Keyboard action button for player 1:
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            eventActionButtonPressed();

        //Keyboard action button for player 2:
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0))
            eventActionButtonPressed();

        for (int i = 0; i < Controllers.getControllers().size; i++) {
            if (Controllers.getControllers().get(i).getButton(Configurations.CORE_GAMEPAD_BUTTON_ACTION))
                eventActionButtonPressed();
        }
    }

    public abstract void eventActionButtonPressed();
}
