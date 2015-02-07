package br.com.animvs.koalory.entities.game.items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;

import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.entities.game.Entity;
import br.com.animvs.koalory.entities.game.mobiles.Player;

public abstract class Item extends Entity {

    public Item(GameController controller, Vector2 spawnPosition){
        super(controller, spawnPosition);
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

    public void collect(Player player) {
        if (getDisposeOnCollect())
            dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        getController().getEntities().removeItem(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
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
