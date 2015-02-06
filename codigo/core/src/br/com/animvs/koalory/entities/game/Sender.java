package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;

/**
 * Created by DALDEGAN on 29/01/2015.
 */
public final class Sender extends Item {

    private String mapName;

    @Override
    protected BodyDef.BodyType getBodyType() {
        return BodyDef.BodyType.KinematicBody;
    }

    @Override
    protected boolean getBodySensor() {
        return true;
    }

    public Sender(GameController controller, Vector2 spawnPosition, String mapName) {
        super(controller, spawnPosition);

        if (mapName == null)
            throw new RuntimeException("The parameter 'mapName' must be != NULL");

        if (mapName.trim().length() == 0)
            throw new RuntimeException("The parameter 'mapName' cannot be EMPTY");

        this.mapName = mapName;
    }

    @Override
    public float getX() {
        return super.getX();
    }

    @Override
    protected void eventAfterBodyCreated(Body body) {
        super.eventAfterBodyCreated(body);
        setPosition(getX(), getY());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        return null;
    }

    @Override
    public void collect(Player player) {
        getController().startMatch(mapName);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
