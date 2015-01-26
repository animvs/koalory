package br.com.animvs.ggj2015.entities.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;

import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.controller.GameController;
import br.com.animvs.ggj2015.entities.game.Player;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public final class InputMapper {
    private GameController controller;
    private Player playerOwner;

    private float movementX;

    private boolean mobileTouchClicked;

    public void setMobileTouchClicked(boolean mobileTouchClicked) {
        this.mobileTouchClicked = mobileTouchClicked;
    }

    public float getMovementX() {
        return movementX;
    }

    public InputMapper(GameController controller, Player playerOwner) {
        this.controller = controller;
        this.playerOwner = playerOwner;
    }

    public void update() {
        updateInput();
    }

    private Controller getController() {
        if (Controllers.getControllers().size - 1 < playerOwner.getPlayerIndex())
            return null;

        return Controllers.getControllers().get(playerOwner.getPlayerIndex());
    }

    private void updateInput() {
        if (!playerOwner.getAlive()) {
            int playerIndex = playerOwner.getPlayerIndex();
            if (Controllers.getControllers().size - 1 >= playerIndex)
                if (Controllers.getControllers().get(playerIndex).getButton(1)) {
                    Gdx.app.log("PLAYER", "Player requesting spawn: " + playerOwner.getPlayerIndex() + " Lives after spawn: " + (controller.getLives() - 1));
                    if (controller.getLives() > 0)
                        playerOwner.spawn(MathUtils.randomBoolean());
                }
        }

        switch (playerOwner.getPlayerIndex()) {
            case 0:
                computePlayer1Keyboard();
                break;
            case 1:
                computePlayer2Keyboard();
                break;
            case 2:
            case 3:
            case 4:
                break; //Players 3,4 and 5 cannot use keyboard.
            default:
                throw new RuntimeException("Player index not mapped to input: " + playerOwner.getPlayerIndex());
        }
        computeJoystick();
        computeTouch();
    }

    private void computePlayer1Keyboard() {
        movementX = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.A))
            movementX -= Configurations.GAMEPLAY_MOVEMENT_SPEED;

        if (Gdx.input.isKeyPressed(Input.Keys.D))
            movementX += Configurations.GAMEPLAY_MOVEMENT_SPEED;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (playerOwner.getAlive())
                playerOwner.tryJump();
            else if (controller.getLives() > 0)
                playerOwner.spawn(MathUtils.randomBoolean());
        }
    }

    private void computePlayer2Keyboard() {
        movementX = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            movementX -= Configurations.GAMEPLAY_MOVEMENT_SPEED;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            movementX += Configurations.GAMEPLAY_MOVEMENT_SPEED;

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            if (playerOwner.getAlive())
                playerOwner.tryJump();
            else if (controller.getLives() > 0)
                playerOwner.spawn(MathUtils.randomBoolean());
        }
    }

    private void computeJoystick() {
        if (playerOwner.getBody() == null)
            return;

        if (getController() == null)
            return;

        if (Controllers.getControllers().get(playerOwner.getPlayerIndex()).getAxis(1) < -0.15f)
            movementX -= Configurations.GAMEPLAY_MOVEMENT_SPEED;

        if (Controllers.getControllers().get(playerOwner.getPlayerIndex()).getAxis(1) > 0.15f)
            movementX += Configurations.GAMEPLAY_MOVEMENT_SPEED;

        if (getController().getButton(1))
            playerOwner.tryJump();
    }

    private void computeTouch() {
        if (mobileTouchClicked)
            if (controller.getLives() > 0)
                playerOwner.spawn(MathUtils.randomBoolean());

        mobileTouchClicked = false;
    }
}
