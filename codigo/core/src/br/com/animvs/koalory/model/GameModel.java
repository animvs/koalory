package br.com.animvs.koalory.model;

import com.badlogic.gdx.utils.Array;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public final class GameModel {
    private Array<LevelModel> levels;

    public Array<LevelModel> getLevels() {
        return levels;
    }

    public GameModel() {
        this.levels = new Array<LevelModel>();
    }

    public void setLevels(Array<LevelModel> levels) {
        this.levels = levels;
    }
}
