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
import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.math.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Polygon extends IShape {
    public int vertexCount;
    public Vector2f[] vertices;
    public Vector2f[] normals;

    public static Polygon generateRectangle(float centerX, float centerY, float width, float height, float density,
            float restitution) {
        Vector2f[] vertices = new Vector2f[4];
        vertices[0] = new Vector2f(-width / 2f, -height / 2f);
        vertices[1] = new Vector2f(width / 2f, -height / 2f);
        vertices[2] = new Vector2f(width / 2f, height / 2f);
        vertices[3] = new Vector2f(-width / 2f, height / 2f);
        return new Polygon(density, restitution, centerX, centerY, vertices);
    }

    // TODO Search for the minimum convex hull (Jarvis algorithm) works for O(n*h) where h is the number of vertices in the MCH
    public Polygon(float density, float restitution, float centreX, float centreY, Vector2f... vertices) {
        body = new Body(centreX, centreY, density, restitution);
        Vector2f tmpV = new Vector2f();
        Vector2f tmpV2 = new Vector2f();

        // Jarvis's algorithm for constructing a minimal convex hull.
        // Find the lowest and rightmost coordinate, it will become the
        // starting point, and exactly belongs to the MCH (min. convex hull)
        tmpV.set(vertices[0]);
        int rightMost = 0;
        for (int i = 0; i < vertices.length; i++) {
            if (vertices[i].x > tmpV.x) {
                tmpV.set(vertices[i]);
                rightMost = i;
            } else if (tmpV.x == vertices[i].x && tmpV.y > vertices[i].y) {
                tmpV.set(vertices[i]);
                rightMost = i;
            }
        }

        List<Integer> hull = new ArrayList<>();
        for (int i = 0; i < vertices.length + 1; i++) {
            hull.add(0);
        }
        int outCount = 0;
        int indexHull = rightMost;

        while (true){
            hull.set(outCount, indexHull);

            // Looking for the vertex with the largest angle counterclockwise from the current vertex
            // (Calculate the angle through the vector product)
            int nextHullIndex = 0;
            for (int i = 1; i < vertices.length; ++i) {
                // Skip the same vertices, because need unique vertices in the triangle
                if (nextHullIndex == indexHull) {
                    nextHullIndex = i;
                    continue;
                }
                // Sort through all the triangles, looking for the most extreme vertex
                tmpV.set(vertices[nextHullIndex]);
                tmpV.sub(vertices[hull.get(outCount)]);

                tmpV2.set(vertices[i]);
                tmpV2.sub(vertices[hull.get(outCount)]);
                float c = Vector2f.crossProduct(tmpV, tmpV2);
                if (c < 0.0f) {
                    nextHullIndex = i;
                }
                // If the vector product is 0, then they lie on one straight line,
                // and need the vertex farthest from the given vertex
                if (c == 0.0f && tmpV2.lengthWithoutSqrt() > tmpV.lengthWithoutSqrt()) {
                    nextHullIndex = i;
                }
            }

            outCount++;
            indexHull = nextHullIndex;

            // When reached the starting vertex, the Jarvis algorithm is complete
            if (nextHullIndex == rightMost) {
                vertexCount = outCount;
                break;
            }
        }

        if (vertexCount > MathPIE.MAX_POLY_VERTEX_COUNT) {
            // TODO create PIE custom exception
            throw new RuntimeException("Error. Too many vertices in polygon.");
        }

        this.vertices = Vector2f.arrayOf(vertexCount);
        this.normals = Vector2f.arrayOf(vertexCount);

        for (int i = 0; i < vertexCount; i++) {
            this.vertices[i].set(vertices[hull.get(i)]);
        }

        for (int i = 0; i < vertexCount; i++) {
            tmpV.set(this.vertices[(i + 1) % vertexCount]);
            tmpV.sub(this.vertices[i]);

            // Take the right normal
            normals[i].set(tmpV.y, -tmpV.x);
            normals[i].normalize();
        }

        computeMassAndInertia();
        computeAABB();

        type = ShapeType.polygon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Polygon polygon = (Polygon) o;
        return vertexCount == polygon.vertexCount && Arrays.equals(vertices, polygon.vertices) && super.equals(polygon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexCount, Arrays.hashCode(vertices), super.hashCode());
    }

    @Override
    public void computeAABB() {
        aabb.min.x = Float.MAX_VALUE;
        aabb.min.y = Float.MAX_VALUE;

        aabb.max.x = -Float.MAX_VALUE;
        aabb.max.y = -Float.MAX_VALUE;

        Vector2f tmpV = new Vector2f();
        for (int i = 0; i < vertexCount; i++) {
            tmpV.set(vertices[i]);
            rotateMatrix.mul(tmpV, tmpV);
            if (tmpV.x < aabb.min.x) {
                aabb.min.x = tmpV.x;
            }
            if (tmpV.y < aabb.min.y) {
                aabb.min.y = tmpV.y;
            }
            if (tmpV.x > aabb.max.x) {
                aabb.max.x = tmpV.x;
            }
            if (tmpV.y > aabb.max.y) {
                aabb.max.y = tmpV.y;
            }
        }

        aabb.min.add(body.position);
        aabb.max.add(body.position);
    }

    @Override
    public String toString() {
        return new StringJoiner("; ", "{", "}")
                .add("center=" + body.position)
                .add("vertices=" + Arrays.toString(vertices))
                .toString();
    }

    public Vector2f getSupport(Vector2f dir) {
        // Looking for the most distant vertex in a given direction
        float bestProjection = -Float.MAX_VALUE;
        Vector2f bestVertex = new Vector2f();

        for (int i = 0; i < vertexCount; ++i) {
            Vector2f v = vertices[i];
            float projection = Vector2f.dotProduct(v, dir);

            if (projection > bestProjection) {
                bestVertex.set(v);
                bestProjection = projection;
            }
        }

        return bestVertex;
    }

    @Override
    protected void computeMassAndInertia() {
        float area = 0f;
        float I = 0f;
        final float k_inv3 = 1f / 3f;

        for (int i = 0; i < vertexCount; ++i) {
            // Split the convex polygon into triangles for which one of the points (0, 0)
            Vector2f p1 = vertices[i];
            Vector2f p2 = vertices[(i + 1) % vertexCount];

            float D = Vector2f.crossProduct(p1, p2);
            float triangleArea = 0.5f * D;

            area += triangleArea;

            float intX2 = p1.x * p1.x + p2.x * p1.x + p2.x * p2.x;
            float intY2 = p1.y * p1.y + p2.y * p1.y + p2.y * p2.y;
            I += (0.25f * k_inv3 * D) * (intX2 + intY2);
        }

        float mass = body.density * area;
        body.invertedMass = (mass != 0f) ? 1f / mass : 0f;
        float inertia = I * body.density;
        body.invertedInertia = (inertia != 0f) ? 1f / inertia : 0f;
    }
}
