package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.controller.PhysicsController;

/**
 * Created by DALDEGAN on 31/01/2015.
 */
public final class DeathZone extends Item {

    private RectangleMapObject rectangle;

    @Override
    protected boolean getDisposeOnCollect() {
        return false;
    }

    protected BodyDef.BodyType getBodyType() {
        return BodyDef.BodyType.StaticBody;
    }

    public DeathZone(GameController controller, RectangleMapObject rectangle) {
        super(controller);
        this.rectangle = rectangle;
    }

    @Override
    protected PhysicsController.TargetPhysicsParameters createBody(float tileSize) {
        PhysicsController.TargetPhysicsParameters bodyParams = new PhysicsController.TargetPhysicsParameters(this, new Vector2(), 0f, getBodyType(),
                rectangle.getRectangle().width, rectangle.getRectangle().height, getBodyDensity(), getBodyRestitution(), getBodySensor());

        return bodyParams;
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        return null;
    }

    @Override
    public void collect(Player player) {
        Gdx.app.log("KILL", "Player " + getController().getPlayers().getPlayerIndex(player) + " has been killed by a death zone");
        player.eventDeath();
    }

    @Override
    protected void eventAfterBodyCreated(Body body) {
        super.eventAfterBodyCreated(body);
        setPosition(rectangle.getRectangle().x + rectangle.getRectangle().width / 2f, rectangle.getRectangle().y + rectangle.getRectangle().height / 2f);

        //dispose unused resources:
        rectangle = null;
    }
}
