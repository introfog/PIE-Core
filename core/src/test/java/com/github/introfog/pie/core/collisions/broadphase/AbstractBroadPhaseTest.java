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
package com.github.introfog.pie.core.collisions.broadphase;

import com.github.introfog.pie.core.TestUtil;
import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapeIOUtil;
import com.github.introfog.pie.core.util.ShapePair;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.AlgorithmicTest;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(AlgorithmicTest.class)
public abstract class AbstractBroadPhaseTest extends PIETest {
    private String outPath;
    private String cmpPath;
    private String sourcePath;
    private AbstractBroadPhase broadPhaseMethod;

    private final static String PATH_TO_SOURCE_FOLDER = "./src/test/resources/com/github/introfog/pie/core/collisions/broadphase/";
    private final static String PATH_TO_TARGET_FOLDER = "./target/test/com/github/introfog/pie/core/collisions/broadphase/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(PATH_TO_TARGET_FOLDER + "Line/");
        createDestinationFolder(PATH_TO_TARGET_FOLDER + "Square/");
    }

    @Before
    public void before() {
        broadPhaseMethod = getBroadPhaseMethod();
    }

    @Test
    public void simpleColumnsTest() throws IOException {
        runBroadPhaseResultTest("Line/5x500line_8487collision");
    }

    @Test
    public void mediumColumnsTest() throws IOException {
        runBroadPhaseResultTest("Line/5x500line_22443collision");
    }

    @Test
    public void simpleRowsTest() throws IOException {
        runBroadPhaseResultTest("Line/500x5line_8487collision");
    }

    @Test
    public void mediumRowsTest() throws IOException {
        runBroadPhaseResultTest("Line/500x5line_22443collision");
    }

    @Test
    public void hardRowsTest() throws IOException {
        runBroadPhaseResultTest("Line/3000x2line+diffSize_20491collision");
    }

    @Test
    public void simpleSquareTest() throws IOException {
        runBroadPhaseResultTest("Square/50x50square_9702collision");
    }

    @Test
    public void mediumSquareTest() throws IOException {
        runBroadPhaseResultTest("Square/50x50square_28518collision");
    }

    @Test
    public void mediumSquareWithDiffSizeTest() throws IOException {
        runBroadPhaseResultTest("Square/70x70square+diffSize_17320collision");
    }

    @Test
    public void simpleSquareScatteredWithDiffSizeTest() throws IOException {
        runBroadPhaseResultTest("Square/100x100square+scattered+diffSize_14344collision");
    }

    @Test
    public void simpleSquareScatteredTest() throws IOException {
        runBroadPhaseResultTest("Square/100x100square+scattered_14602collision");
    }

    @Test
    public void addShapeMethodTest() {
        IShape c1 = new Circle(10, 0, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 15, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        broadPhaseMethod.addShape(c1);
        broadPhaseMethod.addShape(c2);

        List<ShapePair> cmpShapePairs = new ArrayList<>(3);

        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.comparingShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollision());

        IShape c3 = new Circle(10, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        broadPhaseMethod.addShape(c3);
        cmpShapePairs.add(new ShapePair(c1, c3));
        cmpShapePairs.add(new ShapePair(c2, c3));
        TestUtil.comparingShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollision());
    }

    @Test
    public void setShapesMethodTest() {
        IShape c1 = new Circle(10, 0, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 15, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        List<IShape> shapes = new ArrayList<>(2);
        shapes.add(c1);
        shapes.add(c2);
        broadPhaseMethod.setShapes(shapes);
        // Verify that the list of shapes was copied in method setShapes
        shapes.clear();

        List<ShapePair> cmpShapePairs = new ArrayList<>(1);

        cmpShapePairs.add(new ShapePair(c1, c2));
        TestUtil.comparingShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollision());

        IShape c3 = new Circle(10, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        shapes.add(c1);
        shapes.add(c2);
        shapes.add(c3);
        broadPhaseMethod.setShapes(shapes);
        // Verify that the list of shapes was copied in method setShapes
        shapes.clear();

        cmpShapePairs.add(new ShapePair(c1, c3));
        cmpShapePairs.add(new ShapePair(c2, c3));
        TestUtil.comparingShapePairsList(cmpShapePairs, broadPhaseMethod.calculateAabbCollision());
    }

    protected abstract AbstractBroadPhase getBroadPhaseMethod();

    private void runBroadPhaseResultTest(String fileName) throws IOException {
        sourcePath = PATH_TO_SOURCE_FOLDER + fileName + ".pie";
        cmpPath = PATH_TO_SOURCE_FOLDER + fileName + "_answer.pie";
        outPath = PATH_TO_TARGET_FOLDER + fileName + ".pie";

        List<IShape> shapes = ShapeIOUtil.readShapesFromFile(sourcePath);
        broadPhaseMethod.setShapes(shapes);

        runMethodAndCompareResult(broadPhaseMethod);
    }

    private void runMethodAndCompareResult(AbstractBroadPhase method) throws IOException {
        List<ShapePair> outShapes = method.calculateAabbCollision();

        System.out.println("Run file comparing...");
        ShapeIOUtil.writeShapePairsToFile(outShapes, outPath);
        if (ShapeIOUtil.filesIdentical(cmpPath, outPath)) {
            System.out.println("Result: true");
            return;
        }
        System.out.println("Result: false");

        System.out.println("Run shape object comparing...");
        List<ShapePair> cmpShapes = ShapeIOUtil.readShapePairsFromFile(cmpPath);
        TestUtil.comparingShapePairsList(cmpShapes, outShapes);
        System.out.println("Result: true");
    }
}
