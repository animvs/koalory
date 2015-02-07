package br.com.animvs.koalory.entities.game.mobiles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.engine2.graficos.loaders.AnimacaoSkeletalData;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.controller.LoadController;
import br.com.animvs.koalory.entities.engine.ia.IABase;
import br.com.animvs.koalory.entities.game.Entity;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public final class Foe extends Mobile {
    private IABase ia;

    private boolean playerCanKill;
    private String graphicName;
    private final float physicsScale;

    public boolean getPlayerCanKill(){
        return playerCanKill;
    }

    public void setPlayerCanKill(boolean playerCanKill) {
        this.playerCanKill = playerCanKill;
    }

    @Override
    protected float getBodyScaleX() {
        return physicsScale * 0.7f;
    }

    @Override
    protected float getBodyScaleY() {
        return physicsScale * 1.1f;
    }

    @Override
    protected float getBodyFriction() {
        return 0.5f;
    }

    public Foe(GameController controller, Vector2 spawnPosition, String graphicName, float physicsScale, IABase ia) {
        super(controller, spawnPosition);

        if (physicsScale <= 0f)
            throw new RuntimeException("The parameter 'physicScale' must be > 0");

        this.ia = ia;
        this.graphicName = graphicName;
        this.physicsScale = physicsScale;
        this.playerCanKill = true;


        /*getController().getEntities().createEntityBody(this, physicsScale, PhysicsController.TargetPhysicsParameters.Type.RECTANGLE);
        prepareGraphic(graphic);*/
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (getBody() != null) {
            if (getBody().getLinearVelocity().x != 0f) {
                getGraphic().setAnimationSpeedScale(Configurations.CORE_PLAYER_ANIM_SPEED_MULTIPLIER);
            } else
                getGraphic().setAnimationSpeedScale(0f);
        }

        ia.update(this);
        checkFall();
    }

    @Override
    protected void eventDeath(Entity killer) {
        disposeBody();
        //getGraphic().setAnimation("dead", false);

        if (killer != null && killer instanceof Player)
            ((Player) killer).forceJump(true);
        /*else
            Gdx.app.log("FOE", "Koala has committed suicide");*/

        //getController().getSound().playDeathKoala();
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        AnimacaoSkeletal graphic;

        if (graphicName == null || graphicName.trim().length() == 0 || graphicName.trim().equals("koala")) {
            graphic = new AnimacaoSkeletal(getController().getLoad().get(LoadController.SKELETON_KOALA, AnimacaoSkeletalData.class));
            //graphic.setSkin("standard");
            graphic.setAnimation("walk", true);
            graphic.setAnimationSpeedScale(3f);
            graphic.setEscala(0.7f, 0.7f);
            getGraphicOffset().set(0f, -35f);
        } else if (graphicName.toLowerCase().trim().equals("fire")) {
            graphic = new AnimacaoSkeletal(getController().getLoad().get(LoadController.SKELETON_SHADOW, AnimacaoSkeletalData.class));
            graphic.setSkin("standard");
            graphic.setEscala(0.7f, 0.7f);

            getGraphicOffset().set(0f, -15f);
            //Fix to correct spine model with wrong rotation ?
            graphic.setRotacao(-25f);

            graphic.setAnimation("idle", true);
        } else if (graphicName.trim().equals("boss")) {
            graphic = new AnimacaoSkeletal(getController().getLoad().get(LoadController.SKELETON_KOALA, AnimacaoSkeletalData.class));
            //graphic.setSkin("standard");
            graphic.setAnimation("walk", true);
            graphic.setAnimationSpeedScale(3f);
            graphic.setEscala(2.5f, 2.5f);
            getGraphicOffset().set(0f, -125f);
        } else
            throw new RuntimeException("Unknown graphic name when spawning Foe: " + graphicName);

        //Clear unused resources:
        graphicName = null;

        return graphic;
    }

    private void checkFall() {
        if (getY() <= 0f)
            eventDeath(null);
    }

    @Override
    protected void eventAfterBodyCreated(Body body) {
        super.eventAfterBodyCreated(body);

        body.setFixedRotation(true);
        body.getFixtureList().get(0).setFriction(0.5f);
    }
}
