package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.controller.GameController;

/**
 * Created by DALDEGAN on 04/02/2015.
 */
public final class CheckPoint extends Item {

    @Override
    protected BodyDef.BodyType getBodyType() {
        return BodyDef.BodyType.KinematicBody;
    }

    @Override
    protected boolean getBodySensor() {
        return true;
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        return null;
    }

    public CheckPoint(GameController controller, Vector2 spawnPosition) {
        super(controller, spawnPosition);
    }

    @Override
    public void collect(Player player) {
        super.collect(player);

        getController().getLevel().getPlayerStart().set(getX(), getY());
    }
}
