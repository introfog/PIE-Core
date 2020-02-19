package com.introfog.pie.core.collisions.broadphase;

import com.introfog.pie.core.Body;
import com.introfog.pie.core.math.MathPIE;
import com.introfog.pie.core.shape.AABB;
import com.introfog.pie.core.shape.IShape;
import com.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class SpatialHashingMethod extends AbstractBroadPhase {
    private int cellSize;
    private float averageMaxBodiesSize;
    private HashMap<Integer, LinkedList<IShape>> cells;
    private HashMap<Body, LinkedList<Integer>> objects;
    private LinkedHashSet<ShapePair> collisionPairSet;

    public SpatialHashingMethod() {
        averageMaxBodiesSize = 0f;
        cells = new HashMap<>();
        objects = new HashMap<>();
        collisionPairSet = new LinkedHashSet<>();
    }

    @Override
    public void setShapes(List<IShape> shapes) {
        super.setShapes(shapes);
        shapes.forEach((shape) ->
                averageMaxBodiesSize += Math.max(shape.aabb.max.x - shape.aabb.min.x, shape.aabb.max.y - shape.aabb.min.y));
        averageMaxBodiesSize /= shapes.size();
    }

    @Override
    public void processNewShape(IShape shape) {
        averageMaxBodiesSize *= (shapes.size() - 1);
        averageMaxBodiesSize += Math.max(shape.aabb.max.x - shape.aabb.min.x, shape.aabb.max.y - shape.aabb.min.y);
        averageMaxBodiesSize /= shapes.size();
    }

    @Override
    public List<ShapePair> findPossibleCollision() {
        // Сложность O(n) если минимальный и максимальный размер объектов не сильно отличаются, но если очень сильно,
        // то сложность близиться к O(n^2)
        List<ShapePair> possibleCollisionList = new ArrayList<>();

        setCellSize((int) averageMaxBodiesSize * 2);
        clear();

        shapes.forEach((shape) -> optimizedInsert(shape));

        computeCollisions().forEach((pair) -> {
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

    // Медленый из-за округления и умножения лишнего
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

    private void optimizedInsert(IShape shape) {
        // Работает быстрее чем insert
        // Делим AABB на ячейки, пришлось увиличить размер AABB на целую клетку, что бы не проверять дополнительно
        // лежит ли остаток AABB в новой ячейке.
        Body body = shape.body;
        shape.computeAABB();
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

    private LinkedHashSet<ShapePair> computeCollisions() {
        // Использую LinkedHashSet что бы избежать повторяющихся пар, это не очень быстро
        // TODO возможно есть более легкий способ избежать повтора пар кроме как использовать LinkedHashSet (какое-нить лексикографическое сравнение)
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
