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

import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.core.math.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * This class represent convex polygon shape. Polygon is defined by array of vertices
 * and array of normals (calculated when creating an object to improve performance).
 */
public class Polygon extends IShape {
    /** The count of polygon vertices. */
    protected final int vertexCount;
    /** The array of polygon vertices. */
    protected final Vector2f[] vertices;
    /** The array of polygon normals. */
    protected final Vector2f[] normals;

    /**
     * Instantiates a new {@link Polygon} instance based on density,
     * restitution, coordinates of center and list of vertices.
     *
     * <p>
     * Important note, the coordinates of vertices are specified relative to the passed coordinates of center.
     *
     * @param density the density of polygon
     * @param restitution the restitution of polygon
     * @param centreX the X coordinate of center of polygon
     * @param centreY the Y coordinate of center of polygon
     * @param vertices the list of polygon vertices (relative to the passed center)
     */
    public Polygon(float density, float restitution, float centreX, float centreY, List<Vector2f> vertices) {
        // TODO Search for the minimum convex hull (Jarvis algorithm) works for O(n*h) where h is the number of vertices in the MCH
        super(centreX, centreY, density, restitution);

        for (int i = vertices.size() - 1; i > -1; i--) {
            for (int j = i - 1; j > -1; j--) {
                if (Vector2f.distanceWithoutSqrt(vertices.get(i), vertices.get(j)) < MathPie.EPSILON * MathPie.EPSILON) {
                    vertices.remove(i);
                    break;
                }
            }
        }

        List<Integer> hull = Polygon.calculateHullIndices(vertices);
        vertexCount = hull.size();

        if (vertexCount > MathPie.MAX_POLY_VERTEX_COUNT) {
            // TODO create Pie custom exception
            throw new IllegalArgumentException("Error. Too many vertices in polygon.");
        }

        this.vertices = Vector2f.arrayOf(vertexCount);
        this.normals = Vector2f.arrayOf(vertexCount);

        for (int i = 0; i < vertexCount; i++) {
            this.vertices[i].set(vertices.get(hull.get(i)));
        }

        Vector2f tmpV = new Vector2f();
        for (int i = 0; i < vertexCount; i++) {
            tmpV.set(this.vertices[(i + 1) % vertexCount]);
            tmpV.sub(this.vertices[i]);

            // Take the right normal
            normals[i].set(tmpV.y, -tmpV.x);
            normals[i].normalize();
        }

        computeMassAndInertia();
        computeAabb();
    }

    /**
     * Generates rectangle based on coordinates of center, width and height, density and restitution.
     *
     * @param centerX the X coordinate of center of rectangle
     * @param centerY the Y coordinate of center of rectangle
     * @param width the width of rectangle
     * @param height the height of rectangle
     * @param density the density of rectangle
     * @param restitution the restitution of rectangle
     * @return the rectangle
     */
    public static Polygon generateRectangle(float centerX, float centerY, float width, float height, float density,
            float restitution) {
        List<Vector2f> vertices = new ArrayList<>(4);
        vertices.add(new Vector2f(-width / 2f, -height / 2f));
        vertices.add(new Vector2f(width / 2f, -height / 2f));
        vertices.add(new Vector2f(width / 2f, height / 2f));
        vertices.add(new Vector2f(-width / 2f, height / 2f));
        return new Polygon(density, restitution, centerX, centerY, vertices);
    }

    /**
     * Gets the count of polygon vertices.
     *
     * @return the count of vertices
     */
    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * Gets the copy of array of polygon vertices.
     *
     * @return the array of vertices
     */
    public Vector2f[] getVertices() {
        return Arrays.copyOf(vertices, vertices.length);
    }

    /**
     * Gets the copy of array of polygon normals.
     *
     * @return the array of normals
     */
    public Vector2f[] getNormals() {
        return Arrays.copyOf(normals, normals.length);
    }

    @Override
    public void computeAabb() {
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

    /**
     * Calculates and return the most distant polygon vertex in a passed direction.
     *
     * @param direction the direction in which the search for the most distant vertex will occur
     * @return a new vector object that coincides in coordinates with the most
     * distant polygon vertex in the given direction
     */
    public Vector2f calculateSupportVertex(Vector2f direction) {
        // Looking for the most distant vertex in a given direction
        float bestProjection = -Float.MAX_VALUE;
        final Vector2f bestVertex = new Vector2f();

        for (int i = 0; i < vertexCount; ++i) {
            Vector2f v = vertices[i];
            float projection = Vector2f.dotProduct(v, direction);

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

    private static List<Integer> calculateHullIndices(List<Vector2f> vertices) {
        Vector2f tmpV = new Vector2f();
        Vector2f tmpV2 = new Vector2f();

        // Jarvis's algorithm for constructing a minimal convex hull.
        // Find the lowest and rightmost coordinate, it will become the
        // starting point, and exactly belongs to the MCH (min. convex hull)
        tmpV.set(vertices.get(0));
        int rightMost = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Vector2f currentVec = vertices.get(i);
            if (currentVec.x > tmpV.x) {
                tmpV.set(currentVec);
                rightMost = i;
            } else if (tmpV.x == currentVec.x && currentVec.y < tmpV.y) {
                tmpV.set(currentVec);
                rightMost = i;
            }
        }

        List<Integer> hull = new ArrayList<>();
        int outCount = 0;
        int indexHull = rightMost;

        while (true){
            hull.add(indexHull);

            // Looking for the vertex with the largest angle counterclockwise from the current vertex
            // (Calculate the angle through the vector product)
            int nextHullIndex = 0;
            for (int i = 1; i < vertices.size(); ++i) {
                // Skip the same vertices, because need unique vertices in the triangle
                if (nextHullIndex == indexHull) {
                    nextHullIndex = i;
                    continue;
                }
                // Sort through all the triangles, looking for the most extreme vertex
                tmpV.set(vertices.get(nextHullIndex));
                tmpV.sub(vertices.get(hull.get(outCount)));

                tmpV2.set(vertices.get(i));
                tmpV2.sub(vertices.get(hull.get(outCount)));
                float c = Vector2f.crossProduct(tmpV, tmpV2);
                if (c < -MathPie.EPSILON) {
                    nextHullIndex = i;
                }
                // If the vector product is 0, then they lie on one straight line,
                // and need the vertex farthest from the given vertex
                if (MathPie.areEqual(c, 0) && tmpV2.lengthWithoutSqrt() > tmpV.lengthWithoutSqrt()) {
                    nextHullIndex = i;
                }
            }

            outCount++;
            indexHull = nextHullIndex;

            // When reached the starting vertex, the Jarvis algorithm is complete
            if (nextHullIndex == rightMost) {
                break;
            }
        }
        return hull;
    }
}
