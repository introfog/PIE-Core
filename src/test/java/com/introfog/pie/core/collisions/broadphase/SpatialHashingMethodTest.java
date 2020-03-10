package com.introfog.pie.core.collisions.broadphase;

public class SpatialHashingMethodTest extends BroadPhaseResultTestsHandler {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new SpatialHashingMethod();
    }
}
