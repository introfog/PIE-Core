package com.introfog.pie.core.collisions.broadphase;

public class SpatialHashingMethodTest extends BroadPhaseTestsHandler {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new SpatialHashingMethod();
    }
}
