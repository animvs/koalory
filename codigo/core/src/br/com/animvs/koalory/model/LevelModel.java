package br.com.animvs.koalory.model;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
@Deprecated
public final class LevelModel {
    private String mapName;
    private boolean completed;

    public LevelModel(String mapName, boolean completed) {
        this.mapName = mapName;
        this.completed = completed;
    }

    public LevelModel() {
    }

    public String getMapName() {
        return mapName;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
