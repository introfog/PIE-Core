/*
    Copyright 2020 Dmitry Chubrick

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package com.github.introfog.pie.core.collisions.broadphase;

import com.github.introfog.pie.core.shape.AABB;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

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
    }

    @Override
    public void addShape(IShape shape) {
        super.addShape(shape);
        xAxisProjection.add(shape);
    }

    @Override
    public List<ShapePair> insideCollisionCalculating() {
        // The best case is O(n*logn) or O(k*n), in the worst O(n^2)
        // Looking for possible intersections along the X axis, and then use brute force algorithm
        List<ShapePair> possibleCollisionList = new ArrayList<>();

        xAxisProjection.sort((a, b) -> (int) (a.aabb.min.x - b.aabb.min.x));
        // TODO use insertion sorting (effective when the list is almost sorted)

        activeList.clear();
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

        return possibleCollisionList;
    }
}
