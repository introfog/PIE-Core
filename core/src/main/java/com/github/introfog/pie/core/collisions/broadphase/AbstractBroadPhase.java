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

import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.ShapePair;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The abstract class that represents the basic methods and fields
 * for working with broad phase collision detection methods.
 */
public abstract class AbstractBroadPhase implements IBroadPhase {
    /**
     * The shapes among which collisions will be calculated.
     */
    protected Set<IShape> shapes;

    /**
     * Instantiates a new {@link AbstractBroadPhase} instance.
     */
    public AbstractBroadPhase() {
        shapes = new HashSet<>();
    }

    /**
     * Sets the broad phase method shapes.
     *
     * <p>
     * Note, the shape array is copied by calling the copy constructor.
     *
     * @param shapes the shapes among which collisions will be calculated
     */
    @Override
    public void setShapes(Set<IShape> shapes) {
        this.shapes = new HashSet<>(shapes);
    }

    @Override
    public void addShape(IShape shape){
        shapes.add(shape);
    }

    @Override
    public boolean remove(IShape shape) {
        return shapes.remove(shape);
    }

    @Override
    public void clear() {
        shapes.clear();
    }

    @Override
    public Set<IShape> getUnmodifiableShapes() {
        return Collections.unmodifiableSet(shapes);
    }

    /**
     * Calculates the shape Aabb collisions.
     *
     * <p>
     * Note, before calling the {@link #domesticCalculateAabbCollisions()} method, which calculates collisions,
     * the {@link IShape#computeAabb()} method is called for all shapes from the {@link #shapes}, because the
     * broad phase needs the up-to-date aabbs.
     *
     * @return the {@link ShapePair} set in which each item represents
     * a unique shape pair and the Aabb of those shapes intersect
     */
    @Override
    public final Set<ShapePair> calculateAabbCollisions() {
        shapes.forEach(IShape::computeAabb);
        return domesticCalculateAabbCollisions();
    }

    /**
     * Domestic method for calculating the shape Aabb collisions.
     *
     * <p>
     * Note, when this method is called, all shapes from {@link #shapes} have an up-to-date aabb.
     *
     * @return the {@link ShapePair} set in which each item represents
     * a unique shape pair and the Aabb of those shapes intersect
     */
    protected abstract Set<ShapePair> domesticCalculateAabbCollisions();
}
