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
import com.github.introfog.pie.test.PieTest;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AabbTest extends PieTest {
    @Test
    public void defaultConstructorTest() {
        Aabb aabb = new Aabb();

        Vector2f temp = new Vector2f();
        Assert.assertEquals(temp, aabb.min);
        Assert.assertEquals(temp, aabb.max);
    }

    @Test
    public void isIntersectedTest() {
        Aabb a = new Aabb();
        Aabb b = new Aabb();

        a.min.set(0, 0);
        a.max.set(5, 10);
        b.min.set(6, 5);
        b.max.set(10, 15);
        // X axis separation case
        Assert.assertFalse(Aabb.isIntersected(a, b));
        a.min.set(6, 5);
        a.max.set(10, 15);
        b.min.set(0, 0);
        b.max.set(5, 10);
        // X axis separation case
        Assert.assertFalse(Aabb.isIntersected(a, b));

        a.min.set(0, 0);
        a.max.set(7, 10);
        b.min.set(6, 5);
        b.max.set(10, 15);
        Assert.assertTrue(Aabb.isIntersected(a, b));

        a.min.set(0, 0);
        a.max.set(5, 10);
        b.min.set(2, 11);
        b.max.set(7, 17);
        // Y axis separation case
        Assert.assertFalse(Aabb.isIntersected(a, b));
        a.min.set(2, 11);
        a.max.set(7, 17);
        b.min.set(0, 0);
        b.max.set(5, 10);
        // Y axis separation case
        Assert.assertFalse(Aabb.isIntersected(a, b));
    }

    @Test
    public void isContainedTest() {
        Aabb a = new Aabb();
        Aabb b = new Aabb();

        Assert.assertTrue(Aabb.isContained(a, a));

        a.min.set(0, 0);
        a.max.set(7, 10);
        b.min.set(6, 5);
        b.max.set(10, 15);
        // Aabbs are intersect
        Assert.assertFalse(Aabb.isContained(a, b));

        a.min.set(0, 0);
        a.max.set(7, 10);
        b.min.set(2, -1);
        b.max.set(5, 8);
        // Aabbs are intersect
        Assert.assertFalse(Aabb.isContained(a, b));

        a.min.set(0, 0);
        a.max.set(7, 10);
        b.min.set(2, 7);
        b.max.set(5, 11);
        // Aabbs are intersect
        Assert.assertFalse(Aabb.isContained(a, b));

        a.min.set(0, 0);
        a.max.set(10, 10);
        b.min.set(1, 1);
        b.max.set(2, 2);
        // a contain b
        Assert.assertTrue(Aabb.isContained(a, b));

        a.min.set(1, 1);
        a.max.set(2, 2);
        b.min.set(0, 0);
        b.max.set(10, 10);
        // b contain a
        Assert.assertFalse(Aabb.isContained(a, b));
    }

    @Test
    public void unionTest() {
        Aabb a = new Aabb();
        Aabb b = new Aabb();

        a.min.set(0, 0);
        a.max.set(7, 10);
        b.min.set(6, 5);
        b.max.set(10, 15);
        // Aabbs are intersect
        Aabb result = Aabb.union(a, b);
        Assert.assertEquals(new Vector2f(0, 0), result.min);
        Assert.assertEquals(new Vector2f(10, 15), result.max);

        a.min.set(0, 0);
        a.max.set(10, 10);
        b.min.set(1, 1);
        b.max.set(2, 2);
        // a contain b
        result = Aabb.union(a, b);
        Assert.assertEquals(a.min, result.min);
        Assert.assertEquals(a.max, result.max);

        a.min.set(1, 1);
        a.max.set(2, 2);
        b.min.set(0, 0);
        b.max.set(10, 10);
        // b contain a
        result = Aabb.union(a, b);
        Assert.assertEquals(b.min, result.min);
        Assert.assertEquals(b.max, result.max);
    }

    @Test
    public void surfaceAreaTest() {
        Aabb a = new Aabb();
        a.min.set(-1, 4);
        a.max.set(7, 10);
        Assert.assertEquals(48, a.surfaceArea(), PieTest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void deltaSurfaceAreaTest() {
        Aabb a = new Aabb();
        Aabb b = new Aabb();

        // Aabbs are intersect
        a.min.set(0, 0);
        a.max.set(7, 10);
        b.min.set(6, 5);
        b.max.set(10, 15);
        Assert.assertEquals(80, a.deltaSurfaceArea(b), PieTest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(110, b.deltaSurfaceArea(a), PieTest.FLOAT_EPSILON_COMPARISON);

        Assert.assertEquals(0, a.deltaSurfaceArea(a), PieTest.FLOAT_EPSILON_COMPARISON);

        // a contain b
        a.min.set(0, 0);
        a.max.set(10, 8);
        b.min.set(1, 1);
        b.max.set(2, 3);
        Assert.assertEquals(0, a.deltaSurfaceArea(b), PieTest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(78, b.deltaSurfaceArea(a), PieTest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void toStringTest() {
        Aabb aabb = new Aabb();
        aabb.min.set(1.23f, 0.12f);
        aabb.max.set(17.223434f, 123.45645f);
        Assert.assertEquals("{min={1.23; 0.12}; max={17.223434; 123.45645}}", aabb.toString());
    }
}
