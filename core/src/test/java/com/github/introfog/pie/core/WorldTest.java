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
import com.github.introfog.pie.core.shape.ShapePair;
import com.github.introfog.pie.core.util.TestUtil;
import com.github.introfog.pie.test.AssertUtil;
import com.github.introfog.pie.test.PieTest;
import com.github.introfog.pie.test.annotations.IntegrationTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class WorldTest extends PieTest {
    @Test
    public void deadLoopBorderTest() {
        WorldProperties properties = new WorldProperties().setFixedDeltaTime(2f).setDeadLoopBorder(3f);
        World world = new World(properties);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        Set<IShape> shapes = new HashSet<>(2);
        shapes.add(c1);
        shapes.add(c2);
        world.setShapes(shapes);

        world.update(6f);
        Set<ShapePair> cmpShapePairs = new HashSet<>();
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
        WorldProperties properties = new WorldProperties().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(properties);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        Set<IShape> shapes = new HashSet<>(2);
        shapes.add(c1);
        shapes.add(c2);
        world.setShapes(shapes);

        world.update(1.5f);
        Set<ShapePair> cmpShapePairs = new HashSet<>();
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
        WorldProperties properties = new WorldProperties().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(properties);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        Set<IShape> shapes = new HashSet<>(2);
        shapes.add(c1);
        shapes.add(c2);
        world.setShapes(shapes);

        IShape c3 = new Circle(10, 25, 0, 1f, 0.2f);
        world.addShape(c3);
        shapes = new HashSet<>(3);
        shapes.add(c1);
        shapes.add(c2);
        shapes.add(c3);

        Set<IShape> worldShapes = world.getUnmodifiableShapes();
        Assert.assertEquals(shapes, worldShapes);
        Assert.assertThrows(UnsupportedOperationException.class, () -> worldShapes.add(c2));
    }

    @Test
    public void setShapesMethodTest() {
        WorldProperties properties = new WorldProperties().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(properties);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        Set<IShape> shapes = new HashSet<>(2);
        shapes.add(c1);
        shapes.add(c2);
        world.setShapes(shapes);
        shapes.clear();

        world.update(1.5f);
        Set<ShapePair> cmpShapePairs = new HashSet<>();
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
        WorldProperties properties = new WorldProperties().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(properties);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        world.addShape(c1);
        world.addShape(c2);

        world.update(1.5f);
        Set<ShapePair> cmpShapePairs = new HashSet<>();
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs,  world.getCollisions());
    }

    @Test
    public void addEqualShapesTest() {
        WorldProperties properties = new WorldProperties().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(properties);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        world.addShape(c1);
        world.addShape(c2);
        world.addShape(c1);
        world.addShape(c2);

        Assert.assertEquals(2, world.getUnmodifiableShapes().size());
    }

    @Test
    public void removeMethodTest() {
        WorldProperties properties = new WorldProperties().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(properties);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        world.addShape(c1);
        world.addShape(c2);
        Assert.assertEquals(2, world.getUnmodifiableShapes().size());

        world.update(1.5f);
        Set<ShapePair> cmpShapePairs = new HashSet<>();
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs,  world.getCollisions());

        Assert.assertTrue(world.remove(c1));
        Assert.assertFalse(world.remove(c1));
        Assert.assertEquals(1, world.getUnmodifiableShapes().size());
        world.update(1.5f);
        TestUtil.assertEqualsShapePairsList(new HashSet<>(),  world.getCollisions());

        Assert.assertTrue(world.remove(c2));
        Assert.assertEquals(0, world.getUnmodifiableShapes().size());

        Assert.assertFalse(world.remove(c2));
        world.update(1.5f);
        TestUtil.assertEqualsShapePairsList(new HashSet<>(),  world.getCollisions());
    }

    @Test
    public void clearMethodTest() {
        WorldProperties properties = new WorldProperties().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(properties);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        world.addShape(c1);
        world.addShape(c2);
        Assert.assertEquals(2, world.getUnmodifiableShapes().size());

        world.update(1.5f);
        Set<ShapePair> cmpShapePairs = new HashSet<>(3);
        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs,  world.getCollisions());

        world.clear();
        Assert.assertTrue(world.getUnmodifiableShapes().isEmpty());
        world.update(1.5f);
        TestUtil.assertEqualsShapePairsList(new HashSet<>(),  world.getCollisions());
    }

    @Test
    public void highAndLowCollisionSolveIterationsTest() {
        WorldProperties properties = new WorldProperties().setFixedDeltaTime(1f / 3).setDeadLoopBorder(1).setCollisionSolveIterations(10);
        World highIterWorld = new World(properties);

        properties.setCollisionSolveIterations(1);
        World lowIterWorld = new World(properties);

        IShape cHigh1 = new Circle(10, 0, 0, 1f, 0.2f);
        cHigh1.getBody().getVelocity().add(new Vector2f(0.1f, 0));
        IShape cHigh2 = new Circle(10, 15, 0, 1f, 0.2f);
        IShape cHigh3 = new Circle(10, 25, 0, 1f, 0.2f);
        cHigh3.getBody().getVelocity().add(new Vector2f(-0.1f, 0));
        highIterWorld.addShape(cHigh1);
        highIterWorld.addShape(cHigh2);
        highIterWorld.addShape(cHigh3);

        IShape cLow1 = new Circle(10, 0, 0, 1f, 0.2f);
        cLow1.getBody().getVelocity().add(new Vector2f(0.1f, 0));
        IShape cLow2 = new Circle(10, 15, 0, 1f, 0.2f);
        IShape cLow3 = new Circle(10, 25, 0, 1f, 0.2f);
        cLow3.getBody().getVelocity().add(new Vector2f(-0.1f, 0));
        lowIterWorld.addShape(cLow1);
        lowIterWorld.addShape(cLow2);
        lowIterWorld.addShape(cLow3);

        highIterWorld.update(properties.getFixedDeltaTime() + MathPie.EPSILON);
        lowIterWorld.update(properties.getFixedDeltaTime() + MathPie.EPSILON);
        Assert.assertEquals(2, highIterWorld.getCollisions().size());
        Assert.assertEquals(2, lowIterWorld.getCollisions().size());

        // This assert checks that with a large value of the collision solve iterations number,
        // the calculation is more accurate, i.e. in our case, the penetration of one circle
        // into another is less, with a larger value of the collision solve iterations number
        float penetrationWithHigh = cHigh1.getBody().getPosition().x - cHigh2.getBody().getPosition().x;
        float penetrationWithLow = cLow1.getBody().getPosition().x - cLow2.getBody().getPosition().x;
        Assert.assertTrue(penetrationWithLow - penetrationWithHigh > 0.02f);
    }

    @Test
    public void getManifoldsAndCollisionsTest() {
        WorldProperties properties = new WorldProperties().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(properties);

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
        WorldProperties properties = new WorldProperties().setFixedDeltaTime(1f).setDeadLoopBorder(10f);
        World world = new World(properties);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        world.addShape(c1);
        world.addShape(c2);
        Assert.assertEquals(2, world.getUnmodifiableShapes().size());

        world.update(1.5f);
        ShapePair[] arrayShapePairs = world.getCollisions().toArray(new ShapePair[]{});
        Assert.assertEquals(1, arrayShapePairs.length);
        Assert.assertEquals(c1, arrayShapePairs[0].getFirst());
        Assert.assertEquals(c2, arrayShapePairs[0].getSecond());
    }

    @Test
    public void thereIsNoHandlerForPairTest() {
        WorldProperties properties = new WorldProperties();
        properties.getShapeCollisionMapping().putMapping(Circle.class, Circle.class, null);
        Assert.assertNull(properties.getShapeCollisionMapping().getMapping(Circle.class, Circle.class));
        World world = new World(properties);

        IShape c1 = new Circle(10, 0, 0, 1f, 0.2f);
        IShape c2 = new Circle(10, 15, 0, 1f, 0.2f);
        world.addShape(c1);
        world.addShape(c2);
        AssertUtil.doesNotThrow(() -> world.update(1));
    }
}
