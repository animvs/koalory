package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.engine2.matematica.Random;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.controller.PhysicsController;
import br.com.animvs.koalory.entities.game.mobiles.Mobile;
import br.com.animvs.koalory.entities.game.mobiles.Player;

/**
 * Created by DALDEGAN on 05/02/2015.
 */
public final class Projectile extends Mobile {

    private final float radius;

    private Vector2 vectorCache;

    private final float lifeInterval;
    private float lifeCounter;

    private Player target;

    @Override
    protected PhysicsController.TargetPhysicsParameters.Type getBodyShape() {
        return PhysicsController.TargetPhysicsParameters.Type.CIRCLE;
    }

    @Override
    protected float getBodyScaleX() {
        return Random.random(0.35f, 0.45f);
    }

    @Override
    protected float getBodyRestitution() {
        return 0.7f;
    }

    public Projectile(GameController controller, Vector2 spawnPosition, float lifeInterval, float radius) {
        super(controller, spawnPosition);

        if (lifeInterval == 0f)
            throw new RuntimeException("The parameter 'lifeInterval' must be > 0");

        if (spawnPosition == null)
            throw new RuntimeException("The parameter 'spawnPosition' must be != NULL");

        this.radius = radius;
        this.lifeInterval = lifeInterval;
        this.vectorCache = new Vector2();
    }

    //@Override
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
        body.setGravityScale(0f);

        while (true) {
            if (getController().getPlayers().getTotalPlayersInGame() == 0)
                break;

            target = getController().getPlayers().getPlayer(getController().getPlayers().getTotalPlayersInGame() - 1);

            if (target.getAlive())
                break;
        }
    }

    @Override
    protected void eventDeath(Entity killer) {

    }


    public void collect(Player player) {
        //super.collect(player);

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
