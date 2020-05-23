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

public class SweepAndPruneMethod extends AbstractBroadPhase {
    private int CURRENT_AXIS;
    private Vector2f p;
    private Vector2f s;
    private Vector2f s2;
    private List<IShape> xAxisProjection;
    private List<IShape> yAxisProjection;

    public SweepAndPruneMethod() {
        CURRENT_AXIS = 0;
        p = new Vector2f();
        s = new Vector2f();
        s2 = new Vector2f();
        xAxisProjection = new ArrayList<>();
        yAxisProjection = new ArrayList<>();
    }

    @Override
    public void setShapes(List<IShape> shapes) {
        super.setShapes(shapes);
        xAxisProjection = new ArrayList<>(shapes);
        yAxisProjection = new ArrayList<>(shapes);
    }

    @Override
    public void addShape(IShape shape) {
        super.addShape(shape);
        xAxisProjection.add(shape);
        yAxisProjection.add(shape);
    }

    @Override
    public List<ShapePair> insideCollisionCalculating() {
        // The best case is O(n*logn) or O(k*n), in the worst O(n^2)
        // Looking for possible intersections along the current axis, and then use brute force algorithm
        // Each time using dispersion we select the next axis
        List<ShapePair> possibleCollisionList = new ArrayList<>();

        if (CURRENT_AXIS == 0) {
            xAxisProjection.sort((a, b) -> Float.compare(a.aabb.min.x, b.aabb.min.x));
        } else {
            yAxisProjection.sort((a, b) -> Float.compare(a.aabb.min.y, b.aabb.min.y));
        }
        // TODO use insertion sorting (effective when the list is almost sorted)

        p.set(0f, 0f);
        s.set(0f, 0f);
        s2.set(0f, 0f);
        float numBodies = shapes.size();

        AABB currAABB;
        for (int i = 0; i < shapes.size(); i++) {
            if (CURRENT_AXIS == 0) {
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
                if (CURRENT_AXIS == 0 && xAxisProjection.get(j).aabb.min.x > currAABB.max.x) {
                    break;
                } else if (CURRENT_AXIS == 1 && yAxisProjection.get(j).aabb.min.y > currAABB.max.y) {
                    break;
                }


                if (CURRENT_AXIS == 0 && AABB.isIntersected(xAxisProjection.get(j).aabb, currAABB)) {
                    possibleCollisionList.add(new ShapePair(xAxisProjection.get(j), xAxisProjection.get(i)));
                } else if (CURRENT_AXIS == 1 && AABB.isIntersected(yAxisProjection.get(j).aabb, currAABB)) {
                    possibleCollisionList.add(new ShapePair(yAxisProjection.get(j), yAxisProjection.get(i)));
                }
            }
        }

        // With the help of dispersion, we select the next axis (we look for the axis along which the coordinates
        // of the objects are most different) to make fewer checks and reduce the algorithm complexity to O(k*n)
        s.mul(1.0f / numBodies);
        s.mul(s);
        s2.mul(1.0f / numBodies);
        Vector2f dispersion = s2;
        dispersion.sub(s);
        CURRENT_AXIS = 0;
        if (dispersion.y > dispersion.x) {
            CURRENT_AXIS = 1;
        }

        return possibleCollisionList;
    }
}
