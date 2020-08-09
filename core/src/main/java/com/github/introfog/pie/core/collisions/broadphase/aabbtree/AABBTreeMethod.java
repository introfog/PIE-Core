package com.github.introfog.pie.core.collisions.broadphase.aabbtree;

import com.github.introfog.pie.core.collisions.broadphase.AbstractBroadPhase;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.List;

/**
 * The class is a AABB (or R) tree method that stores shapes in a binary AABB tree, which means that each internal
 * node (not a leaf) has exactly 2 children and has an AABB that fully contains the AABBs of his children.
 *
 * <p>
 * This method is effective when have a large number of sedentary shapes.
 *
 * @see AbstractBroadPhase
 */
public class AABBTreeMethod extends AbstractBroadPhase {
    private float enlargedAABBCoefficient;
    private AABBTreeNode root;

    /**
     * Instantiates a new {@link AABBTreeMethod} instance.
     */
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

    /**
     * Sets the enlarged AABB coefficient for the AABB tree.
     *
     * <p>
     * For more information see {@link AABBTreeNode#enlargedAABBCoefficient}.
     *
     * @param enlargedAABBCoefficient the enlarged AABB coefficient
     */
    public void setEnlargedAABBCoefficient(float enlargedAABBCoefficient) {
        if (enlargedAABBCoefficient < 0) {
            // TODO log this situation
            return;
        }
        this.enlargedAABBCoefficient = enlargedAABBCoefficient;
    }

    /**
     * Gets the enlarged AABB coefficient for the AABB tree.
     *
     * <p>
     * For more information see {@link AABBTreeNode#enlargedAABBCoefficient}.
     *
     * @return the enlarged AABB coefficient
     */
    public float getEnlargedAABBCoefficient() {
        return enlargedAABBCoefficient;
    }

    @Override
    protected List<ShapePair> domesticCalculateAabbCollisions() {
        root = AABBTreeNode.updateTree(root);
        return AABBTreeNode.calculateAabbCollisions(root);
    }
}
