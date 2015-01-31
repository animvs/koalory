package br.com.animvs.koalory.entities.engine.graphics.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;

/**
 * Created by ANSCHAU on 24/01/2015.
 */
public class UIInGame extends UIBase {

    private ImageTextButton imgRed;
    private ImageTextButton imgBlue;
    private ImageTextButton imgGreen;
    private Table tbLifes;

    private Touchpad touchpad;

    public UIInGame(GameController controller, String caminhoUISkin) {
        super(controller, caminhoUISkin);
    }

    @Override
    protected void eventBuild(int width, int height, float ratioX, float ratioY) {

        imgRed = new ImageTextButton("90%", getUiSkin(), "cores");

        tbLifes = new Table(getUiSkin());

        Table tbCores = new Table(getUiSkin());
        tbCores.add(imgRed).width(150f * getController().getRatio().y).height(150f * getController().getRatio().y).pad(5f * getController().getRatio().y).row();

        getWindow().add(tbLifes).left().top();
        getWindow().add().expandX().fillX();
        getWindow().add(tbCores).row();

        if (Configurations.SIMULATE_MOBILE_ON_DESKTOP || Gdx.app.getType() != Application.ApplicationType.Desktop) {
            Table tbCenter = new Table(getUiSkin());
            tbCenter.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    /*computeAction();*/
                    getGameController().getInput().setMobileTouchClicked(true);
                    return true;
                }
            });
            tbCenter.add(new Label("", getUiSkin())).fill().expand();
            getWindow().add(tbCenter).colspan(3).fill().expand().row();

            touchpad = new Touchpad(20f, getUiSkin());
            Table tbRodapeRight = new Table(getUiSkin());
            tbRodapeRight.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    /*computeAction();*/
                    getGameController().getInput().setMobileTouchClicked(true);
                    return true;
                }
            });
            getWindow().add(touchpad).bottom().height(220f * getController().getRatio().y).width(220f * getController().getRatio().y).pad(5f * getController().getRatio().y);

            tbRodapeRight.add(new Label("", getUiSkin())).fill().expand();
            getWindow().add(tbRodapeRight).colspan(2).fill().expand();

        } else
            getWindow().add().fill().expand();

        /*setDebug(true);
        getWindow().setDebug(true);*/
    }

    @Override
    protected void eventVisible() {
        getGameController().getSound().playMusicInGame();
    }

    @Override
    protected void eventNotVisible() {

    }

    @Override
    protected void eventoBack() {

    }

    @Override
    public void render() {
        super.render();
        tbLifes.clear();
        updateColors();
        for (int i = 0; i < getGameController().getLives(); i++) {
            Image life = new Image(getUiSkin(), "life");
            tbLifes.add(life).width(59f * getController().getRatio().x).height(54f * getController().getRatio().y).pad(5f * getController().getRatio().y);
        }

        if (Configurations.SIMULATE_MOBILE_ON_DESKTOP || Gdx.app.getType() != Application.ApplicationType.Desktop)
            if (touchpad != null)
                /*if (touchpad.getKnobPercentX() != getGameController().getEntities().getPlayer(0).getMovementXMobile())*/
                    getGameController().getInput().setTouchKnobMovementX(touchpad.getKnobPercentX());

    }

    public void updateColors() {
        imgRed.setText(getGameController().getUI().getColorRecoveredCastCache() + "%");
    }

    /*private void computeAction() {
        if (getGameController().getPlayers().getTotalPlayersInGame() > 0)
            getGameController().getEntities().getPlayer(0).tryJump();
        else
            getGameController().getEntities().getPlayer(0).getInput().setMobileTouchClicked(true);
    }*/

    @Override
    public void eventActionButtonPressed() {

    }
}