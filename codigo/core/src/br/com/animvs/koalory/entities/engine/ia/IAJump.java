package br.com.animvs.koalory.entities.engine.ia;

import com.badlogic.gdx.Gdx;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.entities.game.mobiles.Foe;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public final class IAJump extends IABase {
    private float forceX;
    private float forceY;
    private float interval;

    private float timeCounter;

    public IAJump(GameController controller, float forceX, float forceY, float interval) {
        super(controller);
        this.forceY = forceY;
        this.forceX = forceX;
        this.interval = interval;
        this.timeCounter = interval;
    }

    @Override
    public void update(Foe foeOwner) {
        if (!foeOwner.getAlive() || foeOwner.getBody() == null)
            return;

        timeCounter += Gdx.graphics.getDeltaTime();

        if (timeCounter >= interval) {
            timeCounter = 0f;

            foeOwner.getBody().applyForceToCenter(forceX * Configurations.CORE_PHYSICS_MULTIPLIER, forceY * Configurations.CORE_PHYSICS_MULTIPLIER, true);
        }
    }
}
