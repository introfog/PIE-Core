package com.introfog.pie.core.collisions.broadphase;

import java.io.IOException;

import org.junit.Test;

public class SquareShapesBroadPhaseResultTest extends AbstractBroadPhaseResultTest {
    private final static String PATH_TO_SOURCE_FOLDER = ".\\src\\test\\resources\\com\\introfog\\pie\\core\\collisions\\broadphase\\Square\\";
    private final static String PATH_TO_TARGET_FOLDER = ".\\target\\test\\com\\introfog\\pie\\core\\collisions\\broadphase\\Square\\";

    @Test
    public void simpleSquare() throws IOException {
        super.runBenchmarkTest("50x50square_9702collision", PATH_TO_SOURCE_FOLDER, PATH_TO_TARGET_FOLDER);
    }

    @Test
    public void mediumSquare() throws IOException {
        super.runBenchmarkTest("50x50square_28518collision", PATH_TO_SOURCE_FOLDER, PATH_TO_TARGET_FOLDER);
    }

    @Test
    public void mediumSquareWithDiffSize() throws IOException {
        super.runBenchmarkTest("70x70square+diffSize_17320collision", PATH_TO_SOURCE_FOLDER, PATH_TO_TARGET_FOLDER);
    }

    @Test
    public void simpleSquareScatteredWithDiffSize() throws IOException {
        super.runBenchmarkTest("100x100square+scattered+diffSize_14344collision", PATH_TO_SOURCE_FOLDER, PATH_TO_TARGET_FOLDER);
    }

    @Test
    public void simpleSquareScattered() throws IOException {
        super.runBenchmarkTest("100x100square+scattered_14602collision", PATH_TO_SOURCE_FOLDER, PATH_TO_TARGET_FOLDER);
    }
}
