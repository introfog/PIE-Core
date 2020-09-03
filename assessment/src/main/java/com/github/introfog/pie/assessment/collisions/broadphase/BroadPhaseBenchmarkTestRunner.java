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

import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.github.introfog.pie.core.collisions.broadphase.BruteForceMethod;
import com.github.introfog.pie.core.collisions.broadphase.SpatialHashingMethod;
import com.github.introfog.pie.core.collisions.broadphase.SweepAndPruneMethod;
import com.github.introfog.pie.core.collisions.broadphase.aabbtree.AABBTreeMethod;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapeIOUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
Currently, benchmark tests are run only on the developer's machine, as it was not possible to achieve uniform results
on the machines used in GitHub Action (this is most likely due to different machine capacities). In the future it is
planned to solve this problem, and add benchmark tests to the build action or other pipeline (TeamCity for example).
 */
public class BroadPhaseBenchmarkTestRunner {
    public static void runBroadPhaseBenchmarkTest(BenchmarkTestConfig config) throws IOException {
        List<IShape> methodShapes = ShapeIOUtil.readShapesFromFile(config.sourceFolder + config.fileName + ".pie");
        List<AbstractBroadPhase> methods = BroadPhaseBenchmarkTestRunner.initializeBroadPhaseMethods(methodShapes);

        config.outputTestConfig();

        double[] workingTimes = BroadPhaseBenchmarkTestRunner.runBroadPhaseMethod(methods, methodShapes, config);
        double comparativeTime = 0;
        for (int i = 0; i < methods.size(); i++) {
            if (config.comparativeMethodName.equals(methods.get(i).getClass().getSimpleName())) {
                comparativeTime = workingTimes[i];
                break;
            }
        }

        List<BenchmarkTestMethodResult> methodResults = new ArrayList<>(methods.size());
        for (int i = 0; i < methods.size(); i++) {
            String methodName = methods.get(i).getClass().getSimpleName();
            methodResults.add(new BenchmarkTestMethodResult(methodName, workingTimes[i], comparativeTime,
                    config.expectedCoefficients[i], config.allowedWorkingTimeDifference));
        }

        BenchmarkTestMethodResult.checkAndOutputResults(methodResults);
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

    // TODO Make the method universal using reflection
    private static double[] runBroadPhaseMethod(List<AbstractBroadPhase> methods, List<IShape> methodShapes,
            BenchmarkTestConfig config) {
        for (int i = 0; i < config.warm; i++) {
            methods.forEach(AbstractBroadPhase::calculateAabbCollisions);
        }

        long[] totalNanoTime = new long[methods.size()];
        for (int i = 0; i < config.measure / 2; i++) {
            for (int j = 0; j < methods.size(); j++) {
                long previously = System.nanoTime();
                methods.get(j).calculateAabbCollisions();
                totalNanoTime[j] += System.nanoTime() - previously;
            }
            config.applier.applyAction(methods, methodShapes);
        }

        for (int i = 0; i < config.measure / 2; i++) {
            for (int j = methods.size() - 1; j > -1; j--) {
                long previously = System.nanoTime();
                methods.get(j).calculateAabbCollisions();
                totalNanoTime[j] += System.nanoTime() - previously;
            }
            config.applier.applyAction(methods, methodShapes);
        }

        double[] results = new double[methods.size()];
        for (int i = 0; i < methods.size(); i++) {
            results[i] = (double) config.timeUnit.convert(totalNanoTime[i], TimeUnit.NANOSECONDS) / config.measure;
        }
        return results;
    }
}
