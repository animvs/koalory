package br.com.animvs.koalory.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
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
    private Vector2 playerStart;

    private OrthographicCamera cameraCache;

    //Objects created from tiled:
    private ArrayMap<String, Body> bodiesCollision;

    public LevelController(GameController controller) {
        this.controller = controller;
        this.playerStart = new Vector2();
    }

    public String getMapName() {
        return mapName;
    }

    public Vector2 getPlayerStart() {
        return playerStart;
    }

    public void loadMap(String map) {
        mapName = map;
        this.map = controller.getLoad().get(Configurations.CORE_LEVEL_DIR + map + ".tmx", TiledMap.class);

        renderer = new TileRenderer(this.map, controller);
        renderer.setOverCache(1f);

        bodiesCollision = MapBodyBuilder.buildShapes(this.map, controller.getPhysics().getBoxToWorld(), controller.getPhysics().getWorld(), Configurations.LEVEL_LAYER_COLLISION);

        playerStart.set(Configurations.GAMEPLAY_PLAYER_START.x, Configurations.GAMEPLAY_PLAYER_START.y);

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

    public void restart() {
        playerStart.set(Configurations.GAMEPLAY_PLAYER_START.x, Configurations.GAMEPLAY_PLAYER_START.y);
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
            if (objects.get(i).getName().toLowerCase().trim().equals("color")) {
                Object powerProperty = objects.get(i).getProperties().get("power");

                if (powerProperty == null)
                    throw new RuntimeException("Item color does not have the property 'power'");

                RectangleMapObject rectangle = castLevelObject(objects.get(i));
                controller.getEntities().spawnItemColorProgress(rectangle.getRectangle().getX(), rectangle.getRectangle().getY(), Float.parseFloat(powerProperty.toString()));
            } else if (objects.get(i).getName().toLowerCase().trim().equals("spawn")) {
                RectangleMapObject rectangle = castLevelObject(objects.get(i));

                float spawnInterval = Float.parseFloat(objects.get(i).getProperties().get("spawnInterval").toString());
                float speedX = Float.parseFloat(objects.get(i).getProperties().get("speed").toString());

                String graphic = null;
                String ia = null;
                Float speedY = null;
                Float interval = null;

                if (objects.get(i).getProperties().get("graphic") != null)
                    graphic = objects.get(i).getProperties().get("graphic").toString();

                if (objects.get(i).getProperties().get("speedY") != null)
                    speedY = Float.parseFloat(objects.get(i).getProperties().get("speedY").toString());

                if (objects.get(i).getProperties().get("ia") != null)
                    ia = objects.get(i).getProperties().get("ia").toString();

                if (objects.get(i).getProperties().get("interval") != null)
                    interval = Float.parseFloat(objects.get(i).getProperties().get("interval").toString());

                Vector2 position = new Vector2(rectangle.getRectangle().getX(), rectangle.getRectangle().y);
                controller.getEntities().createSpawner(graphic, position, spawnInterval, ia, speedX, speedY, interval);
            } else if (objects.get(i).getName().toLowerCase().trim().equals("sender")) {
                if (objects.get(i).getProperties().get("map") == null)
                    throw new RuntimeException("Item RECEIVER does not have the property 'map'");

                String mapName = objects.get(i).getProperties().get("map").toString();

                //Verfies if sender's level completed requirements are fulfilled:
                Array<String> missingMaps = new Array<String>();
                boolean cancelCreation = false;

                if (objects.get(i).getProperties().get("requiredMaps") != null) {
                    String[] requiredMaps = objects.get(i).getProperties().get("requiredMaps").toString().split(";");

                    for (int j = 0; j < requiredMaps.length; j++) {
                        if (!controller.getProfile().checkLevelClear(requiredMaps[j])) {
                            missingMaps.add(requiredMaps[j]);
                            cancelCreation = true;
                        }
                    }
                }

                if (cancelCreation) {
                    String message = "Sender creation skipped, level clear requirements where not fulfilled, the following maps still not completed: \"";

                    for (int j = 0; j < missingMaps.size; j++) {
                        if (j > 0)
                            message += ", ";
                        message += missingMaps.get(j);
                    }
                    message += "\"";

                    Gdx.app.log("ITEM SENDER", message);
                    continue;
                }

                RectangleMapObject rectangle = castLevelObject(objects.get(i));
                Vector2 position = new Vector2(rectangle.getRectangle().getX(), rectangle.getRectangle().y);

                controller.getEntities().createSender(position, mapName);
            } else if (objects.get(i).getName().toLowerCase().trim().equals("deathzone")) {
                RectangleMapObject rectangle = castLevelObject(objects.get(i));
                controller.getEntities().createDeathZone(rectangle);
            } else if (objects.get(i).getName().toLowerCase().trim().equals("endlevel")) {
                //TODO: Create a new 'endlevel' item
            } else if (objects.get(i).getName().toLowerCase().trim().equals("playerstart")) {
                RectangleMapObject rectangle = castLevelObject(objects.get(i));
                playerStart.set(rectangle.getRectangle().getX(), rectangle.getRectangle().getY());
            } else if (objects.get(i).getName().toLowerCase().trim().equals("life")) {
                RectangleMapObject rectangle = castLevelObject(objects.get(i));
                controller.getEntities().createLife(rectangle);
            } else if (objects.get(i).getName().toLowerCase().trim().equals("checkpoint")) {
                RectangleMapObject rectangle = castLevelObject(objects.get(i));
                controller.getEntities().createCheckpoint(rectangle);
            } else
                throw new RuntimeException("Unknown object type when loading map - Map: " + mapName + " object: " + objects.get(i).getName());
        }
    }

    private RectangleMapObject castLevelObject(MapObject object) {
        if (!(object instanceof RectangleMapObject))
            throw new RuntimeException("Invalid object body defined on map, EXPECTING RECTANGLE - Map: " + mapName + " object: " + object.getName());

        return (RectangleMapObject) object;
    }
}
