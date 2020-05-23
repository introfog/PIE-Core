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

import com.github.introfog.pie.benchmark.collisions.broadphase.dynamical.DefaultActionApplier;
import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.github.introfog.pie.core.collisions.broadphase.BruteForceMethod;
import com.github.introfog.pie.core.collisions.broadphase.SpatialHashingMethod;
import com.github.introfog.pie.core.collisions.broadphase.SweepAndPruneMethod;
import com.github.introfog.pie.core.collisions.broadphase.SweepAndPruneMyMethod;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapeIOUtil;
import com.github.introfog.pie.test.PIETest;

import java.io.IOException;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;

public abstract class AbstractBroadPhaseBenchmarkTest extends PIETest {
    private final static int DEFAULT_WARM_VALUE = 45;
    private final static int DEFAULT_MEASURE_VALUE = 6;
    private final static double DEFAULT_WORKING_TIME_DIFFERENCE_PERCENT = 0.25;
    private final static TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MICROSECONDS;
    private final static String DEFAULT_COMPARATIVE_METHOD = BruteForceMethod.class.getSimpleName();

    private List<AbstractBroadPhase> broadPhaseMethods;
    private List<IShape> methodShapes;
    private IActionApplier applier;
    private String fileName;
    private String sourceFolder;
    private TimeUnit timeUnit;
    private double workingTimeDifferencePercent;
    private int warm;
    private int measure;
    private double comparativeTime;

    public void runBenchmarkTest(String fileName, String sourceFolder, double[] coefficients) throws IOException {
        runBenchmarkTest(fileName, sourceFolder, DEFAULT_TIME_UNIT, DEFAULT_WARM_VALUE, DEFAULT_MEASURE_VALUE, coefficients, DEFAULT_WORKING_TIME_DIFFERENCE_PERCENT, new DefaultActionApplier());
    }

    public void runBenchmarkTest(String fileName, String sourceFolder, int warm, double[] coefficients) throws IOException {
        runBenchmarkTest(fileName, sourceFolder, DEFAULT_TIME_UNIT, warm, DEFAULT_MEASURE_VALUE, coefficients, DEFAULT_WORKING_TIME_DIFFERENCE_PERCENT, new DefaultActionApplier());
    }

    public void runBenchmarkTest(String fileName, String sourceFolder, double[] coefficients, IActionApplier applier) throws IOException {
        runBenchmarkTest(fileName, sourceFolder, DEFAULT_TIME_UNIT, DEFAULT_WARM_VALUE, DEFAULT_MEASURE_VALUE, coefficients, DEFAULT_WORKING_TIME_DIFFERENCE_PERCENT, applier);
    }

    public void runBenchmarkTest(String fileName, String sourceFolder, int warm, int measure, double[] coefficients, IActionApplier applier) throws IOException {
        runBenchmarkTest(fileName, sourceFolder, DEFAULT_TIME_UNIT, warm, measure, coefficients, DEFAULT_WORKING_TIME_DIFFERENCE_PERCENT, applier);
    }

    public void runBenchmarkTest(String fileName, String sourceFolder, TimeUnit timeUnit, int warm, int measure, double[] coefficients, double workingTimeDifferencePercent, IActionApplier applier) throws IOException {
        this.fileName = fileName;
        this.sourceFolder = sourceFolder;
        this.timeUnit = timeUnit;
        this.warm = warm;
        this.measure = measure;
        this.workingTimeDifferencePercent = workingTimeDifferencePercent;
        this.applier = applier;

        initializeTestMethods();

        double[] workingTimes = runBroadPhaseMethod(broadPhaseMethods);
        for (int i = 0; i < broadPhaseMethods.size(); i++) {
            if (DEFAULT_COMPARATIVE_METHOD.equals(broadPhaseMethods.get(i).getClass().getSimpleName())) {
                comparativeTime = workingTimes[i];
                break;
            }
        }

        outputConfig();

        List<BenchmarkResult> results = new ArrayList<>(broadPhaseMethods.size());
        for (int i = 0; i < broadPhaseMethods.size(); i++) {
            AbstractBroadPhase method = broadPhaseMethods.get(i);
            String methodName = method.getClass().getSimpleName();
            results.add(new BenchmarkResult(methodName, workingTimes[i], comparativeTime, coefficients[i], workingTimeDifferencePercent));
        }

        outputResults(results);
        for(BenchmarkResult result : results) {
            Assert.assertTrue(result.isPassed());
        }
    }

