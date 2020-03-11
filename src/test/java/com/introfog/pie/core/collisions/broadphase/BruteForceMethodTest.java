package com.introfog.pie.core.collisions.broadphase;

public class BruteForceMethodTest extends BroadPhaseTestsHandler {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new BruteForceMethod();
    }
}
