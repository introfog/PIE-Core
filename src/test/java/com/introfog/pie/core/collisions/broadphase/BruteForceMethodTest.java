package com.introfog.pie.core.collisions.broadphase;

public class BruteForceMethodTest extends BroadPhaseResultTestsHandler {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new BruteForceMethod();
    }
}
