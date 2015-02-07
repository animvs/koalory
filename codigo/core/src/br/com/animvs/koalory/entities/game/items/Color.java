package br.com.animvs.koalory.entities.game.items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.engine2.graficos.loaders.AnimacaoSkeletalData;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.controller.LoadController;
import br.com.animvs.koalory.entities.game.mobiles.Player;

/**
 * Created by ANSCHAU on 24/01/2015.
 */
public class Color extends ItemTiled {

    private float colorRecovered;

    @Override
    protected BodyDef.BodyType getBodyType() {
        return BodyDef.BodyType.KinematicBody;
    }

    @Override
    protected boolean getBodySensor() {
        return true;
    }

    public Color(GameController controller, RectangleMapObject rectangleMapObject, float colorRecovered) {
        super(controller, rectangleMapObject);
        this.colorRecovered = colorRecovered;
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        AnimacaoSkeletal graphic = new AnimacaoSkeletal(getController().getLoad().get(LoadController.SKELETON_COLOR, AnimacaoSkeletalData.class));
        graphic.getColor().set(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);

        return graphic;
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
    public void collect(Player player) {
        super.collect(player);

        getController().addColorRecovered(colorRecovered);
        getController().getUI().castValueColors();
    }

    @Override
    public void dispose() {
        getController().getEntities().removeItem(this);

        super.dispose();
    }
}