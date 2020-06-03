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
package com.github.introfog.pie.benchmark.collisions.broadphase;

import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.github.introfog.pie.core.collisions.broadphase.BruteForceMethod;
import com.github.introfog.pie.core.collisions.broadphase.SpatialHashingMethod;
import com.github.introfog.pie.core.collisions.broadphase.SweepAndPruneMethod;
import com.github.introfog.pie.core.collisions.broadphase.SweepAndPruneMyMethod;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapeIOUtil;
import com.github.introfog.pie.test.PIETest;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractBroadPhaseBenchmarkTest extends PIETest {
    private double comparativeTime;
    private BenchmarkTestConfig config;
    private List<IShape> methodShapes;
    private List<AbstractBroadPhase> broadPhaseMethods;

    public void runBroadPhaseBenchmarkTest(BenchmarkTestConfig benchmarkTestConfig) throws IOException {
        config = benchmarkTestConfig;
        run();
    }

    private void run() throws IOException {
        initializeTestMethods();

        config.outputTestConfig();

        double[] workingTimes = runBroadPhaseMethod(broadPhaseMethods);
        for (int i = 0; i < broadPhaseMethods.size(); i++) {
            if (config.comparativeMethodName.equals(broadPhaseMethods.get(i).getClass().getSimpleName())) {
                comparativeTime = workingTimes[i];
                break;
            }
        }

        List<BenchmarkTestMethodResult> methodResults = new ArrayList<>(broadPhaseMethods.size());
        for (int i = 0; i < broadPhaseMethods.size(); i++) {
            String methodName = broadPhaseMethods.get(i).getClass().getSimpleName();
            methodResults.add(new BenchmarkTestMethodResult(methodName, workingTimes[i], comparativeTime,
                    config.expectedCoefficients[i], config.allowedWorkingTimeDifference));
        }

        BenchmarkTestMethodResult.checkAndOutputResults(methodResults);
    }

    private void initializeTestMethods() throws IOException {
        methodShapes = ShapeIOUtil.readShapesFromFile(config.sourceFolder + config.fileName);

        broadPhaseMethods = new ArrayList<>();
        broadPhaseMethods.add(new BruteForceMethod());
        broadPhaseMethods.add(new SpatialHashingMethod());
        broadPhaseMethods.add(new SweepAndPruneMethod());
        broadPhaseMethods.add(new SweepAndPruneMyMethod());
        broadPhaseMethods.forEach(method -> method.setShapes(methodShapes));
    }

    // TODO Make the method universal using reflection
    private double[] runBroadPhaseMethod(List<AbstractBroadPhase> methods) {
        for (int i = 0; i < config.warm; i++) {
            methods.forEach(AbstractBroadPhase::calculateAabbCollision);
        }

        long[] totalNanoTime = new long[methods.size()];
        for (int i = 0; i < config.measure / 2; i++) {
            for (int j = 0; j < methods.size(); j++) {
                long previously = System.nanoTime();
                methods.get(j).calculateAabbCollision();
                totalNanoTime[j] += System.nanoTime() - previously;
            }
            config.applier.applyAction(methods, methodShapes);
        }

        for (int i = 0; i < config.measure / 2; i++) {
            for (int j = methods.size() - 1; j > -1; j--) {
                long previously = System.nanoTime();
                methods.get(j).calculateAabbCollision();
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
