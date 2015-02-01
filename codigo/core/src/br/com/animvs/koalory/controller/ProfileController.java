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
        FileHandle file = Gdx.files.local(Configurations.CORE_SAVEGAME);

        if (!file.exists())
            createNew(file);
        else
            load(file);
    }

    public boolean checkCastleFreed() {
        return checkLevelClear("greenHills1-1") && checkLevelClear("sandPlains") && checkLevelClear("frostPlateau");
    }

    private boolean checkLevelClear(String map) {
        for (int i = 0; i < model.getLevels().size; i++) {
            if (model.getLevels().get(i).getMapName().equals(map)) {
                return model.getLevels().get(i).getCompleted();
            }
        }

        throw new RuntimeException("Map not found when checking level clear: " + map);
    }

    public void registerLevelClear(String level) {
        for (int i = 0; i < model.getLevels().size; i++) {
            if (model.getLevels().get(i).getMapName().equals(level)) {
                model.getLevels().get(i).setCompleted(true);
                save();
                return;
            }
        }

        throw new RuntimeException("Map not found when registering level clear: " + level);
    }

    public void resetProfile() {
        createNew(Gdx.files.local(Configurations.CORE_SAVEGAME));
    }

    private void createNew(FileHandle file) {
        model = new GameModel();

        model.getLevels().clear();

        LevelModel greenHills = new LevelModel("greenHills1-1", false);
        LevelModel sandPlains = new LevelModel("sandPlains", false);
        LevelModel frostPlateau = new LevelModel("frostPlateau", false);
        LevelModel castle1 = new LevelModel("castle1", false);

        model.getLevels().add(greenHills);
        model.getLevels().add(sandPlains);
        model.getLevels().add(frostPlateau);
        model.getLevels().add(castle1);

        Json json = new Json();
        json.toJson(model, file);
    }

    private void load(FileHandle file) {
        Json json = new Json();
        model = json.fromJson(GameModel.class, file);
    }

    private void save() {
        FileHandle file = Gdx.files.local(Configurations.CORE_SAVEGAME);

        Json json = new Json();
        json.toJson(model, file);
    }
}
