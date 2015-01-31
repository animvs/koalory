package br.com.animvs.koalory.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

import br.com.animvs.engine2.physics.MapBodyBuilder;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.entities.engine.graphics.tiles.TileRenderer;

public class LevelController implements Disposable {
    private GameController controller;
    private TiledMap map;
    private String mapName;
    private TileRenderer renderer;

    private OrthographicCamera cameraCache;

    //Objects created from tiled:
    private ArrayMap<String, Body> bodiesCollision;

    public LevelController(GameController controller) {
        this.controller = controller;
    }

    public String getMapName() {
        return mapName;
    }

    public void loadMap(String map) {
        mapName = map;
        this.map = controller.getLoad().get(map + ".tmx", TiledMap.class);

        renderer = new TileRenderer(this.map, controller);
        renderer.setOverCache(1f);

        bodiesCollision = MapBodyBuilder.buildShapes(this.map, controller.getPhysics().getBoxToWorld(), controller.getPhysics().getWorld(), Configurations.LEVEL_LAYER_COLLISION);

        for (int i = 0; i < bodiesCollision.size; i++)
            bodiesCollision.getValueAt(i).setUserData("collision");

        createItemsAndSpawners();
        createPlatforms();
    }

    public void render() {
        if (renderer == null)
            return;

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
        if (renderer != null)
            renderer.dispose();

        if (bodiesCollision != null) {
            for (int i = 0; i < bodiesCollision.size; i++)
                controller.getPhysics().destroyBody(bodiesCollision.getValueAt(i));

            bodiesCollision.clear();
        }
    }

    private void createPlatforms() {
        MapLayer layerPlatforms = map.getLayers().get(Configurations.LEVEL_LAYER_PLATFORMS);

        if (layerPlatforms == null) {
            Gdx.app.log("LEVEL", "WARNING - Platform layer not found when loading level");
            return;
        }

        MapObjects objects = layerPlatforms.getObjects();
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
                float speedX = Float.parseFloat(objects.get(i).getProperties().get("speed").toString());

                Float speedY = null;
                String ia = null;
                Float interval = null;

                if (objects.get(i).getProperties().get("speedY") != null)
                    speedY = Float.parseFloat(objects.get(i).getProperties().get("speedY").toString());

                if (objects.get(i).getProperties().get("ia") != null)
                    ia = objects.get(i).getProperties().get("ia").toString();

                if (objects.get(i).getProperties().get("interval") != null)
                    interval = Float.parseFloat(objects.get(i).getProperties().get("interval").toString());

                Vector2 position = new Vector2(rectangle.getRectangle().getX(), rectangle.getRectangle().y);
                controller.getEntities().createSpawner(position, spawnInterval, ia, speedX, speedY, interval);
            } else if (objects.get(i).getName().equals("sender")) {
                if (objects.get(i).getProperties().get("map") == null)
                    throw new RuntimeException("Item RECEIVER does not have the property 'map'");

                String mapName = objects.get(i).getProperties().get("map").toString();

                RectangleMapObject rectangle = ((RectangleMapObject) objects.get(i));
                Vector2 position = new Vector2(rectangle.getRectangle().getX(), rectangle.getRectangle().y);

                controller.getEntities().createSender(position, mapName);
            } else if (objects.get(i).getName().equals("deathzone")) {
                RectangleMapObject rectangle = ((RectangleMapObject) objects.get(i));
                controller.getEntities().createDeathZone(rectangle);
            } else
                throw new RuntimeException("Unknown object type when loading map: " + objects.get(i).getName());
        }
    }
}
