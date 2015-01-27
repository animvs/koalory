package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.entities.engine.ia.IAStraight;
import br.com.animvs.ggj2015.entities.game.Foe;
import br.com.animvs.ggj2015.entities.game.GGJ15Entity;
import br.com.animvs.ggj2015.entities.game.Item;
import br.com.animvs.ggj2015.entities.game.Spawner;
import br.com.animvs.ggj2015.entities.game.platforms.Platform;
import br.com.animvs.ggj2015.entities.game.platforms.StaticPlatform;

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

        for (int i = 0; i < items.size; i++)
            items.get(i).dispose();

        for (int i = 0; i < foes.size; i++)
            foes.get(i).dispose();

        for (int i = 0; i < platforms.size; i++)
            platforms.get(i).dispose();

        items.clear();
        foes.clear();
        spawners.clear();
        platforms.clear();
    }

    public void spawnItemColorProgress(float x, float y, float colorProgressAmount) {
        Item newItem = new Item(getController(), colorProgressAmount);

        items.add(newItem);
        newItem.setPosition(x + Configurations.CORE_TILE_SIZE / 2f, y + Configurations.CORE_TILE_SIZE / 2f);
        Gdx.app.log("ITEM", "Color pickup Spawned: X: " + x + " Y: " + y);
    }

    public void createSpawner(Vector2 position, float spawnInterval, float foeSpeed) {
        spawners.add(new Spawner(getController(), position, spawnInterval, foeSpeed));
    }

    public void createPlatform(PolylineMapObject line) {
        platforms.add(new StaticPlatform(getController(), line));
    }

    public void createEntityBody(GGJ15Entity entityOwner) {
        createEntityBody(entityOwner, 1f);
    }

    public void createEntityBody(GGJ15Entity entityOwner, float scale) {
        PhysicsController.TargetPhysicsParameters bodyParams = new PhysicsController.TargetPhysicsParameters(entityOwner, new Vector2(600f, 550f), 0f,
                BodyDef.BodyType.DynamicBody, Configurations.GAMEPLAY_ENTITY_SIZE_X * scale, Configurations.GAMEPLAY_ENTITY_SIZE_Y * scale, 1f, 0f, false);

        getController().getPhysics().createRetangleBody(bodyParams);
    }

    public void createPlatformBody(Platform platform, int size) {
        PhysicsController.TargetPhysicsParameters bodyParams = new PhysicsController.TargetPhysicsParameters(platform, new Vector2(), 0f,
                BodyDef.BodyType.KinematicBody, Configurations.CORE_TILE_SIZE * size, 50f, 1f, 0f, false);

        getController().getPhysics().createRetangleBody(bodyParams);
    }

    public void spawnFoe(float x, float y, float speed) {
        Foe newFoe = new Foe(getController(), new IAStraight(getController(), speed), new Vector2(x, y + Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f));
        foes.add(newFoe);
    }

    public void update() {
        for (int i = 0; i < foes.size; i++)
            foes.get(i).update();

        for (int i = 0; i < spawners.size; i++)
            spawners.get(i).update();
    }

    public void removeItem(Item item) {
        items.removeValue(item, true);
    }

    public void processGameWin() {
        if (getController().getColorRecovered() >= 1f) {
            getController().endMatch();
            getController().getUiController().showUIGameWin();
        }
    }
}
