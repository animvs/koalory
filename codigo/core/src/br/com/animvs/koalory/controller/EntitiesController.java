package br.com.animvs.koalory.controller;

import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.entities.engine.ia.IABoss;
import br.com.animvs.koalory.entities.engine.ia.IAJump;
import br.com.animvs.koalory.entities.engine.ia.IAStraight;
import br.com.animvs.koalory.entities.game.CheckPoint;
import br.com.animvs.koalory.entities.game.Color;
import br.com.animvs.koalory.entities.game.DeathZone;
import br.com.animvs.koalory.entities.game.EndLevel;
import br.com.animvs.koalory.entities.game.Entity;
import br.com.animvs.koalory.entities.game.Foe;
import br.com.animvs.koalory.entities.game.Item;
import br.com.animvs.koalory.entities.game.Life;
import br.com.animvs.koalory.entities.game.Sender;
import br.com.animvs.koalory.entities.game.Spawner;
import br.com.animvs.koalory.entities.game.Weight;
import br.com.animvs.koalory.entities.game.platforms.Platform;
import br.com.animvs.koalory.entities.game.platforms.StaticPlatform;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public final class EntitiesController extends BaseController {
    //private DelayedRemovalArray<Player> players;
    /*private DelayedRemovalArray<Item> items;
    private DelayedRemovalArray<Foe> foes;
    private DelayedRemovalArray<Spawner> spawners;
    private DelayedRemovalArray<Platform> platforms;*/

    public EntitiesController(GameController controller) {
        super(controller);
        //players = new DelayedRemovalArray<Player>();
        /*items = new DelayedRemovalArray<Item>();
        foes = new DelayedRemovalArray<Foe>();
        spawners = new DelayedRemovalArray<Spawner>();
        platforms = new DelayedRemovalArray<Platform>();*/
    }

    @Override
    public void initialize() {
    }

    public void restart() {
        /*for (int i = 0; i < Configurations.GAMEPLAY_MAX_PLAYERS; i++)
            players.get(i).restart();*/

        /*items.begin();
        for (int i = 0; i < items.size; i++)
            items.get(i).dispose();
        items.end();

        foes.begin();
        for (int i = 0; i < foes.size; i++)
            foes.get(i).dispose();
        foes.end();

        platforms.begin();
        for (int i = 0; i < platforms.size; i++)
            platforms.get(i).dispose();
        platforms.end();

        items.clear();
        foes.clear();
        spawners.clear();
        platforms.clear();*/
    }

    public void spawnItemColorProgress(float x, float y, float colorProgressAmount) {
        Color color = new Color(getController(), new Vector2(x + Configurations.CORE_TILE_SIZE / 2f, y + Configurations.CORE_TILE_SIZE / 2f), colorProgressAmount);
        color.initialize();

        getController().getStage().registerActor(color);
        //Gdx.app.log("ITEM", "Color pickup Spawned: X: " + x + " Y: " + y);
    }

    public void createDeathZone(RectangleMapObject rectangle) {
        DeathZone deathZone = new DeathZone(getController(), rectangle);
        deathZone.initialize();

        getController().getStage().registerActor(deathZone);
    }

    public void createEndLevel(RectangleMapObject rectangle) {
        EndLevel endLevel = new EndLevel(getController(), new Vector2(rectangle.getRectangle().x + Configurations.CORE_TILE_SIZE / 2f, rectangle.getRectangle().y + Configurations.CORE_TILE_SIZE / 2f));
        endLevel.initialize();

        getController().getStage().registerActor(endLevel);
    }

    public void createLife(RectangleMapObject rectangle) {
        Life life = new Life(getController(), new Vector2(rectangle.getRectangle().x + Configurations.CORE_TILE_SIZE / 2f, rectangle.getRectangle().y + Configurations.CORE_TILE_SIZE / 2f));
        life.initialize();

        getController().getStage().registerActor(life);
    }

    public void createCheckpoint(RectangleMapObject rectangle) {
        CheckPoint checkpoint = new CheckPoint(getController(), new Vector2(rectangle.getRectangle().x + Configurations.CORE_TILE_SIZE / 2f, rectangle.getRectangle().y + Configurations.CORE_TILE_SIZE / 2f));
        checkpoint.initialize();

        getController().getStage().registerActor(checkpoint);
    }

    public void createSender(RectangleMapObject rectangle, String map) {
        Sender sender = new Sender(getController(), new Vector2(rectangle.getRectangle().x + Configurations.CORE_TILE_SIZE / 2f, rectangle.getRectangle().y + Configurations.CORE_TILE_SIZE / 2f), map);
        sender.initialize();

        getController().getStage().registerActor(sender);
    }

    public void createSpawner(String graphic, Vector2 position, float spawnInterval, String ia, float foeSpeedX, Float foeSpeedY, Float interval) {
        //spawners.add(new Spawner(getController(), graphic, position, spawnInterval, ia, foeSpeedX, foeSpeedY, interval));
        getController().getStage().registerActor(new Spawner(getController(), position, graphic, position, spawnInterval, ia, foeSpeedX, foeSpeedY, interval));
    }

    public void createPlatform(PolylineMapObject line) {
        getController().getStage().registerActor(new StaticPlatform(getController(), new Vector2(), line));
    }

    public void createEntityBody(Entity entityOwner, float scale, PhysicsController.TargetPhysicsParameters.Type type) {
        PhysicsController.TargetPhysicsParameters bodyParams = new PhysicsController.TargetPhysicsParameters(entityOwner, new Vector2(600f, 550f), 0f,
                BodyDef.BodyType.DynamicBody, type, Configurations.GAMEPLAY_ENTITY_SIZE_X * scale, Configurations.GAMEPLAY_ENTITY_SIZE_Y * scale, 1f, 0f, false);

        getController().getPhysics().createBody(bodyParams);
    }

    public void createPlatformBody(Platform platform, int size) {
        PhysicsController.TargetPhysicsParameters bodyParams = new PhysicsController.TargetPhysicsParameters(platform, new Vector2(), 0f,
                BodyDef.BodyType.KinematicBody, PhysicsController.TargetPhysicsParameters.Type.RECTANGLE,
                Configurations.CORE_TILE_SIZE * size, Configurations.CORE_PLATFORM_SIZE_Y, 1f, 0f, false);

        getController().getPhysics().createBody(bodyParams);
    }

    public void spawnWeight(Vector2 position, float lifeInterval, float radius) {
        Weight weight = new Weight(getController(), position, lifeInterval, radius);
        weight.initialize();
    }

    public void spawnFoe(String graphic, float physicsScale, float x, float y, float speedX, Float speedY, String ia, Float interval) {
        if (ia == null) {
            Foe newFoe = new Foe(getController(), new Vector2(x, y + Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f), graphic, physicsScale,
                    new IAStraight(getController(), speedX));

            getController().getStage().registerActor(newFoe);
        } else if (ia.trim().toLowerCase().equals("jumper")) {
            float intervalReal = 0f;

            if (interval != null)
                intervalReal = interval.floatValue();

            Foe newFoe = new Foe(getController(), new Vector2(x, y + Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f), graphic, physicsScale,
                    new IAJump(getController(), speedX, speedY, intervalReal));

            getController().getStage().registerActor(newFoe);
        } else
            throw new RuntimeException("AI invalid when loading spawner from TMX: " + ia);
    }

    public void spawnBoss(Vector2 spawnPosition) {
        final float waitInterval = 5f;
        final int attackTimes = 10;
        final float attackInterval = 0.7f;

        Foe newBoss = new Foe(getController(), spawnPosition, "boss", 2.5f, new IABoss(getController(), waitInterval, attackTimes, attackInterval));
        getController().getStage().registerActor(newBoss);
    }

    /*public void update() {
        for (int i = 0; i < items.size; i++)
            items.get(i).update();

        for (int i = 0; i < foes.size; i++)
            foes.get(i).update();

        for (int i = 0; i < spawners.size; i++)
            spawners.get(i).update();

        for (int i = 0; i < platforms.size; i++)
            platforms.get(i).update();
    }*/

    public void removeItem(Item item) {
        getController().getStage().removeActor(item);
    }

    public void processMatchEnd() {
        if ((getController().getLives() == 0) && (getController().getPlayers().getTotalPlayersInGame() == 0)) {
            getController().endMatch();
            getController().getUI().showUIGameOver();
        }
    }
}
