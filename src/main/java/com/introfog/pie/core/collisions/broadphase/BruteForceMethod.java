package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.shape.AABB;
import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class BruteForceMethod extends AbstractBroadPhase {
    public BruteForceMethod(List<IShape> shapes) {
        super(shapes);
    }

    @Override
    public List<Pair<IShape, IShape>> findPossibleCollision() {
        IShape a;
        IShape b;
        List<Pair<IShape, IShape>> possibleCollisionList = new ArrayList<>();

        for (int i = 0; i < shapes.size(); i++) {
            for (int j = i + 1; j < shapes.size(); j++) {
                a = shapes.get(i);
                b = shapes.get(j);

                a.computeAABB();
                b.computeAABB();

                if (AABB.isIntersected(a.aabb, b.aabb)) {
                    possibleCollisionList.add(new Pair<>(a, b));
                }
            }
        }

        return possibleCollisionList;
    }
}
