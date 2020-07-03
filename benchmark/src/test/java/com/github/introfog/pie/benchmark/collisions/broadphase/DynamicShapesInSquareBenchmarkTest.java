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

import com.github.introfog.pie.benchmark.collisions.broadphase.applier.MoveToPointActionApplier;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.BenchmarkTest;

import java.io.IOException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

/*
Currently, benchmark tests are run only on the developer's machine, as it was not possible to achieve uniform results
on the machines used in GitHub Action (this is most likely due to different machine capacities). In the future it is
planned to solve this problem, and add benchmark tests to the build action or other pipeline (TeamCity for example).
 */
@Category(BenchmarkTest.class)
public class DynamicShapesInSquareBenchmarkTest extends PIETest {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/benchmark/collisions/broadphase/Square/";

    @Test
    public void mediumSquareSlowMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("50x50square_28518collision", PATH_TO_SOURCE_FOLDER,
                10, 100, new double[]{1.0, 1.27, 0.18, 0.14}, new MoveToPointActionApplier(30, 2));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void mediumSquareQuickMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("50x50square_28518collision", PATH_TO_SOURCE_FOLDER,
                10, 100, new double[]{1.0, 0.68, 0.11, 0.09}, new MoveToPointActionApplier(30, 20));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }
}
