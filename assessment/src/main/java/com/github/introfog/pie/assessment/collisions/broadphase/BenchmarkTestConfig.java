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
import com.github.introfog.pie.assessment.collisions.broadphase.applier.ToDoNothingAction;
import com.github.introfog.pie.core.collisions.broadphase.BruteForceMethod;

import java.text.NumberFormat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BenchmarkTestConfig {
    public final static int DEFAULT_WARM_VALUE = 45;
    public final static int DEFAULT_MEASURE_VALUE = 6;
    public final static double DEFAULT_ALLOWED_WORKING_TIME_DIFFERENCE = 0.25;
    public final static String DEFAULT_COMPARATIVE_METHOD_NAME = BruteForceMethod.class.getSimpleName();
    public final static TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MICROSECONDS;
    public final static Class<ToDoNothingAction> DEFAULT_ACTION_APPLIER = ToDoNothingAction.class;

    public int warm;
    public int measure;
    public double allowedWorkingTimeDifference;
    public double[] expectedCoefficients;
    public String fileName;
    public String sourceFolder;
    public String comparativeMethodName;
    public TimeUnit timeUnit;
    public IAction applier;

    public BenchmarkTestConfig(String fileName, String sourceFolder, double[] expectedCoefficients) throws ReflectiveOperationException {
        this(fileName, sourceFolder, DEFAULT_TIME_UNIT, DEFAULT_WARM_VALUE, DEFAULT_MEASURE_VALUE, expectedCoefficients,
                DEFAULT_ALLOWED_WORKING_TIME_DIFFERENCE, DEFAULT_ACTION_APPLIER.getDeclaredConstructor().newInstance(),
                DEFAULT_COMPARATIVE_METHOD_NAME);
    }

    public BenchmarkTestConfig(String fileName, String sourceFolder, int warm, double[] expectedCoefficients) throws ReflectiveOperationException {
        this(fileName, sourceFolder, DEFAULT_TIME_UNIT, warm, DEFAULT_MEASURE_VALUE, expectedCoefficients,
                DEFAULT_ALLOWED_WORKING_TIME_DIFFERENCE, DEFAULT_ACTION_APPLIER.getDeclaredConstructor().newInstance(),
                DEFAULT_COMPARATIVE_METHOD_NAME);
    }

    public BenchmarkTestConfig(String fileName, String sourceFolder, int warm, int measure, double[] expectedCoefficients, IAction applier) {
        this(fileName, sourceFolder, DEFAULT_TIME_UNIT, warm, measure, expectedCoefficients,
                DEFAULT_ALLOWED_WORKING_TIME_DIFFERENCE, applier, DEFAULT_COMPARATIVE_METHOD_NAME);
    }

    public BenchmarkTestConfig(String fileName, String sourceFolder, TimeUnit timeUnit, int warm, int measure,
            double[] expectedCoefficients, double allowedWorkingTimeDifference, IAction applier, String comparativeMethodName) {
        this.fileName = fileName;
        this.sourceFolder = sourceFolder;
        this.timeUnit = timeUnit;
        this.warm = warm;
        this.measure = measure;
        this.expectedCoefficients = expectedCoefficients;
        this.allowedWorkingTimeDifference = allowedWorkingTimeDifference;
        this.applier = applier;
        this.comparativeMethodName = comparativeMethodName;
    }

    public void outputTestConfig() {
        System.out.println("PIE BENCHMARK TEST\n");
        int firstColumnWidth = 20;
        int secondColumnWidth = Collections.max(Arrays.asList(8, timeUnit.toString().length(), fileName.length(),
                sourceFolder.length(), comparativeMethodName.length()));

        System.out.println("CONFIG");
        BenchmarkTestConfig.outputConfigLine(firstColumnWidth, secondColumnWidth);

        String stringFormat = "| %-" + firstColumnWidth + "s | %-" + secondColumnWidth + "s |\n";
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.CANADA_FRENCH);
        numberFormat.setMaximumFractionDigits(3);
        System.out.format(stringFormat, "File", fileName);
        System.out.format(stringFormat, "Folder", sourceFolder);
        System.out.format(stringFormat, "Time unit", timeUnit);
        System.out.format(stringFormat, "Warming up", warm);
        System.out.format(stringFormat, "Measure", measure);
        System.out.format(stringFormat, "Comparative method", comparativeMethodName);
        System.out.format(stringFormat, "Allowed difference", allowedWorkingTimeDifference);

        BenchmarkTestConfig.outputConfigLine(firstColumnWidth, secondColumnWidth);
    }

    private static void outputConfigLine(int firstColumnWidth, int secondColumnWidth) {
        System.out.print("+");
        for (int i = 0; i < firstColumnWidth + 2; i++) {
            System.out.print("-");
        }
        System.out.print("+");
        for (int i = 0; i < secondColumnWidth + 2; i++) {
            System.out.print("-");
        }
        System.out.print("+\n");
    }
}
