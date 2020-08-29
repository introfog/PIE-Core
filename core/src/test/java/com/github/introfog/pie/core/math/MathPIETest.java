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
public class MathPIETest extends PIETest {
    @Test(expected = IllegalAccessException.class)
    public void constructorTest() throws IllegalAccessException, InstantiationException {
        MathPIE.class.newInstance();
        Assert.fail("Utility class constructor should be private");
    }

    @Test
    public void fastFloorTest() {
        Assert.assertEquals(1.0, MathPIE.fastFloor(1.0000001f), PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.0, MathPIE.fastFloor(1.00000001f), PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.0, MathPIE.fastFloor(1.23f), PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.0, MathPIE.fastFloor(1.9999999f), PIETest.FLOAT_EPSILON_COMPARISON);

        Assert.assertEquals(2.0, MathPIE.fastFloor(1.99999999f), PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void floatEqualTest() {
        Assert.assertTrue(MathPIE.equal(1.0f, 1.0f));
        Assert.assertTrue(MathPIE.equal(1.0f, 1.0f + MathPIE.EPSILON - PIETest.FLOAT_EPSILON_COMPARISON));

        Assert.assertFalse(MathPIE.equal(1.0f, 1.0f + MathPIE.EPSILON));
        Assert.assertFalse(MathPIE.equal(1.0f, 1.0f + MathPIE.EPSILON + PIETest.FLOAT_EPSILON_COMPARISON));
    }

    @Test
    public void gtTest() {
        float a = 1.0f;
        float b = a * (1.0f - MathPIE.BIAS_ABSOLUTE) / MathPIE.BIAS_RELATIVE;
        Assert.assertTrue(MathPIE.gt(a, b - PIETest.FLOAT_EPSILON_COMPARISON));
        Assert.assertFalse(MathPIE.gt(a, b + PIETest.FLOAT_EPSILON_COMPARISON));
    }
}
