package br.com.animvs.koalory.entities.game.items;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.controller.GameController;

/**
 * Created by DALDEGAN on 07/02/2015.
 */
public final class TeleportReceiver extends ItemTiled {

    private final String id;

    public String getID() {
        return id;
    }

    @Override
    protected boolean getDisposeOnCollect() {
        return false;
    }

    @Override
    protected BodyDef.BodyType getBodyType() {
        return BodyDef.BodyType.StaticBody;
    }

    @Override
    protected boolean getBodySensor() {
        return true;
    }

    public TeleportReceiver(GameController controller, RectangleMapObject rectangle, String id) {
        super(controller, rectangle);

        if (id == null)
            throw new RuntimeException("The parameter 'id' must be != NULL");

        this.id = id;
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        return null;
    }
}
