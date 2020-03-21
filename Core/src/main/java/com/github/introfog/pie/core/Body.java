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
package com.github.introfog.pie.core;

import com.github.introfog.pie.core.math.Vector2f;

import java.util.Objects;

public class Body {
    public float density;
    public float restitution;
    public float invertMass;
    public float staticFriction;
    public float dynamicFriction;
    /** Orientation in radians. */
    public float orientation;
    public float angularVelocity;
    public float torque;
    public float invertInertia;
    public Vector2f position;
    public Vector2f force;
    public Vector2f velocity;

    public Body(float positionX, float positionY, float density, float restitution) {
        this.density = density;
        this.restitution = restitution;

        staticFriction = 0.5f;
        dynamicFriction = 0.3f;
        torque = 0f;

        force = new Vector2f(0f, 0f);
        velocity = new Vector2f(0f, 0f);
        position = new Vector2f(positionX, positionY);
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
}