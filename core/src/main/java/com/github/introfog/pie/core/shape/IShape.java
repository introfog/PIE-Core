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

import com.github.introfog.pie.core.math.RotationMatrix2x2;
import com.github.introfog.pie.core.math.Vector2f;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The IShape abstract class represent a physical object in the
 * {@link com.github.introfog.pie.core.World} that has a shape and body.
 */
public abstract class IShape {
    private static final AtomicInteger lastShapeId = new AtomicInteger();

    private final int shapeId;
    /** The body that stores shape physical parameters. */
    private final Body body;
    /** The shape axis aligned bounding box. */
    private final Aabb aabb;
    /** The rotation matrix. */
    private final RotationMatrix2x2 rotateMatrix;

    /**
     * Instantiates a new {@link IShape} instance.
     */
    public IShape(float centreX, float centreY, float density, float restitution) {
        this(new Body(centreX, centreY, density, restitution), new Aabb(), new RotationMatrix2x2());
    }

    public IShape(Body body, Aabb aabb, RotationMatrix2x2 rotateMatrix) {
        shapeId = lastShapeId.incrementAndGet();
        this.body = body;
        this.aabb = aabb;
        this.rotateMatrix = rotateMatrix;
    }

    public void integrateForce(float deltaTime, Vector2f gravity) {
        body.getVelocity().add(body.getForce(), body.getInvertedMass() * deltaTime);
        body.getVelocity().add(gravity, deltaTime);
        body.angularVelocity += body.getTorque() * body.getInvertedInertia() * deltaTime;
    }

    public void integrateVelocity(float deltaTime) {
        body.getPosition().add(body.getVelocity(), deltaTime);
        final float radian = body.getOrientation() + body.getAngularVelocity() * deltaTime;
        body.orientation = radian;
        rotateMatrix.setAngle(radian);
    }

    /**
     * Apply impulse to shape.
     *
     * @param impulse the impulse vector
     * @param contactVector the point of impulse application (coordinates are set relative to the center of the shape)
     */
    public void applyImpulse(Vector2f impulse, Vector2f contactVector) {
        body.getVelocity().add(impulse, body.getInvertedMass());
        body.angularVelocity += body.getInvertedInertia() * Vector2f.crossProduct(contactVector, impulse);
    }

    /**
     * Gets the shape axis aligned bounding box.
     *
     * @return the axis aligned bounding box
     */
    public Aabb getAabb() {
        return aabb;
    }

    /**
     * Gets the body of shape.
     *
     * @return the body
     */
    public Body getBody() {
        return body;
    }

    /**
     * Gets rotation matrix of shape.
     *
     * @return the rotation matrix
     */
    public RotationMatrix2x2 getRotateMatrix() {
        return rotateMatrix;
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

    /**
     * Checks that some other object is equal to this shape. Equality in our case consider that the links
     * point to the same object in the computer's memory, because there is no situation in which different
     * shapes are equals even though they may have the same coordinates and other parameters.
     *
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        return this == o;
    }

    /**
     * Returns a shape id that is unique for any shape. This approach is used because there is no situation
     * in which different shapes are equals even though they may have the same coordinates and other parameters.
     *
     * @return {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return shapeId;
    }

    /**
     * A helper method for calculating the mass and inertia
     * of a shape that is used when initializing the shape.
     */
    protected abstract void computeMassAndInertia();
}
