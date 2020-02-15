package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.shape.Circle;
import com.introfog.pie.core.shape.IShape;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
public class BroadPhaseBenchmark {
    @State(Scope.Thread)
    public static class BroadPhaseState {
        public AbstractBroadPhase bruteForce;
        public AbstractBroadPhase spatialHashing;
        public AbstractBroadPhase sweepAndPrune;
        public AbstractBroadPhase sweepAndPruneMy;

        @Setup(Level.Trial)
        public void doSetup() {
            List<IShape> shapes = new ArrayList<>();
            shapes.add(new Circle(10f, 0, 0, 1f, 0.2f));
            shapes.add(new Circle(10f, 9, 0, 1f, 0.2f));
            shapes.add(new Circle(10f, 0, 9, 1f, 0.2f));
            shapes.add(new Circle(10f, 9, 9, 1f, 0.2f));

            bruteForce = new BruteForceMethod();
            spatialHashing = new SpatialHashingMethod();
            sweepAndPrune = new SweepAndPruneMethod();
            sweepAndPruneMy = new SweepAndPruneMyMethod();

            bruteForce.setShapes(shapes);
            spatialHashing.setShapes(shapes);
            sweepAndPrune.setShapes(shapes);
            sweepAndPruneMy.setShapes(shapes);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void bruteForceTest(BroadPhaseState state) {
        state.bruteForce.findPossibleCollision();
    }

    /*@Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void spatialHashingTest(BroadPhaseState state) {
        state.spatialHashing.findPossibleCollision();
    }*/

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void sweepAndPruneTest(BroadPhaseState state) {
        state.sweepAndPrune.findPossibleCollision();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void sweepAndPruneMyTest(BroadPhaseState state) {
        state.sweepAndPruneMy.findPossibleCollision();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BroadPhaseBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
