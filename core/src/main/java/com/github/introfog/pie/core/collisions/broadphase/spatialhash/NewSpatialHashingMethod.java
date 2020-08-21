package com.github.introfog.pie.core.collisions.broadphase.spatialhash;

import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.github.introfog.pie.core.collisions.broadphase.BruteForceMethod;
import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.AABB;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewSpatialHashingMethod extends AbstractBroadPhase {
    private int cellSize;
    private final Map<Integer, List<IShape>> cells;
    private final Map<IShape, List<Cell>> objects;

    public NewSpatialHashingMethod() {
        cellSize = 0;
        cells = new HashMap<>();
        objects = new HashMap<>();
    }

    @Override
    public void addShape(IShape shape) {
        super.addShape(shape);
        insert(shape);
    }

    @Override
    public void setShapes(List<IShape> shapes) {
        super.setShapes(shapes);
        cellSize = calculateCellSize();
        cells.clear();
        objects.clear();
        this.shapes.forEach(this::insert);
    }

    @Override
    public List<ShapePair> domesticCalculateAabbCollisions() {
        List<ShapePair> possibleCollisionList = new ArrayList<>();

        int newCellSize = calculateCellSize();
        if (cellSize != newCellSize) {
            cellSize = newCellSize;
            cells.clear();
            objects.clear();
            shapes.forEach(this::insert);
        } else {
            updateCells();
        }

        Set<ShapePair> possibleIntersect = computePossibleAabbIntersections();
        possibleIntersect.forEach((pair) -> {
            if (AABB.isIntersected(pair.first.aabb, pair.second.aabb)) {
                possibleCollisionList.add(pair);
            }
        });

        return possibleCollisionList;
    }

    private void updateCells() {
        for (IShape shape : shapes) {
            List<Cell> objectCells = objects.get(shape);
            AABB cellAabb = new AABB();
            cellAabb.min = new Vector2f(Integer.MAX_VALUE, Integer.MAX_VALUE);
            cellAabb.max = new Vector2f(Integer.MIN_VALUE, Integer.MIN_VALUE);
            for (Cell cell : objectCells) {
                if (cell.x < cellAabb.min.x) {
                    cellAabb.min.x = cell.x;
                }
                if (cell.y < cellAabb.min.y) {
                    cellAabb.min.y = cell.y;
                }
                if (cell.x > cellAabb.max.x) {
                    cellAabb.max.x = cell.x;
                }
                if (cell.y > cellAabb.max.y) {
                    cellAabb.max.y = cell.y;
                }
            }
            cellAabb.min.mul(cellSize);
            cellAabb.max.mul(cellSize);
            if (!AABB.isContained(cellAabb, shape.aabb)) {
                reinsertShape(shape);
            }
        }
    }

    private void reinsertShape(IShape shape) {
        List<Cell> objectCells = objects.get(shape);
        for (Cell cell : objectCells) {
            List<IShape> cellShapes = cells.get(cell.hashCode());
            cellShapes.remove(shape);
            if (cellShapes.isEmpty()) {
                cells.remove(cell.hashCode());
            }
        }
        objects.remove(shape);
        insert(shape);
    }

    private int calculateCellSize() {
        float averageMaxBodiesSize = 0;
        for (IShape shape : shapes) {
            averageMaxBodiesSize += Math.max(shape.aabb.max.x - shape.aabb.min.x, shape.aabb.max.y - shape.aabb.min.y);
        }
        averageMaxBodiesSize /= shapes.size();

        return  (averageMaxBodiesSize == 0) ? 1 : ((int) averageMaxBodiesSize * 2);
    }


    private void insert(IShape shape) {
        AABB aabb = shape.aabb;
        int cellX = MathPIE.fastFloor(aabb.max.x / cellSize) - MathPIE.fastFloor(aabb.min.x / cellSize);
        int cellY = MathPIE.fastFloor(aabb.max.y / cellSize) - MathPIE.fastFloor(aabb.min.y / cellSize);
        // Increment the values ​​of cellX and cellY so that the ends of the shape entering the other cells are also processed
        cellX++;
        cellY++;
        for (int i = 0; i < cellX; i++) {
            for (int j = 0; j < cellY; j++) {
                Cell cell = new Cell(aabb.min.x + i * cellSize, aabb.min.y + j * cellSize, cellSize);

                int cellHash = cell.hashCode();
                if (!cells.containsKey(cellHash)) {
                    cells.put(cellHash, new ArrayList<>());
                }
                cells.get(cellHash).add(shape);

                if (!objects.containsKey(shape)) {
                    objects.put(shape, new ArrayList<>());
                }
                objects.get(shape).add(cell);
            }
        }
    }

    private Set<ShapePair> computePossibleAabbIntersections() {
        // HashSet is used because requires the uniqueness of pairs,
        // for example, two shapes can intersect in several cells at once
        Set<ShapePair> possibleIntersect = new HashSet<>();
        cells.forEach((cell, list) -> {
            possibleIntersect.addAll(BruteForceMethod.calculateAabbCollisionsWithoutAabbUpdating(list));
        });
        return possibleIntersect;
    }
}
