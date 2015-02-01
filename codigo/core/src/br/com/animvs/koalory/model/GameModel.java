package br.com.animvs.koalory.model;

import com.badlogic.gdx.utils.Array;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public final class GameModel {
    private int version;
    private Array<String> levelsCompleted;

    public Array<String> getLevelsCompleted() {
        return levelsCompleted;
    }

    public void setLevelsCompleted(Array<String> levelsCompleted) {
        this.levelsCompleted = levelsCompleted;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public GameModel() {
        this.levelsCompleted = new Array<String>();
    }
}
