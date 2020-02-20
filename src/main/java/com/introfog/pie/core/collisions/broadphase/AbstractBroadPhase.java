package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBroadPhase {
    protected List<IShape> shapes;

    public AbstractBroadPhase() {
        shapes = new ArrayList<>();
    }

    public void setShapes(List<IShape> shapes) {
        this.shapes = shapes;
    }

    public void processNewShape(IShape shape){
        shapes.add(shape);
    }

    public abstract List<ShapePair> findPossibleCollision();
}
