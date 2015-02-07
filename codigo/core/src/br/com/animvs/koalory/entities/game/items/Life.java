package br.com.animvs.koalory.entities.game.items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.LoadController;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.entities.game.mobiles.Player;

/**
 * Created by DALDEGAN on 04/02/2015.
 */
public final class Life extends ItemTiled {

    private TextureRegion regionCache;

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
        return null; //Uses texture region
    }

    public Life(GameController controller, RectangleMapObject rectangleMapObject) {
        super(controller, rectangleMapObject);
    }

    @Override
    public void collect(Player player) {
        super.collect(player);

        getController().incrementLife();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (regionCache == null)
            regionCache = getController().getLoad().get(LoadController.UI_SKIN_PATH, TextureAtlas.class).findRegion("life");

        batch.draw(regionCache, getX() - regionCache.getRegionWidth() / 2f, getY() - regionCache.getRegionHeight() / 2f);
    }
}
