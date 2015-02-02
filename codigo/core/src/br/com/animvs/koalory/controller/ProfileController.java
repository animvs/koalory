package br.com.animvs.koalory.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.model.GameModel;

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
            createNew();
        else
            load();
    }

    public boolean checkLevelClear(String map) {
        return model.getLevelsCompleted().contains(map, false);
    }

    public void registerLevelClear(String level) {
        if (!model.getLevelsCompleted().contains(level, false))
            model.getLevelsCompleted().add(level);

        save();
    }

    public void resetProfile() {
        createNew();
    }

    private void createNew() {
        model = new GameModel();

        model.setVersion(1);
        model.getLevelsCompleted().clear();

        save();
    }

    private void load() {
        FileHandle file = Gdx.files.local(Configurations.CORE_SAVEGAME);

        Json json = new Json();
        try {
            model = json.fromJson(GameModel.class, file);
        } catch (Exception e) {
            Gdx.app.log("PROFILE", "Error reading profile (a new will be created): " + e.getMessage());
        }

        //TODO: Deal game-save version upgrade:
        if (model == null)
            createNew();
        else if (model.getVersion() == 0)
            createNew();
    }

    private void save() {
        FileHandle file = Gdx.files.local(Configurations.CORE_SAVEGAME);

        Json json = new Json();
        json.toJson(model, file);
    }
}
