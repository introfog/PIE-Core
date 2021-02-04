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
package com.github.introfog.pie.core.collisions.narrowphase.impl;

import com.github.introfog.pie.core.Context;
import com.github.introfog.pie.core.PieExceptionMessage;
import com.github.introfog.pie.core.collisions.Manifold;
import com.github.introfog.pie.core.collisions.narrowphase.IShapeCollisionHandler;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.ShapeType;

/**
 * Class is used to handle possible collision between two {@link Circle}.
 */
public class CircleCircleCollisionHandler implements IShapeCollisionHandler {
    @Override
    public Manifold handleCollision(IShape aShape, IShape bShape, Context context) {
        if (aShape.type != ShapeType.CIRCLE || bShape.type != ShapeType.CIRCLE) {
            throw new IllegalArgumentException(PieExceptionMessage.INVALID_SHAPES_TYPE_FOR_NARROW_PHASE_HANDLER);
        }

        Circle circleA = (Circle) aShape;
        Circle circleB = (Circle) bShape;

        Manifold manifold = new Manifold(circleA, circleB, context);
        manifold.normal = Vector2f.sub(circleB.body.position, circleA.body.position);
        final float distanceWithoutSqrt = manifold.normal.lengthWithoutSqrt();

        if (!CircleCircleCollisionHandler.areIntersected(circleA, circleB, distanceWithoutSqrt)) {
            return null;
        }

        manifold.contactCount = 1;
        manifold.penetration = circleA.radius + circleB.radius - (float) Math.sqrt(distanceWithoutSqrt);
        // m->contacts[0] = m->normal * A->radius + a->position;
        manifold.normal.normalize();
        manifold.contacts[0].set(manifold.normal);
        manifold.contacts[0].mul(circleA.radius);
        manifold.contacts[0].add(circleA.body.position);

        if (distanceWithoutSqrt == 0) {
            manifold.normal.set(1f, 0f);
            manifold.penetration = circleA.radius;
            manifold.contacts[0].set(circleA.body.position);
        }

        return manifold;
    }

    private static boolean areIntersected(Circle a, Circle b, float distanceWithoutSqrt) {
        float sumRadius = a.radius + b.radius;
        sumRadius *= sumRadius;
        return sumRadius > distanceWithoutSqrt;
    }
}
