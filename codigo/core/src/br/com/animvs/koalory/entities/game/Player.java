package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.engine2.graficos.loaders.AnimacaoSkeletalData;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.controller.LoadController;
import br.com.animvs.koalory.controller.PlayersController;
import br.com.animvs.koalory.entities.engine.input.InputProcessor;
import br.com.animvs.koalory.entities.game.platforms.Platform;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public final class Player extends Entity {
    private InputProcessor input;

    //private long lastJumpTime;
    private Fixture physicFixture;

    private Vector2 positionCache;

    private Platform groundedPlatform;
    private Vector2 groundedPlatformLastPosition;
    private boolean grounded;

    private boolean mustJump;

    private float friction;

    public InputProcessor getInput() {
        return input;
    }

    //private static final float ANIMATION_Y_VELOCITY_TOLERANCE = 1.3f;
    private static final float ANIMATION_X_VELOCITY_TOLERANCE = 0.3f;

    private boolean getMovingHorizontally() {
        if (getBody() == null)
            return false;

        if (grounded && groundedPlatform != null) {
            float platformVelocityX = groundedPlatform.getBody().getLinearVelocity().x;
            float playerVelocityX = getBody().getLinearVelocity().x;
            return platformVelocityX != playerVelocityX;
        }

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

        positionCache = new Vector2();
        groundedPlatformLastPosition = new Vector2();
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

        getBody().setAwake(true);

        if (getBody() != null) {

            computePlayerGrounded();

            if (mustJump) {
                mustJump = false;
                grounded = false;

                prepareAnimation("jump");

                setPosition(getX(), getY() + 10f);
                getBody().applyForceToCenter(0f, Configurations.GAMEPLAY_JUMP_FORCE * Configurations.CORE_PHYSICS_MULTIPLIER, true);
            }

            computeDeath();

            if (!grounded)
                return;

            if (getMovingHorizontally()) {
                prepareAnimation("walk");
            } else if (grounded) {
                prepareAnimation("walk");
                getGraphic().setAnimationSpeedScale(0f);
            }
        }

        if (getBody() != null && getBody().getLinearVelocity().y > Configurations.GAMEPLAY_JUMP_FORCE * 0.5f)
            getBody().setLinearVelocity(getBody().getLinearVelocity().x, Configurations.GAMEPLAY_JUMP_FORCE * 0.5f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (grounded && groundedPlatform != null) {

            physicFixture.setFriction(0f);
            //getBody().applyForceToCenter(groundedPlatform.getBody().getLinearVelocity().x * 0.5f /** Configurations.CORE_PHYSICS_MULTIPLIER*/, -1f, true);
            float speedX = groundedPlatform.getBody().getLinearVelocity().x + getInput().getMovementX();

            getBody().setLinearVelocity(speedX, groundedPlatform.getBody().getLinearVelocity().y);

            groundedPlatformLastPosition.set(groundedPlatform.getX(), groundedPlatform.getY());
        } else
            physicFixture.setFriction(friction);
    }

    public void forceJump(boolean resetForceY) {
        if (getBody() == null)
            return;

        if (resetForceY)
            getBody().setLinearVelocity(getBody().getLinearVelocity().x, 0f);

        mustJump = true;
    }

    public void tryJump() {
        if (getBody() == null) {
            Gdx.app.log("DEBUG", "JUMP BLOCKED - Physical Body is NULL");
            return;
        }

        if (!grounded) {
            Gdx.app.log("DEBUG", "JUMP BLOCKED - Player is not grounded");
            return;
        }

        if (!grounded)
            forceJump(false);
        else
            forceJump(true);

        if (getController().getSound() != null)
            getController().getSound().playJump();
    }

    public void eventDeath() {
        dispose();

        getController().getSound().playCharacterDeath();
    }

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

        Vector2 spawnLocation = new Vector2(getController().getLevel().getPlayerStart());
        PlayersController playersController = getController().getPlayers();

        if (playersController.getTotalPlayersInGame() > 1) {
            Player playerReference;
            while (true) {
                playerReference = playersController.getPlayer(MathUtils.random(playersController.getTotalPlayersInGame() - 1));

                if (playerReference != this)
                    break;
            }

            spawnLocation.set(playerReference.getX(), playerReference.getY() + Configurations.GAMEPLAY_ENTITY_SIZE_Y * 1.25f);
        } /*else
            spawnLocation.set(Configurations.GAMEPLAY_PLAYER_START.x, Configurations.GAMEPLAY_PLAYER_START.y);*/

        physicFixture = body.getFixtureList().get(0);

        if (getController().getLevel().getMapName().equals("frostPlateau1-1"))
            friction = 0.15f * 0.15f; //Less friction on ice
        else if (getController().getLevel().getMapName().equals("sandPlains1-1")) {
            friction = 0.15f * 1.35f; //More friction on sand
        } else
            friction = 0.15f; //Normal friction

        setPosition(spawnLocation.x, spawnLocation.y);
    }

    public void computeInput() {
        if (getBody() != null) {
            /*if (Configurations.SIMULATE_MOBILE_ON_DESKTOP || Gdx.app.getType() != Application.ApplicationType.Desktop)
                getBody().setLinearVelocity(input.getMovementX(), getBody().getLinearVelocity().y);
            else
                getBody().setLinearVelocity(input.getMovementX(), getBody().getLinearVelocity().y);*/

            float maxX = 1f;
            float minX = -1f;

            if (grounded && groundedPlatform != null) {
                if (groundedPlatform.getBody().getLinearVelocity().x > 0f)
                    maxX += groundedPlatform.getBody().getLinearVelocity().x;

                if (groundedPlatform.getBody().getLinearVelocity().x < 0f)
                    minX += groundedPlatform.getBody().getLinearVelocity().x;
            }

            getBody().applyForceToCenter(input.getMovementX() * 0.35f, 0f, true);

            if (getBody().getLinearVelocity().x > maxX)
                getBody().setLinearVelocity(maxX, getBody().getLinearVelocity().y);

            if (getBody().getLinearVelocity().x < minX)
                getBody().setLinearVelocity(minX, getBody().getLinearVelocity().y);
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
                //if (currentAnimationName != null)
                getGraphic().setAnimation(newAnimationName, false);
            } else
                getGraphic().setAnimation(newAnimationName, true);
        }

        getGraphic().setAnimationSpeedScale(Configurations.CORE_PLAYER_ANIM_SPEED_MULTIPLIER);
    }

    private void computePlayerGrounded() {
        //groundedPlatform = null;
        Array<Contact> contactList = getController().getPhysics().getWorld().getContactList();
        for (int i = 0; i < contactList.size; i++) {
            Contact contact = contactList.get(i);
            if (contact.isTouching() && (contact.getFixtureA() == physicFixture || contact.getFixtureB() == physicFixture)) {

                positionCache.set(getX(), getY());

                WorldManifold manifold = contact.getWorldManifold();
                boolean below = true;
                for (int j = 0; j < manifold.getNumberOfContactPoints(); j++)
                    below |= (getController().getPhysics().toWorld(manifold.getPoints()[j].y) < positionCache.y - Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f);

                groundedPlatform = null;

                if (below) {
                    if (contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals(Configurations.CORE_PLATFORM_USER_DATA))
                        groundedPlatform = (Platform) contact.getFixtureA().getBody().getUserData();

                    if (contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals(Configurations.CORE_PLATFORM_USER_DATA))
                        groundedPlatform = (Platform) contact.getFixtureB().getBody().getUserData();

                    if (groundedPlatform != null) {
                        groundedPlatformLastPosition.set(groundedPlatform.getX(), groundedPlatform.getY());
                        groundedPlatform.eventPlayerSteped(this);
                    }

                    grounded = true;
                    return;
                }

                grounded = false;
                return;
            }
        }
        grounded = false;
    }
}