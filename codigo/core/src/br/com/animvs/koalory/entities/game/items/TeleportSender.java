package br.com.animvs.koalory.entities.game.items;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.entities.game.mobiles.Player;

/**
 * Created by DALDEGAN on 07/02/2015.
 */
public final class TeleportSender extends ItemTiled {
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

    public TeleportSender(GameController controller, RectangleMapObject rectangle, String id) {
        super(controller, rectangle);

        if (id == null)
            throw new RuntimeException("The parameter 'id' must be != NULL");

        this.id = id;
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        return null;
    }

    @Override
    public void collect(Player player) {
        super.collect(player);

        TeleportReceiver receiver = getController().getStage().findTeleportReceiver(id);

        for (int i = 0; i < getController().getPlayers().getTotalPlayersInGame(); i++)
            getController().getPlayers().getPlayer(i).teleportTo(receiver.getX(), receiver.getY());
    }
}
