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

public class BenchmarkResult {
    public String methodName;
    public double methodTime;
    public double comparativeTime;
    public double expectedDifference;
    public double differencePercent;
    public double bottomDifference;
    public double topDifference;
    public double actualDifference;


    public BenchmarkResult(String methodName, double methodTime, double comparativeTime, double expectedDifference,
            double differencePercent) {
        this.methodName = methodName;
        this.methodTime = methodTime;
        this.comparativeTime = comparativeTime;
        this.expectedDifference = expectedDifference;
        this.differencePercent = differencePercent;

        double expectedTime = comparativeTime * expectedDifference;
        actualDifference = methodTime / comparativeTime;
        // The closer the expected difference is to zero, the more additional percentages of the difference will be
        // resolved. This is done due to the fact that when the expected difference is very small (approximately 0.01),
        // the results can be very different.
        if (expectedDifference < 1.0) {
            differencePercent += 0.15 * Math.pow(1 - expectedDifference, 5);
        }
        bottomDifference = expectedTime * (1.0 - differencePercent) / comparativeTime;
        topDifference = expectedTime * (1.0 + differencePercent) / comparativeTime;
    }

    public boolean isPassed() {
        return (bottomDifference < actualDifference) && (actualDifference < topDifference);
    }
}
