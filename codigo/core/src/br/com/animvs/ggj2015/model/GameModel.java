package br.com.animvs.ggj2015.model;

import java.util.ArrayList;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public final class GameModel {
    private ArrayList<LevelModel> levels;

    public ArrayList<LevelModel> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<LevelModel> levels) {
        this.levels = levels;
    }
}
