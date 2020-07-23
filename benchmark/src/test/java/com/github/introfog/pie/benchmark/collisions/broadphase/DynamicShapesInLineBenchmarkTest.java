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
                20, 100, new double[]{1.0, 0.27, 0.035, 0.34, 0.3}, new MoveActionApplier(30, 2, true));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsSlowMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.25, 0.045, 0.04, 0.24}, new MoveActionApplier(30, 2, false));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsQuickMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.2, 0.03, 0.28, 0.4}, new MoveActionApplier(5, 20, true));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsQuickMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.25, 0.04, 0.04, 0.43}, new MoveActionApplier(5, 20, false));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    @Ignore("TODO #65 There is problem with SpatialHashingMethod")
    public void vertical5x500ShapesWith8487CollisionsSomeBodiesSlowMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.27, 0.045, 0.34, 0.12}, new MoveActionApplier(30, 2, true, 7));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    @Ignore("TODO #65 There is problem with SpatialHashingMethod")
    public void horizontal500x5ShapesWith8487CollisionsSomeBodiesSlowMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.25, 0.045, 0.045, 0.1}, new MoveActionApplier(30, 2, false, 7));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsSomeBodiesQuickMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.2, 0.035, 0.28, 0.12}, new MoveActionApplier(5, 20, true, 7));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsSomeBodiesQuickMovingTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.25, 0.04, 0.045, 0.12}, new MoveActionApplier(5, 20, false, 7));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsSlowAddingNewShapesTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.3, 0.06, 0.055, 0.06}, new AddShapesActionApplier(20, 10, new Vector2f(100, 100), false));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void horizontal500x5ShapesWith8487CollisionsQuickAddingNewShapesTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.42, 0.065, 0.065, 0.07}, new AddShapesActionApplier(20, 4, new Vector2f(100, 100), false));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsSlowAddingNewShapesTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.27, 0.055, 0.34, 0.06}, new AddShapesActionApplier(20, 10, new Vector2f(176, 100), true));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    public void vertical5x500ShapesWith8487CollisionsQuickAddingNewShapesTest() throws IOException {
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("5x500line_8487collision", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.39, 0.065, 0.37, 0.075}, new AddShapesActionApplier(20, 4, new Vector2f(176, 100), true));
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    // This test shows how the SweepAndPrune method can analyze which axis is best used to determine possible intersections.
    public void horizontal500x5With8487CollisionsAnd5x500With22443ChangesTest() throws IOException {
        List<IShape> firstShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "5x500line_22443collision.pie");
        List<IShape> secondShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "500x5line_8487collision.pie");
        DefaultActionApplier applier = new ChangeShapesActionApplier(firstShapes, secondShapes, 20);
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_8487collision",
                PATH_TO_SOURCE_FOLDER, 20, 100, new double[]{1.0, 0.55, 0.16, 0.46, 0.13}, applier);
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    // This test shows how the SweepAndPrune method can analyze which axis is best used to determine possible intersections.
    public void horizontal500x5With22443CollisionsAnd5x500With8487ChangesTest() throws IOException {
        List<IShape> firstShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "5x500line_8487collision.pie");
        List<IShape> secondShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "500x5line_22443collision.pie");
        DefaultActionApplier applier = new ChangeShapesActionApplier(firstShapes, secondShapes, 20);
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_22443collision",
                PATH_TO_SOURCE_FOLDER, 20, 100, new double[]{1.0, 0.59, 0.16, 0.38, 0.13}, applier);
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }

    @Test
    // This test shows how the SweepAndPrune method can analyze which axis is best used to determine possible intersections.
    public void horizontal500x5With22443CollisionsAnd5x500With22443ChangesTest() throws IOException {
        List<IShape> firstShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "500x5line_22443collision.pie");
        List<IShape> secondShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "5x500line_22443collision.pie");
        DefaultActionApplier applier = new ChangeShapesActionApplier(firstShapes, secondShapes, 20);
        BenchmarkTestConfig testConfig = new BenchmarkTestConfig("500x5line_22443collision",
                PATH_TO_SOURCE_FOLDER, 20, 100, new double[]{1.0, 0.9, 0.22, 0.4, 0.15}, applier);
        BroadPhaseBenchmarkTestRunner.runBroadPhaseBenchmarkTest(testConfig);
    }
}
