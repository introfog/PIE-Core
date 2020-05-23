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
package com.github.introfog.pie.benchmark.collisions.broadphase.dynamical;

import com.github.introfog.pie.benchmark.collisions.broadphase.AbstractBroadPhaseBenchmarkTest;
import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapeIOUtil;
import com.github.introfog.pie.test.annotations.BenchmarkTest;

import java.io.IOException;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BenchmarkTest.class)
public class DynamicLineShapesBenchmarkTest extends AbstractBroadPhaseBenchmarkTest {
    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/benchmark/collisions/broadphase/Line/";

    @Test
    public void simpleColumnsSlowMovingTest() throws IOException {
        super.runBenchmarkTest("5x500line_8487collision.pie", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.27, 0.035, 0.28}, new SlowHorizontallyMover());
    }

    private static class SlowHorizontallyMover extends DefaultActionApplier {
        @Override
        protected void domesticApplyAction(List<AbstractBroadPhase> methods, List<IShape> methodShapes) {
            if (callCounter > 30) {
                callCounter = -29;
            }
            float diameter = methodShapes.get(0).aabb.max.x - methodShapes.get(0).aabb.min.x;
            diameter /= 10;
            Vector2f offset = new Vector2f(callCounter > 0 ? diameter : -diameter, 0);
            for (int i = 0; i < methodShapes.size(); i++) {
                methodShapes.get(i).body.position.add(offset, i % 2 == 0 ? -1 : 1);
            }
        }
    }

    @Test
    public void simpleRowsSlowMovingTest() throws IOException {
        super.runBenchmarkTest("500x5line_8487collision.pie", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.25, 0.045, 0.04}, new SlowVerticallyMover());
    }

    private static class SlowVerticallyMover extends DefaultActionApplier {
        @Override
        protected void domesticApplyAction(List<AbstractBroadPhase> methods, List<IShape> methodShapes) {
            if (callCounter > 30) {
                callCounter = -29;
            }

            float diameter = methodShapes.get(0).aabb.max.x - methodShapes.get(0).aabb.min.x;
            diameter /= 10;
            Vector2f offset = new Vector2f(0, callCounter > 0 ? diameter : -diameter);
            for (int i = 0; i < methodShapes.size(); i++) {
                methodShapes.get(i).body.position.add(offset, i % 2 == 0 ? -1 : 1);
            }
        }
    }

    @Test
    public void simpleColumnsQuickMovingTest() throws IOException {
        super.runBenchmarkTest("5x500line_8487collision.pie", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.2, 0.03, 0.28}, new QuickHorizontallyMover());
    }

    private static class QuickHorizontallyMover extends DefaultActionApplier {
        @Override
        protected void domesticApplyAction(List<AbstractBroadPhase> methods, List<IShape> methodShapes) {
            if (callCounter > 5) {
                callCounter = -4;
            }
            float diameter = methodShapes.get(0).aabb.max.x - methodShapes.get(0).aabb.min.x;
            Vector2f offset = new Vector2f(callCounter > 0 ? diameter : -diameter, 0);
            for (int i = 0; i < methodShapes.size(); i++) {
                methodShapes.get(i).body.position.add(offset, i % 2 == 0 ? -1 : 1);
            }
        }
    }

    @Test
    public void simpleRowsQuickMovingTest() throws IOException {
        super.runBenchmarkTest("500x5line_8487collision.pie", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.28, 0.04, 0.04}, new QuickVerticallyMover());
    }

    private static class QuickVerticallyMover extends DefaultActionApplier {
        @Override
        protected void domesticApplyAction(List<AbstractBroadPhase> methods, List<IShape> methodShapes) {
            if (callCounter > 5) {
                callCounter = -4;
            }

            float diameter = methodShapes.get(0).aabb.max.x - methodShapes.get(0).aabb.min.x;
            Vector2f offset = new Vector2f(0, callCounter > 0 ? diameter : -diameter);
            for (int i = 0; i < methodShapes.size(); i++) {
                methodShapes.get(i).body.position.add(offset, i % 2 == 0 ? -1 : 1);
            }
        }
    }

    @Test
    public void simpleRowsSlowAddingNewShapesTest() throws IOException {
        super.runBenchmarkTest("500x5line_8487collision.pie", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.3, 0.06, 0.055}, new NewSlowVerticalShapeAdder());
    }

    private static class NewSlowVerticalShapeAdder extends DefaultActionApplier {
        @Override
        protected void domesticApplyAction(List<AbstractBroadPhase> methods, List<IShape> methodShapes) {
            float diameter = methodShapes.get(0).aabb.max.x - methodShapes.get(0).aabb.min.x;
            Vector2f center = methodShapes.get(0).body.position;

            for (int i = 0; i < 10; i++) {
                Circle circle = new Circle(diameter / 2, center.x + i * (diameter - 1), center.y + callCounter * diameter / 2, MathPIE.STATIC_BODY_DENSITY, 0f);
                methods.forEach(method -> method.addShape(circle));
            }
        }
    }

    @Test
    public void simpleRowsQuickAddingNewShapesTest() throws IOException {
        super.runBenchmarkTest("500x5line_8487collision.pie", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.42, 0.065, 0.065}, new NewQuickVerticalShapeAdder());
    }

    private static class NewQuickVerticalShapeAdder extends DefaultActionApplier {
        @Override
        protected void domesticApplyAction(List<AbstractBroadPhase> methods, List<IShape> methodShapes) {
            float diameter = methodShapes.get(0).aabb.max.x - methodShapes.get(0).aabb.min.x;
            Vector2f center = methodShapes.get(0).body.position;

            for (int i = 0; i < 10; i++) {
                Circle circle = new Circle(diameter / 2, center.x + i * (diameter - 1), center.y + callCounter * diameter / 5, MathPIE.STATIC_BODY_DENSITY, 0f);
                methods.forEach(method -> method.addShape(circle));
                methodShapes.add(circle);
            }
        }
    }

    @Test
    public void simpleColumnsSlowAddingNewShapesTest() throws IOException {
        super.runBenchmarkTest("5x500line_8487collision.pie", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.27, 0.055, 0.3}, new NewSlowHorizontalShapeAdder());
    }

    private static class NewSlowHorizontalShapeAdder extends DefaultActionApplier {
        @Override
        protected void domesticApplyAction(List<AbstractBroadPhase> methods, List<IShape> methodShapes) {
            float diameter = methodShapes.get(0).aabb.max.x - methodShapes.get(0).aabb.min.x;
            Vector2f center = methodShapes.get(2000).body.position;

            for (int i = 0; i < 10; i++) {
                Circle circle = new Circle(diameter / 2, center.x - callCounter * diameter / 2, center.y + i * (diameter - 1), MathPIE.STATIC_BODY_DENSITY, 0f);
                methods.forEach(method -> method.addShape(circle));
            }
        }
    }

    @Test
    public void simpleColumnsQuickAddingNewShapesTest() throws IOException {
        super.runBenchmarkTest("5x500line_8487collision.pie", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.39, 0.065, 0.34}, new NewQuickHorizontalShapeAdder());
    }

    private static class NewQuickHorizontalShapeAdder extends DefaultActionApplier {
        @Override
        protected void domesticApplyAction(List<AbstractBroadPhase> methods, List<IShape> methodShapes) {
            float diameter = methodShapes.get(0).aabb.max.x - methodShapes.get(0).aabb.min.x;
            Vector2f center = methodShapes.get(2000).body.position;

            for (int i = 0; i < 10; i++) {
                Circle circle = new Circle(diameter / 2, center.x-+ callCounter * diameter / 5, center.y + i * (diameter - 1), MathPIE.STATIC_BODY_DENSITY, 0f);
                methods.forEach(method -> method.addShape(circle));
                methodShapes.add(circle);
            }
        }
    }

    @Test
    // This test shows how the SweepAndPrune method can analyze which axis is best used to determine possible intersections.
    public void hardRowsAndColumnsChangesTest() throws IOException {
        List<IShape> firstShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "5x500line_22443collision.pie");
        List<IShape> secondShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "500x5line_8487collision.pie");
        DefaultActionApplier applier = new ShapesChanger(firstShapes, secondShapes);
        super.runBenchmarkTest("500x5line_8487collision.pie", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.55, 0.16, 0.46}, applier);
    }


    @Test
    // This test shows how the SweepAndPrune method can analyze which axis is best used to determine possible intersections.
    public void hardColumnsAndRowsChangesTest() throws IOException {
        List<IShape> firstShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "500x5line_8487collision.pie");
        List<IShape> secondShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "5x500line_22443collision.pie");
        DefaultActionApplier applier = new ShapesChanger(firstShapes, secondShapes);
        super.runBenchmarkTest("5x500line_22443collision.pie", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.59, 0.16, 0.38}, applier);
    }

    @Test
    // This test shows how the SweepAndPrune method can analyze which axis is best used to determine possible intersections.
    public void hardColumnsAndHardRowsChangesTest() throws IOException {
        List<IShape> firstShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "500x5line_22443collision.pie");
        List<IShape> secondShapes = ShapeIOUtil.readShapesFromFile(PATH_TO_SOURCE_FOLDER + "5x500line_22443collision.pie");
        DefaultActionApplier applier = new ShapesChanger(firstShapes, secondShapes);
        super.runBenchmarkTest("500x5line_22443collision.pie", PATH_TO_SOURCE_FOLDER,
                20, 100, new double[]{1.0, 0.9, 0.22, 0.4}, applier);
    }

    private static class ShapesChanger extends DefaultActionApplier {
        private List<IShape> firstShapes;
        private List<IShape> secondShapes;

        public ShapesChanger(List<IShape> firstShapes, List<IShape> secondShapes) {
            this.firstShapes = firstShapes;
            this.secondShapes = secondShapes;
        }

        @Override
        protected void domesticApplyAction(List<AbstractBroadPhase> methods, List<IShape> methodShapes) {
            long temp = callCounter % 20;
            if (temp < 10) {
                methods.forEach(method -> method.setShapes(firstShapes));
            } else {
                methods.forEach(method -> method.setShapes(secondShapes));
            }
        }
    }
}
