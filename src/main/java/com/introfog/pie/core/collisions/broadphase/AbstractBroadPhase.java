package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.util.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBroadPhase {
    protected List<IShape> shapes;

    public AbstractBroadPhase(List<IShape> shapes) {
        this.shapes = new ArrayList<>(shapes);
    }

    public void addShape(IShape shape) {
        shapes.add(shape);
    }

    public abstract List<Pair<IShape, IShape>> findPossibleCollision();
}
