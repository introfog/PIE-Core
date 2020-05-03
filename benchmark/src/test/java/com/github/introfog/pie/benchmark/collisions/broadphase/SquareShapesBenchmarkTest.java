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

import com.github.introfog.pie.test.annotations.BenchmarkTest;

import java.io.IOException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

// Run test methods only for the entire test class, otherwise the tests may not pass due to a different performance difference
@Category(BenchmarkTest.class)
public class SquareShapesBenchmarkTest extends AbstractBroadPhaseBenchmarkTest {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/benchmark/collisions/broadphase/Square/";

    @Test
    public void simpleSquare() throws IOException {
        super.runBenchmarkTest("50x50square_9702collision.pie", PATH_TO_SOURCE_FOLDER, 70, new double[]{1.0, 1.3, 0.12, 0.09});
    }

    @Test
    public void mediumSquare() throws IOException {
        super.runBenchmarkTest("50x50square_28518collision.pie", PATH_TO_SOURCE_FOLDER, new double[]{1.0, 7.0, 0.25, 0.22});
    }

    @Test
    public void mediumSquareWithDiffSize() throws IOException {
        super.runBenchmarkTest("70x70square+diffSize_17320collision.pie", PATH_TO_SOURCE_FOLDER, new double[]{1.0, 8, 0.055, 0.06});
    }

    @Test
    public void simpleSquareScatteredWithDiffSize() throws IOException {
        super.runBenchmarkTest("100x100square+scattered+diffSize_14344collision.pie", PATH_TO_SOURCE_FOLDER, new double[]{1.0, 1.1, 0.011, 0.015});
    }

    @Test
    public void simpleSquareScattered() throws IOException {
        super.runBenchmarkTest("100x100square+scattered_14602collision.pie", PATH_TO_SOURCE_FOLDER, new double[]{1.0, 0.06, 0.025, 0.018});
    }
}
