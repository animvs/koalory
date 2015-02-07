package br.com.animvs.koalory.entities.game.mobiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.entities.game.Entity;

/**
 * Created by DALDEGAN on 05/02/2015.
 */
public abstract class Mobile extends Entity {
    private static final float ANIMATION_X_VELOCITY_TOLERANCE = 0.3f;

    //private Vector2 positionCache;

    private float physicsLinerVelocityMinX;
    private float physicsLinerVelocityMaxX;
    private float friction;

    private boolean graphicsFacingRight;

    private boolean alive;

    public final boolean getAlive() {
        return alive;
    }

    public float getPhysicsLinerVelocityMinX() {
        return physicsLinerVelocityMinX;
    }

    public float getPhysicsLinerVelocityMaxX() {
        return physicsLinerVelocityMaxX;
    }

    public float getPhysicsFriction() {
        return friction;
    }

    public void setPhysicsFriction(float friction) {
        if (this.friction != friction)
            updatePhysicsFriction(friction);

        this.friction = friction;
    }

    public boolean getLimitPhysicsVelocityX() {
        return true;
    }

    protected boolean getMovingHorizontally() {
        if (getBody() == null)
            return false;

        float hVelocity = getBody().getLinearVelocity().x;

        if (hVelocity < 0f)
            return hVelocity < ANIMATION_X_VELOCITY_TOLERANCE;
        else
            return hVelocity > ANIMATION_X_VELOCITY_TOLERANCE;
    }

    public Mobile(GameController controller, Vector2 spawnPosition) {
        super(controller, spawnPosition);

        //positionCache = new Vector2();
        physicsLinerVelocityMinX = -1f;
        physicsLinerVelocityMaxX = 1f;
        alive = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (getGraphic() == null)
            return;

        getGraphic().flipX(!graphicsFacingRight);
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (getBody() != null) {
            setVisible(true);

            setX(getController().getPhysics().toWorld(getBody().getPosition().x));
            setY(getController().getPhysics().toWorld(getBody().getPosition().y));
            setRotation(getBody().getAngle() * MathUtils.radDeg);

            if (graphicsFacingRight) {
                if (getBody().getLinearVelocity().x <= -Configurations.CORE_DIRECTION_CHANGE_VELOCITY_MIN)
                    graphicsFacingRight = false;
            } else {
                if (getBody().getLinearVelocity().x >= Configurations.CORE_DIRECTION_CHANGE_VELOCITY_MIN)
                    graphicsFacingRight = true;
            }

            if (getLimitPhysicsVelocityX()) {
                if (getBody().getLinearVelocity().x < physicsLinerVelocityMinX)
                    getBody().setLinearVelocity(physicsLinerVelocityMinX, getBody().getLinearVelocity().y);
                else if (getBody().getLinearVelocity().x > physicsLinerVelocityMaxX)
                    getBody().setLinearVelocity(physicsLinerVelocityMaxX, getBody().getLinearVelocity().y);
            }

            //Mobiles whoes falls beyond Y 0 dies:
            if (getY() <= 0f)
                death(null);
        } else
            setVisible(false);
    }

    @Override
    protected void eventAfterBodyCreated(Body body) {
        super.eventAfterBodyCreated(body);

        if (getController().getLevel().getMapName().equals("frostPlateau1-1"))
            setPhysicsFriction(0.15f * 0.15f); //Less friction on ice
        else if (getController().getLevel().getMapName().equals("sandPlains1-1")) {
            setPhysicsFriction(0.15f * 1.35f); //More friction on sand
        } else
            setPhysicsFriction(0.15f); //Normal friction
    }

    private void updatePhysicsFriction(float friction) {
        if (getBody() == null)
            return;

        for (int i = 0; i < getBody().getFixtureList().size; i++)
            getBody().getFixtureList().get(i).setFriction(friction);
    }

    public final void death(Entity killer) {
        if (!alive)
            return;

        alive = false;
        eventDeath(killer);
    }

    protected abstract void eventDeath(Entity killer);
}
