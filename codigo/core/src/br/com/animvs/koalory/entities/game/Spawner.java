package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;

/**
 * Created by DALDEGAN on 25/01/2015.
 */
public final class Spawner extends Entity {
    private float spawnInterval;

    private float spawnTimer;
    private final String ia;
    private float foeSpeedX;

    private Float foeSpeedY;
    private Float interval;
    private String graphic;

    private Rectangle rectangle;

    @Override
    protected final float getBodyScaleX() {
        return rectangle.width / Configurations.CORE_TILE_SIZE;
    }

    @Override
    protected final float getBodyScaleY() {
        return rectangle.height / Configurations.CORE_TILE_SIZE;
    }

    @Override
    protected BodyDef.BodyType getBodyType() {
        return BodyDef.BodyType.KinematicBody;
    }

    @Override
    protected boolean getBodySensor() {
        return true;
    }

    public Spawner(GameController controller, RectangleMapObject rectangleMapObject, String graphic, float spawnInterval, String ia, float foeSpeedX, Float foeSpeedY, Float interval) {
        super(controller, new Vector2(rectangleMapObject.getRectangle().x + rectangleMapObject.getRectangle().width / 2f, rectangleMapObject.getRectangle().y + rectangleMapObject.getRectangle().height / 2f));
        this.spawnInterval = spawnInterval;
        this.ia = ia;
        this.foeSpeedX = foeSpeedX;
        this.foeSpeedY = foeSpeedY;
        this.interval = interval;
        this.graphic = graphic;
        this.rectangle = rectangleMapObject.getRectangle();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        spawnTimer += Gdx.graphics.getDeltaTime();

        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;
            getController().getEntities().spawnFoe(graphic, 0.7f, rectangle.x + rectangle.width / 2f, rectangle.y + rectangle.height / 2f, foeSpeedX, foeSpeedY, ia, interval);
        }
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        return null;
    }
}
