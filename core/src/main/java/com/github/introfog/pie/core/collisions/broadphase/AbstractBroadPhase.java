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
import com.github.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The abstract class that represents the basic methods for working with broad phase collision detection methods.
 *
 * <p>
 * In the broad phase, collision tests are conservative — usually based on bounding volumes only (the Pie uses
 * the axis aligned bounding box Aabb) — but fast in order to quickly prune away pairs of shapes that do not
 * collide with each other. The output of the broad phase is the potentially colliding set of pairs of shapes
 * (the {@link ShapePair} list).
 *
 * @see Aabb
 */
public abstract class AbstractBroadPhase {
    /**
     * The shapes among which collisions will be calculated.
     */
    protected List<IShape> shapes;

    /**
     * Instantiates a new {@link AbstractBroadPhase} instance.
     */
    public AbstractBroadPhase() {
        shapes = new ArrayList<>();
    }

    /**
     * Sets the broad phase method shapes.
     *
     * <p>
     * Note, the shape array is copied by calling the copy constructor.
     *
     * @param shapes the shapes among which collisions will be calculated
     */
    public void setShapes(List<IShape> shapes) {
        this.shapes = new ArrayList<>(shapes);
    }

    /**
     * Adds the shape to broad phase method.
     *
     * @param shape the shape
     */
    public void addShape(IShape shape){
        shapes.add(shape);
    }

    /**
     * Removes shape from broad phase method.
     *
     * @param shape the shape to be removed from this broad phase method, if present
     * @return {@code true} if this broad phase method contained the specified shape
     */
    public boolean remove(IShape shape) {
        return shapes.remove(shape);
    }

    /**
     * Clears all shapes from broad phase method.
     */
    public void clear() {
        shapes.clear();
    }

    /**
     * Gets the unmodifiable list of all shapes from this broad phase method.
     *
     * @return the unmodifiable list of all shapes
     */
    public List<IShape> getUnmodifiableShapes() {
        return Collections.unmodifiableList(shapes);
    }

    /**
     * Create a new instance of corresponding broad phase method.
     * New instance is a deep copy of original broad phase method.
     *
     * @return the new instance of broad phase method
     */
    public abstract AbstractBroadPhase newInstance();

    /**
     * Calculates the shape Aabb collisions.
     *
     * <p>
     * Note, before calling the {@link #domesticCalculateAabbCollisions()} method, which calculates collisions,
     * the {@link IShape#computeAabb()} method is called for all shapes from the {@link #shapes}, because the
     * broad phase needs the up-to-date Aabbs.
     *
     * @return the {@link ShapePair} list in which each item represents
     * a unique shape pair and the Aabb of those shapes intersect
     */
    public final List<ShapePair> calculateAabbCollisions() {
        shapes.forEach(IShape::computeAabb);
        return domesticCalculateAabbCollisions();
    }

    /**
     * Domestic method for calculating the shape Aabb collisions.
     *
     * <p>
     * Note, when this method is called, all shapes from {@link #shapes} have an up-to-date Aabb.
     *
     * @return the {@link ShapePair} list in which each item represents
     * a unique shape pair and the Aabb of those shapes intersect
     */
    protected abstract List<ShapePair> domesticCalculateAabbCollisions();
}
