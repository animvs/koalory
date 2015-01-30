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

        for (int i = 0; i < bodies.size; i++) {
            destroyBody(bodies.get(i));
        }
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

        public TargetPhysicsParameters(PhysicBodyHolder bodyHolder, Vector2 position, float rotation, BodyDef.BodyType bodyType, float width, float height, float density, float restitution, boolean sensor) {
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
        }
    }

    private GameController controller;
    private DelayedRemovalArray<TargetPhysicsParameters> targetToCreate;

    public PhysicsController(GameController controller, boolean debug, float boxToWorld, float worldToBox, Vector2 gravity) {
        super(debug, boxToWorld, worldToBox, gravity, 20, 30, 12, 5);

        this.controller = controller;

        targetToCreate = new DelayedRemovalArray<TargetPhysicsParameters>();
    }

    public void createRetangleBody(TargetPhysicsParameters parameters) {
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
            Body body = AnimvsBodyFactory.createRetangle(controller.getPhysics(),
                    targetToCreate.get(i).position,
                    targetToCreate.get(i).rotation,
                    targetToCreate.get(i).bodyType,
                    targetToCreate.get(i).width,
                    targetToCreate.get(i).height,
                    targetToCreate.get(i).density,
                    targetToCreate.get(i).restitution,
                    targetToCreate.get(i).sensor);

            targetToCreate.get(i).bodyHolder.setBody(body);
            targetToCreate.removeIndex(i);
        }
        targetToCreate.end();

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

                    private boolean isCollisionWall(Fixture fixtureA, Fixture fixtureB) {
                        if (fixtureA.getBody().getUserData() != null && fixtureA.getBody().getUserData() instanceof String) {
                            if (((String) fixtureA.getBody().getUserData()).equals("collision"))
                                return true;
                        } else if (fixtureB.getBody().getUserData() != null && fixtureB.getBody().getUserData() instanceof String) {
                            if (((String) fixtureB.getBody().getUserData()).equals("collision"))
                                return true;
                        }

                        return false;
                    }

                    @Override
                    public void preSolve(Contact contact, Manifold oldManifold) {
                        boolean player = false;
                        if (contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData() instanceof Player)
                            player = true;
                        else if (contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData() instanceof Player)
                            player = true;

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
                                baseItem.collect();
                                baseItem.dispose();
                                return;
                            }

                            //Colliding with enemy:
                            Foe foe = isFoe(contact.getFixtureA(), contact.getFixtureB());
                            if (foe != null && foe.getAlive()) {
                                if (player.getY() - 32f > foe.getY()) {
                                    //if (contact.getWorldManifold().getNormal().y == 1f || contact.getWorldManifold().getNormal().y == -1f) {
                                    //Player has killed the foe by jumping it's head:
                                    Gdx.app.log("KILL", "Player " + controller.getPlayers().getPlayerIndex(player) + " has killed a Koala");
                                    foe.eventDeath(player);
                                } else {
                                    if (Configurations.DEBUG_PLAYER_IMMORTAL)
                                        return;

                                    //Player has been damaged by the foe:
                                    Gdx.app.log("KILL", "Player " + controller.getPlayers().getPlayerIndex(player) + " has been killed by a Koala");
                                    player.eventDeath();
                                }
                                return;
                            }
                        }
                    }
                });
    }
}
