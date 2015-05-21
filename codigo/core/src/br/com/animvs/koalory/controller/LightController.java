package br.com.animvs.koalory.controller;

import com.badlogic.gdx.graphics.Color;

import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import br.com.animvs.koalory.Configurations;

/**
 * Created by DALDEGAN on 23/02/2015.
 */
public final class LightController extends BaseController {

    private RayHandler rayHandler;

    private PointLight light;

    public LightController(GameController controller) {
        super(controller);
    }

    @Override
    public void initialize() {
        if (getController().getPhysics().getWorld() == null)
            throw new RuntimeException("Light engine must be initialized after Physic engine");

        rayHandler = new RayHandler(getController().getPhysics().getWorld());

        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);

        rayHandler.setAmbientLight(new Color(0f, 0f, 0f, 0.5f));
        rayHandler.setShadows(true);
        rayHandler.setBlurNum(3);

        //light = new ConeLight(rayHandler, 12, new Color(1f, 1f, 1f, 1f), 600f, 0f, 0f, 45f, 90f);

        //light = new DirectionalLight(rayHandler, 12, new Color(0.5f, 0.5f, 0.5f, 0.5f), -45f);
        light = new PointLight(rayHandler, 24, new Color(1f, 1f, 1f, 1f), 600f, Configurations.RESOLUTION_REAL.x, Configurations.RESOLUTION_REAL.y / 2f);
        //new PointLight(rayHandler, 12, new Color(0f, 1f, 0f, 1f), 600f, Configurations.RESOLUTION_REAL.x + 250f, Configurations.RESOLUTION_REAL.y / 2f);

        light.setXray(false);
        light.setStaticLight(false);
        light.setSoft(true);
        light.setSoftnessLength(0f);
    }

    public void updateLightPosition(float x, float y) {
        light.setPosition(x, y);
    }

    public void render() {
        //light.update();
        rayHandler.setCombinedMatrix(getController().getCamera().getMatrix());
        rayHandler.updateAndRender();
    }

    @Override
    public void dispose() {
        super.dispose();

        rayHandler.dispose();
    }
}
