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
public class ShapesInSquareBenchmarkTest extends PIETest {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/benchmark/collisions/broadphase/Square/";

    @Test
    public void square50x50ShapesWith9702CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("50x50square_9702collision",
                PATH_TO_SOURCE_FOLDER, 70, new double[]{1.0, 0.08, 0.1, 0.03});
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void square50x50ShapesWith28518CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("50x50square_28518collision",
                PATH_TO_SOURCE_FOLDER, new double[]{1.0, 0.16, 0.17, 0.05});
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void square70x70ShapesWithDifferentSizeAnd17320CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("70x70square+diffSize_17320collision",
                PATH_TO_SOURCE_FOLDER, new double[]{1.0, 0.06, 0.06, 0.016});
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void square100x100ScatteredShapesWithDifferentSizeAnd14344CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("100x100square+scattered+diffSize_14344collision",
                PATH_TO_SOURCE_FOLDER, new double[]{1.0, 0.02, 0.011, 0.01});
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void square100x100ScatteredShapesWith14602CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("100x100square+scattered_14602collision",
                PATH_TO_SOURCE_FOLDER, new double[]{1.0, 0.01, 0.028, 0.005});
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }
}
