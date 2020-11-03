package com.github.introfog.pie.core.collisions.narrowphase;

import com.github.introfog.pie.core.Context;
import com.github.introfog.pie.core.collisions.Manifold;
import com.github.introfog.pie.core.shape.IShape;

/**
 * Interface for handling possible {@link IShape} collisions based on real shape form. Usually these
 * handlers are used when shapes {@link com.github.introfog.pie.core.shape.Aabb} are known to collision,
 * and the likelihood of the shapes colliding is high. Splitting collision detection into two steps
 * (broad phase and narrow phase) is done to improve performance.
 */
public interface IShapeCollisionHandler {
    /**
     * Handles a collision between two {@link IShape}. In the course of this method, it is determined
     * whether shapes collide, and if so, the basic information about the collision is calculated
     * (see {@link Manifold}).
     *
     * @param aShape the first shape
     * @param bShape the second shape
     * @param context the world context
     * @return the {@link Manifold} instance with main collision information if the shapes collide, otherwise null
     *
     * @throws IllegalArgumentException if wrong shape types passed
     */
    Manifold handleCollision(IShape aShape, IShape bShape, Context context);
}
