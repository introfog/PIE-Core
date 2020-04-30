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
        Assert.assertEquals(0.0001f, context.getEpsilon(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.5f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.1f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0f, context.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(50f, context.getGravity().y, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(BruteForceMethod.class, context.getBroadPhase().getClass());
    }

    @Test
    public void paramConstructorTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(0.1f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.2f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.3f, context.getEpsilon(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.4f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.5f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.6f, context.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.7f, context.getGravity().y, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(SpatialHashingMethod.class, context.getBroadPhase().getClass());
    }

    @Test
    public void copyConstructorTest() {
        Context context = new Context(new Context(1.1f, 1.2f, 1.3f,
                1.4f, 1.5f, new Vector2f(1.6f, 1.7f), new SweepAndPruneMethod()));

        Assert.assertEquals(1.1f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.2f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.3f, context.getEpsilon(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.4f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.5f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.6f, context.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.7f, context.getGravity().y, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(SweepAndPruneMethod.class, context.getBroadPhase().getClass());
    }

    @Test
    public void getFixedDeltaTimeTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(0.1f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setFixedDeltaTimeTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setFixedDeltaTime(1.1f));
        Assert.assertEquals(1.1f, context.getFixedDeltaTime(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getDeadLoopBorderTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(0.2f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setDeadLoopBorderTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setDeadLoopBorder(1.2f));
        Assert.assertEquals(1.2f, context.getDeadLoopBorder(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getEpsilonTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(0.3f, context.getEpsilon(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setEpsilonTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setEpsilon(1.3f));
        Assert.assertEquals(1.3f, context.getEpsilon(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getCorrectPositionPercentTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(0.4f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setCorrectPositionPercentTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setCorrectPositionPercent(1.4f));
        Assert.assertEquals(1.4f, context.getCorrectPositionPercent(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getMinBorderSlopTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(0.5f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setMinBorderSlopTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setMinBorderSlop(1.5f));
        Assert.assertEquals(1.5f, context.getMinBorderSlop(), FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getGravityTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(0.6f, context.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.7f, context.getGravity().y, FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void setGravityTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setGravity(new Vector2f(1.6f, 1.7f)));
        Assert.assertEquals(1.6f, context.getGravity().x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(1.7f, context.getGravity().y, FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void getBroadPhaseTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(SpatialHashingMethod.class, context.getBroadPhase().getClass());
    }

    @Test
    public void setBroadPhaseTest() {
        Context context = new Context(0.1f, 0.2f, 0.3f,
                0.4f, 0.5f, new Vector2f(0.6f, 0.7f), new SpatialHashingMethod());

        Assert.assertEquals(context, context.setBroadPhase(new SweepAndPruneMethod()));
        Assert.assertEquals(SweepAndPruneMethod.class, context.getBroadPhase().getClass());
    }

    @Test
    public void getRestingTest() {
        Context context = new Context();
        context.setGravity(new Vector2f(5f, 10f));
        context.setFixedDeltaTime(0.2f);
        context.setEpsilon(0.13f);

        Assert.assertEquals(5.13f, context.getResting(), FLOAT_EPSILON_COMPARISON);
    }
}
