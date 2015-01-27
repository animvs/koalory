package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.entities.engine.graphics.ParallaxCamera;
import br.com.animvs.ggj2015.entities.game.GGJ15Entity;

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

    public void removeEntity(GGJ15Entity entity) {
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

    public void bringToFront(GGJ15Entity entity) {
        //TODO: Apparently not working. Entities aren't added to stage directly anymore, instead, they are added to row groups:
        controller.getStage().removeEntity(entity);
        controller.getStage().registerEntity(entity);
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
}
