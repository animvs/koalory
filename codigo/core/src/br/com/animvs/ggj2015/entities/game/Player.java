package br.com.animvs.ggj2015.entities.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.engine2.graficos.loaders.AnimacaoSkeletalData;
import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.controller.GameController;
import br.com.animvs.ggj2015.controller.LoadController;
import br.com.animvs.ggj2015.controller.PlayersController;
import br.com.animvs.ggj2015.entities.engine.input.InputProcessor;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public final class Player extends GGJ15Entity {
    //private int playerIndex;
    private InputProcessor input;

    //private boolean alive;
    /*private Vector2 lastPositionCache;*/

    /*private boolean jumping;*/
    private long lastJumpTime;
    /*private float movementXMobile;*/

    public InputProcessor getInput() {
        return input;
    }

    /*public float getMovementXMobile() {
        return movementXMobile;
    }

    public void setMovementXMobile(float movementXMobile) {
        this.movementXMobile = movementXMobile;
    }*/

    /*public InputProcessor getInput() {
        return input;
    }*/

    /*public boolean getAlive() {
        return alive;
    }*/

    /*public int getPlayerIndex() {
        return playerIndex;
    }*/

    private static final float ANIMATION_Y_VELOCITY_TOLERANCE = 1.3f;
    private static final float ANIMATION_X_VELOCITY_TOLERANCE = 0.3f;

    public boolean getJumping() {
        if (getBody() == null)
            return false;

        float vVelocity = getBody().getLinearVelocity().y;

        if (vVelocity < 0f && vVelocity < -ANIMATION_Y_VELOCITY_TOLERANCE)
            return true;

        if (vVelocity > 0f && vVelocity > ANIMATION_Y_VELOCITY_TOLERANCE)
            return true;

        return false;

        //return (getBody().getLinearVelocity().y >= 0.1f && getBody().getLinearVelocity().y <= 0.1f);
    }

    private boolean getMovingHorizontally() {
        if (getBody() == null)
            return false;

        float hVelocity = getBody().getLinearVelocity().x;

        if (hVelocity < 0f)
            return hVelocity < ANIMATION_X_VELOCITY_TOLERANCE;
        else
            return hVelocity > ANIMATION_X_VELOCITY_TOLERANCE;
    }

    public Player(GameController controller, String skinName, InputProcessor inputMapper) {
        super(controller);

        if (inputMapper == null)
            throw new RuntimeException("The parameter 'inputMapper' must be != NULL");

        input = inputMapper;
        setPosition(Configurations.GAMEPLAY_PLAYER_START.x, Configurations.GAMEPLAY_PLAYER_START.y);

        /*lastPositionCache = new Vector2();*/
        //alive = true;

        getController().getEntities().createEntityBody(this);

        setGraphic(new AnimacaoSkeletal(getController().getLoad().get(LoadController.SKELETON_CHARACTER, AnimacaoSkeletalData.class)));
        getGraphic().setSkin(skinName);
        getGraphic().setInterpolationDefault(0.25f);
    }

    public void updateInputOnly() {
        computeInput();
    }

    @Override
    public void update() {
        super.update();

        updateInputOnly();
        /*if (alive) {*/
        if (getBody() != null) {
            //Gdx.app.log("DEBUG", "vX: " + getBody().getLinearVelocity().x + " vY: " + getBody().getLinearVelocity().y);

            computeDeath();

                /*if (getX() != lastPositionCache.x || getY() != lastPositionCache.y)
                    getController().updateCameraDesiredPosition();*/

            if (getBody() != null) {
                if (!getJumping()) {
                    if (getMovingHorizontally())
                        prepareAnimation("walk");
                    else {
                        prepareAnimation("walk");
                        getGraphic().setAnimationSpeedScale(0f);
                    }
                } else {
                    prepareAnimation("jump");
                }
            }
        }
        /*}*/

        if (getBody() != null && getBody().getLinearVelocity().y > Configurations.GAMEPLAY_JUMP_FORCE * 0.5f)
            getBody().setLinearVelocity(getBody().getLinearVelocity().x, Configurations.GAMEPLAY_JUMP_FORCE * 0.5f);
    }

    public void forceJump(boolean resetForceY) {
        if (getBody() == null)
            return;

        if (resetForceY)
            getBody().setLinearVelocity(getBody().getLinearVelocity().x, 0f);

        lastJumpTime = TimeUtils.millis();
        /*jumping = true;*/

        //Gdx.app.log("JUMP", "Player " + playerIndex + " started a Jump");

        getBody().applyForceToCenter(0f, Configurations.GAMEPLAY_JUMP_FORCE, true);
    }

    public void tryJump() {
        if (getBody() == null) {
            Gdx.app.log("DEBUG", "JUMP BLOCKED - Physical Body is NULL");
            return;
        }

        if (getJumping()) {
            Gdx.app.log("DEBUG", "JUMP BLOCKED - Player is JUMPING already - vX: " + getBody().getLinearVelocity().x + " vY: " + getBody().getLinearVelocity().y);
            return;
        }

        if (TimeUtils.timeSinceMillis(lastJumpTime) < 850L) {
            Gdx.app.log("DEBUG", "JUMP BLOCKED - Next jump is NOT READY yet");
            return;
        }

        forceJump(false);

        if (getController().getSound() != null)
            getController().getSound().playJump();
    }

    public void eventDeath() {
        /*alive = false;*/
        dispose();

        //setPosition(Configurations.GAMEPLAY_PLAYER_START.x, Configurations.GAMEPLAY_PLAYER_START.y);
        getController().getSound().playCharacterDeath();
        getController().checkGameOver();
    }

    /*public void restart() {
        setPosition(Configurations.GAMEPLAY_PLAYER_START.x, Configurations.GAMEPLAY_PLAYER_START.y);
        disposeBody();
        *//*alive = false;*//*

        lastPositionCache = new Vector2();

        lastJumpTime = 0L;
        movementXMobile = 0f;
    }*/

    @Override
    public void dispose() {
        super.dispose();

        getInput().setPlayer(null);
        getController().getPlayers().unregisterPlayer(this);
    }

    @Override
    protected void eventAfterBodyCreated(Body body) {
        super.eventAfterBodyCreated(body);

        body.setFixedRotation(true);

        Vector2 spawnLocation = new Vector2();
        PlayersController playersController = getController().getPlayers();

        if (playersController.getTotalPlayersInGame() > 1) {
            Player playerReference;
            while (true){
                playerReference = playersController.getPlayer(MathUtils.random(playersController.getTotalPlayersInGame() - 1));

                if (playerReference != this)
                    break;
            }

            spawnLocation.set(playerReference.getX(), playerReference.getY() + Configurations.GAMEPLAY_ENTITY_SIZE_Y * 1.25f);
        } else
            spawnLocation.set(Configurations.GAMEPLAY_PLAYER_START.x, Configurations.GAMEPLAY_PLAYER_START.y);

        setPosition(spawnLocation.x, spawnLocation.y);

        //setPosition(Configurations.GAMEPLAY_PLAYER_START.x, Configurations.GAMEPLAY_PLAYER_START.y);
        //setPosition(600f, 550f + Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f);
        //getController().getStage().bringToFront(this);

        /*for (int i = 0; i < body.getFixtureList().size; i++)
            body.getFixtureList().get(i).setFriction(0.01f);*/

        /*Gdx.app.log("PLAYER", "Player " + playerIndex + " created.");*/
    }

    public void computeInput() {
        /*input.update();*/

        if (getBody() != null) {
            float movementX = input.getMovementX();

            if (Configurations.SIMULATE_MOBILE_ON_DESKTOP || Gdx.app.getType() != Application.ApplicationType.Desktop)
                getBody().setLinearVelocity(input.getMovementX(), getBody().getLinearVelocity().y);
            else
                getBody().setLinearVelocity(movementX, getBody().getLinearVelocity().y);
        }

    }

    private void computeDeath() {
        if (getY() <= 0f)
            eventDeath();
    }

    public void prepareAnimation(String newAnimationName) {
        String currentAnimationName = getGraphic().getAnimationName();

        if (currentAnimationName == null || !currentAnimationName.equals(newAnimationName)) {
            if (newAnimationName.equals("jump")) {
                if (currentAnimationName != null)
                    getGraphic().setAnimation(newAnimationName, false);
            } else
                getGraphic().setAnimation(newAnimationName, true);
        }

        getGraphic().setAnimationSpeedScale(Configurations.CORE_PLAYER_ANIM_SPEED_MULTIPLIER);
    }

    /*public void endJump() {
        jumping = false;
    }*/
}