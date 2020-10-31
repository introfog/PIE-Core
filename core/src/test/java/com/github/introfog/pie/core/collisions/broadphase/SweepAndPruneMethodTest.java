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

import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;
import com.github.introfog.pie.core.util.TestUtil;
import com.github.introfog.pie.test.annotations.AlgorithmicTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(AlgorithmicTest.class)
public class SweepAndPruneMethodTest extends AbstractBroadPhaseTest {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new SweepAndPruneMethod();
    }

    @Test
    public void yAxisTest() {
        IShape c1 = new Circle(2f, 0, 0, MathPie.STATIC_BODY_DENSITY, 0f);
        IShape c2 = new Circle(2f, 0, 2, MathPie.STATIC_BODY_DENSITY, 0f);
        IShape c3 = new Circle(2f, 5, 10, MathPie.STATIC_BODY_DENSITY, 0f);

        List<IShape> shapes = new ArrayList<>();
        shapes.add(c1);
        shapes.add(c2);
        shapes.add(c3);
        AbstractBroadPhase broadPhaseMethod = getBroadPhaseMethod();
        broadPhaseMethod.setShapes(shapes);

        List<ShapePair> cmpShapePairs = new ArrayList<>(1);

        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollisions());
        // The test is designed so that after the first iteration, the SweepAndPrune method will work along the Y-axis
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollisions());
    }

    @Test
    public void intersectedByCurrentAxisButNotIntersectedByOtherAxisTest() {
        // The test reproduces the situation when shape intersect along the axis
        // that is current in the SAP method, but do not intersect along the other axis
        IShape c1 = new Circle(2f, 0, 0, MathPie.STATIC_BODY_DENSITY, 0f);
        IShape c2 = new Circle(2f, 0, 5, MathPie.STATIC_BODY_DENSITY, 0f);
        IShape c3 = new Circle(2f, 5, 5, MathPie.STATIC_BODY_DENSITY, 0f);
        IShape c4 = new Circle(2f, 0, 10, MathPie.STATIC_BODY_DENSITY, 0f);

        List<IShape> shapes = new ArrayList<>();
        shapes.add(c1);
        shapes.add(c2);
        shapes.add(c3);
        shapes.add(c4);
        AbstractBroadPhase broadPhaseMethod = getBroadPhaseMethod();
        broadPhaseMethod.setShapes(shapes);

        List<ShapePair> cmpShapePairs = new ArrayList<>(1);

        TestUtil.assertEqualsShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollisions());
        TestUtil.assertEqualsShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollisions());
    }

    @Test
    public void newInstanceTest() {
        SweepAndPruneMethod method = (SweepAndPruneMethod) getBroadPhaseMethod();
        method.addShape(new Circle(0, 0, 0, 0, 0));
        SweepAndPruneMethod clone = method.newInstance();
        Assert.assertNotSame(method, clone);
        Assert.assertNotSame(method.shapes, clone.shapes);
        Assert.assertEquals(method.shapes, clone.shapes);
    }
}
