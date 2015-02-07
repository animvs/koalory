package br.com.animvs.koalory.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import br.com.animvs.engine2.matematica.Random;
import br.com.animvs.koalory.entities.engine.input.InputProcessor;
import br.com.animvs.koalory.entities.game.Player;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public final class PlayersController extends BaseController {
    private DelayedRemovalArray<Player> playersInGame;

    public Player getPlayer(int index) {
        return playersInGame.get(index);
    }

    public int getTotalPlayersInGame() {
        return playersInGame.size;
    }

    private Array<com.badlogic.gdx.graphics.Color> playerColors;
    private int nextPlayerColorIndex;

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

        String skinName = Random.randomBoolean() ? "blue" : "green";
        nextPlayerColorIndex++;

        if (nextPlayerColorIndex == playerColors.size)
            nextPlayerColorIndex = 0;

        Player newPLayer = new Player(getController(), skinName, inputMapper, playerColors.get(nextPlayerColorIndex));
        newPLayer.initialize();

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
        playersInGame = new DelayedRemovalArray<Player>();

        float minColor = 0.7f;
        float maxColor = 1f;

        playerColors = new Array<Color>();
        playerColors.add(new Color(maxColor, minColor, minColor, 1f));
        playerColors.add(new Color(minColor, maxColor, minColor, 1f));
        playerColors.add(new Color(minColor, minColor, maxColor, 1f));

        float secondRowSaturation = 0.3f;

        playerColors.add(new Color(maxColor * secondRowSaturation, minColor * secondRowSaturation, minColor * secondRowSaturation, 1f));
        playerColors.add(new Color(minColor * secondRowSaturation, maxColor * secondRowSaturation, minColor * secondRowSaturation, 1f));
        playerColors.add(new Color(minColor * secondRowSaturation, minColor * secondRowSaturation, maxColor * secondRowSaturation, 1f));
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
                        getController().getPlayers().spawnPlayer(getController().getInput().getInputMappers().get(i));
                        Gdx.app.log("PLAYER", "Player " + getController().getPlayers().getTotalPlayersInGame() + " has been spawned");
                    } else
                        Gdx.app.log("PLAYER", "Player spawn blocked: No lives left");
                } else
                    input.getPlayerOwner().tryJump();
            }
        }
    }

    public void update() {
        if (!getController().getInGame())
            return;

        if (getController().getInGame())
            TMP_checkActionButtonRequestInGameMode();
    }

    public void restart() {
        playersInGame.begin();
        for (int i = 0; i < playersInGame.size; i++)
            playersInGame.get(i).dispose();
        playersInGame.end();

        playersInGame.clear();
    }

    @Override
    public void initialize() {
    }
}
