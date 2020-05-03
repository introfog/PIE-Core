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
public class LineShapesBenchmarkTest extends AbstractBroadPhaseBenchmarkTest {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/benchmark/collisions/broadphase/Line/";

    @Test
    public void simpleColumns() throws IOException {
        super.runBenchmarkTest("5x500line_8487collision.pie", PATH_TO_SOURCE_FOLDER, 70, new double[]{1.0, 0.95, 0.055, 0.43});
    }

    @Test
    public void mediumColumns() throws IOException {
        super.runBenchmarkTest("5x500line_22443collision.pie", PATH_TO_SOURCE_FOLDER, new double[]{1.0, 2.8, 0.11, 0.66});
    }

    @Test
    public void simpleRows() throws IOException {
        super.runBenchmarkTest("500x5line_8487collision.pie", PATH_TO_SOURCE_FOLDER, 70, new double[]{1.0, 1.0, 0.055, 0.055});
    }

    @Test
    public void mediumRows() throws IOException {
        super.runBenchmarkTest("500x5line_22443collision.pie", PATH_TO_SOURCE_FOLDER, new double[]{1.0, 3.0, 0.12, 0.12});
    }

    @Test
    public void hardRows() throws IOException {
        super.runBenchmarkTest("3000x2line+diffSize_20491collision.pie", PATH_TO_SOURCE_FOLDER, new double[]{1.0, 0.9, 0.019, 0.019});
    }
}
