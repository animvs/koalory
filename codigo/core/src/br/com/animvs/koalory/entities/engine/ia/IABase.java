package br.com.animvs.koalory.entities.engine.ia;

import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.entities.game.mobiles.Foe;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public abstract class IABase {
    private GameController controller;

    protected final GameController getController() {
        return controller;
    }

    public IABase(GameController controller) {
        this.controller = controller;
    }

    public abstract void update(Foe foeOwner);
}
