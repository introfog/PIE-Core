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
    private BruteForceMethod bruteForceMethod;

    public SweepAndPruneMyMethod() {
        xAxisProjection = this.shapes;
        activeList = new ArrayList<>();
        bruteForceMethod = new BruteForceMethod();
    }

    @Override
    public void setShapes(List<IShape> shapes) {
        super.setShapes(shapes);
        xAxisProjection = this.shapes;
    }

    @Override
    public List<ShapePair> domesticAabbCollisionCalculating() {
        // The best case is O(n*logn) or O(k*n), in the worst O(n^2)
        // Looking for possible intersections along the X axis, and then use brute force algorithm
        List<ShapePair> possibleCollisionList = new ArrayList<>();

        // TODO use insertion sorting (effective when the list is almost sorted)
        xAxisProjection.sort((a, b) -> Float.compare(a.aabb.min.x, b.aabb.min.x));

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
            bruteForceMethod.setShapes(activeList);
            possibleCollisionList.addAll(bruteForceMethod.domesticAabbCollisionCalculating());
        }

        return possibleCollisionList;
    }
}
