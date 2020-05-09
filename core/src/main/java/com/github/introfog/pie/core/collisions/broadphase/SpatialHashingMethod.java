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

import com.github.introfog.pie.core.Body;
import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.shape.AABB;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpatialHashingMethod extends AbstractBroadPhase {
    private int cellSize;
    private float averageMaxBodiesSize;
    private Map<Integer, LinkedList<IShape>> cells;
    private Map<Body, LinkedList<Integer>> objects;
    private Set<ShapePair> collisionPairSet;

    public SpatialHashingMethod() {
        averageMaxBodiesSize = 0f;
        cells = new HashMap<>();
        objects = new HashMap<>();
        collisionPairSet = new HashSet<>();
    }

    @Override
    public List<ShapePair> insideCollisionCalculating() {
        // The complexity is O(n), if the minimum and maximum size of the objects are not very different,
        // but if very different, then the complexity tends to O(n^2)
        List<ShapePair> possibleCollisionList = new ArrayList<>();

        averageMaxBodiesSize = 0;
        for (IShape shape : shapes) {
            averageMaxBodiesSize += Math.max(shape.aabb.max.x - shape.aabb.min.x, shape.aabb.max.y - shape.aabb.min.y);
        }
        averageMaxBodiesSize /= shapes.size();

        setCellSize((int) averageMaxBodiesSize * 2);
        if (cellSize == 0) {
            // TODO create PIE custom exception
            throw new RuntimeException();
        }
        clear();

        shapes.forEach(this::optimizedInsert);

        Set<ShapePair> possibleIntersect = computeCollisions();
        possibleIntersect.forEach((pair) -> {
            if (AABB.isIntersected(pair.first.aabb, pair.second.aabb)) {
                possibleCollisionList.add(pair);
            }
        });

        return possibleCollisionList;
    }



    private int GenerateKey(float x, float y) {
        return ((MathPIE.fastFloor(x / cellSize) * 73856093) ^ (MathPIE.fastFloor(y / cellSize) * 19349663));
    }


    private void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    // Slow due to rounding and multiplication
    private void insert(IShape shape) {
        Body body = shape.body;
        shape.computeAABB();
        AABB aabb = shape.aabb;
        int key;
        int cellX = MathPIE.fastFloor(aabb.max.x / cellSize) - MathPIE.fastFloor(aabb.min.x / cellSize);
        int cellY = MathPIE.fastFloor(aabb.max.y / cellSize) - MathPIE.fastFloor(aabb.min.y / cellSize);
        for (int i = 0; i <= cellX; i++) {
            for (int j = 0; j <= cellY; j++) {
                key = GenerateKey(aabb.min.x + i * cellSize, aabb.min.y + j * cellSize);

                if (cells.containsKey(key)) {
                    cells.get(key).add(shape);
                } else {
                    cells.put(key, new LinkedList<>());
                    cells.get(key).add(shape);
                }

                if (objects.containsKey(body)) {
                    objects.get(body).add(key);
                } else {
                    objects.put(body, new LinkedList<>());
                    objects.get(body).add(key);
                }
            }
        }
    }

    // Faster than insert method
    private void optimizedInsert(IShape shape) {
        // Divide the AABB into cells, so I had to enlarge the size of the AABB by a whole cell,
        // so as not to check whether the rest of the AABB lies in the new cell
        Body body = shape.body;
        AABB aabb = shape.aabb;
        float currX = aabb.min.x;
        float currY = aabb.min.y;
        int key;
        while (currX <= aabb.max.x + cellSize) {
            while (currY <= aabb.max.y + cellSize) {
                key = GenerateKey(currX, currY);

                if (cells.containsKey(key)) {
                    cells.get(key).add(shape);
                } else {
                    cells.put(key, new LinkedList<>());
                    cells.get(key).add(shape);
                }

                if (objects.containsKey(body)) {
                    objects.get(body).add(key);
                } else {
                    objects.put(body, new LinkedList<>());
                    objects.get(body).add(key);
                }

                currY += cellSize;
            }
            currY = aabb.min.y;
            currX += cellSize;
        }
    }

    private void clear() {
        cells.clear();
        objects.clear();
    }

    private Set<ShapePair> computeCollisions() {
        // LinkedHashSet is used to avoid repeating pairs, it is not very fast
        // TODO maybe there’s an easier way to avoid repeating pairs other than using LinkedHashSet (for example some lexicographic comparison)
        collisionPairSet.clear();
        cells.forEach((cell, list) -> {
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    collisionPairSet.add(new ShapePair(list.get(i), list.get(j)));
                }
            }
        });
        return collisionPairSet;
    }
}
