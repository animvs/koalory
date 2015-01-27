package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import br.com.animvs.ggj2015.entities.engine.input.InputProcessor;
import br.com.animvs.ggj2015.entities.game.Player;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public final class PlayersController extends BaseController {
    private Array<Player> playersInGame;

    public Player getPlayer(int index) {
        return playersInGame.get(index);
    }

    public int getTotalPlayersInGame() {
        return playersInGame.size;
    }

    public int getPlayerIndex(Player player) {
        int playerIndex = playersInGame.indexOf(player, true);

        if (playerIndex == -1)
            throw new RuntimeException("Player not found when finding it's index");

        return playerIndex;
    }

    public Player spawnPlayer(InputProcessor inputMapper) {
        if (getController().getLives() == 0)
            throw new RuntimeException("Trying to spawn a Player with NO MORE LIVES left");

        getController().subtractLife();

        String skinName = MathUtils.randomBoolean() ? "blue" : "green";

        Player newPLayer = new Player(getController(), skinName, inputMapper);
        playersInGame.add(newPLayer);
        inputMapper.setPlayer(newPLayer);

        if (playersInGame.size == 1)
            getController().getCamera().setCameraOwner(newPLayer);

        return newPLayer;
    }

    /*public int getPlayerIndex(Player player) {
        return playersInGame.indexOf(player, true);
    }*/

    public PlayersController(GameController controller) {
        super(controller);
        playersInGame = new Array<Player>();
    }

    public void unregisterPlayer(Player player) {
        playersInGame.removeValue(player, true);

        if (getController().getCamera().getCameraOwner() == player) {
            getController().getCamera().setCameraOwner(null);

            //Since the owner of the camera is no longer, try to find a new one:
            if (playersInGame.size > 0) {
                int newRandomPlayerID = MathUtils.random(playersInGame.size - 1);
                getController().getCamera().setCameraOwner(playersInGame.get(newRandomPlayerID));
            }
        }
    }

    /**
     * TODO: Game Modes
     */
    private void TMP_checkActionButtonRequestInGameMode() {
        for (int i = 0; i < getController().getInput().getInputMappers().size; i++) {
            InputProcessor input = getController().getInput().getInputMappers().get(i);
            if (input.getActionPressed()) {
                if (input.getPlayerOwner() == null) {
                    if (getController().getLives() > 0) {
                        Player joiningPlayer = getController().getPlayers().spawnPlayer(getController().getInput().getInputMappers().get(i));
                        Gdx.app.log("PLAYER", "Player " + joiningPlayer + "has been spawned");
                    } else
                        Gdx.app.log("PLAYER","Player spawn blocked: No lives left");
                } else
                    input.getPlayerOwner().tryJump();
            }
        }
    }

    public void update() {
        if (!getController().getInGame())
            return;

        for (int i = 0; i < playersInGame.size; i++)
            playersInGame.get(i).update();

        if (getController().getInGame())
            TMP_checkActionButtonRequestInGameMode();
    }

    public void restart() {
        for (int i = 0; i < playersInGame.size; i++)
            playersInGame.get(i).dispose();

        playersInGame.clear();
    }

    @Override
    public void initialize() {

    }
}
