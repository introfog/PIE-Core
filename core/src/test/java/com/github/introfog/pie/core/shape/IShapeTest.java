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

import com.github.introfog.pie.core.math.RotationMatrix2x2;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.test.PieTest;
import com.github.introfog.pie.test.annotations.UnitTest;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class IShapeTest extends PieTest {
    @Test
    public void defaultConstructorTest() throws NoSuchFieldException, IllegalAccessException {
        IShape shape = new Circle(10, 0,0, 0, 0);
        Assert.assertNotNull(shape.aabb);
        Assert.assertNotNull(shape.rotateMatrix);

        Field field = RotationMatrix2x2.class.getDeclaredField("m00");
        field.setAccessible(true);
        Assert.assertEquals(1, (float) field.get(shape.rotateMatrix), PieTest.FLOAT_EPSILON_COMPARISON);
        field = RotationMatrix2x2.class.getDeclaredField("m01");
        field.setAccessible(true);
        Assert.assertEquals(0, (float) field.get(shape.rotateMatrix), PieTest.FLOAT_EPSILON_COMPARISON);
        field = RotationMatrix2x2.class.getDeclaredField("m10");
        field.setAccessible(true);
        Assert.assertEquals(0, (float) field.get(shape.rotateMatrix), PieTest.FLOAT_EPSILON_COMPARISON);
        field = RotationMatrix2x2.class.getDeclaredField("m11");
        field.setAccessible(true);
        Assert.assertEquals(1, (float) field.get(shape.rotateMatrix), PieTest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setOrientationTest() throws NoSuchFieldException, IllegalAccessException {
        IShape shape = new Circle(10, 0,0, 0, 0);
        shape.setOrientation((float) Math.PI / 3);

        Assert.assertEquals((float) Math.PI / 3, shape.body.orientation, PieTest.FLOAT_EPSILON_COMPARISON);

        Field field = RotationMatrix2x2.class.getDeclaredField("m00");
        field.setAccessible(true);
        Assert.assertEquals(0.5f, (float) field.get(shape.rotateMatrix), PieTest.FLOAT_EPSILON_COMPARISON);
        field = RotationMatrix2x2.class.getDeclaredField("m01");
        field.setAccessible(true);
        Assert.assertEquals(-Math.sqrt(3) / 2, (float) field.get(shape.rotateMatrix), PieTest.FLOAT_EPSILON_COMPARISON);
        field = RotationMatrix2x2.class.getDeclaredField("m10");
        field.setAccessible(true);
        Assert.assertEquals(Math.sqrt(3) / 2, (float) field.get(shape.rotateMatrix), PieTest.FLOAT_EPSILON_COMPARISON);
        field = RotationMatrix2x2.class.getDeclaredField("m11");
        field.setAccessible(true);
        Assert.assertEquals(0.5f, (float) field.get(shape.rotateMatrix), PieTest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void applyImpulseTest() {
        IShape shape = new Circle(1, 0,0, (float) (1 / Math.PI), 0);
        shape.applyImpulse(new Vector2f(10, 10), new Vector2f(1, 0));

        Assert.assertEquals(new Vector2f(10, 10), shape.body.velocity);
        Assert.assertEquals(10, shape.body.angularVelocity, PieTest.FLOAT_EPSILON_COMPARISON);
    }
}
