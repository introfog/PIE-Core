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

import com.github.introfog.pie.core.TestUtil;
import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.Polygon;
import com.github.introfog.pie.core.util.ShapePair;
import com.github.introfog.pie.test.annotations.AlgorithmicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(AlgorithmicTest.class)
public class SpatialHashingMethodTest extends AbstractBroadPhaseTest {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new SpatialHashingMethod();
    }

    @Test
    public void collisionShapesInDifferentCellsTest() {
        // This test verifies that a collision will be detected if one shape steps slightly into the
        // cell and this cell contains a small shape that collides with the original shape
        IShape r1 = Polygon.generateRectangle(180, 180, 80, 80, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        IShape r2 = Polygon.generateRectangle(240, 140, 60, 40, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        // Auxiliary shape to make the cells size equal to 100
        IShape r3 = Polygon.generateRectangle(1000, 1000, 160, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        List<IShape> shapes = new ArrayList<>(3);
        shapes.add(r1);
        shapes.add(r2);
        shapes.add(r3);
        AbstractBroadPhase broadPhaseMethod = getBroadPhaseMethod();
        broadPhaseMethod.setShapes(shapes);

        List<ShapePair> cmpShapePairs = new ArrayList<>(1);

        cmpShapePairs.add(new ShapePair(r1, r2));
        TestUtil.comparingShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollision());
    }

    @Test
    public void collisionShapesWithZeroSizeTest() {
        // This test checks how the SpatialHashingMethod works if the cell size is zero
        IShape c1 = new Circle(0f, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0f);
        IShape c2 = new Circle(0f, 11, 11, MathPIE.STATIC_BODY_DENSITY, 0f);
        IShape c3 = new Circle(0f, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0f);
        IShape c4 = new Circle(0f, 10.00001f, 10, MathPIE.STATIC_BODY_DENSITY, 0f);
        List<IShape> shapes = new ArrayList<>(3);
        shapes.add(c1);
        shapes.add(c2);
        shapes.add(c3);
        shapes.add(c4);
        AbstractBroadPhase broadPhaseMethod = getBroadPhaseMethod();
        broadPhaseMethod.setShapes(shapes);

        List<ShapePair> cmpShapePairs = new ArrayList<>(1);

        cmpShapePairs.add(new ShapePair(c1, c3));
        TestUtil.comparingShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollision());
    }
}
