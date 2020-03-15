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
package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.math.Vector2f;
import com.introfog.pie.core.shape.AABB;
import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.util.ShapePair;

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
        // Лучший случай O(n*logn) или O(k*n), в худщем O(n^2), ищем возможные
        // пересечения по текущей оси, а потом bruteForce. Каждый раз через десперсию выбираем следующую ось
        List<ShapePair> possibleCollisionList = new ArrayList<>();

        if (CURRENT_AXIS == 0) {
            xAxisProjection.sort((a, b) -> (int) (a.aabb.min.x - b.aabb.min.x));
        } else {
            yAxisProjection.sort((a, b) -> (int) (a.aabb.min.y - b.aabb.min.y));
        }
        // TODO использовать сортировку вставкой (эффективна когда почти отсортирован список)

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

            p.x = (currAABB.min.x / 2f + currAABB.max.x / 2f) / numBodies;
            p.y = (currAABB.min.y / 2f + currAABB.max.y / 2f) / numBodies;

            s.add(p);
            p.x *= p.x * numBodies;
            p.y *= p.y * numBodies;
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

        // С помощью дисперсии выбираем следуюущую ось (ищем ось, по которой координаты объектов больше всего различаются)
        // что бы меньше проверок делать и сводить алогритм к  O(k*n)
        s.x *= s.x;
        s.y *= s.y;
        s2.sub(s);
        CURRENT_AXIS = 0;
        if (s.y > s.x) {
            CURRENT_AXIS = 1;
        }

        return possibleCollisionList;
    }
}
