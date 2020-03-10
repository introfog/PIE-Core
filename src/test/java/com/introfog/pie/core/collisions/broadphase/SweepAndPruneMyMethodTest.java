package com.introfog.pie.core.collisions.broadphase;

public class SweepAndPruneMyMethodTest extends BroadPhaseResultTestsHandler {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new SweepAndPruneMyMethod();
    }
}
