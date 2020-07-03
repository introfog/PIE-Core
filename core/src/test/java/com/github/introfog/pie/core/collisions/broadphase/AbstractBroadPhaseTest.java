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
import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.AlgorithmicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(AlgorithmicTest.class)
public abstract class AbstractBroadPhaseTest extends PIETest {
    @Test
    public void addShapeMethodTest() {
        AbstractBroadPhase broadPhaseMethod = getBroadPhaseMethod();
        IShape c1 = new Circle(10, 0, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 15, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        broadPhaseMethod.addShape(c1);
        broadPhaseMethod.addShape(c2);

        List<ShapePair> cmpShapePairs = new ArrayList<>(3);

        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollisions());

        IShape c3 = new Circle(10, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        broadPhaseMethod.addShape(c3);
        cmpShapePairs.add(new ShapePair(c1, c3));
        cmpShapePairs.add(new ShapePair(c2, c3));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollisions());
    }

    @Test
    public void setShapesMethodTest() {
        AbstractBroadPhase broadPhaseMethod = getBroadPhaseMethod();
        IShape c1 = new Circle(10, 0, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 15, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        List<IShape> shapes = new ArrayList<>(2);
        shapes.add(c1);
        shapes.add(c2);
        broadPhaseMethod.setShapes(shapes);
        // Verify that the list of shapes was copied in method setShapes
        shapes.clear();

        List<ShapePair> cmpShapePairs = new ArrayList<>(1);

        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollisions());

        IShape c3 = new Circle(10, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        shapes.add(c1);
        shapes.add(c2);
        shapes.add(c3);
        broadPhaseMethod.setShapes(shapes);
        // Verify that the list of shapes was copied in method setShapes
        shapes.clear();

        cmpShapePairs.add(new ShapePair(c1, c3));
        cmpShapePairs.add(new ShapePair(c2, c3));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollisions());
    }

    protected abstract AbstractBroadPhase getBroadPhaseMethod();
}
