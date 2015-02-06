package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.controller.PhysicsController;

/**
 * Created by DALDEGAN on 05/02/2015.
 */
public final class Weight extends Item {

    private final float radius;

    private Vector2 vectorCache;

    private final float lifeInterval;
    private float lifeCounter;

    private Player target;

    @Override
    protected float getBodyDensity() {
        return 1f;
    }

    @Override
    protected boolean getDisposeOnCollect() {
        return true;
    }

    @Override
    protected BodyDef.BodyType getBodyType() {
        return BodyDef.BodyType.KinematicBody;
    }

    @Override
    protected boolean getBodySensor() {
        return true;
    }

    public Weight(GameController controller, Vector2 spawnPosition, float lifeInterval, float radius) {
        super(controller, spawnPosition);

        if (lifeInterval == 0f)
            throw new RuntimeException("The parameter 'lifeInterval' must be > 0");

        if (spawnPosition == null)
            throw new RuntimeException("The parameter 'spawnPosition' must be != NULL");

        this.radius = radius;
        this.lifeInterval = lifeInterval;
        this.vectorCache = new Vector2();
    }

    @Override
    protected PhysicsController.TargetPhysicsParameters createBody(float tileSize) {
        PhysicsController.TargetPhysicsParameters parameters = new PhysicsController.TargetPhysicsParameters(this, new Vector2(),
                getRotation(), getBodyType(), PhysicsController.TargetPhysicsParameters.Type.CIRCLE, radius, radius, getBodyDensity(), getBodyRestitution(), getBodySensor());

        return parameters;
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        return null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (getDisposed())
            return;

        lifeCounter += Gdx.graphics.getDeltaTime();

        if (lifeCounter >= lifeInterval) {
            dispose();
            return;
        }

        if (target != null) {
            if (!target.getAlive())
                target = null;
            else {
                vectorCache.set(target.getX(), target.getY());
                vectorCache.sub(getX(), getY()).nor();

                /*Gdx.app.log("WEIGHT", "X: " + vectorCache.x + " Y: " + vectorCache.y);
                Gdx.app.log("WEIGHT TARGET", "X: " + target.getX() + " Y: " + target.getY());*/
            }
        }

        getBody().setLinearVelocity(vectorCache.x, vectorCache.y);
    }

    @Override
    protected void eventAfterBodyCreated(Body body) {
        super.eventAfterBodyCreated(body);

        //Gdx.app.log("WEIGHT", "Weight spawned at X: " + spawnPosition.x + " Y: " + spawnPosition.y);

        while (true) {
            target = getController().getPlayers().getPlayer(getController().getPlayers().getTotalPlayersInGame() - 1);

            if (target.getAlive())
                break;
        }
    }

    @Override
    public void collect(Player player) {
        super.collect(player);

        if (!player.getAlive() || player.getBody() == null)
            return;

        vectorCache.set(getX(), getY());
        vectorCache.sub(player.getX(), player.getY()).nor().scl(-1f);

        float force = 65f;

        player.setPhysicsFriction(0.05f);
        if (player.getGrounded())
            vectorCache.set(vectorCache.x * 1.5f, 0f);
            //vectorCache.y = Math.abs(vectorCache.y);

        player.getBody().applyForceToCenter(vectorCache.x * force, vectorCache.y * force * 1.25f, true);
    }
}
