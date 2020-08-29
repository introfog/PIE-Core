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

import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PolygonTest extends PIETest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void staticGenerateRectangleTest() {
        Polygon rectangle = Polygon.generateRectangle(0, 0, 15, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        Assert.assertEquals(4, rectangle.vertexCount);
        Assert.assertEquals(4, rectangle.normals.length);
        Assert.assertEquals(4, rectangle.vertices.length);

        Vector2f vec = new Vector2f(7.5f, -5.0f);
        Assert.assertEquals(vec, rectangle.vertices[0]);
        vec.set(7.5f, 5.0f);
        Assert.assertEquals(vec, rectangle.vertices[1]);
        vec.set(-7.5f, 5.0f);
        Assert.assertEquals(vec, rectangle.vertices[2]);
        vec.set(-7.5f, -5.0f);
        Assert.assertEquals(vec, rectangle.vertices[3]);

        vec.set(1, -0.0f);
        Assert.assertEquals(vec, rectangle.normals[0]);
        vec.set(0, 1);
        Assert.assertEquals(vec, rectangle.normals[1]);
        vec.set(-1, -0.0f);
        Assert.assertEquals(vec, rectangle.normals[2]);
        vec.set(0, -1);
        Assert.assertEquals(vec, rectangle.normals[3]);

        vec.set(-7.5f, -5.0f);
        Assert.assertEquals(vec, rectangle.aabb.min);
        vec.set(7.5f, 5.0f);
        Assert.assertEquals(vec, rectangle.aabb.max);
    }

    @Test
    public void paramConstructorTest() {
        Vector2f[] vertices = Vector2f.arrayOf(8);
        vertices[0].set(0, 0);
        vertices[1].set(7.5f, 5);
        vertices[2].set(7.5f, -5);
        vertices[3].set(-7.5f, -5);
        vertices[4].set(-7.5f, 5);
        vertices[5].set(-6.5f, 5);
        vertices[6].set(7, -3);
        vertices[7].set(0, 4.99f);

        Polygon polygon = new Polygon(0.1f, 0.2f, 0, 0, vertices);
        Assert.assertEquals(4, polygon.vertexCount);
        Assert.assertEquals(4, polygon.normals.length);
        Assert.assertEquals(4, polygon.vertices.length);

        // Check that the polygon has become convex (there are only 4 vertices)
        Vector2f vec = new Vector2f(7.5f, -5.0f);
        Assert.assertEquals(vec, polygon.vertices[0]);
        vec.set(7.5f, 5.0f);
        Assert.assertEquals(vec, polygon.vertices[1]);
        vec.set(-7.5f, 5.0f);
        Assert.assertEquals(vec, polygon.vertices[2]);
        vec.set(-7.5f, -5.0f);
        Assert.assertEquals(vec, polygon.vertices[3]);

        vec.set(1, -0.0f);
        Assert.assertEquals(vec, polygon.normals[0]);
        vec.set(0, 1);
        Assert.assertEquals(vec, polygon.normals[1]);
        vec.set(-1, -0.0f);
        Assert.assertEquals(vec, polygon.normals[2]);
        vec.set(0, -1);
        Assert.assertEquals(vec, polygon.normals[3]);

        vec.set(-7.5f, -5.0f);
        Assert.assertEquals(vec, polygon.aabb.min);
        vec.set(7.5f, 5.0f);
        Assert.assertEquals(vec, polygon.aabb.max);
    }
    
    @Test
    @Ignore("TODO #69 IndexOutOfBoundsException is thrown")
    public void verticesInCircleTest() {
        float radius = 10;
        Vector2f[] vertices = Vector2f.arrayOf(MathPIE.MAX_POLY_VERTEX_COUNT + 2);
        for (int i = 0; i < MathPIE.MAX_POLY_VERTEX_COUNT + 1; i++) {
            float cos = (float) Math.cos(2 * Math.PI * i / MathPIE.MAX_POLY_VERTEX_COUNT);
            float sin = (float) Math.sin(2 * Math.PI * i / MathPIE.MAX_POLY_VERTEX_COUNT);

            vertices[i].set(cos * radius, sin * radius);
        }
        vertices[MathPIE.MAX_POLY_VERTEX_COUNT + 1].set(0, 0);

        new Polygon(0.1f, 0.2f, 0, 0, vertices);
    }

    @Test
    public void aLotOfVerticesExceptionTest() {
        float radius = 10;
        int verticesInCircle = MathPIE.MAX_POLY_VERTEX_COUNT + 1;
        Vector2f[] vertices = Vector2f.arrayOf(verticesInCircle + 1);
        for (int i = 0; i < verticesInCircle; i++) {
            float cos = (float) Math.cos(2 * Math.PI * i / verticesInCircle);
            float sin = (float) Math.sin(2 * Math.PI * i / verticesInCircle);

            vertices[i].set(cos * radius, sin * radius);
        }
        vertices[verticesInCircle].set(0, 0);

        // TODO use custom PIE exception
        junitExpectedException.expect(RuntimeException.class);
        junitExpectedException.expectMessage("Error. Too many vertices in polygon.");
        new Polygon(0.1f, 0.2f, 0, 0, vertices);
    }

    @Test
    public void equalsAndHashCodeItselfTest() {
        Polygon polygon = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);

        Assert.assertEquals(polygon, polygon);
        Assert.assertEquals(polygon.hashCode(), polygon.hashCode());
    }

    @Test
    public void notEqualsAndHashCodeToAnotherEqualCircleTest() {
        Polygon first = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);
        Polygon second = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);

        PIETest.checkEqualsAndHashCodeMethods(first, second, false);
    }

    @Test
    public void equalsToNullTest() {
        Polygon polygon = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);

        Assert.assertFalse(polygon.equals(null));
    }

    @Test
    public void equalsToAnotherClassTest() {
        Polygon polygon = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);

        Assert.assertFalse(polygon.equals(""));
    }

    @Test
    public void computeMassAndInertiaTest() {
        Polygon polygon = Polygon.generateRectangle(0, 0, 1, 2, 5, 0.2f);

        Assert.assertEquals(1f / 10f, polygon.body.invertedMass, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1f / 4.166666f, polygon.body.invertedInertia, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getSupportTest() {
        Polygon polygon = Polygon.generateRectangle(0, 0, 10, 20, 0, 0);

        Assert.assertEquals(new Vector2f(5, 10), polygon.getSupport(new Vector2f(100, 0.1f)));
        Assert.assertEquals(new Vector2f(5, -10), polygon.getSupport(new Vector2f(100, -0.1f)));
    }

    @Test
    public void toStringTest() {
        Polygon polygon = Polygon.generateRectangle(0.23412f, 1.3f, 2.5f, 6.3f, 2.3f, 0.2f);
        Assert.assertEquals("{center={0.23412; 1.3}; vertices=[{1.25; -3.15}, "
                + "{1.25; 3.15}, {-1.25; 3.15}, {-1.25; -3.15}]}", polygon.toString());
    }
}
