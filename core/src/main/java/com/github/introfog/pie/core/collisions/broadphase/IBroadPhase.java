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
package com.github.introfog.pie.core.collisions.broadphase;

import com.github.introfog.pie.core.shape.Aabb;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.ShapePair;

import java.util.Set;

/**
 * The interface that represents the basic methods for working with broad phase collision detection methods.
 *
 * <p>
 * In the broad phase, collision tests are conservative — usually based on bounding volumes only (the Pie uses
 * the axis aligned bounding box Aabb) — but fast in order to quickly prune away pairs of shapes that do not
 * collide with each other. The output of the broad phase is the potentially colliding set of pairs of shapes
 * (the {@link ShapePair} set).
 *
 * @see Aabb
 */
public interface IBroadPhase {
    /**
     * Sets the broad phase method shapes.
     *
     * @param shapes the shapes among which collisions will be calculated
     */
    void setShapes(Set<IShape> shapes);

    /**
     * Adds the shape to broad phase method.
     *
     * @param shape the shape
     */
    void addShape(IShape shape);

    /**
     * Removes shape from broad phase method.
     *
     * @param shape the shape to be removed from this broad phase method, if present
     * @return {@code true} if this broad phase method contained the specified shape, otherwise {@code false}
     */
    boolean remove(IShape shape);

    /**
     * Clears all shapes from broad phase method.
     */
    void clear();

    /**
     * Gets the unmodifiable set of all shapes from this broad phase method.
     *
     * @return the unmodifiable set of all shapes
     */
    Set<IShape> getUnmodifiableShapes();

    /**
     * Create a new instance of corresponding broad phase method.
     * New instance is a deep copy of original broad phase method.
     *
     * @return the new instance of broad phase method
     */
    IBroadPhase newInstance();

    /**
     * Calculates the shape Aabb collisions.
     *
     * @return the {@link ShapePair} set in which each item represents
     * a unique shape pair and the Aabb of those shapes intersect
     */
    Set<ShapePair> calculateAabbCollisions();
}
