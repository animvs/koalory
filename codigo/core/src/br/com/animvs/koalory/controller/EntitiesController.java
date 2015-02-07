package br.com.animvs.koalory.controller;

import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.entities.engine.ia.IABoss;
import br.com.animvs.koalory.entities.engine.ia.IAJump;
import br.com.animvs.koalory.entities.engine.ia.IAStraight;
import br.com.animvs.koalory.entities.game.items.CheckPoint;
import br.com.animvs.koalory.entities.game.items.Color;
import br.com.animvs.koalory.entities.game.items.DeathZone;
import br.com.animvs.koalory.entities.game.items.EndLevel;
import br.com.animvs.koalory.entities.game.mobiles.Foe;
import br.com.animvs.koalory.entities.game.items.Item;
import br.com.animvs.koalory.entities.game.items.Life;
import br.com.animvs.koalory.entities.game.items.Sender;
import br.com.animvs.koalory.entities.game.Spawner;
import br.com.animvs.koalory.entities.game.Weight;
import br.com.animvs.koalory.entities.game.platforms.StaticPlatform;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public final class EntitiesController extends BaseController {

    public EntitiesController(GameController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
    }

    public void restart() {
    }

    public void spawnItemColorProgress(RectangleMapObject rectangleMapObject, float colorProgressAmount) {
        Color color = new Color(getController(), rectangleMapObject, colorProgressAmount);
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
        EndLevel endLevel = new EndLevel(getController(), rectangle);
        endLevel.initialize();

        getController().getStage().registerActor(endLevel);
    }

    public void createLife(RectangleMapObject rectangle) {
        Life life = new Life(getController(), rectangle);
        life.initialize();

        getController().getStage().registerActor(life);
    }

    public void createCheckpoint(RectangleMapObject rectangle) {
        CheckPoint checkpoint = new CheckPoint(getController(), rectangle);
        checkpoint.initialize();

        getController().getStage().registerActor(checkpoint);
    }

    public void createSender(RectangleMapObject rectangle, String map) {
        Sender sender = new Sender(getController(), rectangle, map);
        sender.initialize();

        getController().getStage().registerActor(sender);
    }

    public void createSpawner(String graphic, RectangleMapObject rectangleMapObject, float spawnInterval, String ia, float foeSpeedX, Float foeSpeedY, Float interval) {
        Spawner spawner = new Spawner(getController(), rectangleMapObject, graphic, spawnInterval, ia, foeSpeedX, foeSpeedY, interval);
        spawner.initialize();

        getController().getStage().registerActor(spawner);
    }

    public void createPlatform(PolylineMapObject line) {
        StaticPlatform platform = new StaticPlatform(getController(), line);
        platform.initialize();

        getController().getStage().registerActor(platform);
    }

    public void spawnWeight(Vector2 position, float lifeInterval, float radius) {
        Weight weight = new Weight(getController(), position, lifeInterval, radius);
        weight.initialize();
    }

    public void spawnFoe(String graphic, float physicsScale, float x, float y, float speedX, Float speedY, String ia, Float interval) {
        if (ia == null) {
            Foe newFoe = new Foe(getController(), new Vector2(x, y + Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f), graphic, physicsScale,
                    new IAStraight(getController(), speedX));
            newFoe.initialize();

            getController().getStage().registerActor(newFoe);
        } else if (ia.trim().toLowerCase().equals("jumper")) {
            float intervalReal = 0f;

            if (interval != null)
                intervalReal = interval.floatValue();

            Foe newFoe = new Foe(getController(), new Vector2(x, y + Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f), graphic, physicsScale,
                    new IAJump(getController(), speedX, speedY, intervalReal));
            newFoe.initialize();

            getController().getStage().registerActor(newFoe);
        } else
            throw new RuntimeException("AI invalid when loading spawner from TMX: " + ia);
    }

    public void spawnBoss(Vector2 spawnPosition) {
        final float waitInterval = 5f;
        final int attackTimes = 10;
        final float attackInterval = 0.7f;

        Foe newBoss = new Foe(getController(), spawnPosition, "boss", 2.5f, new IABoss(getController(), waitInterval, attackTimes, attackInterval));
        newBoss.initialize();

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
