package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.entities.engine.ia.IAStraight;
import br.com.animvs.ggj2015.entities.game.Foe;
import br.com.animvs.ggj2015.entities.game.GGJ15Entity;
import br.com.animvs.ggj2015.entities.game.Item;
import br.com.animvs.ggj2015.entities.game.Player;
import br.com.animvs.ggj2015.entities.game.Spawner;

/**
 * Created by DALDEGAN on 24/01/2015.
 */
public final class EntitiesController {
    private GameController controller;

    private DelayedRemovalArray<Player> players;
    private DelayedRemovalArray<Item> items;
    private DelayedRemovalArray<Foe> foes;
    private DelayedRemovalArray<Spawner> spawners;

    public EntitiesController(GameController controller) {
        this.controller = controller;
        players = new DelayedRemovalArray<Player>();
        items = new DelayedRemovalArray<Item>();
        foes = new DelayedRemovalArray<Foe>();
        spawners = new DelayedRemovalArray<Spawner>();
    }

    public void initialize() {
        for (int i = 0; i < Configurations.GAMEPLAY_MAX_PLAYERS; i++)
            players.add(new Player(controller, i));
    }

    public void restart() {
        for (int i = 0; i < items.size; i++)
            items.get(i).dispose();

        for (int i = 0; i < foes.size; i++)
            foes.get(i).dispose();

        items.clear();
        foes.clear();
        spawners.clear();
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public int getNumberPlayers() {
        int count = 0;
        for (int i = 0; i < players.size; i++)
            if (players.get(i).getAlive())
                count++;
        return count;
    }

    public void spawnItemColorProgress(float x, float y, float colorProgressAmount) {
        Item newItem = new Item(controller, colorProgressAmount);

        items.add(newItem);
        newItem.setPosition(x + Configurations.CORE_TILE_SIZE / 2f, y + Configurations.CORE_TILE_SIZE / 2f);
        Gdx.app.log("ITEM", "Color pickup Spawned: X: " + x + " Y: " + y);
    }

    public void createSpawner(Vector2 position, float spawnInterval, float foeSpeed) {
        Spawner newSpawner = new Spawner(controller, position, spawnInterval, foeSpeed);
        spawners.add(newSpawner);
    }

    public void createEntityBody(GGJ15Entity entityOwner) {
        PhysicsController.TargetPhysicsParameters bodyParams = new PhysicsController.TargetPhysicsParameters(entityOwner, new Vector2(600f, 550f), 0f,
                BodyDef.BodyType.DynamicBody, Configurations.GAMEPLAY_ENTITY_SIZE_X, Configurations.GAMEPLAY_ENTITY_SIZE_Y, 1f, 0.1f, false);

        controller.getPhysics().createRetangleBody(bodyParams);
    }

    public void spawnFoe(float x, float y, float speed) {
        Foe newFoe = new Foe(controller, new IAStraight(controller, speed), new Vector2(x, y + Configurations.GAMEPLAY_ENTITY_SIZE_Y / 2f));
        foes.add(newFoe);
    }

    public void update() {
        for (int i = 0; i < players.size; i++)
            players.get(i).update();

        for (int i = 0; i < foes.size; i++)
            foes.get(i).update();

        for (int i = 0; i < spawners.size; i++)
            spawners.get(i).update();
    }

    public void removeItem(Item item) {
        items.removeValue(item, true);
    }

    public void validateGameOver() {
       /* if (players.size == 0) {
            controller.endMatch();
            controller.getUiController().showUIGameOver();
        }*/

        if (controller.getColorRecovered() >= 1f) {
            controller.endMatch();
            controller.getUiController().showUIGameWin();
        }
    }
}
