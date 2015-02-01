package br.com.animvs.koalory.entities.engine.input;

import com.badlogic.gdx.Gdx;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public final class KeyboardProcessor extends InputProcessor {

    private int leftKeyIndex;
    private int rightKeyIndex;
    private int actionKeyIndex;

    public KeyboardProcessor(GameController controller, int leftKeyIndex, int rightKeyIndex, int actionKeyIndex) {
        super(controller);
        this.leftKeyIndex = leftKeyIndex;
        this.rightKeyIndex = rightKeyIndex;
        this.actionKeyIndex = actionKeyIndex;
    }

    @Override
    public boolean getActionPressed() {
        return Gdx.input.isKeyJustPressed(actionKeyIndex);
    }

    @Override
    public void update() {
        //Does nothing
    }

    @Override
    public float getMovementX() {
        float movementX = 0f;

        if (Gdx.input.isKeyPressed(leftKeyIndex))
            movementX -= Configurations.GAMEPLAY_MOVEMENT_SPEED;

        if (Gdx.input.isKeyPressed(rightKeyIndex))
            movementX += Configurations.GAMEPLAY_MOVEMENT_SPEED;

        return movementX;
    }
}
