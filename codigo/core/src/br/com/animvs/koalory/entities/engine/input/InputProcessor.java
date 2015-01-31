package br.com.animvs.koalory.entities.engine.input;

import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.entities.game.Player;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public abstract class InputProcessor {
    private Player playerOwner;
    private GameController controller;

    protected GameController getController() {
        return controller;
    }

    public InputProcessor(GameController controller) {
        this.controller = controller;
    }

    public final Player getPlayerOwner() {
        return playerOwner;
    }

    public final void setPlayer(Player player) {
        playerOwner = player;
    }

    public abstract float getMovementX();

    public abstract boolean getActionPressed();

    public abstract void update();
}
