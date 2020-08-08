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

import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.AABB;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.List;

/**
 * The class is a sweep and prune method that sorts shapes along a certain axis with the highest variance at
 * the previous iteration of collision calculation. Further, if the projections of the shapes AABB on this
 * axis intersect, check if their AABB intersect, if so, put them in the list of possibly intersecting shapes.
 *
 * <p>
 * This method is effective in most cases.
 *
 * @see AbstractBroadPhase
 */
public class SweepAndPruneMethod extends AbstractBroadPhase {
    private int currentSweepAndPruneAxis;
    private final Vector2f p;
    private final Vector2f s;
    private final Vector2f s2;
    private List<IShape> xAxisProjection;
    private List<IShape> yAxisProjection;

    /**
     * Instantiates a new {@link SweepAndPruneMethod} instance.
     */
    public SweepAndPruneMethod() {
        currentSweepAndPruneAxis = 0;
        p = new Vector2f();
        s = new Vector2f();
        s2 = new Vector2f();
        xAxisProjection = this.shapes;
        yAxisProjection = new ArrayList<>();
    }

    @Override
    public void setShapes(List<IShape> shapes) {
        super.setShapes(shapes);
        xAxisProjection = this.shapes;
        yAxisProjection = new ArrayList<>(shapes);
    }

    @Override
    public void addShape(IShape shape) {
        super.addShape(shape);
        yAxisProjection.add(shape);
    }

    @Override
    public List<ShapePair> domesticCalculateAabbCollisions() {
        // The best case is O(n*logn) or O(k*n), in the worst O(n^2)
        // Looking for possible intersections along the current axis, and then use brute force algorithm
        // Each time using variance select the next axis
        List<ShapePair> possibleCollisionList = new ArrayList<>();

        // TODO use insertion sorting (effective when the list is almost sorted)
        if (currentSweepAndPruneAxis == 0) {
            xAxisProjection.sort((a, b) -> Float.compare(a.aabb.min.x, b.aabb.min.x));
        } else {
            yAxisProjection.sort((a, b) -> Float.compare(a.aabb.min.y, b.aabb.min.y));
        }

        p.set(0f, 0f);
        s.set(0f, 0f);
        s2.set(0f, 0f);

        AABB currAABB;
        for (int i = 0; i < shapes.size(); i++) {
            if (currentSweepAndPruneAxis == 0) {
                currAABB = xAxisProjection.get(i).aabb;
            } else {
                currAABB = yAxisProjection.get(i).aabb;
            }

            p.set(currAABB.min.x + currAABB.max.x, currAABB.min.y + currAABB.max.y);
            p.mul(1.0f / 2);
            s.add(p);
            p.mul(p);
            s2.add(p);

            for (int j = i + 1; j < shapes.size(); j++) {
                if (currentSweepAndPruneAxis == 0 && xAxisProjection.get(j).aabb.min.x > currAABB.max.x) {
                    break;
                } else if (currentSweepAndPruneAxis == 1 && yAxisProjection.get(j).aabb.min.y > currAABB.max.y) {
                    break;
                }


                if (currentSweepAndPruneAxis == 0 && AABB.isIntersected(xAxisProjection.get(j).aabb, currAABB)) {
                    possibleCollisionList.add(new ShapePair(xAxisProjection.get(j), xAxisProjection.get(i)));
                } else if (currentSweepAndPruneAxis == 1 && AABB.isIntersected(yAxisProjection.get(j).aabb, currAABB)) {
                    possibleCollisionList.add(new ShapePair(yAxisProjection.get(j), yAxisProjection.get(i)));
                }
            }
        }

        // With the help of variance, select the next axis (look for the axis along which the coordinates
        // of the objects are most different) to make fewer checks and reduce the algorithm complexity to O(k*n)
        s.mul(1.0f / shapes.size());
        s.mul(s);
        s2.mul(1.0f / shapes.size());
        Vector2f variance = s2;
        variance.sub(s);
        currentSweepAndPruneAxis = 0;
        if (variance.y > variance.x) {
            currentSweepAndPruneAxis = 1;
        }

        return possibleCollisionList;
    }
}
