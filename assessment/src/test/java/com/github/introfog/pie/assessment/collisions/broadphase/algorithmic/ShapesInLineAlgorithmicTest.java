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
package com.github.introfog.pie.assessment.collisions.broadphase.algorithmic;

import com.github.introfog.pie.assessment.collisions.broadphase.BroadPhaseAlgorithmicTestRunner;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.AlgorithmicTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(AlgorithmicTest.class)
public class ShapesInLineAlgorithmicTest extends PIETest {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/assessment/collisions/broadphase/Line/";

    @Test
    public void vertical5x500ShapesWith8487CollisionsTest() throws Exception {
        BroadPhaseAlgorithmicTestRunner.runStaticBroadPhaseAlgorithmicTest("5x500line_8487collision", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void vertical5x500ShapesWith22443CollisionsTest() throws Exception {
        BroadPhaseAlgorithmicTestRunner.runStaticBroadPhaseAlgorithmicTest("5x500line_22443collision", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsTest() throws Exception {
        BroadPhaseAlgorithmicTestRunner.runStaticBroadPhaseAlgorithmicTest("500x5line_8487collision", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void horizontal500x5ShapesWith22443CollisionsTest() throws Exception {
        BroadPhaseAlgorithmicTestRunner.runStaticBroadPhaseAlgorithmicTest("500x5line_22443collision", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void horizontal3000x2ShapesWithDifferentSizeAnd20491CollisionsTest() throws Exception {
        BroadPhaseAlgorithmicTestRunner.runStaticBroadPhaseAlgorithmicTest("3000x2line+diffSize_20491collision", PATH_TO_SOURCE_FOLDER);
    }
}
