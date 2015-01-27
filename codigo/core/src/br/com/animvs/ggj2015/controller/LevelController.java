package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

import br.com.animvs.engine2.physics.MapBodyBuilder;
import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.entities.engine.graphics.tiles.TileRenderer;
import br.com.animvs.ggj2015.entities.game.Player;

public class LevelController implements Disposable {
    private GameController controller;
    private TiledMap map;
    private TileRenderer renderer;

    private OrthographicCamera cameraCache;

    private Player cameraOwner;

    //Objects created from tiled:
    private ArrayMap<String, Body> bodiesCollision;

    public LevelController(GameController controller) {
        this.controller = controller;
    }

    public Player getCameraOwner() {
        return cameraOwner;
    }

    public void setCameraOwner(Player cameraOwner) {
        this.cameraOwner = cameraOwner;
    }

    public void loadMap(String mapPath) {
        if (map != null) {
            map.dispose();
            renderer.dispose();
        }

        map = controller.getLoad().get(mapPath, TiledMap.class);

        renderer = new TileRenderer(map, controller);
        renderer.setOverCache(1f);

        bodiesCollision = MapBodyBuilder.buildShapes(map, controller.getPhysics().getBoxToWorld(), controller.getPhysics().getWorld(), Configurations.LEVEL_LAYER_COLLISION);

        for (int i = 0; i < bodiesCollision.size; i++) {
            /*for (int j = 0; j < bodiesCollision.getValueAt(i).getFixtureList().size; j++)
                bodiesCollision.getValueAt(i).getFixtureList().get(j).setUserData("collision");*/
            bodiesCollision.getValueAt(i).setUserData("collision");
        }

        createItemsAndSpawners();
    }

    public void render() {
        if (cameraCache == null) {
            cameraCache = (OrthographicCamera) controller.getStage().getCamera();
            cameraCache.position.set(Configurations.RESOLUTION_REAL.x / 2f, (Configurations.RESOLUTION_REAL.y / 2f) + 74f * 3f, 1f);
            cameraCache.update();
        }

        renderer.getSpriteCache().setShader(controller.getShaderColor().getGDXShader());

        renderer.setBlending(true);

        renderer.setView(cameraCache);

        controller.getShaderColor().update();
        renderer.render();
    }

    @Override
    public void dispose() {
        renderer.dispose();

        for (int i = 0; i < bodiesCollision.size; i++)
            controller.getPhysics().destroyBody(bodiesCollision.getValueAt(i));
    }

    private void createPlatforms() {
        MapObjects objects = map.getLayers().get(Configurations.LEVEL_LAYER_PLATFORMS).getObjects();
        for (int i = 0; i < objects.getCount(); i++) {
            PolylineMapObject line = ((PolylineMapObject) objects.get(i));

            controller.getEntities().createPlatform(line);
        }
    }

    private void createItemsAndSpawners() {
        MapObjects objects = map.getLayers().get(Configurations.LEVEL_LAYER_ITEMS).getObjects();

        for (int i = 0; i < objects.getCount(); i++) {
            if (objects.get(i).getName().equals("color")) {
                Object powerProperty = objects.get(i).getProperties().get("power");

                if (powerProperty == null)
                    throw new RuntimeException("Item color does not have the property 'power'");

                RectangleMapObject rectangle = ((RectangleMapObject) objects.get(i));
                controller.getEntities().spawnItemColorProgress(rectangle.getRectangle().getX(), rectangle.getRectangle().getY(), Float.parseFloat(powerProperty.toString()));
            } else if (objects.get(i).getName().equals("spawn")) {
                RectangleMapObject rectangle = ((RectangleMapObject) objects.get(i));

                float spawnInterval = Float.parseFloat(objects.get(i).getProperties().get("spawnInterval").toString());
                float speed = Float.parseFloat(objects.get(i).getProperties().get("speed").toString());

                controller.getEntities().createSpawner(new Vector2(rectangle.getRectangle().getX(), rectangle.getRectangle().y), spawnInterval, speed);
            }
        }
    }
}
