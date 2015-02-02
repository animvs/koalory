package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.controller.PhysicsController;

public abstract class Item extends Entity {

    protected BodyDef.BodyType getBodyType() {
        return BodyDef.BodyType.DynamicBody;
    }

    protected float getBodyDensity() {
        return 1f;
    }

    protected float getBodyRestitution() {
        return 0.1f;
    }

    protected float getBodyScaleX() {
        return 1f;
    }

    protected float getBodyScaleY() {
        return 1f;
    }

    protected boolean getBodySensor() {
        return false;
    }

    protected boolean getDisposeOnCollect() {
        return true;
    }

    public Item(GameController controller) {
        super(controller);
    }

    public final void initialize() {
        PhysicsController.TargetPhysicsParameters bodyParameters = createBody(Configurations.CORE_TILE_SIZE);
        bodyParameters.bodyHolder = this;

        getController().getPhysics().createRetangleBody(bodyParameters);

        setGraphic(createGraphic());
    }

    protected PhysicsController.TargetPhysicsParameters createBody(float tileSize) {
        PhysicsController.TargetPhysicsParameters bodyParams = new PhysicsController.TargetPhysicsParameters(this, new Vector2(), 0f, getBodyType(),
                tileSize * getBodyScaleX(), tileSize * getBodyScaleY(), getBodyDensity(), getBodyRestitution(), getBodySensor());

        return bodyParams;
    }

    protected abstract AnimacaoSkeletal createGraphic();

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

    public void collect(Player player) {
        if (getDisposeOnCollect())
            dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        getController().getEntities().removeItem(this);
    }

    protected final boolean parsePropertyBoolean(String name, MapObject line) {
        validateProperty(name, line);

        String value = line.getProperties().get(name).toString().trim().toLowerCase();
        if (value.equals("true"))
            return true;
        else if (value.equals("false"))
            return false;

        throw new RuntimeException("Invalid value for property '" + name + "' (of type Boolean): " + value + " - Expecting 'true' or 'false'");
    }

    protected final float parsePropertyFloat(String name, MapObject line) {
        validateProperty(name, line);

        String value = line.getProperties().get(name).toString();
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            throw new RuntimeException("Invalid value for property '" + name + "' (of type Boolean): " + value + " - Expecting a decimal number");
        }
    }

    protected final int parsePropertyInteger(String name, MapObject line) {
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
    protected void validateProperty(String name, MapObject line) throws RuntimeException {
        if (line.getProperties().get(name) == null)
            throw new RuntimeException("Property '" + name + "' not found when loading platform");

        String value = line.getProperties().get(name).toString();

        /*if (value == null)
            throw new RuntimeException("Property '" + name + "' must be != NULL");*/

        if (value.trim().length() == 0)
            throw new RuntimeException("Property '" + name + "' cannot be empty");
    }
}
