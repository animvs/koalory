package br.com.animvs.koalory.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.entities.engine.graphics.ParallaxCamera;
import br.com.animvs.koalory.entities.game.Entity;
import br.com.animvs.koalory.entities.game.items.TeleportReceiver;

public final class StageController {
    private GameController controller;
    private ParallaxCamera cameraCache;

    private Stage stage;

    public Camera getCamera() {
        return stage.getCamera();
    }

    public Batch getBatch() {
        return stage.getBatch();
    }

    public void update() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void registerEntity(Actor entity) {
        stage.addActor(entity);
    }

    public void removeEntity(Entity entity) {
        stage.getActors().removeValue(entity, true);
    }

    public Viewport getViewport() {
        if (stage == null)
            return null;

        return stage.getViewport();
    }

    public StageController(GameController controller, ParallaxCamera camera) {
        // stage = new Stage(new ScalingViewport(Scaling.fit,
        // Configurations.RESOLUTION_REAL.x, Configurations.RESOLUTION_REAL.y));
        if (camera == null)
            throw new RuntimeException("The parameter 'camera' must be != NULL");

        this.controller = controller;
        this.cameraCache = camera;
    }

    public void bringToFront(Entity entity) {
        controller.getStage().removeEntity(entity);
        controller.getStage().registerEntity(entity);
    }

    public void registerActor(Actor actor) {
        stage.addActor(actor);
    }

    public void removeActor(Actor actor) {
        stage.getActors().removeValue(actor, true);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void initialize() {
        stage = new Stage(new FitViewport(Configurations.RESOLUTION_REAL.x, Configurations.RESOLUTION_REAL.y), new PolygonSpriteBatch());
        stage.getViewport().setCamera(cameraCache);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //stage.getCamera().position.set(0f, stage.getCamera().position.y, 0f);
        //stage.getCamera().update();
    }

    public TeleportReceiver findTeleportReceiver(String id) {
        for (int i = 0; i < stage.getActors().size; i++) {
            if (stage.getActors().get(i) instanceof TeleportReceiver) {
                TeleportReceiver receiver = (TeleportReceiver) stage.getActors().get(i);

                if (receiver.getID().equals(id))
                    return receiver;
            }
        }

        throw new RuntimeException("Teleport receiver not found - id: " + id);
    }

    public void restart() {
        stage.clear();
    }
}
