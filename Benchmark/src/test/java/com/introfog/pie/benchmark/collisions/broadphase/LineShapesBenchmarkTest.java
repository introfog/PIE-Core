package com.introfog.pie.benchmark.collisions.broadphase;

import java.io.IOException;

import org.junit.Test;

public class LineShapesBenchmarkTest extends AbstractBroadPhaseBenchmark {
    private final static String PATH_TO_SOURCE_FOLDER = ".\\src\\test\\resources\\com\\introfog\\pie\\benchmark\\collisions\\broadphase\\Line\\";

    @Test
    public void simpleColumns() throws IOException {
        super.runBenchmarkTest("5x500line_8487collision.pie", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void mediumColumns() throws IOException {
        super.runBenchmarkTest("5x500line_22443collision.pie", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void simpleRows() throws IOException {
        super.runBenchmarkTest("500x5line_8487collision.pie", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void mediumRows() throws IOException {
        super.runBenchmarkTest("500x5line_22443collision.pie", PATH_TO_SOURCE_FOLDER);
    }

    @Test
    public void hardRows() throws IOException {
        super.runBenchmarkTest("3000x2line+diffSize_20491collision.pie", PATH_TO_SOURCE_FOLDER);
    }
}
