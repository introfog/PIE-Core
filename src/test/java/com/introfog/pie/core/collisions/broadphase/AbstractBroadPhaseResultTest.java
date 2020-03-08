package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.util.ShapeIOUtil;
import com.introfog.pie.core.util.ShapePair;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBroadPhaseResultTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractBroadPhaseResultTest.class);

    private List<AbstractBroadPhase> broadPhaseMethods;
    private String outPath;
    private String cmpPath;
    private String sourcePath;

    public void runBenchmarkTest(String fileName, String sourceFolder, String destinationFolder) throws IOException {
        sourcePath = sourceFolder + fileName + ".pie";
        cmpPath = sourceFolder + fileName + "_answer.pie";
        outPath = destinationFolder + fileName + ".pie";

        initializeTestMethods();

        List<Boolean> results = new ArrayList<>(broadPhaseMethods.size());
        for (AbstractBroadPhase broadPhaseMethod : broadPhaseMethods) {
            results.add(runMethodAndCompareResult(broadPhaseMethod));
        }

        System.out.println();
        boolean allTestsPassed = true;
        for (int i = 0; i < results.size(); i++) {
            boolean result = results.get(i);
            if (!result) {
                allTestsPassed = false;
                logger.error("Broad phase " + broadPhaseMethods.get(i).getClass().getSimpleName() + " returned incorrect result");
            }
        }

        Assert.assertTrue("Broad phase methods return incorrect result for " + fileName, allTestsPassed);
    }

    private void initializeTestMethods() throws IOException {
        List<IShape> shapes = ShapeIOUtil.readShapesFromFile(sourcePath);

        broadPhaseMethods = new ArrayList<>();
        broadPhaseMethods.add(new BruteForceMethod());
        broadPhaseMethods.add(new SpatialHashingMethod());
        broadPhaseMethods.add(new SweepAndPruneMethod());
        broadPhaseMethods.add(new SweepAndPruneMyMethod());

        broadPhaseMethods.forEach(method -> method.setShapes(shapes));
    }

    private boolean runMethodAndCompareResult(AbstractBroadPhase method) throws IOException {
        List<ShapePair> outShapes = method.calculateAabbCollision();
        logger.info("Count collisions " + outShapes.size() + " for method " + method.getClass().getSimpleName());
        ShapeIOUtil.writeShapePairsToFile(outShapes, outPath);

        if (ShapeIOUtil.filesIdentical(cmpPath, outPath)) {
            return true;
        }

        List<ShapePair> cmpShapes = ShapeIOUtil.readShapePairsFromFile(cmpPath);
        return cmpShapes.containsAll(outShapes);
    }
}