    private void initializeTestMethods() throws IOException {
        Assert.assertTrue("Use .pie file", fileName.matches(".*\\.pie"));
        methodShapes = ShapeIOUtil.readShapesFromFile(sourceFolder + fileName);

        broadPhaseMethods = new ArrayList<>();
        broadPhaseMethods.add(new BruteForceMethod());
        broadPhaseMethods.add(new SpatialHashingMethod());
        broadPhaseMethods.add(new SweepAndPruneMethod());
        broadPhaseMethods.add(new SweepAndPruneMyMethod());
        broadPhaseMethods.forEach(method -> method.setShapes(methodShapes));
    }

    // TODO Make the method universal using reflection
    private double[] runBroadPhaseMethod(List<AbstractBroadPhase> methods) {
        for (int i = 0; i < warm; i++) {
            methods.forEach(AbstractBroadPhase::calculateAabbCollision);
        }

        long[] averageNanoTime = new long[methods.size()];
        for (int i = 0; i < measure / 2; i++) {
            for (int j = 0; j < methods.size(); j++) {
                long previously = System.nanoTime();
                methods.get(j).calculateAabbCollision();
                averageNanoTime[j] += System.nanoTime() - previously;
            }
            applier.applyAction(methods, methodShapes);
        }

        for (int i = 0; i < measure / 2; i++) {
            for (int j = methods.size() - 1; j > -1; j--) {
                long previously = System.nanoTime();
                methods.get(j).calculateAabbCollision();
                averageNanoTime[j] += System.nanoTime() - previously;
            }
            applier.applyAction(methods, methodShapes);
        }

        double[] results = new double[methods.size()];
        for (int i = 0; i < methods.size(); i++) {
            results[i] = (double) timeUnit.convert(averageNanoTime[i], TimeUnit.NANOSECONDS) / measure;
        }
        return results;
    }

    private void outputResults(List<BenchmarkResult> results) {
        System.out.println("\nRESULTS");
        System.out.format("+---------------------------+----------------+-------------+-------------+-------------+-------------+\n");
        System.out.format("| Method name               | Time           | Expected    | Actual      | Bottom      | Top         |\n");
        System.out.format("+---------------------------+----------------+-------------+-------------+-------------+-------------+\n");
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.CANADA_FRENCH);
        numberFormat.setMaximumFractionDigits(3);
        results.forEach((result) -> System.out.format("| %-25s | %-14s | %-11.3f | %-11.3f | %-11.3f | %-11.3f |\n",
                result.methodName, numberFormat.format(result.methodTime), result.expectedDifference,
                result.actualDifference, result.bottomDifference, result.topDifference));
        System.out.format("+---------------------------+----------------+-------------+-------------+-------------+-------------+\n");
    }

    private void outputConfig() {
        System.out.println("PIE BENCHMARK TEST\n");
        int secondCellWidth = Collections.max(Arrays.asList(8, timeUnit.toString().length(), fileName.length(),
                sourceFolder.length(), DEFAULT_COMPARATIVE_METHOD.length()));
        int firstCellWidth = 20;

        System.out.println("CONFIG");
        System.out.print("+");
        for (int i = 0; i < firstCellWidth + 2; i++) {
            System.out.print("-");
        }
        System.out.print("+");
        for (int i = 0; i < secondCellWidth + 2; i++) {
            System.out.print("-");
        }
        System.out.print("+\n");
        String stringFormat = "| %-" + firstCellWidth + "s | %-" + secondCellWidth + "s |\n";
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.CANADA_FRENCH);
        numberFormat.setMaximumFractionDigits(3);
        System.out.format(stringFormat, "File", fileName);
        System.out.format(stringFormat, "Folder", sourceFolder);
        System.out.format(stringFormat, "Time unit", timeUnit);
        System.out.format(stringFormat, "Warming up", warm);
        System.out.format(stringFormat, "Measure", measure);
        System.out.format(stringFormat, "Comparative method", DEFAULT_COMPARATIVE_METHOD);
        System.out.format(stringFormat, "Comparative time", numberFormat.format(comparativeTime));
        System.out.format(stringFormat, "Difference percent", workingTimeDifferencePercent);

        System.out.print("+");
        for (int i = 0; i < firstCellWidth + 2; i++) {
            System.out.print("-");
        }
        System.out.print("+");
        for (int i = 0; i < secondCellWidth + 2; i++) {
            System.out.print("-");
        }
        System.out.print("+\n");
    }
}
