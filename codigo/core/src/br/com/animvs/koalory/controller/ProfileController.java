package br.com.animvs.koalory.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.model.GameModel;
import br.com.animvs.koalory.model.LevelModel;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public final class ProfileController extends BaseController {

    private GameModel model;

    public GameModel getModel() {
        return model;
    }

    public ProfileController(GameController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
    }

    public GameModel createNew() {
        GameModel newGameModel = new GameModel();

        newGameModel.getLevels().clear();

        LevelModel greenHills = new LevelModel("greenHills1-1", false);
        LevelModel sandPlains = new LevelModel("sandPlains", false);
        LevelModel frostPlateau = new LevelModel("frostPlateau", false);
        LevelModel clastle1 = new LevelModel("clastle1", false);

        newGameModel.getLevels().add(greenHills);

        return newGameModel;
    }

    public void load() {
        FileHandle file = Gdx.files.local(Configurations.CORE_SAVEGAME);

        if (!file.exists())
            createNew();
        else {
            Json json = new Json();
            model = json.fromJson(GameModel.class, file);
        }
    }
}
