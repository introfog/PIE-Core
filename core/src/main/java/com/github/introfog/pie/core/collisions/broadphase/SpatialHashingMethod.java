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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpatialHashingMethod extends AbstractBroadPhase {
    private int cellSize;
    private float averageMaxBodiesSize;
    private Map<Integer, List<IShape>> cells;
    private Map<Body, List<Integer>> objects;

    public SpatialHashingMethod() {
        cellSize = 0;
        averageMaxBodiesSize = 0f;
        cells = new HashMap<>();
        objects = new HashMap<>();
    }

    @Override
    public List<ShapePair> insideCollisionCalculating() {
        // The complexity is O(n), if the minimum and maximum size of the objects are not very different,
        // but if very different, then the complexity tends to O(n^2)
        List<ShapePair> possibleCollisionList = new ArrayList<>();

        if (shapes.isEmpty()) {
            return possibleCollisionList;
        }

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

        shapes.forEach(this::insert);

        Set<ShapePair> possibleIntersect = computePossibleIntersections();
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

    private void insert(IShape shape) {
        Body body = shape.body;
        AABB aabb = shape.aabb;
        int key;
        int cellX = MathPIE.fastFloor(aabb.max.x / cellSize) - MathPIE.fastFloor(aabb.min.x / cellSize);
        int cellY = MathPIE.fastFloor(aabb.max.y / cellSize) - MathPIE.fastFloor(aabb.min.y / cellSize);
        // Increment the values ​​of cellX and cellY so that the ends of the shape entering the other cells are also processed
        cellX++;
        cellY++;
        for (int i = 0; i < cellX; i++) {
            for (int j = 0; j < cellY; j++) {
                key = GenerateKey(aabb.min.x + i * cellSize, aabb.min.y + j * cellSize);

                if (!cells.containsKey(key)) {
                    cells.put(key, new ArrayList<>());
                }
                cells.get(key).add(shape);

                if (!objects.containsKey(body)) {
                    objects.put(body, new ArrayList<>());
                }
                objects.get(body).add(key);
            }
        }
    }

    private void clear() {
        cells.clear();
        objects.clear();
    }

    private Set<ShapePair> computePossibleIntersections() {
        // HashSet is used because requires the uniqueness of pairs,
        // for example, two shapes can intersect in several cells at once
        Set<ShapePair> possibleIntersect = new HashSet<>();
        cells.forEach((cell, list) -> {
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    possibleIntersect.add(new ShapePair(list.get(i), list.get(j)));
                }
            }
        });
        return possibleIntersect;
    }
}
