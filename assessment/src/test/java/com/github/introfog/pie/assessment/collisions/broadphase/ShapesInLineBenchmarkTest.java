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

import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.BenchmarkTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

/*
Currently, benchmark tests are run only on the developer's machine, as it was not possible to achieve uniform results
on the machines used in GitHub Action (this is most likely due to different machine capacities). In the future it is
planned to solve this problem, and add benchmark tests to the build action or other pipeline (TeamCity for example).
 */
@Category(BenchmarkTest.class)
public class ShapesInLineBenchmarkTest extends PIETest {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/assessment/collisions/broadphase/Line/";

    @Test
    public void vertical5x500ShapesWith8487CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_8487collision",
                PATH_TO_SOURCE_FOLDER, 70, new double[]{1.0, 0.07, 0.02, 0.03});
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void vertical5x500ShapesWith22443CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_22443collision",
                PATH_TO_SOURCE_FOLDER, new double[]{1.0, 0.1, 0.02, 0.04});
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision",
                PATH_TO_SOURCE_FOLDER, 70, new double[]{1.0, 0.06, 0.012, 0.02});
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith22443CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_22443collision",
                PATH_TO_SOURCE_FOLDER, new double[]{1.0, 0.115, 0.02, 0.035});
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal3000x2ShapesWithDifferentSizeAnd20491CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("3000x2line+diffSize_20491collision",
                PATH_TO_SOURCE_FOLDER, new double[]{1.0, 0.04, 0.006, 0.01});
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }
}
