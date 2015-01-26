package br.com.animvs.ggj2015.entities.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import br.com.animvs.ggj2015.controller.GameController;

/**
 * Created by DALDEGAN on 25/01/2015.
 */
public final class Spawner {
    private GameController controller;

    private float spawnInterval;

    private float spawnTimer;
    private float foeSpeed;

    private Vector2 position;

    public Spawner(GameController controller, Vector2 position, float spawnInterval, float foeSpeed) {
        this.controller = controller;
        this.spawnInterval = spawnInterval;
        this.foeSpeed = foeSpeed;
        this.position = position;
    }

    public final void update() {
        spawnTimer += Gdx.graphics.getDeltaTime();

        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;
            controller.getEntities().spawnFoe(position.x, position.y, foeSpeed);
        }
    }
}
