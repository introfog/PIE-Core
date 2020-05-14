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
import com.github.introfog.pie.core.collisions.broadphase.SpatialHashingMethod;
import com.github.introfog.pie.core.collisions.broadphase.SweepAndPruneMethod;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ContextTest extends PIETest {
    @Test
    public void defaultConstructorTest() {
        Context context = new Context();

        Assert.assertEquals(1f / 60f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1f / 3f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.5f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.1f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0f, context.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(50f, context.getGravity().y, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(BruteForceMethod.class, context.getBroadPhaseMethod().getClass());
    }

    @Test
    public void paramConstructorTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(0.1f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.2f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.3f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.4f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.5f, context.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.6f, context.getGravity().y, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(SpatialHashingMethod.class, context.getBroadPhaseMethod().getClass());
    }

    @Test
    public void copyConstructorTest() {
        Context context = new Context(new Context(1.1f, 1.2f,
                1.3f, 1.4f, new Vector2f(1.5f, 1.6f), new SweepAndPruneMethod()));

        Assert.assertEquals(1.1f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.2f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.3f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.4f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.5f, context.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.6f, context.getGravity().y, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(SweepAndPruneMethod.class, context.getBroadPhaseMethod().getClass());
    }

    @Test
    public void getFixedDeltaTimeTest() {
        Context context = new Context(0.1f, 0.2f,
                0.3f, 0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(0.1f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setFixedDeltaTimeTest() {
        Context context = new Context(0.1f, 0.2f,
                0.3f, 0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setFixedDeltaTime(1.1f));
        Assert.assertEquals(1.1f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getDeadLoopBorderTest() {
        Context context = new Context(0.1f, 0.2f,
                0.3f, 0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(0.2f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setDeadLoopBorderTest() {
        Context context = new Context(0.1f, 0.2f,
                0.3f, 0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setDeadLoopBorder(1.2f));
        Assert.assertEquals(1.2f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getCorrectPositionPercentTest() {
        Context context = new Context(0.1f, 0.2f,
                0.3f, 0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(0.3f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setCorrectPositionPercentTest() {
        Context context = new Context(0.1f, 0.2f,
                0.3f, 0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setCorrectPositionPercent(1.4f));
        Assert.assertEquals(1.4f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getMinBorderSlopTest() {
        Context context = new Context(0.1f, 0.2f,
                0.3f, 0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(0.4f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setMinBorderSlopTest() {
        Context context = new Context(0.1f, 0.2f,
                0.3f, 0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setMinBorderSlop(1.5f));
        Assert.assertEquals(1.5f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getGravityTest() {
        Context context = new Context(0.1f, 0.2f,
                0.3f, 0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(0.5f, context.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.6f, context.getGravity().y, FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setGravityTest() {
        Context context = new Context(0.1f, 0.2f,
                0.3f, 0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setGravity(new Vector2f(1.6f, 1.7f)));
        Assert.assertEquals(1.6f, context.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.7f, context.getGravity().y, FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getBroadPhaseMethodTest() {
        Context context = new Context(0.1f, 0.2f,
                0.3f, 0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(SpatialHashingMethod.class, context.getBroadPhaseMethod().getClass());
    }

    @Test
    public void setBroadPhaseMethodTest() {
        Context context = new Context(0.1f, 0.2f,
                0.3f, 0.4f, new Vector2f(0.5f, 0.6f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setBroadPhaseMethod(new SweepAndPruneMethod()));
        Assert.assertEquals(SweepAndPruneMethod.class, context.getBroadPhaseMethod().getClass());
    }

    @Test
    public void getRestingTest() {
        Context context = new Context();
        context.setGravity(new Vector2f(5f, 10f));
        context.setFixedDeltaTime(0.2f);

        Assert.assertEquals(5.0001f, context.getResting(), FLOAT_EPSILON_COMPARISON);
    }
}
