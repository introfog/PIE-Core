package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.shape.AABB;
import com.introfog.pie.core.Body;
import com.introfog.pie.core.math.MathPIE;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import javafx.util.Pair;

public class SpatialHash {
    private int cellSize;
    private HashMap<Integer, LinkedList<Body>> cells;
    private HashMap<Body, LinkedList<Integer>> objects;
    private LinkedHashSet<Pair<Body, Body>> collisionPairSet;

    private int GenerateKey(float x, float y) {
        return ((MathPIE.fastFloor(x / cellSize) * 73856093) ^ (MathPIE.fastFloor(y / cellSize) * 19349663));
    }

    public SpatialHash() {
        cells = new HashMap<>();
        objects = new HashMap<>();
        collisionPairSet = new LinkedHashSet<>();
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    // Медленый из-за округления и умножения лишнего
    public void insert(Body body) {
        body.shape.computeAABB();
        AABB aabb = body.shape.aabb;
        int key;
        int cellX = MathPIE.fastFloor(aabb.max.x / cellSize) - MathPIE.fastFloor(aabb.min.x / cellSize);
        int cellY = MathPIE.fastFloor(aabb.max.y / cellSize) - MathPIE.fastFloor(aabb.min.y / cellSize);
        for (int i = 0; i <= cellX; i++) {
            for (int j = 0; j <= cellY; j++) {
                key = GenerateKey(aabb.min.x + i * cellSize, aabb.min.y + j * cellSize);

                if (cells.containsKey(key)) {
                    cells.get(key).add(body);
                } else {
                    cells.put(key, new LinkedList<>());
                    cells.get(key).add(body);
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

    public void optimizedInsert(Body body) {
        // Работает быстрее чем insert
        // Делим AABB на ячейки, пришлось увиличить размер AABB на целую клетку, что бы не проверять дополнительно
        // лежит ли остаток AABB в новой ячейке.
        body.shape.computeAABB();
        AABB aabb = body.shape.aabb;
        float currX = aabb.min.x;
        float currY = aabb.min.y;
        int key;
        while (currX <= aabb.max.x + cellSize) {
            while (currY <= aabb.max.y + cellSize) {
                key = GenerateKey(currX, currY);

                if (cells.containsKey(key)) {
                    cells.get(key).add(body);
                } else {
                    cells.put(key, new LinkedList<>());
                    cells.get(key).add(body);
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

    public void clear() {
        cells.clear();
        objects.clear();
    }

    public LinkedHashSet<Pair<Body, Body>> computeCollisions() {
        // Использую LinkedHashSet что бы избежать повторяющихся пар, это не очень быстро
        // TODO возможно есть более легкий способ избежать повтора пар кроме как использовать LinkedHashSet (какое-нить лексикографическое сравнение)
        collisionPairSet.clear();
        cells.forEach((cell, list) -> {
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    collisionPairSet.add(new Pair<>(list.get(i), list.get(j)));
                }
            }
        });
        return collisionPairSet;
    }
}