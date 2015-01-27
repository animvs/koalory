package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

    private Vector2 cameraDesiredPosition;
    private Vector2 cameraPositionCache;

    public OrthographicCamera getCamera() {
        return camera;
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
        cameraDesiredPosition = new Vector2();
        cameraPositionCache = new Vector2();
    }

    @Override
    public void initialize() {
    }

    public void update() {
        if (cameraOwner == null)
            return;

        TMP_updateCameraDesiredPosition();

        if (cameraDesiredPosition.x != camera.position.x || cameraDesiredPosition.y != camera.position.y) {
            cameraPositionCache.set(camera.position.x, getController().getStage().getCamera().position.y);

            cameraPositionCache.lerp(cameraDesiredPosition, Gdx.graphics.getDeltaTime() * Configurations.CORE_CAMERA_SPEED_MULTIPLIER);
            camera.position.set(cameraPositionCache.x, cameraPositionCache.y, camera.position.z);
            camera.update();
        }
    }

    private void TMP_updateCameraDesiredPosition() {
        if (getController().getPlayers().getTotalPlayersInGame() == 0)
            return;

        if (getController().getPlayers().getTotalPlayersInGame() == 1) {
            cameraDesiredPosition.set(getController().getPlayers().getPlayer(0).getX(), getController().getPlayers().getPlayer(0).getY());
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

        cameraDesiredPosition.set(averageX, averageY);

        /*stage.getCamera().position.x = entities.getPlayer(0).getX();
        stage.getCamera().position.y = entities.getPlayer(0).getY();
        stage.getCamera().update();*/
    }
}
