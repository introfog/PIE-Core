package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.util.Pair;

import java.util.List;

public abstract class AbstractBroadPhase {
    protected List<IShape> shapes;

    public void setShapes(List<IShape> shapes) {
        this.shapes = shapes;
    }

    public abstract List<Pair<IShape, IShape>> findPossibleCollision();
}
