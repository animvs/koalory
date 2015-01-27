package br.com.animvs.ggj2015.entities.game.platforms;

import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import br.com.animvs.ggj2015.controller.GameController;
import br.com.animvs.ggj2015.entities.game.GGJ15Entity;

/**
 * Created by DALDEGAN on 27/01/2015.
 */
public abstract class Platform extends GGJ15Entity {
    //float[] vertices = polylineObject.getPolyline().getTransformedVertices();

    private Vector2 initialPosition;

    private int size;
    private boolean fall;
    private float respawnInterval;

    protected final boolean getFall() {
        return fall;
    }

    protected final float getRespawnInterval() {
        return respawnInterval;
    }

    public Platform(GameController controller, PolylineMapObject line) {
        super(controller);

        parseParameters(line);

        initialPosition = new Vector2(line.getPolyline().getX(), line.getPolyline().getY());

        getController().getEntities().createPlatformBody(this, size);
    }

    @Override
    protected void eventAfterBodyCreated(Body body) {
        super.eventAfterBodyCreated(body);

        setPosition(initialPosition.x, initialPosition.y);

        //free unused resource:
        initialPosition = null;
    }

    @Override
    public void update() {
        super.update();
    }

    protected void parseParameters(PolylineMapObject line) {
        size = parsePropertyInteger("size", line);
        fall = parsePropertyBoolean("fall", line);
        respawnInterval = parsePropertyFloat("respawnInterval", line);
    }

    protected final boolean parsePropertyBoolean(String name, PolylineMapObject line) {
        validateProperty(name, line);

        String value = line.getProperties().get(name).toString().trim().toLowerCase();
        if (value.equals("true"))
            return true;
        else if (value.equals("false"))
            return false;

        throw new RuntimeException("Invalid value for property '" + name + "' (of type Boolean): " + value + " - Expecting 'true' or 'false'");
    }

    protected final float parsePropertyFloat(String name, PolylineMapObject line) {
        validateProperty(name, line);

        String value = line.getProperties().get(name).toString();
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            throw new RuntimeException("Invalid value for property '" + name + "' (of type Boolean): " + value + " - Expecting a decimal number");
        }
    }

    protected final int parsePropertyInteger(String name, PolylineMapObject line) {
        validateProperty(name, line);

        String value = line.getProperties().get(name).toString();
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new RuntimeException("Invalid value for property '" + name + "' (of type Boolean): " + value + " - Expecting an integer number");
        }
    }

    /**
     * Validate if the property specified content is valid (size != 0 and NOT NULL)
     *
     * @throws RuntimeException when any requirement was not fulfilled
     */
    protected void validateProperty(String name, PolylineMapObject line) throws RuntimeException {
        if (line.getProperties().get(name) == null)
            throw new RuntimeException("Property '" + name + "' not found when loading platform");

        String value = line.getProperties().get(name).toString();

        /*if (value == null)
            throw new RuntimeException("Property '" + name + "' must be != NULL");*/

        if (value.trim().length() == 0)
            throw new RuntimeException("Property '" + name + "' cannot be empty");
    }

    @Override
    public void dispose() {
        super.dispose();


    }
}