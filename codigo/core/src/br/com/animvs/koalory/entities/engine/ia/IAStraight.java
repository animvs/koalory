package br.com.animvs.koalory.entities.engine.ia;

import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.entities.game.Foe;

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
        if (!foeOwner.getAlive() || foeOwner.getBody() == null)
            return;

        //foeOwner.getBody().setLinearVelocity(speed, foeOwner.getBody().getLinearVelocity().y);
        foeOwner.getBody().applyForceToCenter(speed * 0.5f, 0f, true);
    }
}
