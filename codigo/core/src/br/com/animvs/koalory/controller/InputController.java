package br.com.animvs.koalory.controller;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.entities.engine.input.InputProcessor;
import br.com.animvs.koalory.entities.engine.input.JoystickProcessor;
import br.com.animvs.koalory.entities.engine.input.KeyboardProcessor;
import br.com.animvs.koalory.entities.engine.input.TouchProcessor;

/**
 * Created by DALDEGAN on 27/01/2015.
 */
public final class InputController extends BaseController {

    private Array<InputProcessor> inputMappers;
    private boolean mobileTouchClicked;
    private float touchKnobMovementX;

    public Array<InputProcessor> getInputMappers() {
        return inputMappers;
    }

    public float getTouchKnobMovementX() {
        return touchKnobMovementX;
    }

    public void setTouchKnobMovementX(float touchKnobMovementX) {
        this.touchKnobMovementX = touchKnobMovementX;
    }

    public boolean getMobileTouchClicked() {
        return mobileTouchClicked;
    }

    public void setMobileTouchClicked(boolean mobileTouchClicked) {
        this.mobileTouchClicked = mobileTouchClicked;
    }

    public InputController(GameController controller) {
        super(controller);
        inputMappers = new Array<InputProcessor>();
    }

    @Override
    public void initialize() {
        //TODO: Configurable input mappers:
        inputMappers.add(new KeyboardProcessor(getController(), Input.Keys.A, Input.Keys.D, Input.Keys.SPACE));
        inputMappers.add(new KeyboardProcessor(getController(), Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.ENTER));

        if (Configurations.SIMULATE_MOBILE_ON_DESKTOP || Gdx.app.getType() != Application.ApplicationType.Desktop)
            inputMappers.add(new TouchProcessor(getController()));

        for (int i = 0; i < Controllers.getControllers().size; i++)
            inputMappers.add(new JoystickProcessor(getController(), Controllers.getControllers().get(i), Configurations.CORE_GAMEPAD_BUTTON_ACTION));
    }

    public void update() {
        for (int i = 0; i < inputMappers.size; i++)
            inputMappers.get(i).update();

        mobileTouchClicked = false;
        touchKnobMovementX = 0f;
    }

    public boolean checkAnyInputHasActionPressed() {
        for (int i = 0; i < inputMappers.size; i++) {
            if (inputMappers.get(i).getActionPressed())
                return true;
        }

        return false;
    }
}
