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
import com.github.introfog.pie.core.collisions.narrowphase.IShapeCollisionHandler;
import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.core.shape.Body;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.ShapePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The World is the main class in Pie library.
 * It controls the interaction and updating of the states of all bodies entering the world.
 */
public final class World {
    private float accumulator;
    private final Context context;
    private Set<ShapePair> mayBeCollision;
    private Set<IShape> shapes;
    private final List<Manifold> manifolds;

    /**
     * Instantiates a new {@link World} instance based on {@link WorldProperties}.
     *
     *
     * @param worldProperties the {@link WorldProperties} instance
     */
    public World(WorldProperties worldProperties) {
        this.context = new Context(worldProperties);
        this.shapes = new HashSet<>();
        this.mayBeCollision = new HashSet<>();
        this.manifolds = new ArrayList<>();
    }

    /**
     * Updating the physical condition of all shapes in the world.
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
     * The method returns the list of {@link Manifold} from the last run of the {@link #update} method.
     * Each {@link #update} call clears this list.
     *
     * @return the current list of manifolds
     */
    public List<Manifold> getManifolds() {
        return manifolds;
    }

    /**
     * The method returns the collision set of {@link ShapePair} from the last run of the {@link #update} method.
     * Each {@link #update} call clears this set.
     *
     * @return the current collision set of shape pair
     */
    public Set<ShapePair> getCollisions() {
        return getManifolds().stream().map(m -> new ShapePair(m.aShape, m.bShape)).collect(Collectors.toSet());
    }

    /**
     * Adds a new shape to the world.
     *
     * @param shape the new shape
     */
    public void addShape(IShape shape) {
        shapes.add(shape);
        context.getBroadPhaseMethod().addShape(shape);
    }

    /**
     * Sets the new shapes in the world.
     *
     * @param shapes the new shapes
     */
    public void setShapes(Set<IShape> shapes) {
        this.shapes = new HashSet<>(shapes);
        context.getBroadPhaseMethod().setShapes(shapes);
    }

    /**
     * Removes shape from the world.
     *
     * @param shape the shape to be removed from this world, if present
     * @return {@code true} if this world contained the specified shape
     */
    public boolean remove(IShape shape) {
        context.getBroadPhaseMethod().remove(shape);
        return shapes.remove(shape);
    }

    /**
     * Clears all shapes from the world.
     */
    public void clear() {
        context.getBroadPhaseMethod().clear();
        shapes.clear();
    }

    /**
     * Gets the shapes in the world.
     *
     * @return the unmodifiable set of shapes in the world
     */
    public Set<IShape> getUnmodifiableShapes() {
        return Collections.unmodifiableSet(shapes);
    }

    private void step() {
        // Broad phase
        mayBeCollision = context.getBroadPhaseMethod().calculateAabbCollisions();

        // Integrate forces
        // Hanna modification Euler's method is used!
        shapes.forEach(this::integrateForces);

        // Narrow phase
        manifolds.clear();
        for (final ShapePair pair : mayBeCollision) {
            if (!MathPie.areEqual(pair.getFirst().getBody().invertedMass + pair.getSecond().getBody().invertedMass, 0f)) {

                final IShapeCollisionHandler handler = context.getShapeCollisionMapping().getMapping(pair);
                if (handler == null) {
                    // TODO #18 Add logging about this situation
                } else {
                    final Manifold manifold = handler.handleCollision(pair.getFirst(), pair.getSecond(), context);
                    if (manifold != null) {
                        manifolds.add(manifold);
                    }
                }
            }
        }

        // Solve collisions
        for (int i = 0; i < context.getCollisionSolveIterations(); i++) {
            manifolds.forEach(Manifold::solve);
        }

        // Integrate velocities
        shapes.forEach(this::integrateVelocity);

        // Integrate forces
        // Hanna modification Euler's method is used!
        shapes.forEach(this::integrateForces);

        // Correct positions
        manifolds.forEach(Manifold::correctPosition);

        // Clear all forces
        shapes.forEach(shape -> shape.getBody().force.set(0f, 0f));
    }

    private void integrateForces(IShape shape) {
        final Body body = shape.getBody();
        if (body.invertedMass == 0.0f) {
            return;
        }

        body.velocity.add(body.force, body.invertedMass * context.getFixedDeltaTime() * 0.5f);
        body.velocity.add(context.getGravity(), context.getFixedDeltaTime() * 0.5f);
        body.angularVelocity += body.torque * body.invertedInertia * context.getFixedDeltaTime() * 0.5f;
    }

    private void integrateVelocity(IShape shape) {
        final Body body = shape.getBody();
        if (body.invertedMass == 0.0f) {
            return;
        }

        body.position.add(body.velocity, context.getFixedDeltaTime());

        shape.setOrientation(body.orientation + body.angularVelocity * context.getFixedDeltaTime());
    }
}
