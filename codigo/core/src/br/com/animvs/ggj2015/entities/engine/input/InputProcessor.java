package br.com.animvs.ggj2015.entities.engine.input;

import br.com.animvs.ggj2015.entities.game.Player;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public abstract class InputProcessor {
    private Player playerOwner;

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
