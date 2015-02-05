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
    private boolean killsIA;
    private boolean killsPlayer;

    public boolean getKillsIA() {
        return killsIA;
    }

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
        this.killsPlayer = true; //TRUE by default

        parseParameters();
    }

    @Override
    protected PhysicsController.TargetPhysicsParameters createBody(float tileSize) {
        PhysicsController.TargetPhysicsParameters bodyParams = new PhysicsController.TargetPhysicsParameters(this, new Vector2(), 0f, getBodyType(), true,
                rectangle.getRectangle().width, rectangle.getRectangle().height, getBodyDensity(), getBodyRestitution(), getBodySensor());

        return bodyParams;
    }

    @Override
    protected AnimacaoSkeletal createGraphic() {
        return null;
    }

    @Override
    public void collect(Player player) {
        if (!killsPlayer)
            return;

        Gdx.app.log("KILL", "Player " + getController().getPlayers().getPlayerIndex(player) + " has been killed by a death zone");
        player.death(this);
    }

    @Override
    protected void eventAfterBodyCreated(Body body) {
        super.eventAfterBodyCreated(body);
        setPosition(rectangle.getRectangle().x + rectangle.getRectangle().width / 2f, rectangle.getRectangle().y + rectangle.getRectangle().height / 2f);

        for (int i = 0; i < body.getFixtureList().size; i++)
            body.getFixtureList().get(i).setSensor(true);

        //dispose unused resources:
        rectangle = null;
    }

    private void parseParameters() {
        if (rectangle.getProperties().get("killsIA") != null)
            killsIA = parsePropertyBoolean("killsIA", rectangle);

        if (rectangle.getProperties().get("killsPlayer") != null)
            killsPlayer = parsePropertyBoolean("killsPlayer", rectangle);
    }
}
