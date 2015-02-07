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
            bodiesCollision.getValueAt(i).setUserData(Configurations.CORE_WALL_USER_DATA);

        createItemsAndSpawners();
        createPlatforms();
        preparebackground(map);
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

    private void preparebackground(String map) {
        if (map.equals("castle1"))
            controller.getBackground().setType(BackgroundController.Type.CASTLE);
        else if (map.equals("sandPlains1-1"))
            controller.getBackground().setType(BackgroundController.Type.DESERT);
        else
            controller.getBackground().setType(BackgroundController.Type.JUNGLE);
    }

    private void createItemsAndSpawners() {
        MapObjects objects = map.getLayers().get(Configurations.LEVEL_LAYER_ITEMS).getObjects();

        for (int i = 0; i < objects.getCount(); i++) {
            if (objects.get(i).getName().toLowerCase().trim().equals("color"))
                createColor(objects.get(i));
            else if (objects.get(i).getName().toLowerCase().trim().equals("spawn"))
                createSpawn(objects.get(i));
            else if (objects.get(i).getName().toLowerCase().trim().equals("sender"))
                createSender(objects.get(i));
            else if (objects.get(i).getName().toLowerCase().trim().equals("deathzone"))
                createDeathZone(objects.get(i));
            else if (objects.get(i).getName().toLowerCase().trim().equals("endlevel"))
                createEndLevel(objects.get(i));
            else if (objects.get(i).getName().toLowerCase().trim().equals("playerstart"))
                createPlayerStart(objects.get(i));
            else if (objects.get(i).getName().toLowerCase().trim().equals("life"))
                createLife(objects.get(i));
            else if (objects.get(i).getName().toLowerCase().trim().equals("checkpoint"))
                createCheckpoint(objects.get(i));
            else if (objects.get(i).getName().toLowerCase().trim().equals("boss"))
                createBoss(objects.get(i));
            else
                throw new RuntimeException("Unknown object type when loading map - Map: " + mapName + " object: " + objects.get(i).getName());
        }
    }

    private void createBoss(MapObject mapObject) {
        RectangleMapObject rectangle = castLevelObject(mapObject);
        controller.getEntities().spawnBoss(new Vector2(rectangle.getRectangle().x, rectangle.getRectangle().y));
    }

    private void createCheckpoint(MapObject mapObject) {
        RectangleMapObject rectangle = castLevelObject(mapObject);
        controller.getEntities().createCheckpoint(rectangle);
    }

    private void createLife(MapObject mapObject) {
        RectangleMapObject rectangle = castLevelObject(mapObject);
        controller.getEntities().createLife(rectangle);
    }

    private void createPlayerStart(MapObject mapObject) {
        RectangleMapObject rectangle = castLevelObject(mapObject);
        playerStart.set(rectangle.getRectangle().getX(), rectangle.getRectangle().getY());
    }

    private void createEndLevel(MapObject mapObject) {
        RectangleMapObject rectangle = castLevelObject(mapObject);
        controller.getEntities().createEndLevel(rectangle);
    }

    private void createColor(MapObject mapObject) {
        Object powerProperty = mapObject.getProperties().get("power");

        if (powerProperty == null)
            throw new RuntimeException("Item color does not have the property 'power'");

        RectangleMapObject rectangle = castLevelObject(mapObject);
        controller.getEntities().spawnItemColorProgress(rectangle, Float.parseFloat(powerProperty.toString()));
    }

    private void createDeathZone(MapObject mapObject) {
        RectangleMapObject rectangle = castLevelObject(mapObject);
        controller.getEntities().createDeathZone(rectangle);
    }

    private void createSender(MapObject mapObject) {
        if (mapObject.getProperties().get("map") == null)
            throw new RuntimeException("Item RECEIVER does not have the property 'map'");

        String mapName = mapObject.getProperties().get("map").toString();

        //Verfies if sender's level completed requirements are fulfilled:
        Array<String> missingMaps = new Array<String>();
        boolean cancelCreation = false;

        if (mapObject.getProperties().get("requiredMaps") != null) {
            String[] requiredMaps = mapObject.getProperties().get("requiredMaps").toString().split(";");

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
            return;
        }

        RectangleMapObject rectangle = castLevelObject(mapObject);
        controller.getEntities().createSender(rectangle, mapName);
    }

    private void createSpawn(MapObject mapObject) {
        RectangleMapObject rectangle = castLevelObject(mapObject);

        float spawnInterval = Float.parseFloat(mapObject.getProperties().get("spawnInterval").toString());
        float speedX = Float.parseFloat(mapObject.getProperties().get("speed").toString());

        String graphic = null;
        String ia = null;
        Float speedY = null;
        Float interval = null;

        if (mapObject.getProperties().get("graphic") != null)
            graphic = mapObject.getProperties().get("graphic").toString();

        if (mapObject.getProperties().get("speedY") != null)
            speedY = Float.parseFloat(mapObject.getProperties().get("speedY").toString());

        if (mapObject.getProperties().get("ia") != null)
            ia = mapObject.getProperties().get("ia").toString();

        if (mapObject.getProperties().get("interval") != null)
            interval = Float.parseFloat(mapObject.getProperties().get("interval").toString());

        //Vector2 position = new Vector2(rectangle.getRectangle().x, rectangle.getRectangle().y);
        controller.getEntities().createSpawner(graphic, rectangle, spawnInterval, ia, speedX, speedY, interval);
    }

    private RectangleMapObject castLevelObject(MapObject object) {
        if (!(object instanceof RectangleMapObject))
            throw new RuntimeException("Invalid object body defined on map, EXPECTING RECTANGLE - Map: " + mapName + " object: " + object.getName());

        return (RectangleMapObject) object;
    }
}
