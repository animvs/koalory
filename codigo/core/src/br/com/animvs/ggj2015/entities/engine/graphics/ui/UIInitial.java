package br.com.animvs.ggj2015.entities.engine.graphics.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ArrayMap;

import br.com.animvs.ggj2015.controller.UIController;

/**
 * Created by ANSCHAU on 23/01/2015.
 */
public class UIInitial extends UIBase {

    private UIController controller;

    public UIInitial(UIController controller, AssetManager assetManager, String caminhoUISkin, ArrayMap<String, BitmapFont> fontes) {
        super(controller, assetManager, caminhoUISkin, fontes);
        this.controller = controller;
    }

    @Override
    public void eventActionButtonPressed() {
        startGame();
    }

    @Override
    protected void eventBuild(int width, int height, float ratioX, float ratioY) {
        getWindow().setBackground("frame-blue");

        Image imgLogo = new Image(getUiSkin(), "logo");
        ImageButton btnPlay = new ImageButton(getUiSkin(), "btn-play");
        btnPlay.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                startGame();
                return true;
            }
        });

        ImageButton btnCredits = new ImageButton(getUiSkin(), "btn-credit");
        btnCredits.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                controller.showUICredits();
                return true;
            }
        });

        Table tbPlay = new Table(getUiSkin());
        tbPlay.add(imgLogo).width(460 * getController().getRatio().x).height(142f * getController().getRatio().y).pad(20f * getController().getRatio().x).row();
        tbPlay.add(btnPlay).width(239f * getController().getRatio().x).height(241f * getController().getRatio().y);


        Table tbCredits = new Table(getUiSkin());
        tbCredits.add().fillX().expandX();
        tbCredits.add(btnCredits).width(100f * getController().getRatio().x).height(100f * getController().getRatio().y).pad(20f * getController().getRatio().x);

        getWindow().add().row();
        getWindow().add(tbPlay).fill().expand().row();
        getWindow().add(tbCredits).fillX().expandX();
    }

    @Override
    protected void eventVisible() {
        controller.getGameController().getSound().playMusicInGame();
    }

    @Override
    protected void eventNotVisible() {

    }

    @Override
    protected void eventoBack() {

    }

    private void startGame() {
        controller.getGameController().startMatch();
    }
}
