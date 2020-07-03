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
package com.github.introfog.pie.benchmark.collisions.broadphase.algorithmic;

import com.github.introfog.pie.benchmark.collisions.broadphase.BroadPhaseAlgorithmicTestRunner;
import com.github.introfog.pie.benchmark.collisions.broadphase.applier.MoveToPointActionApplier;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.AlgorithmicTest;

import java.io.IOException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(AlgorithmicTest.class)
public class DynamicShapesInSquareAlgorithmicTest extends PIETest {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/benchmark/collisions/broadphase/Square/";

    @Test
    public void mediumSquareSlowMovingTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("50x50square_28518collision",
                PATH_TO_SOURCE_FOLDER,60, new MoveToPointActionApplier(30, 2));
    }

    @Test
    public void mediumSquareQuickMovingTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("50x50square_28518collision",
                PATH_TO_SOURCE_FOLDER, 60, new MoveToPointActionApplier(30, 20));
    }
}
