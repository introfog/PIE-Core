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
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class ContextTest extends PieTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void defaultConstructorTest() {
        Context context = new Context();

        Assert.assertEquals(1f / 60f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1f / 3f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.5f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.1f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1, context.getCollisionSolveIterations());
        Assert.assertEquals(0f, context.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(50f, context.getGravity().y, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(BruteForceMethod.class, context.getBroadPhaseMethod().getClass());

        Assert.assertNotNull(context.getShapeCollisionMapping().getMapping(Circle.class, Circle.class));
        Assert.assertNotNull(context.getShapeCollisionMapping().getMapping(Circle.class, Polygon.class));
        Assert.assertNotNull(context.getShapeCollisionMapping().getMapping(Polygon.class, Polygon.class));
    }

    @Test
    public void copyConstructorTest() {
        Context context = new Context();
        context.setFixedDeltaTime(1.1f);
        context.setDeadLoopBorder(1.2f);
        context.setCorrectPositionPercent(1.3f);
        context.setMinBorderSlop(1.4f);
        context.setCollisionSolveIterations(5);
        context.setGravity(new Vector2f(1.6f, 1.7f));
        SweepAndPruneMethod method = new SweepAndPruneMethod();
        context.setBroadPhaseMethod(method);
        ShapeCollisionHandlersMapper mapper = new ShapeCollisionHandlersMapper();
        context.setShapeCollisionMapping(mapper);

        Assert.assertEquals(1.1f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.2f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.3f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.4f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(5, context.getCollisionSolveIterations());
        Assert.assertEquals(1.6f, context.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.7f, context.getGravity().y, FLOAT_EPSILON_COMPARISON);
        Assert.assertSame(method, context.getBroadPhaseMethod());
        Assert.assertSame(mapper, context.getShapeCollisionMapping());
    }

    @Test
    public void setCollisionSolveIterationsTest() {
        Context context = new Context();

        Assert.assertEquals(context, context.setCollisionSolveIterations(19));
        Assert.assertEquals(19, context.getCollisionSolveIterations());

        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(PieExceptionMessage.COLLISION_SOLVE_ITERATION_MUST_NOT_BE_NEGATIVE);
        context.setCollisionSolveIterations(-2);
    }

    @Test
    public void setFixedDeltaTimeTest() {
        Context context = new Context();

        Assert.assertEquals(context, context.setFixedDeltaTime(1.1f));
        Assert.assertEquals(1.1f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setDeadLoopBorderTest() {
        Context context = new Context();

        Assert.assertEquals(context, context.setDeadLoopBorder(1.2f));
        Assert.assertEquals(1.2f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setCorrectPositionPercentTest() {
        Context context = new Context();

        Assert.assertEquals(context, context.setCorrectPositionPercent(1.4f));
        Assert.assertEquals(1.4f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
    }


    @Test
    public void setMinBorderSlopTest() {
        Context context = new Context();

        Assert.assertEquals(context, context.setMinBorderSlop(1.5f));
        Assert.assertEquals(1.5f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
    }


    @Test
    public void setGravityTest() {
        Context context = new Context();

        Vector2f gravity2 = new Vector2f(1.6f, 1.7f);
        Assert.assertEquals(context, context.setGravity(gravity2));
        Assert.assertSame(gravity2, context.getGravity());
    }

    @Test
    public void setBroadPhaseMethodTest() {
        Context context = new Context();

        SweepAndPruneMethod method = new SweepAndPruneMethod();
        Assert.assertEquals(context, context.setBroadPhaseMethod(method));
        Assert.assertSame(method, context.getBroadPhaseMethod());
    }


    @Test
    public void setShapeCollisionMappingTest() {
        Context context = new Context();

        ShapeCollisionHandlersMapper mapper = ShapeCollisionHandlersMapper.createAndGetDefaultMapping();
        Assert.assertSame(context, context.setShapeCollisionMapping(mapper));
        Assert.assertSame(mapper, context.getShapeCollisionMapping());
    }

    @Test
    public void getRestingTest() {
        Context context = new Context();
        context.setGravity(new Vector2f(5f, 10f));
        context.setFixedDeltaTime(0.2f);

        Assert.assertEquals(5.0001f, context.getResting(), FLOAT_EPSILON_COMPARISON);
    }
}
