package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.engine2.graficos.loaders.AnimacaoSkeletalData;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.controller.LoadController;
import br.com.animvs.koalory.entities.engine.ia.IABase;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public class Foe extends Mobile {
    private IABase ia;
    private Vector2 spawnPosition;

    public Foe(GameController controller, String graphic, IABase ia, Vector2 spawnPosition) {
        super(controller);

        this.ia = ia;
        this.spawnPosition = spawnPosition;

        getController().getEntities().createEntityBody(this, 0.7f, true);
        prepareGraphic(graphic);
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

    private void prepareGraphic(String graphicName) {
        AnimacaoSkeletal graphic;

        if (graphicName == null || graphicName.trim().length() == 0) {
            graphic = new AnimacaoSkeletal(getController().getLoad().get(LoadController.SKELETON_KOALA, AnimacaoSkeletalData.class));
            //graphic.setSkin("standard");
            graphic.setAnimation("walk", true);
            graphic.setAnimationSpeedScale(3f);
            graphic.setEscala(0.7f, 0.7f);
            getGraphicOffset().set(0f, -35f);
        } else if (graphicName.toLowerCase().trim().equals("fire")) {
            graphic = new AnimacaoSkeletal(getController().getLoad().get(LoadController.SKELETON_SHADOW, AnimacaoSkeletalData.class));
            graphic.setSkin("standard");

            getGraphicOffset().set(0f, -15f);
            //Fix to correct spine model with wrong rotation ?
            graphic.setRotacao(-25f);

            graphic.setAnimation("idle", true);
        } else
            throw new RuntimeException("Unknown graphic when spawning Foe: " + graphicName);

        setGraphic(graphic);
    }

    private void checkFall() {
        if (getY() <= 0f)
            eventDeath(null);
    }

    @Override
    protected void eventAfterBodyCreated(Body body) {
        super.eventAfterBodyCreated(body);

        setPosition(spawnPosition.x, spawnPosition.y);
        body.setFixedRotation(true);

        body.getFixtureList().get(0).setFriction(0.5f);

        //Clean unused resources:
        spawnPosition = null;
    }
}
