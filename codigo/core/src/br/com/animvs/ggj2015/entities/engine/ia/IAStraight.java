package br.com.animvs.ggj2015.entities.engine.ia;

import br.com.animvs.ggj2015.controller.GameController;
import br.com.animvs.ggj2015.entities.game.Foe;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public final class IAStraight extends IABase {
    private float speed;

    public IAStraight(GameController controller, float speed) {
        super(controller);
        this.speed = speed;
    }

    @Override
    public void update(Foe foeOwner) {
        if (!foeOwner.getAlive())
            return;

        foeOwner.getBody().setLinearVelocity(speed, foeOwner.getBody().getLinearVelocity().y);
    }
}
