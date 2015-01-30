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
public class Foe extends Entity {
    private IABase ia;
    private Vector2 spawnPosition;

    private boolean alive;
    private Vector2 offset;

    public boolean getAlive() {
        return alive;
    }

    public Foe(GameController controller, IABase ia, Vector2 spawnPosition) {
        super(controller);

        this.ia = ia;
        this.spawnPosition = spawnPosition;

        getController().getEntities().createEntityBody(this, 0.7f);

        AnimacaoSkeletal graphic = new AnimacaoSkeletal(controller.getLoad().get(LoadController.SKELETON_SHADOW, AnimacaoSkeletalData.class));
        graphic.setSkin("standard");

        //Fix to correct spine model with wrong rotation ?
        offset = new Vector2(0f, -15f);
        graphic.setRotacao(-25f);

        setGraphic(graphic);
        graphic.setAnimation("idle", true);

        alive = true;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        if (getBody() != null)
            getBody().setTransform(getController().getPhysics().toBox(x), getBody().getPosition().y, getBody().getAngle());

        if (getGraphic() != null)
            getGraphic().setPosicao(x + offset.x, getY());
    }

    @Override
    public void setY(float y) {
        super.setY(y);

        if (getGraphic() != null)
            getGraphic().setPosicao(getX() + offset.x, y + offset.y);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);

        if (getBody() != null)
            getBody().setTransform(getController().getPhysics().toBox(x), getController().getPhysics().toBox(y), getBody().getAngle());

        if (getGraphic() != null)
            getGraphic().setPosicao(x + offset.x, y + offset.y);
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
        //getGraphic().setAnimation("dead", false);

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
        getGraphic().setAnimation("idle", true);

        body.getFixtureList().get(0).setFriction(0.5f);

        //Clean unused resources:
        spawnPosition = null;
    }
}
