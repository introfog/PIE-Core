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
package com.github.introfog.pie.assessment.collisions.broadphase;

import com.github.introfog.pie.assessment.collisions.broadphase.applier.IAction;
import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.github.introfog.pie.core.collisions.broadphase.BruteForceMethod;
import com.github.introfog.pie.core.collisions.broadphase.SpatialHashingMethod;
import com.github.introfog.pie.core.collisions.broadphase.SweepAndPruneMethod;
import com.github.introfog.pie.core.collisions.broadphase.AABBTreeMethod;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapeIOUtil;
import com.github.introfog.pie.core.util.ShapePair;
import com.github.introfog.pie.core.util.TestUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class BroadPhaseAlgorithmicTestRunner {
    public static void runStaticBroadPhaseAlgorithmicTest(String fileName, String sourceFolder) throws IOException {
        List<IShape> methodShapes = ShapeIOUtil.readShapesFromFile(sourceFolder + fileName + ".pie");
        List<AbstractBroadPhase> methods = BroadPhaseAlgorithmicTestRunner.initializeBroadPhaseMethods(methodShapes);

        for (AbstractBroadPhase method : methods) {
            methodShapes.forEach(IShape::computeAABB);
            List<ShapePair> cmpShapes = BruteForceMethod.calculateAabbCollisionsWithoutAabbUpdating(methodShapes);
            String messagePrefix = "Method: " + method.getClass().getSimpleName() + ". ";
            TestUtil.assertEqualsShapePairsList(cmpShapes, method.calculateAabbCollisions(), messagePrefix);
        }
    }

    public static void runDynamicBroadPhaseAlgorithmicTest(String fileName, String sourceFolder, int call, IAction applier) throws IOException {
        List<IShape> methodShapes = ShapeIOUtil.readShapesFromFile(sourceFolder + fileName + ".pie");
        List<AbstractBroadPhase> methods = BroadPhaseAlgorithmicTestRunner.initializeBroadPhaseMethods(methodShapes);

        for (int i = 0; i < call; i++) {
            methodShapes.forEach(IShape::computeAABB);
            List<ShapePair> cmpShapes = BruteForceMethod.calculateAabbCollisionsWithoutAabbUpdating(methodShapes);
            for (AbstractBroadPhase method : methods) {
                String messagePrefix = "Iteration: " + i + "; Method: " + method.getClass().getSimpleName() + ". ";
                TestUtil.assertEqualsShapePairsList(cmpShapes, method.calculateAabbCollisions(), messagePrefix);
            }
            applier.applyAction(methods, methodShapes);
        }
    }

    private static List<AbstractBroadPhase> initializeBroadPhaseMethods(List<IShape> shapes) {
        List<AbstractBroadPhase> methods = new ArrayList<>();
        methods.add(new BruteForceMethod());
        methods.add(new SpatialHashingMethod());
        methods.add(new SweepAndPruneMethod());
        methods.add(new AABBTreeMethod());
        methods.forEach(method -> method.setShapes(shapes));
        return methods;
    }
}
