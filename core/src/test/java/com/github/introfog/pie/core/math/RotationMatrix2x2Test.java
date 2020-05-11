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

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class RotationMatrix2x2Test extends PIETest {
    @Test
    public void setAngleTest() throws NoSuchFieldException, IllegalAccessException {
        RotationMatrix2x2 matrix = new RotationMatrix2x2();
        matrix.setAngle((float) Math.PI / 3);

        Field field = RotationMatrix2x2.class.getDeclaredField("m00");
        field.setAccessible(true);
        Assert.assertEquals(0.5f, (float) field.get(matrix), PIETest.FLOAT_EPSILON_COMPARISON);
        field = RotationMatrix2x2.class.getDeclaredField("m01");
        field.setAccessible(true);
        Assert.assertEquals(-Math.sqrt(3) / 2, (float) field.get(matrix), PIETest.FLOAT_EPSILON_COMPARISON);
        field = RotationMatrix2x2.class.getDeclaredField("m10");
        field.setAccessible(true);
        Assert.assertEquals(Math.sqrt(3) / 2, (float) field.get(matrix), PIETest.FLOAT_EPSILON_COMPARISON);
        field = RotationMatrix2x2.class.getDeclaredField("m11");
        field.setAccessible(true);
        Assert.assertEquals(0.5f, (float) field.get(matrix), PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void mulMatrixOnVectorTest() {
        RotationMatrix2x2 matrix = new RotationMatrix2x2();
        matrix.setAngle((float) Math.PI / 3);

        Vector2f in = new Vector2f(1, 2);
        Vector2f out = new Vector2f();
        matrix.mul(in, out);
        Assert.assertEquals(0.5 - Math.sqrt(3), out.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.0 + Math.sqrt(3) / 2, out.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void mulMatrixOnCoordinatesTest() {
        RotationMatrix2x2 matrix = new RotationMatrix2x2();
        matrix.setAngle((float) Math.PI / 3);

        Vector2f out = new Vector2f();
        matrix.mul(2, 1, out);
        Assert.assertEquals(1.0 - Math.sqrt(3) / 2, out.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.5 + Math.sqrt(3), out.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void transposeMulMatrixOnVectorTest() {
        RotationMatrix2x2 matrix = new RotationMatrix2x2();
        matrix.setAngle((float) Math.PI / 3);

        Vector2f in = new Vector2f(1, 2);
        Vector2f out = new Vector2f();
        matrix.transposeMul(in, out);
        Assert.assertEquals(0.5 + Math.sqrt(3), out.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.0 - Math.sqrt(3) / 2, out.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void transposeMulMatrixOnCoordinatesTest() {
        RotationMatrix2x2 matrix = new RotationMatrix2x2();
        matrix.setAngle((float) Math.PI / 3);

        Vector2f out = new Vector2f();
        matrix.transposeMul(2, 1, out);
        Assert.assertEquals(1.0 + Math.sqrt(3) / 2, out.x, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.5 - Math.sqrt(3), out.y, PIETest.FLOAT_EPSILON_COMPARISON);
    }
}
