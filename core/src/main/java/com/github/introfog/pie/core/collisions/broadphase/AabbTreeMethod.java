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

import com.github.introfog.pie.core.collisions.broadphase.aabbtree.AabbTreeNode;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.List;

/**
 * The class is a Aabb (or R) tree method that stores shapes in a binary Aabb tree, which means that each internal
 * node (not a leaf) has exactly 2 children and has an Aabb that fully contains the Aabbs of his children.
 *
 * <p>
 * This method is effective when have a large number of sedentary shapes.
 *
 * @see AbstractBroadPhase
 */
public class AabbTreeMethod extends AbstractBroadPhase {
    private float enlargedAabbCoefficient;
    private AabbTreeNode root;

    /**
     * Instantiates a new {@link AabbTreeMethod} instance.
     */
    public AabbTreeMethod() {
        setEnlargedAabbCoefficient(AabbTreeNode.DEFAULT_ENLARGED_AABB_COEFFICIENT);
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
            root = new AabbTreeNode(shape, enlargedAabbCoefficient);
        } else {
            root = AabbTreeNode.insertLeaf(root, shape);
        }
    }

    @Override
    public boolean remove(IShape shape) {
        boolean removed = super.remove(shape);
        if (removed) {
            root = AabbTreeNode.removeLeaf(root, shape);
        }
        return removed;
    }

    @Override
    public void clear() {
        super.clear();
        root = null;
    }

    @Override
    public AabbTreeMethod newInstance() {
        AabbTreeMethod aabbTreeMethod = new AabbTreeMethod();
        aabbTreeMethod.setShapes(shapes);
        aabbTreeMethod.setEnlargedAabbCoefficient(getEnlargedAabbCoefficient());
        return aabbTreeMethod;
    }

    /**
     * Sets the enlarged Aabb coefficient for the Aabb tree.
     *
     * <p>
     * For more information see {@link AabbTreeNode#enlargedAabbCoefficient}.
     *
     * @param enlargedAabbCoefficient the enlarged Aabb coefficient
     */
    public void setEnlargedAabbCoefficient(float enlargedAabbCoefficient) {
        if (enlargedAabbCoefficient < 0) {
            // TODO log this situation
            return;
        }
        this.enlargedAabbCoefficient = enlargedAabbCoefficient;
    }

    /**
     * Gets the enlarged Aabb coefficient for the Aabb tree.
     *
     * <p>
     * For more information see {@link AabbTreeNode#enlargedAabbCoefficient}.
     *
     * @return the enlarged Aabb coefficient
     */
    public float getEnlargedAabbCoefficient() {
        return enlargedAabbCoefficient;
    }

    @Override
    protected List<ShapePair> domesticCalculateAabbCollisions() {
        root = AabbTreeNode.updateTree(root);
        return AabbTreeNode.calculateAabbCollisions(root);
    }
}
