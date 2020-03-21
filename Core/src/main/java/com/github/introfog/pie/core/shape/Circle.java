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

import java.util.Objects;

public class Circle extends IShape {
    public float radius;

    public Circle(float radius, float centreX, float centreY, float density, float restitution) {
        body = new Body(centreX, centreY, density, restitution);
        this.radius = radius;

        computeMass();
        computeAABB();

        type = ShapeType.circle;
    }

    @Override
    public void computeAABB() {
        aabb.min.set(body.position.x - radius, body.position.y - radius);
        aabb.max.set(body.position.x + radius, body.position.y + radius);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Circle circle = (Circle) o;
        return Float.compare(circle.radius, radius) == 0 && super.equals(circle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(radius, super.hashCode());
    }

    @Override
    protected void computeMass() {
        float mass = (float) Math.PI * radius * radius * body.density;
        body.invertMass = (mass == 0f) ? 0f : 1f / mass;

        float inertia = radius * radius / body.invertMass;
        body.invertInertia = (inertia != 0.0f) ? 1.0f / inertia : 0.0f;
    }
}