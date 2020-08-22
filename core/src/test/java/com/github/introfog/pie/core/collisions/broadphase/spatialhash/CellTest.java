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
package com.github.introfog.pie.core.collisions.broadphase.spatialhash;

import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CellTest extends PIETest {
    @Test
    public void constructorTest() {
        Cell cell = new Cell(10.00001f, 29.99f, 10);

        Assert.assertEquals(1, cell.x);
        Assert.assertEquals(2, cell.y);
    }

    @Test
    public void equalsAndHashCodeItselfTest() {
        Cell cell = new Cell(10, 20, 10);

        Assert.assertTrue(cell.equals(cell));
        Assert.assertEquals(cell.hashCode(), cell.hashCode());
    }

    @Test
    public void equalsAndHashCodeToAnotherEqualBodyTest() {
        Cell first = new Cell(10, 20, 10);
        Cell second = new Cell(10, 20, 10);

        PIETest.checkEqualsAndHashCodeMethods(first, second, true);
    }

    @Test
    public void equalsAndHashCodeToAnotherNotEqualBodyTest() {
        Cell first = new Cell(30, 20, 10);
        Cell second = new Cell(10, 20, 10);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = new Cell(10, 30, 10);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);
    }

    @Test
    public void equalsToNullTest() {
        Cell cell = new Cell(10, 20, 10);

        Assert.assertFalse(cell.equals(null));
    }
}
