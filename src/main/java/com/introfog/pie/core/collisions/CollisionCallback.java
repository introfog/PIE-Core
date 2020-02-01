package com.introfog.pie.core.collisions;

import com.introfog.pie.core.Manifold;

public interface CollisionCallback {
    void handleCollision(Manifold manifold);
}
