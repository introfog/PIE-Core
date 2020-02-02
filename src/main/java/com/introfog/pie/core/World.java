package com.introfog.pie.core;

import com.introfog.pie.core.collisionDetection.BroadPhase;
import com.introfog.pie.core.collisions.Manifold;
import com.introfog.pie.core.math.MathPIE;
import com.introfog.pie.core.shape.Shape;

import java.util.LinkedList;

import javafx.util.Pair;

public class World {
    private float accumulator;
    private LinkedList<Pair<Body, Body>> mayBeCollision;
    private BroadPhase broadPhase;

    public boolean onDebugDraw = false;
    public int iterations = 1;
    public int amountMayBeCollisionBodies = 0;
    public LinkedList<Manifold> collisions;
    public LinkedList<Body> bodies;


    private void narrowPhase() {
        amountMayBeCollisionBodies = mayBeCollision.size();

        collisions.clear();
        mayBeCollision.forEach((collision) -> {
            if (collision.getKey().invertMass != 0f || collision.getValue().invertMass != 0f) {
                Manifold manifold = new Manifold(collision.getKey(), collision.getValue());
                manifold.initializeCollision();
                if (manifold.areBodiesCollision) {
                    collisions.add(manifold);
                }
            }
        });

        mayBeCollision.clear();
    }

    private void step() {
        //broadPhase.bruteForce (mayBeCollision);
        //broadPhase.sweepAndPruneMyRealisation (mayBeCollision);
        //broadPhase.sweepAndPrune (mayBeCollision);
        broadPhase.spatialHashing(mayBeCollision);

        // Integrate forces
        // Hanna modification Euler's method is used!
        bodies.forEach((body) -> integrateForces(body));

        narrowPhase();

        // Solve collisions
        for (int i = 0; i < iterations; i++) {
            collisions.forEach((collision) -> collision.solve());
        }

        // Integrate velocities
        bodies.forEach((body) -> integrateVelocity(body));

        // Integrate forces
        // Hanna modification Euler's method is used!
        bodies.forEach((body) -> integrateForces(body));

        // Correct positions
        collisions.forEach((collision) -> collision.correctPosition());

        // Clear all forces
        bodies.forEach((body) -> body.force.set(0f, 0f));
    }

    private void integrateForces(Body b) {
        if (b.invertMass == 0.0f) {
            return;
        }

        b.velocity.add(b.force, b.invertMass * MathPIE.FIXED_DELTA_TIME * 0.5f);
        b.velocity.add(MathPIE.GRAVITY, MathPIE.FIXED_DELTA_TIME * 0.5f);
        b.angularVelocity += b.torque * b.invertInertia * MathPIE.FIXED_DELTA_TIME * 0.5f;
    }

    private void integrateVelocity(Body b) {
        if (b.invertMass == 0.0f) {
            return;
        }

        b.position.add(b.velocity, MathPIE.FIXED_DELTA_TIME);
        b.orientation += b.angularVelocity * MathPIE.FIXED_DELTA_TIME;
        b.setOrientation(b.orientation);
    }

    private static class WorldHolder {
        private final static World instance = new World();
    }

    private World() {
        bodies = new LinkedList<>();
        mayBeCollision = new LinkedList<>();
        collisions = new LinkedList<>();
        broadPhase = new BroadPhase(bodies);
    }


    public static World getInstance() {
        return WorldHolder.instance;
    }

    public void update(float deltaTime) {
        // TODO добавить линейную интерполяцию

        accumulator += deltaTime;

        // Предотвращение петли смерти
        if (accumulator > MathPIE.DEAD_LOOP_BORDER) {
            accumulator = MathPIE.DEAD_LOOP_BORDER;
        }

        while (accumulator > MathPIE.FIXED_DELTA_TIME) {
            // Обновление физики всегда происходит через равный промежуток времени
            step();
            accumulator -= MathPIE.FIXED_DELTA_TIME;
        }
    }

    public void addShape(Shape shape) {
        bodies.add(shape.body);
        broadPhase.addBody(shape);
    }

    public int getAmountBodies() {
        return bodies.size();
    }
}