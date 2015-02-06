package br.com.animvs.koalory.entities.engine.ia;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import br.com.animvs.engine2.matematica.Random;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.entities.game.Foe;
import br.com.animvs.koalory.entities.game.Player;

/**
 * Created by DALDEGAN on 05/02/2015.
 */
public final class IABoss extends IABase {

    private final float attackInterval;
    private final int attackTimes;
    private final float waitInterval;

    private float waitCounter;
    private int attacksPerformed;
    private float attackCounter;

    private State state;

    private Vector2 directionCache;

    private enum State {
        WAIT, ATTACK
    }

    private void setState(State newState) {
        switch (newState) {
            case WAIT:
                waitCounter = 0f;
                break;
            case ATTACK:
                attacksPerformed = 0;
                attackCounter = 0f;
                break;
            default:
                throw new RuntimeException("Unknown state during state change in IABoss: " + state.name());
        }

        this.state = newState;
    }

    public IABoss(GameController controller, float waitInterval, int attackTimes, float attackInterval) {
        super(controller);

        if (attackTimes <= 0)
            throw new RuntimeException("The parameter 'attackTimes' must be > 0");

        this.state = State.WAIT;
        this.waitInterval = waitInterval;
        this.attackTimes = attackTimes;
        this.attackInterval = attackInterval;

        this.directionCache = new Vector2();
    }

    @Override
    public void update(Foe foeOwner) {
        switch (state) {
            case WAIT:
                processStateWait();
                break;
            case ATTACK:
                processStateAttack(foeOwner);
                break;
            default:
                throw new RuntimeException("Unknown state during update() in IABoss: " + state.name());
        }
    }

    private void processStateWait() {
        waitCounter += Gdx.graphics.getDeltaTime();

        if (waitCounter >= waitInterval)
            setState(State.ATTACK);
    }

    private void processStateAttack(Foe foeOwner) {
        //Wait for any player to appear to attack:
        if (getController().getPlayers().getTotalPlayersInGame() == 0)
            return;

        if (attacksPerformed >= attackTimes) {
            setState(State.WAIT);
            return;
        }

        attackCounter += Gdx.graphics.getDeltaTime();

        if (attackCounter >= attackInterval) {
            attackCounter = 0f;
            attacksPerformed++;

            float lifeInterval = 10f;

            float forceMin = 50f;
            float forceMax = forceMin * 3f;
            float forceToUse = Random.random(forceMin, forceMax);

            float radiusMin = 10f;
            float radiusMax = 20f;

            Player target = getController().getPlayers().getPlayer(Random.random(getController().getPlayers().getTotalPlayersInGame() - 1));

            directionCache.set(foeOwner.getX(), foeOwner.getY());
            directionCache.sub(target.getX(), target.getY()).nor();
            directionCache.scl(-1f);

            Vector2 spawnPosition = new Vector2(foeOwner.getX(), foeOwner.getY());
            if (directionCache.x < 0f)
                spawnPosition.x -= 150f;
            else
                spawnPosition.x += 150f;

            getController().getEntities().spawnWeight(spawnPosition, directionCache.x * forceToUse, directionCache.y * forceToUse,
                    lifeInterval, Random.random(radiusMin, radiusMax));
        }
    }
}
