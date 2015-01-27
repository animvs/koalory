package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;

import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.entities.engine.input.InputProcessor;
import br.com.animvs.ggj2015.entities.engine.input.JoystickMapper;
import br.com.animvs.ggj2015.entities.engine.input.KeyboardMapper;

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

    public void setTouchKnobMovementX(float touchKnobMovementX) {
        this.touchKnobMovementX = touchKnobMovementX;
    }

    public InputController(GameController controller) {
        super(controller);
        inputMappers = new Array<InputProcessor>();
    }

    @Override
    public void initialize() {
        //TODO: Configurable input mappers:
        inputMappers.add(new KeyboardMapper(Input.Keys.A, Input.Keys.D, Input.Keys.SPACE));
        inputMappers.add(new KeyboardMapper(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.ENTER));

        for (int i = 0; i < Controllers.getControllers().size; i++)
            inputMappers.add(new JoystickMapper(Controllers.getControllers().get(i), Configurations.CORE_GAMEPAD_BUTTON_ACTION));
    }

    public void update() {
        for (int i = 0; i < inputMappers.size; i++)
            inputMappers.get(i).update();
    }

    public boolean checkAnyInputHasActionPressed() {
        for (int i = 0; i < inputMappers.size; i++) {
            if (inputMappers.get(i).getActionPressed())
                return true;
        }

        return false;
    }
}
