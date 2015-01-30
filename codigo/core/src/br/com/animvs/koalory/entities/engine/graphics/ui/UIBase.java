package br.com.animvs.koalory.entities.engine.graphics.ui;

import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.ui.AnimvsUI2;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public abstract class UIBase extends AnimvsUI2 {

    private GameController controller;

    protected final GameController getGameController() {
        return controller;
    }

    public UIBase(GameController controller, String caminhoUISkin) {
        super(controller.getUI(), controller.getLoad().getAssetManager(), caminhoUISkin, controller.getFonts().getFonts());

        this.controller = controller;
    }

    @Override
    public void render() {
        super.render();

        if (controller.getInput().checkAnyInputHasActionPressed())
            eventActionButtonPressed();

        /*
        //Keyboard action button for player 1:
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            eventActionButtonPressed();

        //Keyboard action button for player 2:
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0))
            eventActionButtonPressed();

        int maxJoysticks = Controllers.getControllers().size > Configurations.GAMEPLAY_MAX_PLAYERS ? Configurations.GAMEPLAY_MAX_PLAYERS : Controllers.getControllers().size;

        for (int i = 0; i < maxJoysticks; i++) {

            if (controller.getPlayers().getPlayer(i).getInput().getButtonActionJustPressed())
                eventActionButtonPressed();
        }*/
    }

    public abstract void eventActionButtonPressed();
}
