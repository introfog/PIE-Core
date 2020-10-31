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
package com.github.introfog.pie.core.shape;

import com.github.introfog.pie.core.Body;
import com.github.introfog.pie.core.math.RotationMatrix2x2;
import com.github.introfog.pie.core.math.Vector2f;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The IShape abstract class represent a physical object in the
 * {@link com.github.introfog.pie.core.World} that has a shape and body.
 */
public abstract class IShape {
    private static final AtomicInteger lastShapeId = new AtomicInteger();

    /** The shape type. */
    public ShapeType type;
    /** The shape axis aligned bounding box. */
    public Aabb aabb;
    /** The body that stores shape physical parameters. */
    public Body body;
    /** The rotation matrix. */
    public RotationMatrix2x2 rotateMatrix;

    private final int shapeId;

    /**
     * Instantiates a new {@link IShape} instance.
     */
    public IShape() {
        shapeId = lastShapeId.incrementAndGet();
        aabb = new Aabb();
        rotateMatrix = new RotationMatrix2x2();
        rotateMatrix.setAngle(0f);
    }

    /**
     * Sets the shape orientation in space.
     *
     * @param radian the angle in radians that the shape will have in space
     */
    public final void setOrientation(float radian) {
        body.orientation = radian;
        rotateMatrix.setAngle(radian);
    }

    /**
     * Apply impulse to shape.
     *
     * @param impulse the impulse vector
     * @param contactVector the point of impulse application (coordinates are set relative to the center of the shape)
     */
    public final void applyImpulse(Vector2f impulse, Vector2f contactVector) {
        body.velocity.add(impulse, body.invertedMass);
        body.angularVelocity += body.invertedInertia * Vector2f.crossProduct(contactVector, impulse);
    }

    /**
     * Calculates the current axis aligned bounding box for the shape.
     *
     * <p>
     * The shapes in the world are constantly moving and rotating and hence their Aabb changes,
     * this method update the Aabb. The update takes place in the broad phase of collision detection,
     * see {@link com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase}.
     */
    public abstract void computeAabb();

    @Override
    public boolean equals(Object o) {
        // In general, consider shapes the same only when they refer to the
        // same object in memory, this is provided by the shapeId variable
        return this == o;
    }

    @Override
    public int hashCode() {
        return shapeId;
    }

    /**
     * A helper method for calculating the mass and inertia
     * of a shape that is used when initializing the shape.
     */
    protected abstract void computeMassAndInertia();
}
