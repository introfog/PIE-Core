package com.introfog.pie.core.collisions.broadphase;

public class SweepAndPruneMethodTest extends BroadPhaseTestsHandler {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new SweepAndPruneMethod();
    }
}
