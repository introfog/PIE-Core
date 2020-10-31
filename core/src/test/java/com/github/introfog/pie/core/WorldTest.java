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

import com.github.introfog.pie.core.collisions.Manifold;
import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;
import com.github.introfog.pie.core.util.TestUtil;
import com.github.introfog.pie.test.PieTest;
import com.github.introfog.pie.test.annotations.IntegrationTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class WorldTest extends PieTest {
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
        List<ShapePair> cmpShapePairs = new ArrayList<>(3);
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs,  world.getCollisions());

        IShape c3 = new Circle(10, 10, 10, MathPie.STATIC_BODY_DENSITY, 0.2f);
        world.addShape(c3);
        world.update(1f);
        // Collisions have not changed, because in the first update method call, the deltaTime was transferred larger
        // than the deadLoopBorder, and the accumulator became equal to the deadLoopBorder, and one iteration was
        // performed, and the second call to the update method will not be enough for one iteration
        TestUtil.assertEqualsShapePairsList(cmpShapePairs,  world.getCollisions());
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
        List<ShapePair> cmpShapePairs = new ArrayList<>(3);
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs,  world.getCollisions());

        IShape c3 = new Circle(10, 7, 25, 1f, 0.2f);
        world.addShape(c3);
        world.update(0.6f);
        cmpShapePairs.clear();
        cmpShapePairs.add(new ShapePair(c1, c2));
        cmpShapePairs.add(new ShapePair(c1, c3));
        cmpShapePairs.add(new ShapePair(c2, c3));
        // Here it is checked that the accumulator accumulates deltaTime, and when it is larger than the fixedDeltaTime,
        // an iteration of collision resolution occurs
        TestUtil.assertEqualsShapePairsList(cmpShapePairs,  world.getCollisions());
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
        List<ShapePair> cmpShapePairs = new ArrayList<>(3);
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs,  world.getCollisions());

        IShape c3 = new Circle(10, 7, 25, 1f, 0.2f);
        shapes.add(c1);
        shapes.add(c2);
        shapes.add(c3);
        world.setShapes(shapes);
        world.update(0.6f);
        cmpShapePairs.clear();
        cmpShapePairs.add(new ShapePair(c1, c2));
        cmpShapePairs.add(new ShapePair(c1, c3));
        cmpShapePairs.add(new ShapePair(c2, c3));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs,  world.getCollisions());
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
        List<ShapePair> cmpShapePairs = new ArrayList<>(3);
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs,  world.getCollisions());
    }

    @Test
    public void removeMethodTest() {
        Context context = new Context().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(context);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        world.addShape(c1);
        world.addShape(c2);
        Assert.assertEquals(2, world.getUnmodifiableShapes().size());

        world.update(1.5f);
        List<ShapePair> cmpShapePairs = new ArrayList<>(3);
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs,  world.getCollisions());

        Assert.assertTrue(world.remove(c1));
        Assert.assertFalse(world.remove(c1));
        Assert.assertEquals(1, world.getUnmodifiableShapes().size());
        world.update(1.5f);
        TestUtil.assertEqualsShapePairsList(new ArrayList<>(),  world.getCollisions());

        Assert.assertTrue(world.remove(c2));
        Assert.assertEquals(0, world.getUnmodifiableShapes().size());

        Assert.assertFalse(world.remove(c2));
        world.update(1.5f);
        TestUtil.assertEqualsShapePairsList(new ArrayList<>(),  world.getCollisions());
    }

    @Test
    public void clearMethodTest() {
        Context context = new Context().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(context);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        world.addShape(c1);
        world.addShape(c2);
        Assert.assertEquals(2, world.getUnmodifiableShapes().size());

        world.update(1.5f);
        List<ShapePair> cmpShapePairs = new ArrayList<>(3);
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs,  world.getCollisions());

        world.clear();
        Assert.assertTrue(world.getUnmodifiableShapes().isEmpty());
        world.update(1.5f);
        TestUtil.assertEqualsShapePairsList(new ArrayList<>(),  world.getCollisions());
    }

    @Test
    public void highAndLowCollisionSolveIterationsTest() {
        Context context = new Context().setFixedDeltaTime(1f / 3).setDeadLoopBorder(1).setCollisionSolveIterations(10);
        World highIterWorld = new World(context);

        context.setCollisionSolveIterations(1);
        World lowIterWorld = new World(context);

        IShape cHigh1 = new Circle(10, 0, 0, 1f, 0.2f);
        cHigh1.body.velocity.add(new Vector2f(0.1f, 0));
        IShape cHigh2 = new Circle(10, 15, 0, 1f, 0.2f);
        IShape cHigh3 = new Circle(10, 25, 0, 1f, 0.2f);
        cHigh3.body.velocity.add(new Vector2f(-0.1f, 0));
        highIterWorld.addShape(cHigh1);
        highIterWorld.addShape(cHigh2);
        highIterWorld.addShape(cHigh3);

        IShape cLow1 = new Circle(10, 0, 0, 1f, 0.2f);
        cLow1.body.velocity.add(new Vector2f(0.1f, 0));
        IShape cLow2 = new Circle(10, 15, 0, 1f, 0.2f);
        IShape cLow3 = new Circle(10, 25, 0, 1f, 0.2f);
        cLow3.body.velocity.add(new Vector2f(-0.1f, 0));
        lowIterWorld.addShape(cLow1);
        lowIterWorld.addShape(cLow2);
        lowIterWorld.addShape(cLow3);

        highIterWorld.update(context.getFixedDeltaTime() + PieTest.FLOAT_EPSILON_COMPARISON);
        lowIterWorld.update(context.getFixedDeltaTime() + PieTest.FLOAT_EPSILON_COMPARISON);

        // This assert checks that with a large value of the collision solve iterations number,
        // the calculation is more accurate, i.e. in our case, the penetration of one circle
        // into another is less, with a larger value of the collision solve iterations number
        float penetrationWithHigh = cHigh1.body.position.x - cHigh2.body.position.x;
        float penetrationWithLow = cLow1.body.position.x - cLow2.body.position.x;
        Assert.assertTrue(penetrationWithLow - penetrationWithHigh > 1.7f);
    }

    @Test
    public void getManifoldsAndCollisionsTest() {
        Context context = new Context().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(context);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        world.addShape(c1);
        world.addShape(c2);
        Assert.assertEquals(2, world.getUnmodifiableShapes().size());

        world.update(1.5f);
        List<Manifold> manifolds = world.getManifolds();
        Assert.assertEquals(1, manifolds.size());
        Assert.assertEquals(c1, manifolds.get(0).aShape);
        Assert.assertEquals(c2, manifolds.get(0).bShape);
    }

    @Test
    public void getCollisionsTest() {
        Context context = new Context().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(context);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        world.addShape(c1);
        world.addShape(c2);
        Assert.assertEquals(2, world.getUnmodifiableShapes().size());

        world.update(1.5f);
        List<ShapePair> collisions = world.getCollisions();
        Assert.assertEquals(1, collisions.size());
        Assert.assertEquals(c1, collisions.get(0).first);
        Assert.assertEquals(c2, collisions.get(0).second);
    }
}
