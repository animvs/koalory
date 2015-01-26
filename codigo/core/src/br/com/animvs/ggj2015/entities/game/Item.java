package br.com.animvs.ggj2015.entities.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.engine2.graficos.loaders.AnimacaoSkeletalData;
import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.controller.GameController;
import br.com.animvs.ggj2015.controller.LoadController;
import br.com.animvs.ggj2015.controller.PhysicsController;

/**
 * Created by ANSCHAU on 24/01/2015.
 */
public class Item extends GGJ15Entity {

    private float colorRecovered;

    private GameController controller;

    public Item(GameController controller, float colorRecovered) {
        super(controller);
        this.colorRecovered = colorRecovered;
        this.controller = controller;

        float tileSize = Configurations.CORE_TILE_SIZE;

        PhysicsController.TargetPhysicsParameters bodyParams = new PhysicsController.TargetPhysicsParameters(this, new Vector2(), 0f, BodyDef.BodyType.KinematicBody, tileSize, tileSize, 1f, 0.1f, true);
        bodyParams.bodyHolder = this;
        controller.getPhysics().createRetangleBody(bodyParams);

        AnimacaoSkeletal graphic = new AnimacaoSkeletal(controller.getLoad().get(LoadController.SKELETON_COLOR_PICKUP, AnimacaoSkeletalData.class));
        graphic.getColor().set(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);
        setGraphic(graphic);
    }

    public void collect() {
        controller.addColorRecovered(colorRecovered);
        controller.getEntities().validateGameOver();
        controller.getUiController().castValueColors();
    }

    @Override
    public void dispose() {
        controller.getEntities().removeItem(this);

        super.dispose();
    }
}