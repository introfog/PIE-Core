package com.introfog.pie.core.collisions.broadphase;

import java.io.IOException;

import org.junit.Test;

public class LineShapesBroadPhaseResultTest extends AbstractBroadPhaseResultTest {
    private final static String PATH_TO_SOURCE_FOLDER = ".\\src\\test\\resources\\com\\introfog\\pie\\core\\collisions\\broadphase\\Line\\";
    private final static String PATH_TO_TARGET_FOLDER = ".\\target\\test\\com\\introfog\\pie\\core\\collisions\\broadphase\\Line\\";

    @Test
    public void simpleColumns() throws IOException {
        super.runBenchmarkTest("5x500line_8487collision", PATH_TO_SOURCE_FOLDER, PATH_TO_TARGET_FOLDER);
    }

    @Test
    public void mediumColumns() throws IOException {
        super.runBenchmarkTest("5x500line_22443collision", PATH_TO_SOURCE_FOLDER, PATH_TO_TARGET_FOLDER);
    }

    @Test
    public void simpleRows() throws IOException {
        super.runBenchmarkTest("500x5line_8487collision", PATH_TO_SOURCE_FOLDER, PATH_TO_TARGET_FOLDER);
    }

    @Test
    public void mediumRows() throws IOException {
        super.runBenchmarkTest("500x5line_22443collision", PATH_TO_SOURCE_FOLDER, PATH_TO_TARGET_FOLDER);
    }

    @Test
    public void hardRows() throws IOException {
        super.runBenchmarkTest("3000x2line+diffSize_20491collision", PATH_TO_SOURCE_FOLDER, PATH_TO_TARGET_FOLDER);
    }
}
