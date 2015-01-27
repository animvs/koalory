package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.entities.engine.graphics.ParallaxCamera;
import br.com.animvs.ggj2015.entities.game.Player;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public final class CameraController extends BaseController {
    private ParallaxCamera camera;
    private Player cameraOwner;

    private float desiredZoom;
    private Vector2 desiredPosition;
    private Vector2 positionCache;

    public float getZoom() {
        return camera.zoom;
    }

    public void setCameraOwner(Player cameraOwner) {
        this.cameraOwner = cameraOwner;
    }

    public Player getCameraOwner() {
        return cameraOwner;
    }

    public CameraController(GameController controller, ParallaxCamera camera) {
        super(controller);
        this.camera = camera;
        desiredPosition = new Vector2();
        positionCache = new Vector2();
    }

    public Matrix4 calculateParallaxMatrix() {
        return camera.calculateParallaxMatrix(1f, 1f);
    }

    @Override
    public void initialize() {
    }

    public void update() {
        if (cameraOwner == null)
            return;

        updateDesired();

        boolean needUpdate = false;

        //Update camera position:
        if (desiredPosition.x != camera.position.x || desiredPosition.y != camera.position.y) {
            positionCache.set(camera.position.x, getController().getStage().getCamera().position.y);

            positionCache.lerp(desiredPosition, Gdx.graphics.getDeltaTime() * Configurations.CORE_CAMERA_SPEED_MULTIPLIER);
            camera.position.set(positionCache.x, positionCache.y, camera.position.z);
            needUpdate = true;
        }

        //Update camera zoom:
        if (desiredZoom != camera.zoom) {
            needUpdate = true;
            camera.zoom = MathUtils.lerp(camera.zoom, desiredZoom, Gdx.graphics.getDeltaTime() * Configurations.CORE_CAMERA_SPEED_MULTIPLIER);
        }

        if (needUpdate)
            camera.update();
    }

    private void updateDesired() {
        if (getController().getPlayers().getTotalPlayersInGame() == 0) {
            desiredZoom = 1f;
            return;
        }

        if (getController().getPlayers().getTotalPlayersInGame() == 1) {
            desiredZoom = 1f;
            desiredPosition.set(getController().getPlayers().getPlayer(0).getX(), getController().getPlayers().getPlayer(0).getY());
            return;
        }

        Player playerLeft = null;
        Player playerRight = null;

        Player playerTop = null;
        Player playerBottom = null;

        PlayersController playersController = getController().getPlayers();
        for (int i = 0; i < playersController.getTotalPlayersInGame(); i++) {
            if (playerLeft == null)
                playerLeft = playersController.getPlayer(i);

            if (playerRight == null)
                playerRight = playersController.getPlayer(i);

            if (playerTop == null)
                playerTop = playersController.getPlayer(i);

            if (playerBottom == null)
                playerBottom = playersController.getPlayer(i);

            //Find farther left player:
            if (playersController.getPlayer(i).getX() < playerLeft.getX())
                playerLeft = playersController.getPlayer(i);

            //Find farther left right:
            if (playersController.getPlayer(i).getX() > playerRight.getX())
                playerRight = playersController.getPlayer(i);

            //Find farther top player:
            if (playersController.getPlayer(i).getY() > playerTop.getY())
                playerTop = playersController.getPlayer(i);

            //Find farther bottom right:
            if (playersController.getPlayer(i).getY() < playerBottom.getY())
                playerBottom = playersController.getPlayer(i);
        }

        float averageX = playerLeft.getX() + (playerRight.getX() - playerLeft.getX()) / 2f;
        float averageY = playerLeft.getY() + (playerRight.getY() - playerLeft.getY()) / 2f;

        desiredPosition.set(averageX, averageY);

        updateDesiredZoom(playerLeft, playerRight, playerTop, playerBottom);
    }

    private void updateDesiredZoom(Player playerLeft, Player playerRight, Player playerTop, Player playerBottom) {
        float coverageX = playerRight.getX() - playerLeft.getX();
        float coverageY = playerTop.getX() - playerBottom.getX();

        float zoomNeededX = 1f;
        float zoomNeededY = 1f;

        zoomNeededX = coverageX / Configurations.RESOLUTION_REAL.x * 1.5f;
        zoomNeededY = coverageY / Configurations.RESOLUTION_REAL.y * 1.5f;

        if (zoomNeededX > zoomNeededY)
            desiredZoom = zoomNeededX;
        else
            desiredZoom = zoomNeededY;

        desiredZoom = MathUtils.clamp(desiredZoom, 1f, Configurations.GAMEPLAY_ZOOM_MAX);
    }
}
