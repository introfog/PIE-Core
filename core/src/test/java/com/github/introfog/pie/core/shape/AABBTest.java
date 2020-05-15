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
public class AABBTest extends PIETest {
    @Test
    public void defaultConstructorTest() {
        AABB aabb = new AABB();

        Vector2f temp = new Vector2f();
        Assert.assertEquals(temp, aabb.min);
        Assert.assertEquals(temp, aabb.max);
    }

    @Test
    public void isIntersectedTest() {
        AABB a = new AABB();
        AABB b = new AABB();

        a.min.set(0, 0);
        a.max.set(5, 10);
        b.min.set(6, 5);
        b.max.set(10, 15);
        // X axis separation case
        Assert.assertFalse(AABB.isIntersected(a, b));
        a.min.set(6, 5);
        a.max.set(10, 15);
        b.min.set(0, 0);
        b.max.set(5, 10);
        // X axis separation case
        Assert.assertFalse(AABB.isIntersected(a, b));

        a.min.set(0, 0);
        a.max.set(7, 10);
        b.min.set(6, 5);
        b.max.set(10, 15);
        Assert.assertTrue(AABB.isIntersected(a, b));

        a.min.set(0, 0);
        a.max.set(5, 10);
        b.min.set(2, 11);
        b.max.set(7, 17);
        // Y axis separation case
        Assert.assertFalse(AABB.isIntersected(a, b));
        a.min.set(2, 11);
        a.max.set(7, 17);
        b.min.set(0, 0);
        b.max.set(5, 10);
        // Y axis separation case
        Assert.assertFalse(AABB.isIntersected(a, b));
    }
}
