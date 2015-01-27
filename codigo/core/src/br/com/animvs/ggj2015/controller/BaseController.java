package br.com.animvs.ggj2015.controller;

import com.badlogic.gdx.utils.Disposable;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public abstract class BaseController implements Disposable {
    private GameController controller;

    public final GameController getController() {
        return controller;
    }

    public BaseController(GameController controller) {
        this.controller = controller;
    }

    public abstract void initialize();

    public void dispose() {
    }
}
