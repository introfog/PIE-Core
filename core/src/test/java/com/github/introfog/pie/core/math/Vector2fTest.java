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
package com.github.introfog.pie.core.math;

import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class Vector2fTest extends PIETest {
    @Test
    public void defaultConstructorTest() {
        Vector2f vec = new Vector2f();
        Assert.assertEquals(0, vec.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0, vec.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void paramConstructorTest() {
        Vector2f vec = new Vector2f(1.23f, 2.07f);
        Assert.assertEquals(1.23f, vec.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(2.07f, vec.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void copyConstructorTest() {
        Vector2f vec = new Vector2f(1.23f, 2.07f);
        Vector2f copy = new Vector2f(vec);
        Assert.assertEquals(1.23f, copy.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(2.07f, copy.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void equalsAndHashCodeItselfTest() {
        Vector2f vec = new Vector2f(0.1f, 0.2f);

        Assert.assertTrue(vec.equals(vec));
        Assert.assertEquals(vec.hashCode(), vec.hashCode());
    }

    @Test
    public void equalsAndHashCodeToAnotherEqualBodyTest() {
        Vector2f first = new Vector2f(0.1f, 0.2f);
        Vector2f second = new Vector2f(0.1f, 0.2f);

        PIETest.checkEqualsAndHashCodeMethods(first, second, true);
    }

    @Test
    public void equalsAndHashCodeToAnotherNotEqualBodyTest() {
        Vector2f first = new Vector2f(0.1f, 0.2f);
        Vector2f second = new Vector2f(0.2f, 0.2f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = new Vector2f(0.1f, 0.3f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);
    }

    @Test
    public void equalsToNullTest() {
        Vector2f vec = new Vector2f(0.1f, 0.2f);

        Assert.assertFalse(vec.equals(null));
    }

    @Test
    public void toStringTest() {
        Vector2f vec = new Vector2f(0.1f, 0.2f);
        Assert.assertEquals("Vector2f [0.1][0.2]", vec.toString());
    }

    @Test
    public void setCoordinatesTest() {
        Vector2f vec = new Vector2f(0.1f, 0.2f);
        vec.set(0.3f, 0.4f);
        Assert.assertEquals(0.3f, vec.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.4f, vec.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setVector2fTest() {
        Vector2f vec = new Vector2f(0.1f, 0.2f);
        Vector2f temp = new Vector2f(0.3f, 0.4f);
        vec.set(temp);
        Assert.assertEquals(0.3f, vec.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.4f, vec.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void lengthWithoutSqrtTest() {
        Vector2f vec = new Vector2f(1f, 2f);
        Assert.assertEquals(5, vec.lengthWithoutSqrt(), PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void mulByFloatTest() {
        Vector2f vec = new Vector2f(0.1f, 0.2f);
        vec.mul(10);
        Assert.assertEquals(1, vec.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(2, vec.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void subByVector2fTest() {
        Vector2f vec = new Vector2f(0.1f, 0.2f);
        Vector2f temp = new Vector2f(0.3f, 0.5f);
        vec.sub(temp);
        Assert.assertEquals(-0.2f, vec.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(-0.3f, vec.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void addByVector2fAndByFloatTest() {
        Vector2f vec = new Vector2f(0.1f, 0.2f);
        Vector2f temp = new Vector2f(0.3f, 0.5f);
        vec.add(temp, 2);
        Assert.assertEquals(0.7f, vec.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.2f, vec.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void addByVector2fTest() {
        Vector2f vec = new Vector2f(0.1f, 0.2f);
        Vector2f temp = new Vector2f(0.3f, 0.5f);
        vec.add(temp);
        Assert.assertEquals(0.4f, vec.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.7f, vec.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void normalizeTest() {
        Vector2f vec = new Vector2f(1, 0);
        vec.normalize();
        Assert.assertEquals(1, vec.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0, vec.y, PIETest.FLOAT_EPSILON_COMPARISON);

        vec = new Vector2f(1, 1);
        vec.normalize();
        Assert.assertEquals(1 / Math.sqrt(2), vec.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1 / Math.sqrt(2), vec.y, PIETest.FLOAT_EPSILON_COMPARISON);

        vec = new Vector2f(0, 0);
        vec.normalize();
        Assert.assertEquals(0, vec.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0, vec.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void negativeTest() {
        Vector2f vec = new Vector2f(0.1f, 0.2f);
        vec.negative();
        Assert.assertEquals(-0.1f, vec.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(-0.2f, vec.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void staticMulByFloatTest() {
        Vector2f vec = new Vector2f(0.1f, 0.2f);
        Vector2f res = Vector2f.mul(vec, 10);
        Assert.assertEquals(1, res.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(2, res.y, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertTrue(vec != res);
    }

    @Test
    public void staticMulByVector2fTest() {
        Vector2f first = new Vector2f(0.1f, 0.2f);
        Vector2f second = new Vector2f(10f, 20f);
        Vector2f res = Vector2f.mul(first, second);
        Assert.assertEquals(1, res.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(4, res.y, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertTrue(res != first);
        Assert.assertTrue(res != second);
    }

    @Test
    public void staticSubByVector2fTest() {
        Vector2f first = new Vector2f(0.1f, 0.2f);
        Vector2f second = new Vector2f(10f, 20f);
        Vector2f res = Vector2f.sub(first, second);

        Assert.assertEquals(-9.9, res.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(-19.8, res.y, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertTrue(res != first);
        Assert.assertTrue(res != second);
    }

    @Test
    public void staticDistanceWithoutSqrtBetweenVector2f() {
        Vector2f first = new Vector2f(0f, 3f);
        Vector2f second = new Vector2f(1f, 5f);
        Assert.assertEquals(5, Vector2f.distanceWithoutSqrt(first, second), PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(5, Vector2f.distanceWithoutSqrt(second, first), PIETest.FLOAT_EPSILON_COMPARISON);

        Assert.assertEquals(0, Vector2f.distanceWithoutSqrt(first, first), PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void staticDotProductBetweenVector2f() {
        Vector2f first = new Vector2f(0f, 3f);
        Vector2f second = new Vector2f(1f, 5f);
        Assert.assertEquals(15, Vector2f.dotProduct(first, second), PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(15, Vector2f.dotProduct(second, first), PIETest.FLOAT_EPSILON_COMPARISON);

        Assert.assertEquals(9, Vector2f.dotProduct(first, first), PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void staticCrossProductBetweenVector2f() {
        Vector2f first = new Vector2f(0f, 3f);
        Vector2f second = new Vector2f(1f, 5f);

        Assert.assertEquals(-3, Vector2f.crossProduct(first, second), PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(3, Vector2f.crossProduct(second, first), PIETest.FLOAT_EPSILON_COMPARISON);

        Assert.assertEquals(0, Vector2f.crossProduct(first, first), PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void staticCrossProductBetweenVector2fAndFloat() {
        Vector2f vec = new Vector2f(1f, 3f);

        Vector2f actual = Vector2f.crossProduct(10, vec);
        Vector2f expected = new Vector2f(-30, 10);

        Assert.assertEquals(expected, actual);
        Assert.assertNotEquals(vec, actual);
    }

    @Test
    public void arrayOfTest() {
        Vector2f[] vectors = Vector2f.arrayOf(2);
        Vector2f expected = new Vector2f();
        Assert.assertEquals(expected, vectors[0]);
        Assert.assertEquals(expected, vectors[1]);

        Assert.assertTrue(vectors[0] != vectors[1]);
    }
}
