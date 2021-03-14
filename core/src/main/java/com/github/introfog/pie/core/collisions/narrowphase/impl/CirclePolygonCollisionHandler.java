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
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.Polygon;
import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.core.math.Vector2f;

/**
 * Class is used to handle possible collision between {@link Circle} and {@link Polygon}.
 */
public class CirclePolygonCollisionHandler implements IShapeCollisionHandler {
    /**
     * Handles a collision between {@link Circle} and {@link Polygon}. It doesn't matter what order the parameters
     * are in, circle and polygon or polygon and circle. In the course of this method, it is determined whether
     * shapes collide, and if so, the basic information about the collision is calculated (see {@link Manifold}).
     *
     * @param aShape the first shape
     * @param bShape the second shape
     * @param context the world context
     * @return the {@link Manifold} instance with main collision information if the shapes collide, otherwise null
     *
     * @throws IllegalArgumentException if wrong shape types passed
     */
    @Override
    public Manifold handleCollision(IShape aShape, IShape bShape, Context context) {
        if ((Circle.class.equals(aShape.getClass()) && Circle.class.equals(bShape.getClass()))
                || (Polygon.class.equals(aShape.getClass()) && Polygon.class.equals(bShape.getClass()))) {
            throw new IllegalArgumentException(PieExceptionMessage.INVALID_SHAPES_TYPE_FOR_NARROW_PHASE_HANDLER);
        }

        Circle circleA = Circle.class.equals(aShape.getClass()) ? (Circle) aShape : (Circle) bShape;
        Polygon polygonB = Polygon.class.equals(aShape.getClass()) ? (Polygon) aShape : (Polygon) bShape;

        Manifold manifold = new Manifold(circleA, polygonB, context);
        // Translate center coordinates to polygon coordinates
        // center = B->u.Transpose( ) * (center - b->position);
        Vector2f centerA = new Vector2f(circleA.getBody().position);
        centerA.sub(polygonB.getBody().position);
        polygonB.getRotateMatrix().transposeMul(centerA, centerA);

        // Looking for the nearest edge of the polygon to the center of the circle,
        // projecting the center on each edge normal of the polygon
        float separation = -Float.MAX_VALUE;
        float separationIfCircleInPolygon = Float.MAX_VALUE;
        int indexFaceNormalIfCircleInPolygon = 0;
        int indexFaceNormal = 0;
        float dotProduct;
        Vector2f projection = new Vector2f();
        Vector2f realProjection = new Vector2f();
        Vector2f tmpV = new Vector2f();
        for (int i = 0; i < polygonB.getVertices().length; i++) {
            tmpV.set(centerA);
            tmpV.sub(polygonB.getVertices()[i]);
            dotProduct = Vector2f.dotProduct(polygonB.getNormals()[i], tmpV);
            projection.x = dotProduct * polygonB.getNormals()[i].x;
            projection.y = dotProduct * polygonB.getNormals()[i].y;

            // The sign of the scalar product indicates whether the center is on the opposite side of the line from the normal
            if (dotProduct > 0f && projection.lengthWithoutSqrt() > circleA.getRadius() * circleA.getRadius()) {
                return null;
            }

            if (dotProduct > separation) {
                realProjection.set(projection);
                separation = dotProduct;
                indexFaceNormal = i;
            }
            // Save the nearest edge to the center of the circle, if the center is inside the polygon,
            // the usual dotProduct > separation gives an incorrect answer,
            // because all derivatives are negative, but need less modulo
            if (Math.abs(dotProduct) < separationIfCircleInPolygon) {
                separationIfCircleInPolygon = Math.abs(dotProduct);
                indexFaceNormalIfCircleInPolygon = i;
            }
        }

        // If max scalar product is less than 0, then the center of the circle inside the polygon
        if (separation < MathPie.EPSILON) {
            // m->normal = -(B->u * B->m_normals[faceNormal]);
            // m->contacts[0] = m->normal * A->radius + a->position;

            manifold.contactCount = 1;
            polygonB.getRotateMatrix().mul(polygonB.getNormals()[indexFaceNormalIfCircleInPolygon], manifold.normal);
            manifold.normal.negative();

            manifold.contacts[0].set(manifold.normal);
            manifold.contacts[0].mul(circleA.getRadius());
            manifold.contacts[0].add(circleA.getBody().position);
            manifold.penetration = circleA.getRadius();
            return manifold;
        }

        // Found the nearest edge to the center of the circle, and the center of the circle lies outside the polygon.
        // Now define the Voronoi region in which the center of the circle is located relative to the nearest edge of the polygon
        Vector2f v1 = new Vector2f(polygonB.getVertices()[indexFaceNormal]);
        Vector2f v2 = new Vector2f(polygonB.getVertices()[(indexFaceNormal + 1) % polygonB.getVertices().length]);

        float dot1 = Vector2f.dotProduct(Vector2f.sub(centerA, v1), Vector2f.sub(v2, v1));
        float dot2 = Vector2f.dotProduct(Vector2f.sub(centerA, v2), Vector2f.sub(v1, v2));

        if (dot1 <= 0f) {
            // Closer to the first vertex
            if (Vector2f.distanceWithoutSqrt(centerA, v1) > circleA.getRadius() * circleA.getRadius()) {
                return null;
            }

            manifold.penetration = circleA.getRadius() - (float) Math.sqrt(Vector2f.distanceWithoutSqrt(centerA, v1));

            manifold.contactCount = 1;
            // Vec2 n = v1 - center;
            // n = B->u * n;
            // n.Normalize( );
            // m->normal = n;
            // v1 = B->u * v1 + b->position;
            // m->contacts[0] = v1;
            Vector2f n = Vector2f.sub(v1, centerA);
            polygonB.getRotateMatrix().mul(n, n);
            n.normalize();
            manifold.normal.set(n);
            polygonB.getRotateMatrix().mul(v1, v1);
            v1.add(polygonB.getBody().position);
            manifold.contacts[0].set(v1);
        } else if (dot2 <= 0f) {
            // Closer to the second vertex
            if (Vector2f.distanceWithoutSqrt(centerA, v2) > circleA.getRadius() * circleA.getRadius()) {
                return null;
            }

            manifold.penetration = circleA.getRadius() - (float) Math.sqrt(Vector2f.distanceWithoutSqrt(centerA, v2));

            manifold.contactCount = 1;
            // Vec2 n = v2 - center;
            // v2 = B->u * v2 + b->position;
            // m->contacts[0] = v2;
            // n = B->u * n;
            // n.Normalize( );
            // m->normal = n;
            Vector2f n = Vector2f.sub(v2, centerA);
            polygonB.getRotateMatrix().mul(n, n);
            n.normalize();
            manifold.normal.set(n);
            polygonB.getRotateMatrix().mul(v2, v2);
            v2.add(polygonB.getBody().position);
            manifold.contacts[0].set(v2);
        } else {
            // Closer to the front vertex
            Vector2f n = new Vector2f(polygonB.getNormals()[indexFaceNormal]);

            manifold.penetration = circleA.getRadius() - (float) Math.sqrt(realProjection.lengthWithoutSqrt());

            manifold.contactCount = 1;
            // n = B->u * n;
            // m->normal = -n;
            // m->contacts[0] = m->normal * A->radius + a->position;
            polygonB.getRotateMatrix().mul(n, n);
            n.negative();
            manifold.normal.set(n);
            manifold.contacts[0].set(manifold.normal);
            manifold.contacts[0].mul(circleA.getRadius());
            manifold.contacts[0].add(circleA.getBody().position);
        }
        return manifold;
    }
}
