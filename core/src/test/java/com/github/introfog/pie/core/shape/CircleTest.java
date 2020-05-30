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

import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CircleTest extends PIETest {
    @Test
    public void paramConstructorTest() {
        Circle circle = new Circle(10, 1, 3, 0.1f, 0.2f);

        Assert.assertEquals(ShapeType.circle, circle.type);
        Assert.assertEquals(new Vector2f(1, 3), circle.body.position);
        Assert.assertEquals(0.1f, circle.body.density, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.2f, circle.body.restitution, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void computeAABBTest() {
        Circle circle = new Circle(10, 1, 3, 0.1f, 0.2f);

        Assert.assertEquals(new Vector2f(-9, -7), circle.aabb.min);
        Assert.assertEquals(new Vector2f(11, 13), circle.aabb.max);
    }

    @Test
    public void equalsAndHashCodeItselfTest() {
        Circle circle = new Circle(10, 1, 3, 0.1f, 0.2f);

        Assert.assertTrue(circle.equals(circle));
        Assert.assertEquals(circle.hashCode(), circle.hashCode());
    }

    @Test
    public void equalsAndHashCodeToAnotherEqualCircleTest() {
        Circle first = new Circle(10, 1, 3, 0.1f, 0.2f);
        Circle second = new Circle(10, 1, 3, 0.1f, 0.2f);

        PIETest.checkEqualsAndHashCodeMethods(first, second, true);
    }

    @Test
    public void equalsAndHashCodeToAnotherNotEqualCircleTest() {
        Circle first = new Circle(10, 1, 3, 0.1f, 0.2f);
        Circle second = new Circle(11, 1, 3, 0.1f, 0.2f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = new Circle(10, 2, 3, 0.1f, 0.2f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = new Circle(10, 1, 4, 0.1f, 0.2f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = new Circle(10, 1, 3, 0.2f, 0.2f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = new Circle(10, 1, 3, 0.1f, 0.3f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = new Circle(10, 1, 3, 0.1f, 0.2f);
        second.type = ShapeType.polygon;
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);
    }

    @Test
    public void equalsToNullTest() {
        Circle circle = new Circle(10, 1, 3, 0.1f, 0.2f);

        Assert.assertFalse(circle.equals(null));
    }

    @Test
    public void computeMassAndInertiaTest() {
        Circle circle = new Circle(10, 0, 0, (float) (1 / Math.PI), 0);

        Assert.assertEquals(1f / 100f, circle.body.invertMass, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1f / 10000f, circle.body.invertInertia, PIETest.FLOAT_EPSILON_COMPARISON);
    }
}