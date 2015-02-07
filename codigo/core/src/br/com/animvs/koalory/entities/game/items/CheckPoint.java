package br.com.animvs.koalory.entities.game.items;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.entities.game.mobiles.Player;

/**
 * Created by DALDEGAN on 04/02/2015.
 */
public final class CheckPoint extends ItemTiled {

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

    public CheckPoint(GameController controller, RectangleMapObject rectangleMapObject) {
        super(controller, rectangleMapObject);
    }

    @Override
    public void collect(Player player) {
        super.collect(player);

        getController().getLevel().getPlayerStart().set(getX(), getY());
    }
}
