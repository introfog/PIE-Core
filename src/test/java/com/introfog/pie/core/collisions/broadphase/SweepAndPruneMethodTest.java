package com.introfog.pie.core.collisions.broadphase;

public class SweepAndPruneMethodTest extends BroadPhaseResultTestsHandler {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new SweepAndPruneMethod();
    }
}
