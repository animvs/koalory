package br.com.animvs.koalory.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import br.com.animvs.engine2.physics.AnimvsBodyFactory;
import br.com.animvs.engine2.physics.AnimvsPhysicsController;
import br.com.animvs.koalory.Configurations;
import br.com.animvs.koalory.entities.game.DeathZone;
import br.com.animvs.koalory.entities.game.Foe;
import br.com.animvs.koalory.entities.game.Item;
import br.com.animvs.koalory.entities.game.Player;
import br.com.animvs.koalory.entities.physics.PhysicBodyHolder;

/**
 * Created by DALDEGAN on 23/01/2015.
 */
public final class PhysicsController extends AnimvsPhysicsController {

    public void restart() {
        Array<Body> bodies = new Array<Body>();
        getWorld().getBodies(bodies);

        for (int i = 0; i < bodies.size; i++)
            destroyBody(bodies.get(i));
    }

    public static class TargetPhysicsParameters {
        public PhysicBodyHolder bodyHolder;
        public Vector2 position;
        public float rotation;
        public BodyDef.BodyType bodyType;
        public float width;
        public float height;
        public float density;
        public float restitution;
        public boolean sensor;
        public boolean rectangle;

        public TargetPhysicsParameters(PhysicBodyHolder bodyHolder, Vector2 position, float rotation, BodyDef.BodyType bodyType, boolean rectangle, float width, float height, float density, float restitution, boolean sensor) {
            if (bodyHolder == null)
                throw new RuntimeException("The parameter 'bodyHolder' must be != null");

            this.bodyHolder = bodyHolder;
            this.position = position;
            this.rotation = rotation;
            this.bodyType = bodyType;
            this.width = width;
            this.height = height;
            this.density = density;
            this.restitution = restitution;
            this.sensor = sensor;
            this.rectangle = rectangle;
        }
    }

    private GameController controller;
    private DelayedRemovalArray<TargetPhysicsParameters> targetToCreate;

    public PhysicsController(GameController controller, boolean debug, float boxToWorld, float worldToBox, Vector2 gravity, int minFPS, int maxFPS) {
        super(debug, boxToWorld, worldToBox, gravity, minFPS, maxFPS, 8, 3);

        this.controller = controller;

        targetToCreate = new DelayedRemovalArray<TargetPhysicsParameters>();
    }

    @Override
    public void initialize() {
        super.initialize();
        getWorld().setContactListener(
                new ContactListener() {
                    private Item isItem(Fixture fixtureA, Fixture fixtureB) {
                        if (fixtureA.getBody().getUserData() != null && fixtureA.getBody().getUserData() instanceof Item)
                            return (Item) fixtureA.getBody().getUserData();
                        else if (fixtureB.getBody().getUserData() != null && fixtureB.getBody().getUserData() instanceof Item)
                            return (Item) fixtureB.getBody().getUserData();

                        return null;
                    }

                    private Player isPlayer(Fixture fixtureA, Fixture fixtureB) {
                        if (fixtureA.getBody().getUserData() != null && fixtureA.getBody().getUserData() instanceof Player)
                            return (Player) fixtureA.getBody().getUserData();
                        else if (fixtureB.getBody().getUserData() != null && fixtureB.getBody().getUserData() instanceof Player)
                            return (Player) fixtureB.getBody().getUserData();

                        return null;
                    }

                    private Foe isFoe(Fixture fixtureA, Fixture fixtureB) {
                        if (fixtureA.getBody().getUserData() != null && fixtureA.getBody().getUserData() instanceof Foe)
                            return (Foe) fixtureA.getBody().getUserData();
                        else if (fixtureB.getBody().getUserData() != null && fixtureB.getBody().getUserData() instanceof Foe)
                            return (Foe) fixtureB.getBody().getUserData();

                        return null;
                    }

                    private DeathZone isDeathZone(Fixture fixtureA, Fixture fixtureB) {
                        if (fixtureA.getBody().getUserData() != null && fixtureA.getBody().getUserData() instanceof DeathZone)
                            return (DeathZone) fixtureA.getBody().getUserData();
                        else if (fixtureB.getBody().getUserData() != null && fixtureB.getBody().getUserData() instanceof DeathZone)
                            return (DeathZone) fixtureB.getBody().getUserData();

                        return null;
                    }

                    /*private boolean isCollisionWall(Fixture fixtureA, Fixture fixtureB) {
                        if (fixtureA.getBody().getUserData() != null && fixtureA.getBody().getUserData() instanceof String) {
                            if (((String) fixtureA.getBody().getUserData()).equals("collision"))
                                return true;
                        } else if (fixtureB.getBody().getUserData() != null && fixtureB.getBody().getUserData() instanceof String) {
                            if (((String) fixtureB.getBody().getUserData()).equals("collision"))
                                return true;
                        }

                        return false;
                    }*/

                    @Override
                    public void preSolve(Contact contact, Manifold oldManifold) {
                        /*boolean player = false;
                        if (contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData() instanceof Player)
                            player = true;
                        else if (contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData() instanceof Player)
                            player = true;*/

                        /*if (player && contact.getWorldManifold().getNormal().y > 0.85f)
                            isPlayer(contact.getFixtureA(), contact.getFixtureB()).endJump();*/

                        /*boolean player = false;
                        boolean wall = false;

                        if (contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData() instanceof Player)
                            player = true;
                        else if (contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData() instanceof Player)
                            player = true;

                        if (contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData() instanceof String)
                            wall = true;
                        else if (contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData() instanceof String)
                            wall = true;

                        //Landed with wall:
                        if (player && wall && contact.getWorldManifold().getNormal().y >= 0.85f) {
                            Gdx.app.log("JUMP", "Jump ended");
                            isPlayer(contact.getFixtureA(), contact.getFixtureB()).endJump();
                        }*/
                    }

                    @Override
                    public void postSolve(Contact contact, ContactImpulse impulse) {
                    }

                    @Override
                    public void endContact(Contact contact) {
                    }

                    @Override
                    public void beginContact(Contact contact) {
                        Player player = isPlayer(contact.getFixtureA(), contact.getFixtureB());

                        if (player != null) {
                            //Collecting item:
                            Item baseItem = isItem(contact.getFixtureA(), contact.getFixtureB());
                            if (baseItem != null) {
                                baseItem.collect(player);
                                return;
                            }

                            //Colliding with enemy:
                            Foe foe = isFoe(contact.getFixtureA(), contact.getFixtureB());
                            if (foe != null && foe.getAlive()) {
                                if (player.getY() - Configurations.CORE_TILE_SIZE / 2f > foe.getY()) {
                                    //if (contact.getWorldManifold().getNormal().y == 1f || contact.getWorldManifold().getNormal().y == -1f) {
                                    //Player has killed the foe by jumping it's head:

                                    int playerIndex = -1;
                                    try {
                                        playerIndex = controller.getPlayers().getPlayerIndex(player);
                                    } catch (Exception e) {
                                    }

                                    Gdx.app.log("KILL", "Player " + (playerIndex == -1 ? "UNKNOWN" : String.valueOf(playerIndex)) + " has killed a Koala");
                                    foe.eventDeath(player);
                                } else {
                                    int playerIndex = -1;
                                    if (Configurations.DEBUG_PLAYER_IMMORTAL)
                                        return;

                                    try {
                                        playerIndex = controller.getPlayers().getPlayerIndex(player);
                                    } catch (Exception e) {
                                    }

                                    //Player has been damaged by the foe:
                                    Gdx.app.log("KILL", "Player " + (playerIndex == -1 ? "UNKNOWN" : String.valueOf(playerIndex)) + " has killed a Koala");
                                    player.eventDeath();
                                }
                                return;
                            }
                        } else {
                            Foe foe = isFoe(contact.getFixtureA(), contact.getFixtureB());

                            if (foe != null) {
                                DeathZone deathZone = isDeathZone(contact.getFixtureA(), contact.getFixtureB());

                                if (deathZone != null && deathZone.getKillsIA())
                                    foe.eventDeath(null);
                            }
                        }
                    }
                });
    }

