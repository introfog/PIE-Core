package com.introfog.pie.core.collisions.broadphase;

public class SweepAndPruneMyMethodTest extends BroadPhaseTestsHandler {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new SweepAndPruneMyMethod();
    }
}
