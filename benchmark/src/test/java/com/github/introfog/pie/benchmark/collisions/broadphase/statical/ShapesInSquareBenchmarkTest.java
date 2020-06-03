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
package com.github.introfog.pie.benchmark.collisions.broadphase.statical;

import com.github.introfog.pie.benchmark.collisions.broadphase.AbstractBroadPhaseBenchmarkTest;
import com.github.introfog.pie.benchmark.collisions.broadphase.BenchmarkTestConfig;
import com.github.introfog.pie.test.annotations.BenchmarkTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

// Run test methods only for the entire test class, otherwise the tests may not pass due to a different performance difference
@Category(BenchmarkTest.class)
public class ShapesInSquareBenchmarkTest extends AbstractBroadPhaseBenchmarkTest {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/benchmark/collisions/broadphase/Square/";

    @Test
    public void square50x50ShapesWith9702CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("50x50square_9702collision.pie",
                PATH_TO_SOURCE_FOLDER, 70, new double[]{1.0, 0.4, 0.12, 0.09});
        super.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void square50x50ShapesWith28518CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("50x50square_28518collision.pie",
                PATH_TO_SOURCE_FOLDER, new double[]{1.0, 1.5, 0.25, 0.22});
        super.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void square70x70ShapesWithDifferentSizeAnd17320CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("70x70square+diffSize_17320collision.pie",
                PATH_TO_SOURCE_FOLDER, new double[]{1.0, 0.8, 0.055, 0.06});
        super.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void square100x100ScatteredShapesWithDifferentSizeAnd14344CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("100x100square+scattered+diffSize_14344collision.pie",
                PATH_TO_SOURCE_FOLDER, new double[]{1.0, 0.15, 0.011, 0.015});
        super.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void square100x100ScatteredShapesWith14602CollisionsTest() throws Exception {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("100x100square+scattered_14602collision.pie",
                PATH_TO_SOURCE_FOLDER, new double[]{1.0, 0.03, 0.025, 0.018});
        super.runBroadPhaseBenchmarkTest(testConfig);
    }
}
