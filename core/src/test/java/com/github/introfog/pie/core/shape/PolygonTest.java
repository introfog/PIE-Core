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
import com.github.introfog.pie.test.PieTest;
import com.github.introfog.pie.test.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PolygonTest extends PieTest {
    @Test
    public void staticGenerateRectangleTest() {
        Polygon rectangle = Polygon.generateRectangle(0, 0, 15, 10, MathPie.STATIC_BODY_DENSITY, 0.2f);
        Assert.assertEquals(4, rectangle.getVertices().length);
        Assert.assertEquals(4, rectangle.getNormals().length);
        Assert.assertEquals(4, rectangle.getVertices().length);

        Vector2f vec = new Vector2f(7.5f, -5.0f);
        Assert.assertEquals(vec, rectangle.getVertices()[0]);
        vec.set(7.5f, 5.0f);
        Assert.assertEquals(vec, rectangle.getVertices()[1]);
        vec.set(-7.5f, 5.0f);
        Assert.assertEquals(vec, rectangle.getVertices()[2]);
        vec.set(-7.5f, -5.0f);
        Assert.assertEquals(vec, rectangle.getVertices()[3]);

        vec.set(1, -0.0f);
        Assert.assertEquals(vec, rectangle.getNormals()[0]);
        vec.set(0, 1);
        Assert.assertEquals(vec, rectangle.getNormals()[1]);
        vec.set(-1, -0.0f);
        Assert.assertEquals(vec, rectangle.getNormals()[2]);
        vec.set(0, -1);
        Assert.assertEquals(vec, rectangle.getNormals()[3]);

        vec.set(-7.5f, -5.0f);
        Assert.assertEquals(vec, rectangle.getAabb().min);
        vec.set(7.5f, 5.0f);
        Assert.assertEquals(vec, rectangle.getAabb().max);
    }

    @Test
    public void paramConstructorTest() {
        List<Vector2f> vertices = new ArrayList<>(8);
        vertices.add(new Vector2f(0, 0));
        vertices.add(new Vector2f(7.5f, 5));
        vertices.add(new Vector2f(7.5f, -5));
        vertices.add(new Vector2f(-7.5f, -5));
        vertices.add(new Vector2f(-7.5f, 5));
        vertices.add(new Vector2f(-6.5f, 5));
        vertices.add(new Vector2f(7, -3));
        vertices.add(new Vector2f(0, 4.99f));

        Polygon polygon = new Polygon(0.1f, 0.2f, 0, 0, vertices);
        Assert.assertEquals(4, polygon.getVertices().length);
        Assert.assertEquals(4, polygon.getNormals().length);
        Assert.assertEquals(4, polygon.getVertices().length);

        // Check that the polygon has become convex (there are only 4 vertices)
        Vector2f vec = new Vector2f(7.5f, -5.0f);
        Assert.assertEquals(vec, polygon.getVertices()[0]);
        vec.set(7.5f, 5.0f);
        Assert.assertEquals(vec, polygon.getVertices()[1]);
        vec.set(-7.5f, 5.0f);
        Assert.assertEquals(vec, polygon.getVertices()[2]);
        vec.set(-7.5f, -5.0f);
        Assert.assertEquals(vec, polygon.getVertices()[3]);

        vec.set(1, -0.0f);
        Assert.assertEquals(vec, polygon.getNormals()[0]);
        vec.set(0, 1);
        Assert.assertEquals(vec, polygon.getNormals()[1]);
        vec.set(-1, -0.0f);
        Assert.assertEquals(vec, polygon.getNormals()[2]);
        vec.set(0, -1);
        Assert.assertEquals(vec, polygon.getNormals()[3]);

        vec.set(-7.5f, -5.0f);
        Assert.assertEquals(vec, polygon.getAabb().min);
        vec.set(7.5f, 5.0f);
        Assert.assertEquals(vec, polygon.getAabb().max);
    }
    
    @Test
    public void verticesInCircleTest() {
        float radius = 10;
        List<Vector2f> vertices = new ArrayList<>(MathPie.MAX_POLY_VERTEX_COUNT + 2);
        for (int i = 0; i < MathPie.MAX_POLY_VERTEX_COUNT + 1; i++) {
            float cos = (float) Math.cos(2 * Math.PI * i / MathPie.MAX_POLY_VERTEX_COUNT);
            float sin = (float) Math.sin(2 * Math.PI * i / MathPie.MAX_POLY_VERTEX_COUNT);

            vertices.add(new Vector2f(cos * radius, sin * radius));
        }
        vertices.add(new Vector2f(0, 0));

        Polygon polygon = new Polygon(0.1f, 0.2f, 0, 0, vertices);
        Assert.assertEquals(MathPie.MAX_POLY_VERTEX_COUNT, polygon.getVertices().length);
    }

    @Test
    public void aLotOfVerticesExceptionTest() {
        float radius = 10;
        int verticesInCircle = MathPie.MAX_POLY_VERTEX_COUNT + 1;
        List<Vector2f> vertices = new ArrayList<>(verticesInCircle + 1);
        for (int i = 0; i < verticesInCircle; i++) {
            float cos = (float) Math.cos(2 * Math.PI * i / verticesInCircle);
            float sin = (float) Math.sin(2 * Math.PI * i / verticesInCircle);

            vertices.add(new Vector2f(cos * radius, sin * radius));
        }
        vertices.add(new Vector2f(0, 0));

        // TODO use custom Pie exception
        Assert.assertThrows("Error. Too many vertices in polygon.",
                RuntimeException.class, () -> new Polygon(0.1f, 0.2f, 0, 0, vertices));
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

        PieTest.checkEqualsAndHashCodeMethods(first, second, false);
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

        Assert.assertEquals(1f / 10f, polygon.getBody().getInvertedMass(), PieTest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1f / 4.166666f, polygon.getBody().getInvertedInertia(), PieTest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getSupportTest() {
        Polygon polygon = Polygon.generateRectangle(0, 0, 10, 20, 0, 0);

        Assert.assertEquals(new Vector2f(5, 10), polygon.calculateSupportVertex(new Vector2f(100, 0.1f)));
        Assert.assertEquals(new Vector2f(5, -10), polygon.calculateSupportVertex(new Vector2f(100, -0.1f)));
    }

    @Test
    public void toStringTest() {
        Polygon polygon = Polygon.generateRectangle(0.23412f, 1.3f, 2.5f, 6.3f, 2.3f, 0.2f);
        Assert.assertEquals("{center={0.23412; 1.3}; vertices=[{1.25; -3.15}, "
                + "{1.25; 3.15}, {-1.25; 3.15}, {-1.25; -3.15}]}", polygon.toString());
    }
}
