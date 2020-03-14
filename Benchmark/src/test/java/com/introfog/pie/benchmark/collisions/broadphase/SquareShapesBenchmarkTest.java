package com.introfog.pie.benchmark.collisions.broadphase;

import java.io.IOException;

import org.junit.Test;

public class SquareShapesBenchmarkTest extends AbstractBroadPhaseBenchmark {
    private final static String PATH_TO_SOURCE_FOLDER = ".\\src\\test\\resources\\com\\introfog\\pie\\benchmark\\collisions\\broadphase\\Square\\";

    @Test
    public void simpleSquare() throws IOException {
        super.runBenchmarkTest("50x50square_9702collision.pie", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void mediumSquare() throws IOException {
        super.runBenchmarkTest("50x50square_28518collision.pie", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void mediumSquareWithDiffSize() throws IOException {
        super.runBenchmarkTest("70x70square+diffSize_17320collision.pie", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void simpleSquareScatteredWithDiffSize() throws IOException {
        super.runBenchmarkTest("100x100square+scattered+diffSize_14344collision.pie", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void simpleSquareScattered() throws IOException {
        super.runBenchmarkTest("100x100square+scattered_14602collision.pie", PATH_TO_SOURCE_FOLDER);
    }
}
