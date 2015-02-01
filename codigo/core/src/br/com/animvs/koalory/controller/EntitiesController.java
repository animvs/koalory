package br.com.animvs.koalory.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.entities.engine.ia.IAJump;
import br.com.animvs.koalory.entities.engine.ia.IAStraight;
import br.com.animvs.koalory.entities.game.Color;
import br.com.animvs.koalory.entities.game.DeathZone;
import br.com.animvs.koalory.entities.game.Entity;
import br.com.animvs.koalory.entities.game.Foe;
import br.com.animvs.koalory.entities.game.Item;
import br.com.animvs.koalory.entities.game.Sender;
import br.com.animvs.koalory.entities.game.Spawner;
import br.com.animvs.koalory.entities.game.platforms.Platform;
import br.com.animvs.koalory.entities.game.platforms.StaticPlatform;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public final class EntitiesController extends BaseController {
    //private DelayedRemovalArray<Player> players;
    private DelayedRemovalArray<Item> items;
    private DelayedRemovalArray<Foe> foes;
    private DelayedRemovalArray<Spawner> spawners;
    private DelayedRemovalArray<Platform> platforms;

    public EntitiesController(GameController controller) {
        super(controller);
        //players = new DelayedRemovalArray<Player>();
        items = new DelayedRemovalArray<Item>();
        foes = new DelayedRemovalArray<Foe>();
        spawners = new DelayedRemovalArray<Spawner>();
        platforms = new DelayedRemovalArray<Platform>();
    }

    @Override
    public void initialize() {
    }

    public void restart() {
        /*for (int i = 0; i < Configurations.GAMEPLAY_MAX_PLAYERS; i++)
            players.get(i).restart();*/

        items.begin();
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
        platforms.clear();
    }

    public void spawnItemColorProgress(float x, float y, float colorProgressAmount) {
        Color color = new Color(getController(), colorProgressAmount);

        items.add(color);
        color.setPosition(x + Configurations.CORE_TILE_SIZE / 2f, y + Configurations.CORE_TILE_SIZE / 2f);
        color.initialize();
        Gdx.app.log("ITEM", "Color pickup Spawned: X: " + x + " Y: " + y);
    }

    public void createDeathZone(RectangleMapObject rectangle) {
        DeathZone sender = new DeathZone(getController(), rectangle);
        sender.initialize();
        items.add(sender);
    }

    public void createSender(Vector2 position, String map) {
        Sender sender = new Sender(getController(), map);
        sender.setPosition(position.x, position.y);
        sender.initialize();
        items.add(sender);
    }

    public void createSpawner(String graphic, Vector2 position, float spawnInterval, String ia, float foeSpeedX, Float foeSpeedY, Float interval) {
        spawners.add(new Spawner(getController(), graphic, position, spawnInterval, ia, foeSpeedX, foeSpeedY, interval));
    }

    public void createPlatform(PolylineMapObject line) {
        platforms.add(new StaticPlatform(getController(), line));
    }

    public void createEntityBody(Entity entityOwner) {
        createEntityBody(entityOwner, 1f);
    }

    public void createEntityBody(Entity entityOwner, float scale) {
        PhysicsController.TargetPhysicsParameters bodyParams = new PhysicsController.TargetPhysicsParameters(entityOwner, new Vector2(600f, 550f), 0f,
                BodyDef.BodyType.DynamicBody, Configurations.GAMEPLAY_ENTITY_SIZE_X * scale, Configurations.GAMEPLAY_ENTITY_SIZE_Y * scale, 1f, 0f, false);

        getController().getPhysics().createRetangleBody(bodyParams);
    }

    public void createPlatformBody(Platform platform, int size) {
        PhysicsController.TargetPhysicsParameters bodyParams = new PhysicsController.TargetPhysicsParameters(platform, new Vector2(), 0f,
                BodyDef.BodyType.KinematicBody, Configurations.CORE_TILE_SIZE * size, Configurations.CORE_PLATFORM_SIZE_Y, 1f, 0f, false);

        getController().getPhysics().createRetangleBody(bodyParams);
    }

    public void spawnFoe(String graphic, float x, float y, float speedX, Float speedY, String ia, Float interval) {
        if (ia == null) {
            Foe newFoe = new Foe(getController(), graphic, new IAStraight(getController(), speedX), new Vector2(x, y + Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f));
            foes.add(newFoe);
        } else if (ia.trim().toLowerCase().equals("jumper")) {
            float intervalReal = 0f;

            if (interval != null)
                intervalReal = interval.floatValue();

            Foe newFoe = new Foe(getController(), graphic, new IAJump(getController(), speedX, speedY, intervalReal), new Vector2(x, y + Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f));
            foes.add(newFoe);
        } else
            throw new RuntimeException("AI invalid when loading spawner from TMX: " + ia);
    }

    public void update() {
        for (int i = 0; i < items.size; i++)
            items.get(i).update();

        for (int i = 0; i < foes.size; i++)
            foes.get(i).update();

        for (int i = 0; i < spawners.size; i++)
            spawners.get(i).update();

        for (int i = 0; i < platforms.size; i++)
            platforms.get(i).update();
    }

    public void removeItem(Item item) {
        items.removeValue(item, true);
    }

    public void processMatchEnd() {
        if (getController().getColorRecovered() >= 1f) {
            getController().endMatch();

            if (getController().getLevel().getMapName().equals("castle1")) {
                getController().getProfile().resetProfile();
                getController().getUI().showUIGameWin();
            } else {
                getController().getProfile().registerLevelClear(getController().getLevel().getMapName());
                /*if (getController().getProfile().checkCastleFreed())
                    getController().startMatch("castle1");
                else*/
                    getController().startMatch(null);
            }
        } else if ((getController().getLives() == 0) && (getController().getPlayers().getTotalPlayersInGame() == 0)) {
            getController().endMatch();
            getController().getUI().showUIGameOver();
        }
    }
}
