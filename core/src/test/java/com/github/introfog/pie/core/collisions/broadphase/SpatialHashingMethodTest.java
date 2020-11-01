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

import com.github.introfog.pie.core.util.TestUtil;
import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.Polygon;
import com.github.introfog.pie.core.shape.ShapePair;
import com.github.introfog.pie.test.annotations.UnitTest;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SpatialHashingMethodTest extends AbstractBroadPhaseTest {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new SpatialHashingMethod();
    }

    @Test
    public void collisionShapesInDifferentCellsTest() {
        // This test verifies that a collision will be detected if one shape steps slightly into the
        // cell and this cell contains a small shape that collides with the original shape
        IShape r1 = Polygon.generateRectangle(180, 180, 80, 80, MathPie.STATIC_BODY_DENSITY, 0.2f);
        IShape r2 = Polygon.generateRectangle(240, 140, 60, 40, MathPie.STATIC_BODY_DENSITY, 0.2f);
        // Auxiliary shape to make the cells size equal to 100
        IShape r3 = Polygon.generateRectangle(1000, 1000, 160, 10, MathPie.STATIC_BODY_DENSITY, 0.2f);
        Set<IShape> shapes = new HashSet<>(3);
        shapes.add(r1);
        shapes.add(r2);
        shapes.add(r3);
        AbstractBroadPhase broadPhaseMethod = getBroadPhaseMethod();
        broadPhaseMethod.setShapes(shapes);

        Set<ShapePair> cmpShapePairs = new HashSet<>();

        cmpShapePairs.add(new ShapePair(r1, r2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollisions());
    }

    @Test
    public void collisionShapesWithZeroSizeTest() {
        // This test checks how the SpatialHashingMethod works if the cell size is zero
        IShape c1 = new Circle(0f, 10, 10, MathPie.STATIC_BODY_DENSITY, 0f);
        IShape c2 = new Circle(0f, 11, 11, MathPie.STATIC_BODY_DENSITY, 0f);
        IShape c3 = new Circle(0f, 10, 10, MathPie.STATIC_BODY_DENSITY, 0f);
        IShape c4 = new Circle(0f, 10.00001f, 10, MathPie.STATIC_BODY_DENSITY, 0f);
        Set<IShape> shapes = new HashSet<>(3);
        shapes.add(c1);
        shapes.add(c2);
        shapes.add(c3);
        shapes.add(c4);
        AbstractBroadPhase broadPhaseMethod = getBroadPhaseMethod();
        broadPhaseMethod.setShapes(shapes);

        Set<ShapePair> cmpShapePairs = new HashSet<>();

        cmpShapePairs.add(new ShapePair(c1, c3));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollisions());
    }

    @Test
    public void newInstanceTest() {
        SpatialHashingMethod method = (SpatialHashingMethod) getBroadPhaseMethod();
        method.addShape(new Circle(0, 0, 0, 0, 0));
        SpatialHashingMethod clone = method.newInstance();
        Assert.assertNotSame(method, clone);
        Assert.assertNotSame(method.shapes, clone.shapes);
        Assert.assertEquals(method.shapes, clone.shapes);
    }
}
