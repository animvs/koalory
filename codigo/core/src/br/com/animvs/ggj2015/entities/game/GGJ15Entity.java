package br.com.animvs.ggj2015.entities.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.ggj2015.Configurations;
import br.com.animvs.ggj2015.controller.GameController;
import br.com.animvs.ggj2015.entities.physics.PhysicBodyHolder;

public abstract class GGJ15Entity extends Group implements Disposable, PhysicBodyHolder {
    private GameController controller;
    private Body physicBody;

    private AnimacaoSkeletal graphic;

    private boolean disposed;

    private boolean facingRight;

    protected final GameController getController() {
        return controller;
    }

    public AnimacaoSkeletal getGraphic() {
        return graphic;
    }

    public void setGraphic(AnimacaoSkeletal graphic) {
        this.graphic = graphic;
    }

    private PolygonSpriteBatch polygonSpriteBatchCache;

    protected final boolean getDisposed() {
        return disposed;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        if (physicBody != null)
            physicBody.setTransform(controller.getPhysics().toBox(x), physicBody.getPosition().y, physicBody.getAngle());

        if (graphic != null)
            graphic.setPosicao(x, getY());
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        if (physicBody != null)
            physicBody.setTransform(physicBody.getPosition().x, controller.getPhysics().toBox(y), physicBody.getAngle());

        if (graphic != null)
            graphic.setPosicao(getX(), y - (Configurations.GAMEPLAY_ENTITY_SIZE_Y * graphic.getEscala().y) / 2f);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);

        if (physicBody != null)
            physicBody.setTransform(controller.getPhysics().toBox(x), controller.getPhysics().toBox(y), physicBody.getAngle());

        if (graphic != null)
            graphic.setPosicao(x, y - (Configurations.GAMEPLAY_ENTITY_SIZE_Y * graphic.getEscala().y) / 2f);
    }

    @Override
    public Body getBody() {
        return physicBody;
    }

    @Override
    public void setBody(Body body) {
        if (body == null)
            return;

        if (disposed && body != null) {
            controller.getPhysics().destroyBody(physicBody);
            body = null;
        } else {
            this.physicBody = body;

            //TODO: Set the entity position instead of body ?
            physicBody.setTransform(controller.getPhysics().toBox(getX()), controller.getPhysics().toBox(getY()), getRotation() * MathUtils.degRad);
        }

        /*for (int i = 0; i < physicBody.getFixtureList().size; i++)
            physicBody.getFixtureList().get(i).setUserData(this);*/

        this.eventAfterBodyCreated(body);
    }

    public void update() {
        if (physicBody != null) {
            setVisible(true);

            setX(controller.getPhysics().toWorld(physicBody.getPosition().x));
            setY(controller.getPhysics().toWorld(physicBody.getPosition().y));
            setRotation(physicBody.getAngle() * MathUtils.radDeg);

            if (getBody().getLinearVelocity().x != 0f)
                facingRight = getBody().getLinearVelocity().x > 0f;
        } else
            setVisible(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (polygonSpriteBatchCache == null)
            polygonSpriteBatchCache = (PolygonSpriteBatch) batch;

        if (graphic != null) {
            graphic.flipX(!facingRight);

            batch.end();
            graphic.render(polygonSpriteBatchCache, Gdx.graphics.getDeltaTime());
            batch.begin();
        }

        super.draw(batch, parentAlpha);
    }

    public GGJ15Entity(GameController controller) {
        this.controller = controller;
        controller.getStage().registerEntity(this);
    }

    protected void eventAfterBodyCreated(Body body) {
        physicBody = body;
        physicBody.setUserData(this);
    }

    @Override
    public void dispose() {
        if (disposed)
            return;

        disposed = true;

        controller.getStage().removeEntity(this);
        graphic = null;

        disposeBody();
    }

    protected final void disposeBody() {
        if (physicBody != null) {
            controller.getPhysics().destroyBody(physicBody);
            physicBody = null;
        }
    }
}
