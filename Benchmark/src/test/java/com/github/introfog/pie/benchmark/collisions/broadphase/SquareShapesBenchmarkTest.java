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

import java.io.IOException;

import org.junit.Test;

public class SquareShapesBenchmarkTest extends AbstractBroadPhaseBenchmark {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/benchmark/collisions/broadphase/Square/";

    @Test
    public void simpleSquare() throws IOException {
        super.runBenchmarkTest("50x50square_9702collision.pie", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void mediumSquare() throws IOException {
        super.runBenchmarkTest("50x50square_28518collision.pie", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void mediumSquareWithDiffSize() throws IOException {
        super.runBenchmarkTest("70x70square+diffSize_17320collision.pie", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void simpleSquareScatteredWithDiffSize() throws IOException {
        super.runBenchmarkTest("100x100square+scattered+diffSize_14344collision.pie", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void simpleSquareScattered() throws IOException {
        super.runBenchmarkTest("100x100square+scattered_14602collision.pie", PATH_TO_SOURCE_FOLDER);
    }
}
