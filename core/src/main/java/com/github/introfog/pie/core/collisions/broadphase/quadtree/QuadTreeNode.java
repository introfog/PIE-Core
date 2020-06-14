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
package com.github.introfog.pie.core.collisions.broadphase.quadtree;

import com.github.introfog.pie.core.collisions.broadphase.BruteForceMethod;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.AABB;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuadTreeNode {
    public static final int DEFAULT_MAX_SHAPES_IN_ONE_NODE = 50;
    public static final int DEFAULT_MIN_BOUNDING_BOX_SIZE = 10;

    public AABB boundingBox;
    public QuadTreeNode parent;
    public QuadTreeNode[] children;
    public AABB[] childrenAABB;
    public List<IShape> shapes;

    private int maxShapesInLeaf;
    private float minBoundingBoxSize;

    public QuadTreeNode(AABB boundingBox, QuadTreeNode parent) {
        if (parent == null) {
            maxShapesInLeaf = DEFAULT_MAX_SHAPES_IN_ONE_NODE;
            minBoundingBoxSize = DEFAULT_MIN_BOUNDING_BOX_SIZE;
        } else {
            maxShapesInLeaf = parent.maxShapesInLeaf;
            minBoundingBoxSize = parent.minBoundingBoxSize;
        }

        this.boundingBox = boundingBox;

        float boundingBoxSize = Math.max(boundingBox.max.x - boundingBox.min.x, boundingBox.max.y - boundingBox.min.y);
        this.boundingBox.max.x = this.boundingBox.min.x + boundingBoxSize;
        this.boundingBox.max.y = this.boundingBox.min.y + boundingBoxSize;

        this.parent = parent;
        this.children = new QuadTreeNode[] {null, null, null, null};

        float centerX = (boundingBox.max.x + boundingBox.min.x) / 2f;
        float centerY = (boundingBox.max.y + boundingBox.min.y) / 2f;
        this.childrenAABB = new AABB[] {
                new AABB(new Vector2f(centerX, centerY), new Vector2f(boundingBox.max)),
                new AABB(new Vector2f(boundingBox.min.x, centerY), new Vector2f(centerX, boundingBox.max.y)),
                new AABB(new Vector2f(boundingBox.min), new Vector2f(centerX, centerY)),
                new AABB(new Vector2f(centerX, boundingBox.min.y), new Vector2f(boundingBox.max.x, centerY))
        };
        this.shapes = new ArrayList<>();
    }

    public int getMaxShapesInLeaf() {
        return maxShapesInLeaf;
    }

    public void setMaxShapesInLeaf(int maxShapesInLeaf) {
        this.maxShapesInLeaf = maxShapesInLeaf;
        for (QuadTreeNode child : children) {
            if (child != null) {
                child.setMaxShapesInLeaf(maxShapesInLeaf);
            }
        }
    }

    public float getMinBoundingBoxSize() {
        return minBoundingBoxSize;
    }

    public void setMinBoundingBoxSize(float minBoundingBoxSize) {
        this.minBoundingBoxSize = minBoundingBoxSize;
        for (QuadTreeNode child : children) {
            if (child != null) {
                child.setMinBoundingBoxSize(minBoundingBoxSize);
            }
        }
    }

    public void clear() {
        shapes.clear();

        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                children[i].clear();
                children[i] = null;
            }
        }
    }

    public Set<ShapePair> calculateAllAabbCollision() {
        Set<ShapePair> collisions = new HashSet<>(BruteForceMethod.calculateAabbCollisionsWithoutAabbUpdating(shapes));

        for (QuadTreeNode child : children) {
            if (child != null) {
                collisions.addAll(child.calculateAllAabbCollision());
            }
        }
        return collisions;
    }

    public void update() {
        for(int i = shapes.size() - 1; i >= 0; --i) {
            if (!AABB.isContained(boundingBox, shapes.get(i).aabb)) {
                insertUp(shapes.remove(i));
            }
        }
        for (QuadTreeNode child : children) {
            if (child != null) {
                child.update();
            }
        }
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null && children[i].isEmpty()) {
                children[i] = null;
            }
        }
    }

    public boolean isEmpty() {
        for (QuadTreeNode child : children) {
            if (child != null) {
                return false;
            }
        }
        return shapes.isEmpty();
    }

    public void insertDown(List<IShape> shapes) {
        shapes.forEach(this::insertDown);
    }

    public void insertUp(IShape shape) {
        if (!AABB.isContained(boundingBox, shape.aabb)) {
            if (parent != null) {
                parent.insertUp(shape);
            }
        } else {
            insertDown(shape);
        }
    }

    public void insertDown(IShape shape) {
        AABB aabb = shape.aabb;

        if (!AABB.isIntersected(boundingBox, aabb)) {
            return;
        }
        if ((boundingBox.max.x - boundingBox.min.x) <= 2 * minBoundingBoxSize) {
            shapes.add(shape);
            return;
        }

        boolean childrenAreNull = children[0] == null && children[1] == null && children[2] == null && children[3] == null;
        if (childrenAreNull && shapes.size() < maxShapesInLeaf) {
            shapes.add(shape);
            return;
        } else {
            for (IShape nodeShape : shapes) {
                for (int i = 0; i < children.length; i++) {
                    if (AABB.isIntersected(childrenAABB[i], nodeShape.aabb)) {
                        if (children[i] == null) {
                            children[i] = new QuadTreeNode(childrenAABB[i], this);
                        }
                        children[i].insertDown(nodeShape);
                    }
                }
            }
            shapes.clear();
        }

        for (int i = 0; i < children.length; i++) {
            if (AABB.isIntersected(childrenAABB[i], aabb)) {
                if (children[i] == null) {
                    children[i] = new QuadTreeNode(childrenAABB[i], this);
                }
                children[i].insertDown(shape);
            }
        }
    }
}
