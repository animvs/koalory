package br.com.animvs.koalory.entities.game.mobiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.engine2.graficos.loaders.AnimacaoSkeletalData;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.controller.LoadController;
import br.com.animvs.koalory.controller.PhysicsController;
import br.com.animvs.koalory.controller.PlayersController;
import br.com.animvs.koalory.entities.engine.input.InputProcessor;
import br.com.animvs.koalory.entities.game.Entity;
import br.com.animvs.koalory.entities.game.platforms.Platform;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public final class Player extends Mobile {
    private InputProcessor input;

    //private long lastJumpTime;
    //private Fixture physicFixture;

    private Vector2 positionCache;
    private boolean mustJump;

    private Platform groundedPlatform;
    private Vector2 groundedPlatformLastPosition;
    private boolean grounded;

    private String graphicSkin;
    private com.badlogic.gdx.graphics.Color graphicColor;

    private Vector2 teleportTo;

    /*private boolean inDamage;
    private float damageTimeCounter;*/

    public InputProcessor getInput() {
        return input;
    }

    //private static final float ANIMATION_Y_VELOCITY_TOLERANCE = 1.3f;

    @Override
    protected float getBodyScaleX() {
        return 0.7f;
    }

    @Override
    protected float getBodyScaleY() {
        return 1.6f;
    }

    @Override
    protected PhysicsController.TargetPhysicsParameters.Shape getBodyShape() {
        return PhysicsController.TargetPhysicsParameters.Shape.PLAYER;
    }

    public boolean getGrounded() {
        return grounded;
    }

    @Override
    protected boolean getMovingHorizontally() {
        if (grounded && groundedPlatform != null) {
            float platformVelocityX = groundedPlatform.getBody().getLinearVelocity().x;
            float playerVelocityX = getBody().getLinearVelocity().x;
            return platformVelocityX != playerVelocityX;
        }

        return super.getMovingHorizontally();
    }

    public Player(GameController controller, String skinName, InputProcessor inputMapper, com.badlogic.gdx.graphics.Color color) {
        super(controller, new Vector2(Configurations.GAMEPLAY_PLAYER_START.x, Configurations.GAMEPLAY_PLAYER_START.y));

        if (inputMapper == null)
            throw new RuntimeException("The parameter 'inputMapper' must be != NULL");

        positionCache = new Vector2();
        groundedPlatformLastPosition = new Vector2();
        input = inputMapper;

        this.graphicSkin = skinName;
        this.graphicColor = color;

        /*lastPositionCache = new Vector2();*/
        //alive = true;

        //getController().getEntities().createEntityBody(this, 1f, PhysicsController.TargetPhysicsParameters.Type.PLAYER);
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        AnimacaoSkeletal graphic = new AnimacaoSkeletal(getController().getLoad().get(LoadController.SKELETON_CHARACTER, AnimacaoSkeletalData.class));

        getGraphicOffset().set(0f, -50f);
        graphic.setSkin(graphicSkin);

        graphic.getColor().set(graphicColor);
        graphic.setInterpolationDefault(0.25f);

        //Clean unused resources:
        graphicSkin = null;
        graphicColor = null;

        return graphic;
    }

    public void updateInputOnly() {
        computeInput();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        updateInputOnly();

        if (getBody() != null) {

            if (teleportTo != null) {
                setPosition(teleportTo.x, teleportTo.y);

                getController().getStage().getCamera().position.x = teleportTo.x;
                getController().getStage().getCamera().position.y = teleportTo.y;
                getController().getStage().getCamera().update();

                teleportTo = null;
            }

            computeGrounded();

            if (mustJump) {
                mustJump = false;
                grounded = false;

                prepareAnimation("jump");

                setPosition(getX(), getY() + 10f);

                if (groundedPlatform != null)
                    setPosition(getX(), getY() + 10f * groundedPlatform.getSpeed());

                getBody().applyForceToCenter(0f, Configurations.GAMEPLAY_PLAYER_JUMP_FORCE * Configurations.CORE_PHYSICS_MULTIPLIER, true);
            }

            /*processDamage();*/

            if (!grounded) {
                clampByCamera();

                /*if (getBody() != null)
                    getBody().setAwake(true);*/

                return;
            }

            if (getMovingHorizontally()) {
                prepareAnimation("walk");
            } else if (grounded) {
                prepareAnimation("walk");
                getGraphic().setAnimationSpeedScale(0, 0f);
            }

            if (getBody().getLinearVelocity().y > Configurations.GAMEPLAY_PLAYER_JUMP_FORCE * 0.5f)
                getBody().setLinearVelocity(getBody().getLinearVelocity().x, Configurations.GAMEPLAY_PLAYER_JUMP_FORCE * 0.5f);

            getBody().setAwake(true);
        }

        clampByCamera();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        getController().getLight().updateLightPosition(getX(), getY());

        if (grounded && groundedPlatform != null) {

            //setPhysicsFriction(0f);
            //getBody().applyForceToCenter(groundedPlatform.getBody().getLinearVelocity().x * 0.5f *//** Configurations.CORE_PHYSICS_MULTIPLIER*//*, -1f, true);
            float speedX = groundedPlatform.getBody().getLinearVelocity().x + getInput().getMovementX();

            getBody().setLinearVelocity(speedX, groundedPlatform.getBody().getLinearVelocity().y);
            groundedPlatformLastPosition.set(groundedPlatform.getX(), groundedPlatform.getY());
        }

        super.draw(batch, parentAlpha);
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

            spawnLocation.set(playerReference.getX(), playerReference.getY());//1.25f);
        }

        for (int i = 0; i < body.getFixtureList().size; i++)
            body.getFixtureList().get(i).setRestitution(0f);

        setPosition(spawnLocation.x, spawnLocation.y);
    }

    @Override
    protected void eventDeath(Entity killer) {
        dispose();

        getController().getSound().playCharacterDeath();
    }

    public void computeInput() {
        if (getBody() != null) {
            /*if (grounded)
                getBody().setLinearVelocity(input.getMovementX(), getBody().getLinearVelocity().y);
            else*/
            getBody().applyForceToCenter(input.getMovementX() * 0.35f, 0f, true);
        }
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

        getGraphic().setAnimationSpeedScale(0, Configurations.CORE_PLAYER_ANIM_SPEED_MULTIPLIER);
    }

    private void clampByCamera() {
        if (getController().getPlayers().getTotalPlayersInGame() == 1)
            return;

        boolean clamped = false;

        float distanceAllowed = (Configurations.RESOLUTION_REAL.x * 0.95f) * getController().getCamera().getZoom();

        /*if (getController().getCamera().getPlayerLeft() == null)
            Gdx.app.log("PLAYER DEBUGGER", "PLAYER LEFT is null during camera clamp");*/

        /*if (getController().getCamera().getPlayerRight() == null)
            Gdx.app.log("PLAYER DEBUGGER", "PLAYER RIGHT is null during camera clamp");*/

        positionCache.set(getX(), getY());

        float minX = getController().getCamera().getPlayerRight() != null ? getController().getCamera().getPlayerRight().getX() : getX();
        minX -= distanceAllowed;

        float maxX = getController().getCamera().getPlayerLeft() != null ? getController().getCamera().getPlayerLeft().getX() : getX();
        maxX += distanceAllowed;

        if (getX() < minX) {
            positionCache.x = minX;
            clamped = true;
        } else if (getX() > maxX) {

            positionCache.x = maxX;
            clamped = true;
        }

        if (clamped)
            setPosition(positionCache.x, positionCache.y);
    }

    private void computeGrounded() {
        Array<Contact> contactList = getController().getPhysics().getWorld().getContactList();
        for (int i = 0; i < contactList.size; i++) {
            Contact contact = contactList.get(i);
            if (contact.isTouching() && (checkOwnsFixture(contact.getFixtureA()) || checkOwnsFixture(contact.getFixtureB()))) {

                positionCache.set(getX(), getY());

                /*WorldManifold manifold = contact.getWorldManifold();
                boolean below = true;
                for (int j = 0; j < manifold.getPoints().length; j++)
                    below &= (getController().getPhysics().toWorld(manifold.getPoints()[j].y) < positionCache.y *//*- Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f*//*);*/

                groundedPlatform = null;

                /*if (below) {*/
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
                /*}*/

                /*grounded = false;
                return;*/
            }
        }
        grounded = false;
    }

    public void teleportTo(float x, float y) {
        teleportTo = new Vector2(x, y);
    }

    /*public void eventTouched(Foe foe, Contact contact) {
        if (inDamage || getBody() == null || foe.getBody() == null)
            return;

        inDamage = true;
        damageTimeCounter = 0f;

        contactCache.set(getX() - foe.getX(), getY() - foe.getY()).nor();

        float repulsionForce = 150f;

        contactCache.set(getX() - foe.getX(), getY() - foe.getY()).nor();

        if (grounded)
            contactCache.set(contactCache.x, Math.abs(contactCache.y));
            //contactCache.set(contactCache.x + contactCache.y, 0f);

        getBody().applyForce(contactCache.x * foe.getBody().getLinearVelocity().x * -repulsionForce,
                contactCache.y * repulsionForce * 0.35f,
                contact.getWorldManifold().getPoints()[0].x, contact.getWorldManifold().getPoints()[0].y, true);

        Gdx.app.log("DAMAGE", "Damage started ...");
    }

    private void processDamage() {
        if (!inDamage)
            return;

        damageTimeCounter += Gdx.graphics.getDeltaTime();

        float power = 0.01f;

        //getBody().applyLinearImpulse(contactCache.x * power, contactCache.y * power * 7f, 0f,0f, true);

        if (damageTimeCounter >= 0.5f) {
            inDamage = false;
            Gdx.app.log("DAMAGE", "Damage ended");
        } else
            Gdx.app.log("DAMAGE", "Current damage time: " + damageTimeCounter);
    }*/
}