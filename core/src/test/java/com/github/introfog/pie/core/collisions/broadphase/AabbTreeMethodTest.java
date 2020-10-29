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
package com.github.introfog.pie.core.collisions.broadphase;

import com.github.introfog.pie.core.collisions.broadphase.aabbtree.AabbTreeNode;
import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.test.PieTest;
import com.github.introfog.pie.test.annotations.UnitTest;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AabbTreeMethodTest extends AbstractBroadPhaseTest {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new AabbTreeMethod();
    }

    @Test
    public void setEnlargedAabbCoefficientTest() throws NoSuchFieldException, IllegalAccessException {
        AabbTreeMethod method = new AabbTreeMethod();

        Assert.assertEquals(AabbTreeNode.DEFAULT_ENLARGED_AABB_COEFFICIENT, method.getEnlargedAabbCoefficient(),
                FLOAT_EPSILON_COMPARISON);

        method.setEnlargedAabbCoefficient(0.37f);
        Assert.assertEquals(0.37f, method.getEnlargedAabbCoefficient(), FLOAT_EPSILON_COMPARISON);

        method.setEnlargedAabbCoefficient(-1);
        Assert.assertEquals(0.37f, method.getEnlargedAabbCoefficient(), FLOAT_EPSILON_COMPARISON);

        method.setEnlargedAabbCoefficient(0.25f);
        method.addShape(new Circle(5, 6, 5, MathPie.STATIC_BODY_DENSITY, 0));
        Field field = method.getClass().getDeclaredField("root");
        field.setAccessible(true);
        AabbTreeNode root = (AabbTreeNode) field.get(method);

        Assert.assertEquals(new Vector2f(-1.5f, -2.5f), root.aabb.min);
        Assert.assertEquals(new Vector2f(13.5f, 12.5f), root.aabb.max);
    }

    @Test
    public void newInstanceTest() {
        AabbTreeMethod method = (AabbTreeMethod) getBroadPhaseMethod();
        method.setEnlargedAabbCoefficient(0.74f);
        method.addShape(new Circle(0, 0, 0, 0, 0));
        AabbTreeMethod clone = method.newInstance();
        Assert.assertNotSame(method, clone);
        Assert.assertNotSame(method.shapes, clone.shapes);
        Assert.assertEquals(method.shapes, clone.shapes);
        Assert.assertEquals(method.getEnlargedAabbCoefficient(), clone.getEnlargedAabbCoefficient(), PieTest.FLOAT_EPSILON_COMPARISON);
    }
}
