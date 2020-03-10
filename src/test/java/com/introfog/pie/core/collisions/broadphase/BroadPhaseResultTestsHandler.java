package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.util.ShapeIOUtil;
import com.introfog.pie.core.util.ShapePair;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public abstract class BroadPhaseResultTestsHandler {
    private String outPath;
    private String cmpPath;
    private String sourcePath;
    private AbstractBroadPhase broadPhaseMethod;

    private final static String PATH_TO_SOURCE_FOLDER = ".\\src\\test\\resources\\com\\introfog\\pie\\core\\collisions\\broadphase\\";
    private final static String PATH_TO_TARGET_FOLDER = ".\\target\\test\\com\\introfog\\pie\\core\\collisions\\broadphase\\";

    @Test
    public void simpleColumns() throws IOException {
        runBroadPhaseResultTest("Line\\5x500line_8487collision");
    }

    @Test
    public void mediumColumns() throws IOException {
        runBroadPhaseResultTest("Line\\5x500line_22443collision");
    }

    @Test
    public void simpleRows() throws IOException {
        runBroadPhaseResultTest("Line\\500x5line_8487collision");
    }

    @Test
    public void mediumRows() throws IOException {
        runBroadPhaseResultTest("Line\\500x5line_22443collision");
    }

    @Test
    public void hardRows() throws IOException {
        runBroadPhaseResultTest("Line\\3000x2line+diffSize_20491collision");
    }

    @Test
    public void simpleSquare() throws IOException {
        runBroadPhaseResultTest("Square\\50x50square_9702collision");
    }

    @Test
    public void mediumSquare() throws IOException {
        runBroadPhaseResultTest("Square\\50x50square_28518collision");
    }

    @Test
    public void mediumSquareWithDiffSize() throws IOException {
        runBroadPhaseResultTest("Square\\70x70square+diffSize_17320collision");
    }

    @Test
    public void simpleSquareScatteredWithDiffSize() throws IOException {
        runBroadPhaseResultTest("Square\\100x100square+scattered+diffSize_14344collision");
    }

    @Test
    public void simpleSquareScattered() throws IOException {
        runBroadPhaseResultTest("Square\\100x100square+scattered_14602collision");
    }


    protected abstract AbstractBroadPhase getBroadPhaseMethod();

    private void runBroadPhaseResultTest(String fileName) throws IOException {
        sourcePath = PATH_TO_SOURCE_FOLDER + fileName + ".pie";
        cmpPath = PATH_TO_SOURCE_FOLDER + fileName + "_answer.pie";
        outPath = PATH_TO_TARGET_FOLDER + fileName + ".pie";

        initializeTestMethod();

        runMethodAndCompareResult(broadPhaseMethod);
    }

    private void initializeTestMethod() throws IOException {
        List<IShape> shapes = ShapeIOUtil.readShapesFromFile(sourcePath);

        broadPhaseMethod = getBroadPhaseMethod();
        broadPhaseMethod.setShapes(shapes);
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

        comparingByObjects(outShapes);
    }

    private void comparingByObjects(List<ShapePair> outShapes) throws IOException {
        System.out.println("Run shape object comparing...");

        List<ShapePair> cmpShapes = ShapeIOUtil.readShapePairsFromFile(cmpPath);
        if (cmpShapes.size() != outShapes.size()) {
            Assert.assertEquals("Different number of shape collisions", cmpShapes.size(), outShapes.size());
        }

        Map<Integer, List<ShapePair>> cmpMap = new HashMap<>(cmpShapes.size());
        for (ShapePair pair : cmpShapes) {
            int hashCode = pair.hashCode();
            cmpMap.putIfAbsent(hashCode, new ArrayList<>());
            cmpMap.get(hashCode).add(pair);
        }

        Map<Integer, List<ShapePair>> outMap = new HashMap<>(cmpShapes.size());
        for (ShapePair pair : outShapes) {
            int hashCode = pair.hashCode();
            outMap.putIfAbsent(hashCode, new ArrayList<>());
            outMap.get(hashCode).add(pair);
        }

        if (cmpMap.size() != outMap.size()) {
            Assert.assertEquals("Different size of maps", cmpMap.size(), outMap.size());
        }

        for (Integer hash : cmpMap.keySet()) {
            List<ShapePair> cmpList = cmpMap.get(hash);
            List<ShapePair> outList = outMap.get(hash);

            Assert.assertNotNull("Out map does not contain hash " + hash + " from cmp map", outList);
            Assert.assertTrue("Values cmp and out map for hash " + hash + " are different", cmpList.containsAll(outList));
        }

        System.out.println("Result: true");
    }
}