    public void createBody(TargetPhysicsParameters parameters) {
        targetToCreate.add(parameters);
    }

    @Override
    protected void eventAfterUpdate() {
        super.eventAfterUpdate();

        createBodies();
    }

    private void createBodies() {
        targetToCreate.begin();
        for (int i = 0; i < targetToCreate.size; i++) {
            //Gdx.app.log("PHYSICS", "Creating body ...");

            Body body;

            if (targetToCreate.get(i).rectangle)
                body = createRectangleBody(targetToCreate.get(i));
            else
                body = createPlayerBody(targetToCreate.get(i));

            targetToCreate.get(i).bodyHolder.setBody(body);
            targetToCreate.removeIndex(i);
        }
        targetToCreate.end();
    }

    private Body createTriangleBody(TargetPhysicsParameters parameters) {
        Body body = AnimvsBodyFactory.createTriangle(controller.getPhysics(),
                parameters.position,
                parameters.rotation,
                parameters.bodyType,
                parameters.width,
                parameters.height,
                parameters.density,
                parameters.restitution,
                parameters.sensor);

        return body;
    }

    private Body createPlayerBody(TargetPhysicsParameters parameters) {
        Vector2[] vertices = new Vector2[5];
        vertices[0] = new Vector2(0f * parameters.width, -0.5f * parameters.height);
        vertices[1] = new Vector2(0.5f * parameters.width, 0f * parameters.height);
        //vertices[2] = new Vector2(0.25f * parameters.width, 0.4f * parameters.height);

        vertices[2] = new Vector2(0.125f * parameters.width, 0.5f * parameters.height);
        vertices[3] = new Vector2(-0.125f * parameters.width, 0.5f * parameters.height);

        //vertices[5] = new Vector2(-0.25f * parameters.width, 0.4f * parameters.height);
        vertices[4] = new Vector2(-0.5f * parameters.width, 0f * parameters.height);

        return AnimvsBodyFactory.createByVertex(this, parameters.position, parameters.rotation, parameters.bodyType, vertices,
                parameters.density * 1.8f, parameters.restitution, parameters.width, parameters.height, parameters.sensor);
    }

    private Body createRectangleBody(TargetPhysicsParameters parameters) {
        Body body = AnimvsBodyFactory.createRetangle(controller.getPhysics(),
                parameters.position,
                parameters.rotation,
                parameters.bodyType,
                parameters.width,
                parameters.height,
                parameters.density,
                parameters.restitution,
                parameters.sensor);

        return body;
    }
}
