package com.introfog.pie.benchmark.collisions.broadphase;

import com.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.introfog.pie.core.collisions.broadphase.BruteForceMethod;
import com.introfog.pie.core.collisions.broadphase.SpatialHashingMethod;
import com.introfog.pie.core.collisions.broadphase.SweepAndPruneMethod;
import com.introfog.pie.core.collisions.broadphase.SweepAndPruneMyMethod;
import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.util.ShapeIOUtil;

import java.io.IOException;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;

public abstract class AbstractBroadPhaseBenchmark {
    private final static int DEFAULT_WARM_VALUE = 5;
    private final static int DEFAULT_MEASURE_VALUE = 10;
    private final static TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MICROSECONDS;

    private List<AbstractBroadPhase> broadPhaseMethods;
    String fileName;
    String sourceFolder;
    private TimeUnit timeUnit;
    private int warm;
    private int measure;

    public void runBenchmarkTest(String fileName, String sourceFolder) throws IOException {
        runBenchmarkTest(fileName, sourceFolder, DEFAULT_TIME_UNIT);
    }

    public void runBenchmarkTest(String fileName, String sourceFolder, TimeUnit timeUnit) throws IOException {
        runBenchmarkTest(fileName, sourceFolder, timeUnit, DEFAULT_WARM_VALUE, DEFAULT_MEASURE_VALUE);
    }

    public void runBenchmarkTest(String fileName, String sourceFolder, int warm, int measure) throws IOException {
        runBenchmarkTest(fileName, sourceFolder, DEFAULT_TIME_UNIT, warm, measure);
    }

    public void runBenchmarkTest(String fileName, String sourceFolder, TimeUnit timeUnit, int warm, int measure) throws IOException {
        this.fileName = fileName;
        this.sourceFolder = sourceFolder;
        this.timeUnit = timeUnit;
        this.warm = warm;
        this.measure = measure;

        initializeTestMethods();

        outputConfig();

        Map<String, Double> results = new LinkedHashMap<>(broadPhaseMethods.size());
        broadPhaseMethods.forEach(method -> results.put(method.getClass().getSimpleName(), runBroadPhaseMethod(method)));

        outputResults(results);
    }

    private void initializeTestMethods() throws IOException {
        Assert.assertTrue("Use .json file", fileName.matches(".*\\.pie"));
        List<IShape> shapes = ShapeIOUtil.readShapesFromFile(sourceFolder + fileName);

        broadPhaseMethods = new ArrayList<>();
        broadPhaseMethods.add(new BruteForceMethod());
        broadPhaseMethods.add(new SpatialHashingMethod());
        broadPhaseMethods.add(new SweepAndPruneMethod());
        broadPhaseMethods.add(new SweepAndPruneMyMethod());

        broadPhaseMethods.forEach(method -> method.setShapes(shapes));
    }

    // TODO Make the method universal using reflection
    private double runBroadPhaseMethod(AbstractBroadPhase method) {
        for (int i = 0; i < warm; i++) {
            method.calculateAabbCollision();
        }
        long averageNanoTime = 0;
        for (int i = 0; i < measure; i++) {
            long previously = System.nanoTime();
            method.calculateAabbCollision();
            averageNanoTime += System.nanoTime() - previously;
        }

        return (double) timeUnit.convert(averageNanoTime, TimeUnit.NANOSECONDS) / measure;
    }

    private void outputResults(Map<String, Double> results) {
        System.out.println("\nRESULTS");
        System.out.format("+---------------------------+----------------+\n");
        System.out.format("| Method name               | Time           |\n");
        System.out.format("+---------------------------+----------------+\n");
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.CANADA_FRENCH);
        numberFormat.setMaximumFractionDigits(3);
        results.forEach((method, time) -> System.out.format("| %-25s | %-14s |\n", method, numberFormat.format(time)));
        System.out.format("+---------------------------+----------------+\n");
    }

    private void outputConfig() {
        System.out.println("PIE BENCHMARK TEST\n");
        int cellWidth = Collections.max(Arrays.asList(8, timeUnit.toString().length(), fileName.length(), sourceFolder.length()));

        System.out.println("CONFIG");
        System.out.format("+--------------+");
        for (int i = 0; i < cellWidth + 2; i++) {
            System.out.print("-");
        }
        System.out.print("+\n");
        String format = "| %-12s | %-" + cellWidth + "s |\n";
        System.out.format(format, "File", fileName);
        System.out.format(format, "Folder", sourceFolder);
        System.out.format(format, "Time unit", timeUnit);
        System.out.format(format, "Warming up", warm);
        System.out.format(format, "Measure", measure);
        System.out.format("+--------------+");
        for (int i = 0; i < cellWidth + 2; i++) {
            System.out.print("-");
        }
        System.out.print("+\n");
    }
}
