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

import com.github.introfog.pie.benchmark.collisions.broadphase.applier.DefaultActionApplier;
import com.github.introfog.pie.benchmark.collisions.broadphase.applier.AddShapesActionApplier;
import com.github.introfog.pie.benchmark.collisions.broadphase.applier.MoveActionApplier;
import com.github.introfog.pie.benchmark.collisions.broadphase.applier.ChangeShapesActionApplier;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapeIOUtil;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.BenchmarkTest;

import java.io.IOException;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/*
Currently, benchmark tests are run only on the developer's machine, as it was not possible to achieve uniform results
on the machines used in GitHub Action (this is most likely due to different machine capacities). In the future it is
planned to solve this problem, and add benchmark tests to the build action or other pipeline (TeamCity for example).
 */
@Category(BenchmarkTest.class)
public class DynamicShapesInLineBenchmarkTest extends PIETest {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/benchmark/collisions/broadphase/Line/";

    @Test
    public void vertical5x500ShapesWith8487CollisionsSlowMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.06, 0.06, 0.01, 0.28}, new MoveActionApplier(30, 2, true));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsSlowMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.065, 0.07, 0.015, 0.23}, new MoveActionApplier(30, 2, false));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsQuickMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.05, 0.06, 0.01, 0.4}, new MoveActionApplier(5, 20, true));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsQuickMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.06, 0.07, 0.015, 0.44}, new MoveActionApplier(5, 20, false));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsSomeBodiesSlowMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.06, 0.06, 0.015, 0.06}, new MoveActionApplier(30, 2, true, 7));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsSomeBodiesSlowMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.07, 0.08, 0.015, 0.05}, new MoveActionApplier(30, 2, false, 7));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsSomeBodiesQuickMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.05, 0.06, 0.012, 0.09}, new MoveActionApplier(5, 20, true, 7));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsSomeBodiesQuickMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.07, 0.08, 0.02, 0.09}, new MoveActionApplier(5, 20, false, 7));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsSlowAddingNewShapesTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.06, 0.06, 0.03, 0.02}, new AddShapesActionApplier(20, 10, new Vector2f(100, 100), false));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsQuickAddingNewShapesTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.08, 0.07, 0.03, 0.02}, new AddShapesActionApplier(20, 4, new Vector2f(100, 100), false));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsSlowAddingNewShapesTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.06, 0.06, 0.03, 0.02}, new AddShapesActionApplier(20, 10, new Vector2f(176, 100), true));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsQuickAddingNewShapesTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.08, 0.07, 0.03, 0.025}, new AddShapesActionApplier(20, 4, new Vector2f(176, 100), true));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    // This test shows how the SweepAndPrune method can analyze which axis is best used to determine possible intersections.
    public void horizontal500x5With8487CollisionsAnd5x500With22443ChangesTest() throws IOException {
        List<IShape> firstShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "5x500line_22443collision.pie");
        List<IShape> secondShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "500x5line_8487collision.pie");
        DefaultActionApplier applier = new ChangeShapesActionApplier(firstShapes, secondShapes, 20);
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision",
                PATH_TO_SOURCE_FOLDER, 20, 100, new double[]{1.0, 0.09, 0.09, 0.15, 0.05}, applier);
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    // This test shows how the SweepAndPrune method can analyze which axis is best used to determine possible intersections.
    public void horizontal500x5With22443CollisionsAnd5x500With8487ChangesTest() throws IOException {
        List<IShape> firstShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "5x500line_8487collision.pie");
        List<IShape> secondShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "500x5line_22443collision.pie");
        DefaultActionApplier applier = new ChangeShapesActionApplier(firstShapes, secondShapes, 20);
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_22443collision",
                PATH_TO_SOURCE_FOLDER, 20, 100, new double[]{1.0, 0.1, 0.1, 0.15, 0.05}, applier);
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    // This test shows how the SweepAndPrune method can analyze which axis is best used to determine possible intersections.
    public void horizontal500x5With22443CollisionsAnd5x500With22443ChangesTest() throws IOException {
        List<IShape> firstShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "500x5line_22443collision.pie");
        List<IShape> secondShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "5x500line_22443collision.pie");
        DefaultActionApplier applier = new ChangeShapesActionApplier(firstShapes, secondShapes, 20);
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_22443collision",
                PATH_TO_SOURCE_FOLDER, 20, 100, new double[]{1.0, 0.12, 0.11, 0.17, 0.06}, applier);
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }
}
