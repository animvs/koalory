package br.com.animvs.koalory.entities.engine.input;

import br.com.animvs.koalory.controller.GameController;

/**
 * Created by DALDEGAN on 31/01/2015.
 */
public final class TouchProcessor extends InputProcessor {
    private float movementX;
    private boolean touchPressed;

    public TouchProcessor(GameController controller) {
        super(controller);
    }

    @Override
    public float getMovementX() {
        return movementX;
    }

    @Override
    public boolean getActionPressed() {
        return touchPressed;
    }

    @Override
    public void update() {
        this.movementX = getController().getInput().getTouchKnobMovementX();
        this.touchPressed = getController().getInput().getMobileTouchClicked();
    }
}
