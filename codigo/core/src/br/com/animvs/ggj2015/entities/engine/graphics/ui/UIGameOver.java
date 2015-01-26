package br.com.animvs.ggj2015.entities.engine.graphics.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import br.com.animvs.ggj2015.controller.GameController;

/**
 * Created by ANSCHAU on 24/01/2015.
 */
public class UIGameOver extends UIBase {
    public UIGameOver(GameController controller, String caminhoUISkin) {
        super(controller, caminhoUISkin);
    }

    @Override
    protected void eventBuild(int width, int height, float ratioX, float ratioY) {
        getWindow().setBackground("frame-blue");

        Label lblTitle = new Label(getGameController().getLanguage().getLang().getValor("main.ui.gameover.title"), getUiSkin(), "title-white");
        lblTitle.setAlignment(Align.center);
        Label lblMessage = new Label(getGameController().getLanguage().getLang().getValor("main.ui.gameover.message"), getUiSkin(), "title-white");
        lblMessage.setAlignment(Align.center);

        Image imgRestart = new Image(getUiSkin(), "btn-restart");
        imgRestart.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                getGameController().startMatch();
                return true;
            }
        });

        Image imgHome = new Image(getUiSkin(), "btn-home");
        imgHome.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                goToUIInitial();
                return true;
            }
        });

        Table tbBotoes = new Table(getUiSkin());
        tbBotoes.add(imgHome).width(101f * getController().getRatio().x).height(97f * getController().getRatio().y).fillX().expandX();
        tbBotoes.add(imgRestart).width(85f * getController().getRatio().x).height(97f * getController().getRatio().y).fillX().expandX();

        getWindow().add().fill().expand().row();
        getWindow().add(lblTitle).fillX().expandX().row();
        getWindow().add(lblMessage).fill().expand().row();
        getWindow().add(tbBotoes).width(800f * getController().getRatio().x).fillX().expandX().row();
        getWindow().add().fill().expand();
    }

    @Override
    protected void eventVisible() {

    }

    @Override
    protected void eventNotVisible() {

    }

    @Override
    protected void eventoBack() {

    }

    @Override
    public void eventActionButtonPressed() {
        goToUIInitial();
    }

    private void goToUIInitial() {
        getGameController().getUiController().showUIInitial();
    }
}
