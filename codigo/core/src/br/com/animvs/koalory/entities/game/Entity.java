package br.com.animvs.koalory.entities.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;

import br.com.animvs.engine2.graficos.AnimacaoSkeletal;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.controller.GameController;
import br.com.animvs.koalory.controller.PhysicsController;
import br.com.animvs.koalory.entities.physics.PhysicBodyHolder;

public abstract class Entity extends Group implements Disposable, PhysicBodyHolder {
    private GameController controller;
    private Vector2 spawnPosition;
    private Body physicBody;

    private AnimacaoSkeletal graphic;

    private boolean disposed;

    protected BodyDef.BodyType getBodyType() {
        return BodyDef.BodyType.DynamicBody;
    }

    protected PhysicsController.TargetPhysicsParameters.Type getBodyShape() {
        return PhysicsController.TargetPhysicsParameters.Type.RECTANGLE;
    }

    protected float getBodyDensity() {
        return 1f;
    }

    protected float getBodyFriction() {
        return 0.1f;
    }

    protected float getBodyRestitution() {
        return 0.1f;
    }

    protected float getBodyScaleX() {
        return 1f;
    }

    protected float getBodyScaleY() {
        return 1f;
    }

    protected boolean getBodySensor() {
        return false;
    }

    protected boolean getDisposeOnCollect() {
        return true;
    }

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

    private Vector2 graphicOffset;

    protected final boolean getDisposed() {
        return disposed;
    }

    public final Vector2 getGraphicOffset() {
        return graphicOffset;
    }

    @Override
    public float getX() {
        if (getBody() == null)
            return super.getX();

        return getController().getPhysics().toWorld(getBody().getPosition().x);
    }

    @Override
    public float getY() {
        if (getBody() == null)
            return super.getY();

        return getController().getPhysics().toWorld(getBody().getPosition().y);
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
            graphic.setPosicao(x + graphicOffset.x, (y - (Configurations.GAMEPLAY_ENTITY_SIZE_Y * graphic.getEscala().y) / 2f) + graphicOffset.y);
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (graphic != null) {
            /*graphic.flipX(!facingRight);*/

            if (polygonSpriteBatchCache == null)
                polygonSpriteBatchCache = (PolygonSpriteBatch) batch;

            graphic.setPosicao(getX() + graphicOffset.x, getY() + graphicOffset.y);

            batch.end();
            graphic.render(polygonSpriteBatchCache, Gdx.graphics.getDeltaTime());
            batch.begin();
        }

        super.draw(batch, parentAlpha);
    }

    public Entity(GameController controller, Vector2 spawnPosition) {
        if (spawnPosition == null)
            throw new RuntimeException("The parameter 'spawnPosition' must be != NULL");

        this.controller = controller;
        this.spawnPosition = spawnPosition;
        this.graphicOffset = new Vector2();
        controller.getStage().registerEntity(this);
    }

    protected void eventAfterBodyCreated(Body body) {
        physicBody = body;
        physicBody.setUserData(this);

        setPosition(spawnPosition.x, spawnPosition.y);

        //Clean unused resources:
        spawnPosition = null;
    }

    public final void initialize() {
        PhysicsController.TargetPhysicsParameters bodyParameters = createBody();
        bodyParameters.bodyHolder = this;

        //getController().getPhysics().createRetangleBody(bodyParameters);
        getController().getPhysics().createBody(bodyParameters);

        setGraphic(createGraphic());
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

    protected final boolean checkOwnsFixture(Fixture fixture) {
        for (int i = 0; i < getBody().getFixtureList().size; i++) {
            if (getBody().getFixtureList().get(i) == fixture && fixture.getUserData() != null && fixture.getUserData().equals(Configurations.CORE_PLAYER_GROUNDER_USER_DATA))
                return true;
        }

        return false;
    }

    protected final void disposeBody() {
        if (physicBody != null) {
            controller.getPhysics().destroyBody(physicBody);
            physicBody = null;
        }
    }

    protected abstract AnimacaoSkeletal createGraphic();

    private PhysicsController.TargetPhysicsParameters createBody() {
        PhysicsController.TargetPhysicsParameters bodyParams = new PhysicsController.TargetPhysicsParameters(this, new Vector2(), 0f, getBodyType(),
                getBodyShape(), Configurations.CORE_TILE_SIZE * getBodyScaleX(), Configurations.CORE_TILE_SIZE * getBodyScaleY(), getBodyDensity(), getBodyFriction(), getBodyRestitution(), getBodySensor());

        return bodyParams;
    }
}
