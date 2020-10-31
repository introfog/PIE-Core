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
import com.github.introfog.pie.assessment.collisions.broadphase.applier.AddShapesAction;
import com.github.introfog.pie.assessment.collisions.broadphase.applier.IAction;
import com.github.introfog.pie.assessment.collisions.broadphase.applier.MoveShapesAction;
import com.github.introfog.pie.assessment.collisions.broadphase.applier.ChangeShapesAction;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapeIOUtil;
import com.github.introfog.pie.test.PieTest;
import com.github.introfog.pie.test.annotations.AlgorithmicTest;

import java.io.IOException;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(AlgorithmicTest.class)
public class DynamicShapesInLineAlgorithmicTest extends PieTest {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/assessment/collisions/broadphase/Line/";

    @Test
    public void vertical5x500ShapesWith8487CollisionsSlowMovingTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("5x500line_8487collision",
                PATH_TO_SOURCE_FOLDER, 60, new MoveShapesAction(30, 2, true));
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsSlowMovingTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("500x5line_8487collision",
                PATH_TO_SOURCE_FOLDER, 60, new MoveShapesAction(30, 2, false));
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsQuickMovingTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("5x500line_8487collision",
                PATH_TO_SOURCE_FOLDER,10, new MoveShapesAction(5, 20, true));
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsQuickMovingTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("500x5line_8487collision",
                PATH_TO_SOURCE_FOLDER,10, new MoveShapesAction(5, 20, false));
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsSomeBodiesSlowMovingTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("5x500line_8487collision",
                PATH_TO_SOURCE_FOLDER, 60, new MoveShapesAction(30, 2, true, 7));
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsSomeBodiesSlowMovingTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("500x5line_8487collision",
                PATH_TO_SOURCE_FOLDER, 60, new MoveShapesAction(30, 2, false, 7));
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsSomeBodiesQuickMovingTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("5x500line_8487collision",
                PATH_TO_SOURCE_FOLDER,10, new MoveShapesAction(5, 20, true, 7));
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsSomeBodiesQuickMovingTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("500x5line_8487collision",
                PATH_TO_SOURCE_FOLDER,10, new MoveShapesAction(5, 20, false, 7));
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsSlowAddingNewShapesTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("500x5line_8487collision",
                PATH_TO_SOURCE_FOLDER,10, new AddShapesAction(20, 10, new Vector2f(100, 100), false));
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsQuickAddingNewShapesTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("500x5line_8487collision",
                PATH_TO_SOURCE_FOLDER, 10, new AddShapesAction(20, 4, new Vector2f(100, 100), false));
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsSlowAddingNewShapesTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("5x500line_8487collision",
                PATH_TO_SOURCE_FOLDER, 10, new AddShapesAction(20, 10, new Vector2f(176, 100), true));
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsQuickAddingNewShapesTest() throws IOException {
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("5x500line_8487collision",
                PATH_TO_SOURCE_FOLDER, 10, new AddShapesAction(20, 4, new Vector2f(176, 100), true));
    }

    @Test
    // This test shows how the SweepAndPrune method can analyze which axis is best used to determine possible intersections.
    public void horizontal500x5With8487CollisionsAnd5x500With22443ChangesTest() throws IOException {
        List<IShape> firstShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "5x500line_22443collision.pie");
        List<IShape> secondShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "500x5line_8487collision.pie");
        IAction applier = new ChangeShapesAction(firstShapes, secondShapes, 8);
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("500x5line_8487collision",
                PATH_TO_SOURCE_FOLDER,16, applier);
    }


    @Test
    // This test shows how the SweepAndPrune method can analyze which axis is best used to determine possible intersections.
    public void horizontal500x5With22443CollisionsAnd5x500With8487ChangesTest() throws IOException {
        List<IShape> firstShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "5x500line_8487collision.pie");
        List<IShape> secondShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "500x5line_22443collision.pie");
        IAction applier = new ChangeShapesAction(firstShapes, secondShapes, 8);
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("500x5line_22443collision",
                PATH_TO_SOURCE_FOLDER, 16, applier);
    }

    @Test
    // This test shows how the SweepAndPrune method can analyze which axis is best used to determine possible intersections.
    public void horizontal500x5With22443CollisionsAnd5x500With22443ChangesTest() throws IOException {
        List<IShape> firstShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "500x5line_22443collision.pie");
        List<IShape> secondShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "5x500line_22443collision.pie");
        IAction applier = new ChangeShapesAction(firstShapes, secondShapes, 8);
        BroadPhaseAlgorithmicTestRunner.runDynamicBroadPhaseAlgorithmicTest("500x5line_22443collision",
                PATH_TO_SOURCE_FOLDER, 16, applier);
    }
}
