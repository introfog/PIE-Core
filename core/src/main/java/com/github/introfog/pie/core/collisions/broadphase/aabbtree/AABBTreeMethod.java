package com.github.introfog.pie.core.collisions.broadphase.aabbtree;

import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.List;

public class AABBTreeMethod extends AbstractBroadPhase {
    private float enlargedAABBCoefficient;
    private AABBTreeNode root;

    public AABBTreeMethod() {
        setEnlargedAABBCoefficient(AABBTreeNode.DEFAULT_ENLARGED_AABB_COEFFICIENT);
    }

    @Override
    public void setShapes(List<IShape> shapes) {
        this.shapes.clear();
        root = null;
        shapes.forEach(this::addShape);
    }

    @Override
    public void addShape(IShape shape) {
        super.addShape(shape);
        if (root == null) {
            root = new AABBTreeNode(shape, enlargedAABBCoefficient);
        } else {
            root = AABBTreeNode.insertLeaf(root, shape);
        }
    }

    public float getEnlargedAABBCoefficient() {
        return enlargedAABBCoefficient;
    }

    public void setEnlargedAABBCoefficient(float enlargedAABBCoefficient) {
        if (enlargedAABBCoefficient < 0) {
            // TODO log this situation
            return;
        }
        this.enlargedAABBCoefficient = enlargedAABBCoefficient;
    }

    @Override
    protected List<ShapePair> domesticCalculateAabbCollisions() {
        root = AABBTreeNode.updateTree(root);
        return AABBTreeNode.calculateAabbCollisions(root);
    }
}
