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
package com.github.introfog.pie.core.collisions;

import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.math.Vector2f;

public class CollisionCircleCircle implements CollisionCallback {
    public static final CollisionCircleCircle instance = new CollisionCircleCircle();
    private static float distanceWithoutSqrt;

    private boolean areIntersected(Circle a, Circle b) {
        float sumRadius = a.radius + b.radius;
        sumRadius *= sumRadius;
        return sumRadius > distanceWithoutSqrt;
    }

    @Override
    public void handleCollision(Manifold manifold) {
        Circle A = manifold.circleA;
        Circle B = manifold.circleB;

        manifold.normal = Vector2f.sub(B.body.position, A.body.position);
        distanceWithoutSqrt = manifold.normal.lengthWithoutSqrt();

        if (!areIntersected(A, B)) {
            manifold.areBodiesCollision = false;
            return;
        }

        manifold.contactCount = 1;
        manifold.penetration = A.radius + B.radius - (float) Math.sqrt(distanceWithoutSqrt);
        // m->contacts[0] = m->normal * A->radius + a->position;
        manifold.normal.normalize();
        manifold.contacts[0].set(manifold.normal);
        manifold.contacts[0].mul(A.radius);
        manifold.contacts[0].add(A.body.position);

        if (distanceWithoutSqrt == 0) {
            manifold.normal.set(1f, 0f);
            manifold.penetration = A.radius;
            manifold.contacts[0].set(A.body.position);
        }
    }
}
