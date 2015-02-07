package br.com.animvs.koalory.entities.game.platforms;

import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.controller.GameController;

/**
 * Created by DALDEGAN on 27/01/2015.
 */
public final class StaticPlatform extends Platform{
    public StaticPlatform(GameController controller, PolylineMapObject line) {
        super(controller, line);
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        return null;
    }
}
