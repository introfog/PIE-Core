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
public class ContextTest extends PieTest {
    @Test
    public void setCollisionSolveIterationsTest() {
        Context context = new Context(new WorldProperties().setCollisionSolveIterations(19));
        Assert.assertEquals(19, context.getCollisionSolveIterations());
    }

    @Test
    public void setFixedDeltaTimeTest() {
        Context context = new Context(new WorldProperties().setFixedDeltaTime(1.1f));
        Assert.assertEquals(1.1f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setDeadLoopBorderTest() {
        Context context = new Context(new WorldProperties().setDeadLoopBorder(1.2f));
        Assert.assertEquals(1.2f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setCorrectPositionPercentTest() {
        Context context = new Context(new WorldProperties().setCorrectPositionPercent(1.4f));
        Assert.assertEquals(1.4f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setMinBorderSlopTest() {
        Context context = new Context(new WorldProperties().setMinBorderSlop(1.5f));
        Assert.assertEquals(1.5f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setGravityTest() {
        Vector2f gravity = new Vector2f(1.6f, 1.7f);
        Context context = new Context(new WorldProperties().setGravity(gravity));
        Assert.assertEquals(gravity, context.getGravity());
    }

    @Test
    public void setBroadPhaseMethodTest() {
        Context context = new Context(new WorldProperties().setBroadPhaseMethod(new SweepAndPruneMethod()));
        Assert.assertEquals(SweepAndPruneMethod.class, context.getBroadPhaseMethod().getClass());
    }

    @Test
    public void setShapeCollisionMappingTest() {
        ShapeCollisionHandlersMapper mapper = ShapeCollisionHandlersMapper.createAndGetDefaultMapping();
        Context context = new Context(new WorldProperties().setShapeCollisionMapping(mapper));
        ShapeCollisionHandlersMapper actualMapper = context.getShapeCollisionMapping();
        Assert.assertNotNull(actualMapper.getMapping(Circle.class, Circle.class));
        Assert.assertNotNull(actualMapper.getMapping(Circle.class, Polygon.class));
        Assert.assertNotNull(actualMapper.getMapping(Polygon.class, Circle.class));
        Assert.assertNotNull(actualMapper.getMapping(Polygon.class, Polygon.class));
    }

    @Test
    public void getRestingTest() {
        Context context = new Context(new WorldProperties()
                .setGravity(new Vector2f(5f, 10f))
                .setFixedDeltaTime(0.2f));
        Assert.assertEquals(5.0001f, context.getResting(), FLOAT_EPSILON_COMPARISON);
    }
}
