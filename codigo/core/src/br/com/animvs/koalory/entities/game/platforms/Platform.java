package br.com.animvs.koalory.entities.game.platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.ArrayMap;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.engine2.graficos.loaders.AnimacaoSkeletalData;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.controller.LoadController;
import br.com.animvs.koalory.entities.game.Entity;
import br.com.animvs.koalory.entities.game.Player;

/**
 * Created by DALDEGAN on 27/01/2015.
 */
public abstract class Platform extends Entity {
    //float[] vertices = polylineObject.getPolyline().getTransformedVertices();

    private Vector2 initialPosition;

    private int size;
    private float speed;
    private boolean waitStart;

    private Vector2 positionCache;
    private Vector2[] path;
    private int pathIndex;
    //private float timeCounter;

    private boolean increasingIndex;
    private boolean started;

    private ArrayMap<AnimacaoSkeletal, Vector2> graphics;

    public Platform(GameController controller, PolylineMapObject line) {
        super(controller);

        increasingIndex = true;
        positionCache = new Vector2();

        preparePath(line);
        parseParameters(line);

        initialPosition = new Vector2(line.getPolyline().getX(), line.getPolyline().getY());

        getController().getEntities().createPlatformBody(this, size);

        float platformWidth = Configurations.CORE_TILE_SIZE * size;

        //Since platforms are composed by many pottentially blocks, it's rendering is done through cached offset graphics according to it's size:
        graphics = new ArrayMap<AnimacaoSkeletal, Vector2>();
        for (int i = 0; i < size; i++) {
            AnimacaoSkeletal graphic = new AnimacaoSkeletal(getController().getLoad().get(LoadController.SKELETON_PLATFORM, AnimacaoSkeletalData.class));
            Vector2 graphicOffset = new Vector2(-((platformWidth / 2f) - (Configurations.CORE_TILE_SIZE / 2f)) + (i * Configurations.CORE_TILE_SIZE), 0f);

            graphics.put(graphic, graphicOffset);
        }
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        if (getBody() != null)
            getBody().setTransform(getBody().getPosition().x, getController().getPhysics().toBox(y), getBody().getAngle());

        if (getGraphic() != null)
            getGraphic().setPosicao(getX(), y /*- Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f*/);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);

        if (getBody() != null)
            getBody().setTransform(getController().getPhysics().toBox(x), getController().getPhysics().toBox(y), getBody().getAngle());

        if (getGraphic() != null)
            getGraphic().setPosicao(x, y /*- Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f*/);
    }

    @Override
    protected void eventAfterBodyCreated(Body body) {
        super.eventAfterBodyCreated(body);

        setPosition(initialPosition.x, initialPosition.y);

        for (int i = 0; i < body.getFixtureList().size; i++)
            body.getFixtureList().get(i).setUserData(Configurations.CORE_PLATFORM_USER_DATA);

        //free unused resource:
        initialPosition = null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        updateGraphic();

        if (!started)
            return;

        Vector2 desiredPosition;

        if (increasingIndex)
            desiredPosition = path[pathIndex + 1];
        else
            desiredPosition = path[pathIndex - 1];


        positionCache.sub(desiredPosition);
        positionCache.nor().scl(-1f);

        getBody().setLinearVelocity(positionCache.x * speed, positionCache.y * speed);

        //if (timeCounter >= speed) {
        positionCache.set(getX(), getY());
        if (positionCache.dst2(desiredPosition) <= Configurations.CORE_PLATFORM_PATH_DISTANCE_TOLERANCE) {
            //timeCounter = 0f;

            if (increasingIndex) {
                pathIndex++;
                if (pathIndex == path.length - 1) {
                    pathIndex = path.length - 1;

                    increasingIndex = false;
                }
            } else {
                pathIndex--;
                if (pathIndex == 0) {
                    pathIndex = 0;
                    increasingIndex = true;
                }
            }
        }
    }

    public void eventPlayerSteped(Player player) {
        if (waitStart)
            started = true;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.end();
        for (int i = 0; i < graphics.size; i++)
            graphics.getKeyAt(i).render(batch, Gdx.graphics.getDeltaTime());
        batch.begin();
    }

    protected void parseParameters(PolylineMapObject line) {
        size = parsePropertyInteger("size", line);
        //fall = parsePropertyBoolean("fall", line);
        //respawnInterval = parsePropertyFloat("respawnInterval", line);
        speed = parsePropertyFloat("speed", line);

        if (line.getProperties().get("waitStart") != null)
            waitStart = parsePropertyBoolean("waitStart", line);

        if (!waitStart)
            started = true;
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

    private void preparePath(PolylineMapObject line) {
        float[] vertices = line.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] /*/ Configurations.CORE_TILE_SIZE*/;
            worldVertices[i].y = vertices[i * 2 + 1] /*/ Configurations.CORE_TILE_SIZE*/;
        }

        path = worldVertices;
    }

    private void updateGraphic() {
        for (int i = 0; i < graphics.size; i++)
            graphics.getKeyAt(i).setPosicao(getX() - graphics.getValueAt(i).x, getY() - graphics.getValueAt(i).y);
    }
}