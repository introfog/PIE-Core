/*
   Copyright 2020 Dmitry Chubrick

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.github.introfog.pie.core;

import com.github.introfog.pie.core.collisions.Manifold;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.LinkedList;
import java.util.List;

/**
 * The World is the main class in PIE library.
 * It controls the interaction and updating of the states of all bodies entering the world.
 */
public class World {
    private int collisionSolveIterations;
    private float accumulator;
    private Context context;
    private List<ShapePair> mayBeCollision;
    private List<IShape> shapes;
    private List<Manifold> collisions;

    /**
     * Instantiates a new {@link com.github.introfog.pie.core.World} instance based on
     * {@link com.github.introfog.pie.core.Context} instance.
     *
     * @param context the {@link com.github.introfog.pie.core.Context} instance
     */
    public World(Context context) {
        this.context = new Context(context);
        collisionSolveIterations = 1;
        shapes = new LinkedList<>();
        mayBeCollision = new LinkedList<>();
        collisions = new LinkedList<>();
        this.context.getBroadPhase().setShapes(shapes);
    }

    // TODO Create good JavaDoc
    /**
     * Updating the physical condition of all bodies in the world.
     *
     * The world will be updated after an equal period of time equal to the
     * {@link com.github.introfog.pie.core.Context#getFixedDeltaTime()} value.
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
     * Adds a new shape to the world.
     *
     * @param shape the new shape
     */
    public void addShape(IShape shape) {
        shape.computeAABB();
        shapes.add(shape);
        context.getBroadPhase().addShape(shape);
    }

    /**
     * Gets the shapes in the world.
     *
     * @return the shapes
     */
    public List<IShape> getShapes() {
        return shapes;
    }

    /**
     * Sets the new shapes in the world.
     *
     * @param shapes the new shapes
     */
    public void setShapes(List<IShape> shapes) {
        this.shapes = shapes;
        context.getBroadPhase().setShapes(shapes);
    }

    private void narrowPhase() {
        collisions.clear();
        mayBeCollision.forEach((collision) -> {
            if (collision.first.body.invertMass != 0f || collision.second.body.invertMass != 0f) {
                Manifold manifold = new Manifold(collision.first, collision.second, context);
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
        mayBeCollision = context.getBroadPhase().calculateAabbCollision();

        // Integrate forces
        // Hanna modification Euler's method is used!
        shapes.forEach(this::integrateForces);

        // Narrow phase
        narrowPhase();

        // Solve collisions
        for (int i = 0; i < collisionSolveIterations; i++) {
            collisions.forEach(Manifold::solve);
        }

        // Integrate velocities
        shapes.forEach(this::integrateVelocity);

        // Integrate forces
        // Hanna modification Euler's method is used!
        shapes.forEach(this::integrateForces);

        // Correct positions
        collisions.forEach(Manifold::correctPosition);

        // Clear all forces
        shapes.forEach((shape) -> shape.body.force.set(0f, 0f));
    }

    private void integrateForces(IShape shape) {
        Body body = shape.body;
        if (body.invertMass == 0.0f) {
            return;
        }

        body.velocity.add(body.force, body.invertMass * context.getFixedDeltaTime() * 0.5f);
        body.velocity.add(context.getGravity(), context.getFixedDeltaTime() * 0.5f);
        body.angularVelocity += body.torque * body.invertInertia * context.getFixedDeltaTime() * 0.5f;
    }

    private void integrateVelocity(IShape shape) {
        Body body = shape.body;
        if (body.invertMass == 0.0f) {
            return;
        }

        body.position.add(body.velocity, context.getFixedDeltaTime());

        shape.setOrientation(body.orientation + body.angularVelocity * context.getFixedDeltaTime());
    }
}
