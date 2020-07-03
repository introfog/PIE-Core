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

import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;
import com.github.introfog.pie.core.util.TestUtil;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.IntegrationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class WorldTest extends PIETest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void deadLoopBorderTest() {
        Context context = new Context().setFixedDeltaTime(2f).setDeadLoopBorder(3f);
        World world = new World(context);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        List<IShape> shapes = new ArrayList<>(2);
        shapes.add(c1);
        shapes.add(c2);
        world.setShapes(shapes);

        world.update(6f);
        List<ShapePair> collisions = world.getCollisions().stream().map(m -> new ShapePair(m.aShape, m.bShape)).
                collect(Collectors.toList());
        List<ShapePair> cmpShapePairs = new ArrayList<>(3);
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, collisions);

        IShape c3 = new Circle(10, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        world.addShape(c3);
        world.update(1f);
        collisions = world.getCollisions().stream().map(m -> new ShapePair(m.aShape, m.bShape)).
                collect(Collectors.toList());
        // Collisions have not changed, because in the first update method call, the deltaTime was transferred larger
        // than the deadLoopBorder, and the accumulator became equal to the deadLoopBorder, and one iteration was
        // performed, and the second call to the update method will not be enough for one iteration
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, collisions);
    }

    @Test
    public void accumulatorTest() {
        Context context = new Context().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(context);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        List<IShape> shapes = new ArrayList<>(2);
        shapes.add(c1);
        shapes.add(c2);
        world.setShapes(shapes);

        world.update(1.5f);
        List<ShapePair> collisions = world.getCollisions().stream().map(m -> new ShapePair(m.aShape, m.bShape)).
                collect(Collectors.toList());
        List<ShapePair> cmpShapePairs = new ArrayList<>(3);
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, collisions);

        IShape c3 = new Circle(10, 7, 25, 1f, 0.2f);
        world.addShape(c3);
        world.update(0.6f);
        collisions = world.getCollisions().stream().map(m -> new ShapePair(m.aShape, m.bShape)).
                collect(Collectors.toList());
        cmpShapePairs.clear();
        cmpShapePairs.add(new ShapePair(c1, c2));
        cmpShapePairs.add(new ShapePair(c1, c3));
        cmpShapePairs.add(new ShapePair(c2, c3));
        // Here it is checked that the accumulator accumulates deltaTime, and when it is larger than the fixedDeltaTime,
        // an iteration of collision resolution occurs
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, collisions);
    }

    @Test
    public void getUnmodifiableShapesMethodTest() {
        Context context = new Context().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(context);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        List<IShape> shapes = new ArrayList<>(2);
        shapes.add(c1);
        shapes.add(c2);
        world.setShapes(shapes);

        IShape c3 = new Circle(10, 25, 0, 1f, 0.2f);
        world.addShape(c3);
        shapes = new ArrayList<>(3);
        shapes.add(c1);
        shapes.add(c2);
        shapes.add(c3);

        List<IShape> worldShapes = world.getUnmodifiableShapes();
        Assert.assertEquals(shapes, worldShapes);

        junitExpectedException.expect(UnsupportedOperationException.class);
        worldShapes.add(c2);
    }

    @Test
    public void setShapesMethodTest() {
        Context context = new Context().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(context);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        List<IShape> shapes = new ArrayList<>(2);
        shapes.add(c1);
        shapes.add(c2);
        world.setShapes(shapes);
        shapes.clear();

        world.update(1.5f);
        List<ShapePair> collisions = world.getCollisions().stream().map(m -> new ShapePair(m.aShape, m.bShape)).
                collect(Collectors.toList());
        List<ShapePair> cmpShapePairs = new ArrayList<>(3);
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, collisions);

        IShape c3 = new Circle(10, 7, 25, 1f, 0.2f);
        shapes.add(c1);
        shapes.add(c2);
        shapes.add(c3);
        world.setShapes(shapes);
        world.update(0.6f);
        collisions = world.getCollisions().stream().map(m -> new ShapePair(m.aShape, m.bShape)).
                collect(Collectors.toList());
        cmpShapePairs.clear();
        cmpShapePairs.add(new ShapePair(c1, c2));
        cmpShapePairs.add(new ShapePair(c1, c3));
        cmpShapePairs.add(new ShapePair(c2, c3));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, collisions);
    }

    @Test
    public void addShapeMethodTest() {
        Context context = new Context().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(context);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        world.addShape(c1);
        world.addShape(c2);

        world.update(1.5f);
        List<ShapePair> collisions = world.getCollisions().stream().map(m -> new ShapePair(m.aShape, m.bShape)).
                collect(Collectors.toList());
        List<ShapePair> cmpShapePairs = new ArrayList<>(3);
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, collisions);
    }

    // TODO add test for collisionSolveIterations
}
