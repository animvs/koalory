package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Scaling;

import br.com.animvs.ggj2015.entities.engine.graphics.ui.UICredits;
import br.com.animvs.ggj2015.entities.engine.graphics.ui.UIGameOver;
import br.com.animvs.ggj2015.entities.engine.graphics.ui.UIGameWin;
import br.com.animvs.ggj2015.entities.engine.graphics.ui.UIInGame;
import br.com.animvs.ggj2015.entities.engine.graphics.ui.UIInitial;
import br.com.animvs.ui.AnimvsUI2;
import br.com.animvs.ui.AnimvsUIController;
import br.com.animvs.ui.AnimvsUIController2;

/**
 * Created by ANSCHAU on 23/01/2015.
 */
public class UIController extends AnimvsUIController2 {
    private GameController controller;

    private Skin uiSkin;

    private int colorRecoveredCastCache;

    public int getColorRecoveredCastCache() {
        return colorRecoveredCastCache;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }

    public GameController getGameController() {
        return controller;
    }

    public UIController(GameController controller, String styleJSONPath, Vector2 resolutionReal) {
        super(styleJSONPath, resolutionReal);
        this.controller = controller;

        uiSkin = AnimvsUIController.createSkin(controller.getLoad().getAssetManager(),
                LoadController.UI_SKIN_PATH,
                controller.getFonts().getFonts(),
                LoadController.UI_JSON_PATH);

        showUIInitial();
    }

    @Override
    public void setUI(AnimvsUI2 uiCurrent) {
        super.setUI(uiCurrent);

        if (getUI() != null && controller.getStage() != null && controller.getStage().getViewport() != null)
            getUI().setBoundsByViewport(controller.getStage().getViewport());
    }

    @Override
    protected Rectangle getBounds(Rectangle rectangleToBeSet) {
        return super.getBounds(rectangleToBeSet);
    }

    @Override
    public Vector2 getRatio(Vector2 ratioToBeSet) {
        if (ratioToBeSet == null)
            throw new RuntimeException("The parameter 'ratioToBeSet' must be != null");

        if (controller.getStage() != null && controller.getStage().getViewport() != null)
            ratioToBeSet.set(controller.getStage().getViewport().getScreenWidth() / getResolutionReal().x, controller.getStage().getViewport().getScreenHeight() / getResolutionReal().y);
        else
            ratioToBeSet.set(Gdx.graphics.getWidth() / getResolutionReal().x, Gdx.graphics.getHeight() / getResolutionReal().y);

        return ratioToBeSet;
    }

    public Group addRemainder(Actor actor) {
        Group group = new Group();
        group.addActor(actor);

        TextureRegion region = controller.getLoad().get(LoadController.UI_SKIN_PATH, TextureAtlas.class).findRegion("button-exclamation");

        //TODO: Width and height are oddly inverted:
        Vector2 size = Scaling.fit.apply(region.getRegionHeight(), region.getRegionWidth(), actor.getWidth() * 0.3f, actor.getHeight() * 0.5f);
        //Vector2 size = new Vector2(actor.getWidth() * 0.25f, actor.getHeight() * 0.5f);

        Image imageExclamation = new Image(getUiSkin(), "button-exclamation");
        imageExclamation.setSize(size.x, size.y);

        imageExclamation.setPosition(actor.getWidth() - imageExclamation.getWidth(), actor.getHeight() - imageExclamation.getHeight());
        group.addActor(imageExclamation);

        return group;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (getUI() != null) {
            getUI().setBoundsByViewport(controller.getStage().getViewport());
        }
    }

    public void showUIInitial() {
        UIInitial uiInitial = new UIInitial(this, controller.getLoad().getAssetManager(), LoadController.UI_SKIN_PATH, controller.getFonts().getFonts());
        uiInitial.create();
        setUI(uiInitial);
    }

    public void showUIInGame() {
        UIInGame inGame = new UIInGame(this, controller.getLoad().getAssetManager(), LoadController.UI_SKIN_PATH, controller.getFonts().getFonts());
        inGame.create();
        setUI(inGame);
    }

    public void showUIGameOver() {
        UIGameOver uiGameOver = new UIGameOver(this, controller.getLoad().getAssetManager(), LoadController.UI_SKIN_PATH, controller.getFonts().getFonts());
        uiGameOver.create();
        setUI(uiGameOver);
    }

    public void showUIGameWin() {
        UIGameWin uiGameWin = new UIGameWin(this, controller.getLoad().getAssetManager(), LoadController.UI_SKIN_PATH, controller.getFonts().getFonts());
        uiGameWin.create();
        setUI(uiGameWin);
    }

    public void showUICredits() {
        UICredits uiCredits = new UICredits(this, controller.getLoad().getAssetManager(), LoadController.UI_SKIN_PATH, controller.getFonts().getFonts());
        uiCredits.create();
        setUI(uiCredits);
    }

    public void castValueColors() {
        colorRecoveredCastCache = (int) (controller.getColorRecovered() * 100f);
    }
}
