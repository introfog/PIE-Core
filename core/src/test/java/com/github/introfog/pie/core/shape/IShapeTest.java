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
public class IShapeTest extends PieTest {
    @Test
    public void defaultConstructorTest() {
        IShape shape = new Circle(10, 0,0, 0, 0);
        Assert.assertNotNull(shape.getAabb());
        Assert.assertNotNull(shape.getRotateMatrix());


        Assert.assertEquals(1, shape.getRotateMatrix().m00, PieTest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0, shape.getRotateMatrix().m01, PieTest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0, shape.getRotateMatrix().m10, PieTest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1, shape.getRotateMatrix().m11, PieTest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void applyImpulseTest() {
        IShape shape = new Circle(1, 0,0, (float) (1 / Math.PI), 0);
        shape.applyImpulse(new Vector2f(10, 10), new Vector2f(1, 0));

        Assert.assertEquals(new Vector2f(10, 10), shape.getBody().getVelocity());
        Assert.assertEquals(10, shape.getBody().getAngularVelocity(), PieTest.FLOAT_EPSILON_COMPARISON);
    }
}
