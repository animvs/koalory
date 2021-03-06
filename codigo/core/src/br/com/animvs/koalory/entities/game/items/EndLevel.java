package br.com.animvs.koalory.entities.game.items;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.entities.game.mobiles.Player;

/**
 * Created by DALDEGAN on 05/02/2015.
 */
public final class EndLevel extends ItemTiled {
    @Override
    protected boolean getBodySensor() {
        return true;
    }

    @Override
    protected BodyDef.BodyType getBodyType() {
        return BodyDef.BodyType.KinematicBody;
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        return null;
    }

    public EndLevel(GameController controller, RectangleMapObject rectangleMapObject) {
        super(controller, rectangleMapObject);
    }

    @Override
    public void collect(Player player) {
        super.collect(player);

        getController().endMatch();

        if (getController().getLevel().getMapName().equals("castle1")) {
            getController().getProfile().resetProfile();
            getController().getUI().showUIGameWin();
        } else {
            getController().getProfile().registerLevelClear(getController().getLevel().getMapName());
                /*if (getController().getProfile().checkCastleFreed())
                    getController().startMatch("castle1");
                else*/
            getController().startMatch(null);
        }
    }
}
