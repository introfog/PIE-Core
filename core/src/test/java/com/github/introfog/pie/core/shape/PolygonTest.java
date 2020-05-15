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

    // TODO review this test
//    @Test
//    public void problemInMCHTest() {
//        float radius = 10;
//        Vector2f[] vertices = Vector2f.arrayOf(MathPIE.MAX_POLY_VERTEX_COUNT + 2);
//        for (int i = 0; i < MathPIE.MAX_POLY_VERTEX_COUNT + 1; i++) {
//            float cos = (float) Math.cos(2 * Math.PI * i / MathPIE.MAX_POLY_VERTEX_COUNT);
//            float sin = (float) Math.sin(2 * Math.PI * i / MathPIE.MAX_POLY_VERTEX_COUNT);
//
//            vertices[i].set(cos * radius, sin * radius);
//        }
//        vertices[MathPIE.MAX_POLY_VERTEX_COUNT + 1].set(0, 0);
//
//        Polygon polygon = new Polygon(0.1f, 0.2f, 0, 0, vertices);
//    }

    @Test
    public void aLotOfVerticesExceptionTest() {
        // TODO use custom PIE exception
        junitExpectedException.expect(RuntimeException.class);
        junitExpectedException.expectMessage("Error. Too many vertices in polygon.");

        float radius = 10;
        int verticesInCircle = MathPIE.MAX_POLY_VERTEX_COUNT + 1;
        Vector2f[] vertices = Vector2f.arrayOf(verticesInCircle + 1);
        for (int i = 0; i < verticesInCircle; i++) {
            float cos = (float) Math.cos(2 * Math.PI * i / verticesInCircle);
            float sin = (float) Math.sin(2 * Math.PI * i / verticesInCircle);

            vertices[i].set(cos * radius, sin * radius);
        }
        vertices[verticesInCircle].set(0, 0);
        new Polygon(0.1f, 0.2f, 0, 0, vertices);
    }

    @Test
    public void equalsAndHashCodeItselfTest() {
        Polygon polygon = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);

        Assert.assertTrue(polygon.equals(polygon));
        Assert.assertEquals(polygon.hashCode(), polygon.hashCode());
    }

    @Test
    public void equalsAndHashCodeToAnotherEqualCircleTest() {
        Polygon first = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);
        Polygon second = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);

        PIETest.checkEqualsAndHashCodeMethods(first, second, true);
    }

    @Test
    public void equalsAndHashCodeToAnotherNotEqualCircleTest() {
        Polygon first = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);
        Polygon second = Polygon.generateRectangle(11, 1, 3, 4, 0.1f, 0.2f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = Polygon.generateRectangle(10, 2, 3, 4, 0.1f, 0.2f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = Polygon.generateRectangle(10, 1, 3.5f, 4, 0.1f, 0.2f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = Polygon.generateRectangle(10, 1, 3, 5, 0.12f, 0.2f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.22f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);
        second.type = ShapeType.circle;
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);
        second.vertices[2].x += 0.5f;
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);
        second.vertexCount++;
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);
        Vector2f temp = new Vector2f(second.vertices[3]);
        second.vertices[3].set(second.vertices[1]);
        second.vertices[1].set(temp);
        Assert.assertFalse(first.equals(second));
        Assert.assertFalse(second.equals(first));
        // It is not clear why the hash codes for the objects are the same, they
        // must be different, so the standard hash code generator is not ideal.
        Assert.assertEquals(first.hashCode(), second.hashCode());

        second = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);
        second.normals[0].x += 1;
        PIETest.checkEqualsAndHashCodeMethods(first, second, true);
    }

    @Test
    public void equalsToNullTest() {
        Polygon polygon = Polygon.generateRectangle(10, 1, 3, 4, 0.1f, 0.2f);

        Assert.assertFalse(polygon.equals(null));
    }

    @Test
    public void computeMassAndInertiaTest() {
        Polygon polygon = Polygon.generateRectangle(0, 0, 1, 2, 5, 0.2f);

        Assert.assertEquals(1f / 10f, polygon.body.invertMass, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1f / 4.166666f, polygon.body.invertInertia, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getSupportTest() {
        Polygon polygon = Polygon.generateRectangle(0, 0, 10, 20, 0, 0);

        Assert.assertEquals(new Vector2f(5, 10), polygon.getSupport(new Vector2f(100, 0.1f)));
        Assert.assertEquals(new Vector2f(5, -10), polygon.getSupport(new Vector2f(100, -0.1f)));
    }
}
