package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.engine2.matematica.Random;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.LoadController;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.controller.PhysicsController;
import br.com.animvs.koalory.entities.game.mobiles.Foe;
import br.com.animvs.koalory.entities.game.mobiles.Mobile;
import br.com.animvs.koalory.entities.game.mobiles.Player;

/**
 * Created by DALDEGAN on 05/02/2015.
 */
public final class Projectile extends Mobile {

    private Vector2 vectorCache;

    private final float lifeInterval;
    private float lifeCounter;
    private final float inDamageInterval = 1f;

    private Player target;
    private final Foe boss;

    private boolean inDamage;
    private float inDamageCounter;

    private TextureRegion regionCache;

    private float radiusScale;
    private float graphicRotation;

    @Override
    protected PhysicsController.TargetPhysicsParameters.Type getBodyShape() {
        return PhysicsController.TargetPhysicsParameters.Type.CIRCLE;
    }

    @Override
    protected float getBodyScaleX() {
        return radiusScale;
    }

    @Override
    protected float getBodyRestitution() {
        return 0.7f;
    }

    public Projectile(GameController controller, Vector2 spawnPosition, float lifeInterval, Foe boss) {
        super(controller, spawnPosition);

        if (lifeInterval == 0f)
            throw new RuntimeException("The parameter 'lifeInterval' must be > 0");

        if (spawnPosition == null)
            throw new RuntimeException("The parameter 'spawnPosition' must be != NULL");

        if (boss == null)
            throw new RuntimeException("The parameter 'boss' must be != NULL");

        this.radiusScale = Random.random(0.35f, 0.45f);
        this.lifeInterval = lifeInterval;
        this.boss = boss;
        this.vectorCache = new Vector2();
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        return null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (regionCache == null)
            regionCache = getController().getLoad().get(LoadController.ATLAS_OBJECTS, TextureAtlas.class).findRegion("fireball");

        float graphicSize = 35f * (1f + radiusScale);

        graphicRotation += Gdx.graphics.getDeltaTime() * 20f;

        if (graphicRotation > 1000000f)
            graphicRotation = 0f;

        float rotation = MathUtils.radDeg * MathUtils.sin(graphicRotation);

        if (inDamage)
            batch.setColor(1f, 0.7f, 0.7f, parentAlpha);

        batch.draw(regionCache, getX() - graphicSize / 2f, getY() - graphicSize / 2f, graphicSize / 2f, graphicSize / 2f, graphicSize, graphicSize, 1f, 1f, rotation);

        if (inDamage)
            batch.setColor(1f, 1f, 1f, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (getDisposed())
            return;

        if (inDamage)
            processInDamage();
        else
            processNotInDamage();
        //getBody().setLinearVelocity(vectorCache.x, vectorCache.y);
    }

    private void processInDamage() {
        inDamageCounter += Gdx.graphics.getDeltaTime();

        if (inDamageCounter >= inDamageInterval) {
            inDamage = false;
            return;
        }

        vectorCache.set(boss.getX(), boss.getY());
        vectorCache.sub(getX(), getY()).nor();//.scl(-1f);

        float force = Configurations.GAMEPLAY_BOSS_PROJETILE_FORCE;

        //getBody().setLinearVelocity(vectorCache.x * force, vectorCache.y * force);
        getBody().setLinearVelocity(0f, 0f);
        getBody().applyLinearImpulse(vectorCache.x * force, vectorCache.y * force, getX(), getY(), true);

        /*getBody().setLinearVelocity(0f, 0f);
        getBody().applyForceToCenter(vectorCache.x * force, vectorCache.y * force, true);*/
    }

    private void processNotInDamage() {
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

        float force = 0.004f;

        getBody().applyLinearImpulse(vectorCache.x * force, vectorCache.y * force, getX(), getY(), true);
    }

    @Override
    protected void eventAfterBodyCreated(Body body) {
        super.eventAfterBodyCreated(body);

        //Gdx.app.log("WEIGHT", "Weight spawned at X: " + spawnPosition.x + " Y: " + spawnPosition.y);
        body.setGravityScale(0f);
        body.setFixedRotation(true);

        target = getController().getPlayers().getPlayerRandom();
    }

    @Override
    protected void eventDeath(Entity killer) {
    }

    public void eventPlayerHit(Player player) {
        //super.collect(player);

        if (!player.getAlive() || player.getBody() == null || !boss.getAlive() || boss.getBody() == null)
            return;

        inDamage = true;
        inDamageCounter = 0f;
    }
}
