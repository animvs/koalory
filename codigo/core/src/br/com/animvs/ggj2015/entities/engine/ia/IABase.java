package br.com.animvs.ggj2015.entities.engine.ia;

import br.com.animvs.ggj2015.controller.GameController;
import br.com.animvs.ggj2015.entities.game.Foe;

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
