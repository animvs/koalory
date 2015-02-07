package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;

/**
 * Created by DALDEGAN on 07/02/2015.
 */
public abstract class ItemTiled extends Item {

    private final Rectangle rectangle;

    @Override
    protected final float getBodyScaleX() {
        return rectangle.width / Configurations.CORE_TILE_SIZE;
    }

    @Override
    protected final float getBodyScaleY() {
        return rectangle.height / Configurations.CORE_TILE_SIZE;
    }

    public ItemTiled(GameController controller, RectangleMapObject rectangle) {
        super(controller, new Vector2(rectangle.getRectangle().x + rectangle.getRectangle().width / 2f, rectangle.getRectangle().y + rectangle.getRectangle().height / 2f));
        this.rectangle = rectangle.getRectangle();
    }
}
