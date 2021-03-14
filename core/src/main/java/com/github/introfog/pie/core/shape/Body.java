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

import com.github.introfog.pie.core.math.Vector2f;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The class stores all the physical parameters of a shape, such as mass, position, speed, etc.
 */
public class Body {
    /** The density. */
    protected final float density;
    /** The restitution. */
    protected final float restitution;

    /** The static friction. */
    protected final float staticFriction;
    /** The dynamic friction. */
    protected final float dynamicFriction;

    /** The body orientation in radians. */
    protected float orientation;
    /** The angular velocity. */
    protected float angularVelocity;
    /** The torque. */
    protected final float torque;

    /** The inverted mass. */
    protected float invertedMass;
    /** The inverted inertia. */
    protected float invertedInertia;

    /** The position. */
    protected final Vector2f position;
    /** The velocity. */
    protected final Vector2f velocity;
    /** The force. */
    protected final Vector2f force;

    /**
     * Instantiates a new {@link Body} instance.
     *
     * @param positionX the body position in X axis
     * @param positionY the body position in Y axis
     * @param density the body density
     * @param restitution the body restitution
     */
    public Body(float positionX, float positionY, float density, float restitution) {
        this.density = density;
        this.restitution = restitution;

        this.staticFriction = 0.5f;
        this.dynamicFriction = 0.3f;

        this.torque = 0;

        this.position = new Vector2f(positionX, positionY);
        this.velocity = new Vector2f(0f, 0f);
        this.force = new Vector2f(0f, 0f);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Body body = (Body) o;
        return Float.compare(body.getDensity(), getDensity()) == 0 &&
                Float.compare(body.getRestitution(), getRestitution()) == 0 &&
                getPosition().equals(body.getPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDensity(), getRestitution(), getPosition());
    }

    @Override
    public String toString() {
        return new StringJoiner("; ", "{", "}")
                .add("position=" + getPosition())
                .add("density=" + getDensity())
                .add("restitution=" + getRestitution())
                .toString();
    }

    public float getDensity() {
        return density;
    }

    public float getRestitution() {
        return restitution;
    }

    public float getStaticFriction() {
        return staticFriction;
    }

    public float getDynamicFriction() {
        return dynamicFriction;
    }

    public float getOrientation() {
        return orientation;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public float getTorque() {
        return torque;
    }

    public float getInvertedMass() {
        return invertedMass;
    }

    public float getInvertedInertia() {
        return invertedInertia;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public Vector2f getForce() {
        return force;
    }
}
