package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import br.com.animvs.koalory.controller.GameController;

/**
 * Created by DALDEGAN on 25/01/2015.
 */
public final class Spawner {
    private GameController controller;

    private float spawnInterval;

    private float spawnTimer;
    private final String ia;
    private float foeSpeedX;

    private Vector2 position;
    private Float foeSpeedY;
    private Float interval;
    private String graphic;

    public Spawner(GameController controller, String graphic, Vector2 position, float spawnInterval, String ia, float foeSpeedX, Float foeSpeedY, Float interval) {
        this.controller = controller;
        this.spawnInterval = spawnInterval;
        this.ia = ia;
        this.foeSpeedX = foeSpeedX;
        this.position = position;
        this.foeSpeedY = foeSpeedY;
        this.interval = interval;
        this.graphic = graphic;
    }

    public final void update() {
        spawnTimer += Gdx.graphics.getDeltaTime();

        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;
            controller.getEntities().spawnFoe(graphic, position.x, position.y, foeSpeedX, foeSpeedY, ia, interval);
        }
    }
}
