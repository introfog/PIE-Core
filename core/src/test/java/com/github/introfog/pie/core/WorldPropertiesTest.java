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
package com.github.introfog.pie.core;

import com.github.introfog.pie.core.collisions.broadphase.BruteForceMethod;
import com.github.introfog.pie.core.collisions.broadphase.SweepAndPruneMethod;
import com.github.introfog.pie.core.collisions.narrowphase.ShapeCollisionHandlersMapper;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.Polygon;
import com.github.introfog.pie.test.PieTest;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class WorldPropertiesTest extends PieTest {
    @Test
    public void defaultConstructorTest() {
        WorldProperties properties = new WorldProperties();

        Assert.assertEquals(1f / 60f, properties.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1f / 3f, properties.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.5f, properties.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.1f, properties.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1, properties.getCollisionSolveIterations());
        Assert.assertEquals(0f, properties.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(50f, properties.getGravity().y, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(BruteForceMethod.class, properties.getBroadPhaseMethod().getClass());

        Assert.assertNotNull(properties.getShapeCollisionMapping().getMapping(Circle.class, Circle.class));
        Assert.assertNotNull(properties.getShapeCollisionMapping().getMapping(Circle.class, Polygon.class));
        Assert.assertNotNull(properties.getShapeCollisionMapping().getMapping(Polygon.class, Polygon.class));
    }

    @Test
    public void setCollisionSolveIterationsTest() {
        WorldProperties properties = new WorldProperties();

        Assert.assertEquals(properties, properties.setCollisionSolveIterations(19));
        Assert.assertEquals(19, properties.getCollisionSolveIterations());

        Assert.assertThrows(PieExceptionMessage.COLLISION_SOLVE_ITERATION_MUST_NOT_BE_NEGATIVE,
                IllegalArgumentException.class, () -> properties.setCollisionSolveIterations(-2));
    }

    @Test
    public void setFixedDeltaTimeTest() {
        WorldProperties properties = new WorldProperties();

        Assert.assertEquals(properties, properties.setFixedDeltaTime(1.1f));
        Assert.assertEquals(1.1f, properties.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setDeadLoopBorderTest() {
        WorldProperties properties = new WorldProperties();

        Assert.assertEquals(properties, properties.setDeadLoopBorder(1.2f));
        Assert.assertEquals(1.2f, properties.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setCorrectPositionPercentTest() {
        WorldProperties properties = new WorldProperties();

        Assert.assertEquals(properties, properties.setCorrectPositionPercent(1.4f));
        Assert.assertEquals(1.4f, properties.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setMinBorderSlopTest() {
        WorldProperties properties = new WorldProperties();

        Assert.assertEquals(properties, properties.setMinBorderSlop(1.5f));
        Assert.assertEquals(1.5f, properties.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setGravityTest() {
        WorldProperties properties = new WorldProperties();

        Vector2f gravity2 = new Vector2f(1.6f, 1.7f);
        Assert.assertEquals(properties, properties.setGravity(gravity2));
        Assert.assertSame(gravity2, properties.getGravity());
    }

    @Test
    public void setBroadPhaseMethodTest() {
        WorldProperties properties = new WorldProperties();

        SweepAndPruneMethod method = new SweepAndPruneMethod();
        Assert.assertEquals(properties, properties.setBroadPhaseMethod(method));
        Assert.assertSame(method, properties.getBroadPhaseMethod());
    }


    @Test
    public void setShapeCollisionMappingTest() {
        WorldProperties properties = new WorldProperties();

        ShapeCollisionHandlersMapper mapper = ShapeCollisionHandlersMapper.createAndGetDefaultMapping();
        Assert.assertSame(properties, properties.setShapeCollisionMapping(mapper));
        Assert.assertSame(mapper, properties.getShapeCollisionMapping());
    }
}
