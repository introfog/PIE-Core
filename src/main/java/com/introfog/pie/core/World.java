package com.introfog.pie.core;

import com.introfog.pie.core.collisions.broadphase.BroadPhase;
import com.introfog.pie.core.collisions.Manifold;
import com.introfog.pie.core.shape.IShape;

import java.util.LinkedList;
import java.util.List;

import javafx.util.Pair;

/**
 * The World is the main class in PIE library.
 * It controls the interaction and updating of the states of all bodies entering the world.
 */
public class World {
    private int collisionSolveIterations;
    private float accumulator;
    private Context context;
    private BroadPhase broadPhase;
    private List<Pair<Body, Body>> mayBeCollision;
    private List<Body> bodies;
    private List<Manifold> collisions;

    /**
     * Instantiates a new {@link World} instance.
     */
    public World(Context context) {
        this.context = new Context(context);
        collisionSolveIterations = 1;
        bodies = new LinkedList<>();
        mayBeCollision = new LinkedList<>();
        collisions = new LinkedList<>();
        broadPhase = new BroadPhase(bodies);
    }

    // TODO Create good JavaDoc
    /**
     * Updating the physical condition of all bodies in the world.
     *
     * The world will be updated after an equal period of time equal to the {@link Context#getFixedDeltaTime()} value.
     *
     * @param deltaTime the time elapsed since the last method call
     */
    public void update(float deltaTime) {
        accumulator += deltaTime;

        // Prevention of the death loop (when step takes more time than there is for one step,
        // and the engine starts to slow down more and more).
        if (accumulator > context.getDeadLoopBorder()) {
            accumulator = context.getDeadLoopBorder();
        }

        while (accumulator > context.getFixedDeltaTime()) {
            // Physics update always occurs after an equal period of time.
            step();
            accumulator -= context.getFixedDeltaTime();
        }
    }

    /**
     * Adds a new body to the world.
     *
     * @param shape the new shape
     */
    public void addShape(IShape shape) {
        bodies.add(shape.body);
        broadPhase.addBody(shape);
    }

    /**
     * Sets the number of collision solve iterations.
     *
     * @param collisionSolveIterations the number of iteration
     */
    public void setCollisionSolveIterations(int collisionSolveIterations) {
        this.collisionSolveIterations = collisionSolveIterations;
    }

    /**
     * The method returns collisions from the last run of the {@link #update} method.
     * Each {@link #update} call clears collisions.
     *
     * @return the current collisions
     */
    public List<Manifold> getCollisions() {
        return collisions;
    }

    /**
     * Gets the bodies in the world.
     *
     * @return the bodies
     */
    public List<Body> getBodies() {
        return bodies;
    }

    private void narrowPhase() {
        collisions.clear();
        mayBeCollision.forEach((collision) -> {
            if (collision.getKey().invertMass != 0f || collision.getValue().invertMass != 0f) {
                Manifold manifold = new Manifold(collision.getKey(), collision.getValue(), context);
                manifold.initializeCollision();
                if (manifold.areBodiesCollision) {
                    collisions.add(manifold);
                }
            }
        });

        mayBeCollision.clear();
    }

    private void step() {
        // Broad phase
        //broadPhase.bruteForce (mayBeCollision);
        //broadPhase.sweepAndPruneMyRealisation (mayBeCollision);
        //broadPhase.sweepAndPrune (mayBeCollision);
        broadPhase.spatialHashing(mayBeCollision);

        // Integrate forces
        // Hanna modification Euler's method is used!
        bodies.forEach(this::integrateForces);

        // Narrow phase
        narrowPhase();

        // Solve collisions
        for (int i = 0; i < collisionSolveIterations; i++) {
            collisions.forEach(Manifold::solve);
        }

        // Integrate velocities
        bodies.forEach(this::integrateVelocity);

        // Integrate forces
        // Hanna modification Euler's method is used!
        bodies.forEach(this::integrateForces);

        // Correct positions
        collisions.forEach(Manifold::correctPosition);

        // Clear all forces
        bodies.forEach((body) -> body.force.set(0f, 0f));
    }

    private void integrateForces(Body b) {
        if (b.invertMass == 0.0f) {
            return;
        }

        b.velocity.add(b.force, b.invertMass * context.getFixedDeltaTime() * 0.5f);
        b.velocity.add(context.getGravity(), context.getFixedDeltaTime() * 0.5f);
        b.angularVelocity += b.torque * b.invertInertia * context.getFixedDeltaTime() * 0.5f;
    }

    private void integrateVelocity(Body b) {
        if (b.invertMass == 0.0f) {
            return;
        }

        b.position.add(b.velocity, context.getFixedDeltaTime());
        b.orientation += b.angularVelocity * context.getFixedDeltaTime();
        b.setOrientation(b.orientation);
    }
}