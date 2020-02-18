package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.shape.AABB;
import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.List;

public class SweepAndPruneMyMethod extends AbstractBroadPhase {
    private List<IShape> xAxisProjection;
    private List<IShape> activeList;

    public SweepAndPruneMyMethod() {
        xAxisProjection = new ArrayList<>();
        activeList = new ArrayList<>();
    }

    @Override
    public void setShapes(List<IShape> shapes) {
        super.setShapes(shapes);
        xAxisProjection = new ArrayList<>(shapes);
        activeList = new ArrayList<>();
    }

    @Override
    public void addShape(IShape shape) {
        xAxisProjection.add(shape);
    }

    @Override
    public List<ShapePair> findPossibleCollision() {
        // Лучший случай O(n*logn) или O(k*n), в худщем O(n^2), ищем
        // возможные пересечения по оси Х, а потом bruteForce
        List<ShapePair> possibleCollisionList = new ArrayList<>();

        shapes.forEach(IShape::computeAABB);
        xAxisProjection.sort((a, b) -> (int) (a.aabb.min.x - b.aabb.min.x));
        // TODO использовать сортировку вставкой (эффективна когда почти отсортирован список)

        activeList.add(xAxisProjection.get(0));
        float currEnd = xAxisProjection.get(0).aabb.max.x;

        for (int i = 1; i < xAxisProjection.size(); i++) {
            if (xAxisProjection.get(i).aabb.min.x <= currEnd) {
                activeList.add(xAxisProjection.get(i));
            } else {
                IShape first = activeList.remove(0);
                activeList.forEach((shape) -> {
                    if (AABB.isIntersected(first.aabb, shape.aabb)) {
                        possibleCollisionList.add(new ShapePair(first, shape));
                    }
                });
                if (!activeList.isEmpty()) {
                    i--;
                } else {
                    activeList.add(xAxisProjection.get(i));
                }
                currEnd = activeList.get(0).aabb.max.x;
            }
        }
        if (!activeList.isEmpty()) {
            int size = activeList.size();
            for (int i = 0; i < size; i++) {
                IShape first = activeList.remove(0);
                activeList.forEach((shape) -> {
                    if (AABB.isIntersected(first.aabb, shape.aabb)) {
                        possibleCollisionList.add(new ShapePair(first, shape));
                    }
                });
            }
        }

        activeList.clear();
        return possibleCollisionList;
    }
}
