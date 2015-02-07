package br.com.animvs.koalory.entities.engine.ia;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.entities.game.mobiles.Foe;
import br.com.animvs.koalory.entities.game.mobiles.Player;

/**
 * Created by DALDEGAN on 05/02/2015.
 */
public final class IABoss extends IABase {

    private float timeCounter;
    private int attacksPerformed;
    private float attackCounter;

    private State state;

    private Vector2 distanceCameraCache;

    //private Vector2 directionCache;

    private enum State {
        WAIT_CAMERA, MOVE, ATTACK
    }

    private void setState(State newState) {
        switch (newState) {
            case WAIT_CAMERA:
                timeCounter = 0f;
            case MOVE:
                timeCounter = 0f;
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

    public IABoss(GameController controller) {
        super(controller);

        this.state = State.WAIT_CAMERA;
        this.distanceCameraCache = new Vector2();

        //this.directionCache = new Vector2();
    }

    @Override
    public void update(Foe foeOwner) {
        switch (state) {
            case WAIT_CAMERA:
                processWaitStart(foeOwner);
                break;
            case MOVE:
                processMoveForward(foeOwner);
                break;
            case ATTACK:
                processStateAttack(foeOwner);
                break;
            default:
                throw new RuntimeException("Unknown state during update() in IABoss: " + state.name());
        }
    }

    private void processWaitStart(Foe foeOwner) {
        distanceCameraCache.set(foeOwner.getX(), foeOwner.getY());

        if (distanceCameraCache.dst(getController().getCamera().getPosition().x, getController().getCamera().getPosition().y ) < 500f) {
            setState(State.MOVE);
            Gdx.app.log("BOSS", "BOSS started ...");
        }
    }

    private void processMoveForward(Foe foeOwner) {
        timeCounter += Gdx.graphics.getDeltaTime();

        move(foeOwner);
        //foeOwner.getBody().setLinearVelocity(-0.2f, 0f);

        if (timeCounter >= Configurations.GAMEPLAY_BOSS_WAIT_INTERVAL)
            setState(State.ATTACK);
    }

    private void processStateAttack(Foe foeOwner) {
        //Wait for any player to appear to attack:
        if (getController().getPlayers().getTotalPlayersInGame() == 0)
            return;

        if (attacksPerformed >= Configurations.GAMEPLAY_BOSS_ATTACKS_PER_STATE) {
            setState(State.MOVE);
            return;
        }

        attackCounter += Gdx.graphics.getDeltaTime();

        if (attackCounter >= Configurations.GAMEPLAY_BOSS_ATTACK_INTERVAL) {
            attackCounter = 0f;
            attacksPerformed++;

            Vector2 spawnPosition = new Vector2(foeOwner.getX(), foeOwner.getY() + 200f);
            getController().getEntities().spawnProjectile(spawnPosition, Configurations.GAMEPLAY_BOSS_PROJECTILES_LIFE_INTERVAL, foeOwner);
        }
    }

    private void move(Foe foeOwner) {
        if (foeOwner == null || !foeOwner.getAlive() || foeOwner.getBody() == null)
            return;

        Player target = getController().getPlayers().getPlayerRandom();
        if (target == null)
            return;

        boolean directionRight = foeOwner.getX() < target.getX();

        if (directionRight)
            foeOwner.getBody().applyLinearImpulse(Configurations.GAMEPLAY_BOSS_SPEED, 0f, foeOwner.getX(), foeOwner.getY(), true);
        else
            foeOwner.getBody().applyLinearImpulse(-Configurations.GAMEPLAY_BOSS_SPEED, 0f, foeOwner.getX(), foeOwner.getY(), true);
    }
}
