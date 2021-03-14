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
    /** The position. */
    public final Vector2f position;
    /** The density. */
    protected final float density;
    /** The restitution. */
    public final float restitution;

    /** The static friction. */
    public final float staticFriction;
    /** The dynamic friction. */
    public final float dynamicFriction;
    /** The torque. */
    public final float torque;

    /** The inverted mass. */
    protected float invertedMass;
    /** The inverted inertia. */
    protected float invertedInertia;
    /** The body orientation in radians. */
    protected float orientation;


    /** The angular velocity. */
    public float angularVelocity;
    /** The force. */
    public final Vector2f force;
    /** The velocity. */
    public final Vector2f velocity;

    /**
     * Instantiates a new {@link Body} instance.
     *
     * @param positionX the body position in X axis
     * @param positionY the body position in Y axis
     * @param density the body density
     * @param restitution the body restitution
     */
    public Body(float positionX, float positionY, float density, float restitution) {
        position = new Vector2f(positionX, positionY);
        this.density = density;
        this.restitution = restitution;

        staticFriction = 0.5f;
        dynamicFriction = 0.3f;
        torque = 0f;
        force = new Vector2f(0f, 0f);
        velocity = new Vector2f(0f, 0f);
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
        return Float.compare(body.density, density) == 0 &&
                Float.compare(body.restitution, restitution) == 0 &&
                position.equals(body.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(density, restitution, position);
    }

    @Override
    public String toString() {
        return new StringJoiner("; ", "{", "}")
                .add("position=" + position)
                .add("density=" + density)
                .add("restitution=" + restitution)
                .toString();
    }

    public float getInvertedMass() {
        return invertedMass;
    }

    public float getInvertedInertia() {
        return invertedInertia;
    }

    public float getOrientation() {
        return orientation;
    }

    public float getDensity() {
        return density;
    }
}
