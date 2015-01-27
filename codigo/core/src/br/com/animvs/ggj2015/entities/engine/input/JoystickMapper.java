package br.com.animvs.ggj2015.entities.engine.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.MathUtils;

import br.com.animvs.ggj2015.Configurations;

/**
 * Created by DALDEGAN on 27/01/2015.
 */
public final class JoystickMapper extends InputProcessor {

    private Controller joystick;
    private int actionButtonIndex;

    private boolean buttonActionPressedLastFrame;
    private boolean buttonActionPressedNow;

    public JoystickMapper(Controller joystick, int actionButtonIndex) {
        if (joystick == null)
            throw new RuntimeException("The parameter 'joystick' must be != NULL");

        if (actionButtonIndex < 0)
            throw new RuntimeException("The parameter 'actionButtonIndex' must be > 0");

        this.joystick = joystick;
        this.actionButtonIndex = actionButtonIndex;
    }

    @Override
    public float getMovementX() {
        float movementX = 0f;

        //TODO: Is there a way to know how many axis the joystick owns ?
        for (int i = 0; i < 5; i++) {
            if (joystick.getAxis(i) < -0.15f)
                movementX -= Configurations.GAMEPLAY_MOVEMENT_SPEED;

            if (joystick.getAxis(i) > 0.15f)
                movementX += Configurations.GAMEPLAY_MOVEMENT_SPEED;
        }

        return MathUtils.clamp(movementX, -Configurations.GAMEPLAY_MOVEMENT_SPEED, Configurations.GAMEPLAY_MOVEMENT_SPEED);
    }

    @Override
    public void update() {
        buttonActionPressedLastFrame = buttonActionPressedNow;
        buttonActionPressedNow = joystick.getButton(Configurations.CORE_GAMEPAD_BUTTON_ACTION);
    }

    @Override
    public boolean getActionPressed() {
        return buttonActionPressedNow && !buttonActionPressedLastFrame;
    }
}
