package br.com.animvs.ggj2015.controller;

/**
 * Created by DALDEGAN on 26/01/2015.
 */
public abstract class BaseController {
    private GameController controller;

    public final GameController getController() {
        return controller;
    }

    public BaseController(GameController controller){
        this.controller = controller;
    }

    public abstract void initialize();
}
