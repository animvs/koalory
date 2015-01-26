package br.com.animvs.ggj2015.entities.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.engine2.graficos.loaders.AnimacaoSkeletalData;
import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.controller.GameController;
import br.com.animvs.ggj2015.controller.LoadController;
import br.com.animvs.ggj2015.entities.engine.input.InputMapper;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public final class Player extends GGJ15Entity {
    private int playerIndex;
    private InputMapper input;

    private boolean alive;
    private Vector2 lastPositionCache;

    /*private boolean jumping;*/
    private long lastJumpTime;
    private float movementXMobile;

    public float getMovementXMobile() {
        return movementXMobile;
    }

    public void setMovementXMobile(float movementXMobile) {
        this.movementXMobile = movementXMobile;
    }

    public InputMapper getInput() {
        return input;
    }

    public boolean getAlive() {
        return alive;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public boolean getJumping() {
        if (getBody() == null)
            return false;

        return getBody().getLinearVelocity().y != 0f;
        //return jumping;
    }

    public Player(GameController controller, int playerIndex) {
        super(controller);

        this.playerIndex = playerIndex;
        input = new InputMapper(controller, this);
        setPosition(Configurations.GAMEPLAY_PLAYER_START.x, Configurations.GAMEPLAY_PLAYER_START.y);
    }

    public void spawn(boolean blue) {
        if (alive)
            throw new RuntimeException("Player cannot be spawn when alive. Player index: " + playerIndex);

        if (getController().getLives() == 0)
            throw new RuntimeException("There is no more lives left to spawn player: " + playerIndex);

        getController().subtractLife();

        lastPositionCache = new Vector2();
        alive = true;
        //setVisible(true);

        getController().getEntities().createEntityBody(this);

        setGraphic(new AnimacaoSkeletal(getController().getLoad().get(LoadController.SKELETON_CHARACTER, AnimacaoSkeletalData.class)));
        getGraphic().setSkin(blue ? "blue" : "green");
        getGraphic().setAnimation("walk", true);
        getGraphic().setInterpolationDefault(0.25f);
    }

    @Override
    public void update() {
        super.update();

        computeInput();
        if (alive) {
            if (getBody() != null) {
                computeDeath();

                if (getX() != lastPositionCache.x || getY() != lastPositionCache.y)
                    getController().updateCameraDesiredPosition();

                if (getBody() != null) {
                    if (!getJumping()) {
                        if (getBody().getLinearVelocity().x != 0f) {
                            getGraphic().setAnimation("walk", true);
                            getGraphic().setAnimationSpeedScale(Configurations.CORE_PLAYER_ANIM_SPEED_MULTIPLIER);
                        } else{
                            getGraphic().setAnimation("jump", false);
                            getGraphic().setAnimationSpeedScale(Configurations.CORE_PLAYER_ANIM_SPEED_MULTIPLIER);
                        }
                    } else {
                        getGraphic().setAnimation("jump", false);
                        getGraphic().setAnimationSpeedScale(Configurations.CORE_PLAYER_ANIM_SPEED_MULTIPLIER);
                    }
                }
            }
        } else {
            /*if (!entities.getPlayer(0).getAlive()) {
                stage.getCamera().position.x = 600f;
                stage.getCamera().position.y = 550f;

                return;
            }*/
        }

        if (getBody() != null && getBody().getLinearVelocity().y > Configurations.GAMEPLAY_JUMP_FORCE * 0.5f)
            getBody().setLinearVelocity(getBody().getLinearVelocity().x, Configurations.GAMEPLAY_JUMP_FORCE * 0.5f);
    }

    public void forceJump(boolean resetForceY) {
        if (getBody() == null)
            return;

        if (resetForceY)
            getBody().setLinearVelocity(getBody().getLinearVelocity().x, 0f);

        if (resetForceY)
            getBody().setLinearVelocity(getBody().getLinearVelocity().x, 0f);

        lastJumpTime = TimeUtils.millis();
        /*jumping = true;*/

        Gdx.app.log("JUMP", "Player " + playerIndex + " started a Jump");

        getGraphic().setAnimation("jump", false);
        getBody().applyForceToCenter(0f, Configurations.GAMEPLAY_JUMP_FORCE, true);
    }

    public void tryJump() {
        if (getBody() == null || getJumping() /*|| TimeUtils.timeSinceMillis(lastJumpTime) < 500L*/)
            return;

        if (TimeUtils.timeSinceMillis(lastJumpTime) < 500L)
            return;

        forceJump(false);

        if (getController().getSound() != null)
            getController().getSound().playJump();
    }

    @Override
    protected void eventAfterBodyCreated(Body body) {
        super.eventAfterBodyCreated(body);

        body.setFixedRotation(true);
        setPosition(Configurations.GAMEPLAY_PLAYER_START.x, Configurations.GAMEPLAY_PLAYER_START.y);
        //setPosition(600f, 550f + Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f);
        //getController().getStage().bringToFront(this);

        /*for (int i = 0; i < body.getFixtureList().size; i++)
            body.getFixtureList().get(i).setFriction(0.01f);*/

        Gdx.app.log("PLAYER", "Player " + playerIndex + " created.");
    }

    private void computeInput() {
        input.update();

        if (alive && getBody() != null) {
            float movementX = input.getMovementX();
            /*if (getJumping())
                movementX *= 0.75f;*/

            if (Configurations.SIMULATE_MOBILE_ON_DESKTOP || Gdx.app.getType() != Application.ApplicationType.Desktop)
                getBody().setLinearVelocity(movementXMobile, getBody().getLinearVelocity().y);
            else
                getBody().setLinearVelocity(movementX, getBody().getLinearVelocity().y);
        }

    }

    public void eventDeath() {
        alive = false;
        disposeBody();
        setBody(null);
        setPosition(Configurations.GAMEPLAY_PLAYER_START.x, Configurations.GAMEPLAY_PLAYER_START.y);
        getController().getSound().playDeathCharacter();
        getController().checkGameOver();
        //setVisible(false);
    }

    private void computeDeath() {
        if (getY() <= 0f)
            eventDeath();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /*public void endJump() {
        jumping = false;
    }*/
}