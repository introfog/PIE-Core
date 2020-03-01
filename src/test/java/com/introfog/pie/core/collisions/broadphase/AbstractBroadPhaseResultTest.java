package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.util.ShapeIOUtil;
import com.introfog.pie.core.util.ShapePair;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

public abstract class AbstractBroadPhaseResultTest {
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
                System.out.println("Broad phase " + broadPhaseMethods.get(i).getClass().getSimpleName() + " returned incorrect result");
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
        List<ShapePair> result = method.calculateAabbCollision();
        System.out.println("Count collisions " + result.size() + " for method " + method.getClass().getSimpleName());
        ShapeIOUtil.writeShapePairsToFile(result, outPath);

        return ShapeIOUtil.filesIdentical(cmpPath, outPath);
    }
}
