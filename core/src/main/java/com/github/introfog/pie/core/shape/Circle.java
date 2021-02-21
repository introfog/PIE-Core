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

import java.util.StringJoiner;

/**
 * This class represent circle shape. Circle is defined by {@link #radius} field.
 */
public class Circle extends IShape {
    /** The radius of circle. */
    private final float radius;

    /**
     * Instantiates a new {@link Circle} instance based on radius, coordinates of center, density and restitution.
     *
     * @param radius the radius of circle
     * @param centreX the X coordinate of center of circle
     * @param centreY the Y coordinate of center of circle
     * @param density the density of circle
     * @param restitution the restitution of circle
     */
    public Circle(float radius, float centreX, float centreY, float density, float restitution) {
        super(centreX, centreY, density, restitution);
        if (radius < 0) {
            // TODO Create custom Pie exception
            throw new IllegalArgumentException();
        }
        this.radius = radius;

        computeMassAndInertia();
        computeAabb();
    }

    /**
     * Gets the radius of circle.
     *
     * @return the radius
     */
    public final float getRadius() {
        return radius;
    }

    @Override
    public void computeAabb() {
        getAabb().min.set(getBody().position.x - radius, getBody().position.y - radius);
        getAabb().max.set(getBody().position.x + radius, getBody().position.y + radius);
    }

    @Override
    public String toString() {
        return new StringJoiner("; ", "{", "}")
                .add("center=" + getBody().position)
                .add("radius=" + radius)
                .toString();
    }

    @Override
    protected void computeMassAndInertia() {
        float mass = (float) Math.PI * radius * radius * getBody().density;
        getBody().invertedMass = (mass == 0f) ? 0f : 1f / mass;

        float inertia = radius * radius / (getBody().invertedMass == 0 ? 1 : getBody().invertedMass);
        getBody().invertedInertia = (inertia != 0.0f) ? 1.0f / inertia : 0.0f;
    }
}
