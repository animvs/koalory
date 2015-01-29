package br.com.animvs.ggj2015.entities.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.engine2.graficos.loaders.AnimacaoSkeletalData;
import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.controller.GameController;
import br.com.animvs.ggj2015.controller.LoadController;
import br.com.animvs.ggj2015.entities.engine.ia.IABase;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public class Foe extends GGJ15Entity {
    private IABase ia;
    private Vector2 spawnPosition;

    private boolean alive;

    public boolean getAlive() {
        return alive;
    }

    public Foe(GameController controller, IABase ia, Vector2 spawnPosition) {
        super(controller);

        this.ia = ia;
        this.spawnPosition = spawnPosition;

        float scale = 0.7f;

        getController().getEntities().createEntityBody(this, scale);

        AnimacaoSkeletal graphic = new AnimacaoSkeletal(controller.getLoad().get(LoadController.SKELETON_KOALA, AnimacaoSkeletalData.class));
        graphic.setEscala(scale, scale);
        setGraphic(graphic);
        graphic.setAnimation("walk", true);

        alive = true;
    }

    @Override
    public void update() {
        super.update();

        if (getBody() != null) {
            if (getBody().getLinearVelocity().x != 0f) {
                getGraphic().setAnimationSpeedScale(Configurations.CORE_PLAYER_ANIM_SPEED_MULTIPLIER);
            } else
                getGraphic().setAnimationSpeedScale(0f);
        }

        ia.update(this);
        checkFall();
    }

    public void eventDeath(Player killer) {
        if (!alive)
            return;

        alive = false;

        disposeBody();
        getGraphic().setAnimation("dead", false);

        if (killer != null)
            killer.forceJump(true);
        /*else
            Gdx.app.log("FOE", "Koala has committed suicide");*/

        getController().getSound().playDeathKoala();
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
        getGraphic().setAnimation("walk", true);

        body.getFixtureList().get(0).setFriction(0.1f);
        //Gdx.app.log("FOE", "Koala spawned at X: " + getX() + " Y: " + getY());
    }
}
