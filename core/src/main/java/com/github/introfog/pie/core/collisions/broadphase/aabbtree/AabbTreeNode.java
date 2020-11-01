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
package com.github.introfog.pie.core.collisions.broadphase.aabbtree;

import com.github.introfog.pie.core.collisions.broadphase.AabbTreeMethod;
import com.github.introfog.pie.core.shape.Aabb;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The class is an Aabb node of a Aabb tree that is binary (each non-leaf element has
 * exactly 2 children) and the node Aabb completely contains the Aabbs of its children.
 *
 * @see AabbTreeMethod
 */
public class AabbTreeNode {
    /** The constant DEFAULT_ENLARGED_AABB_COEFFICIENT. */
    public static final float DEFAULT_ENLARGED_AABB_COEFFICIENT = 0.15f;

    /**
     * Auxiliary flag that is used when calculating collisions and is used to
     * mark that the children of the marked node have already been viewed.
     */
    public boolean checked;

    /**
     * The enlarged Aabb coefficient that is used when initializing a tree leaf and is used to create a larger
     * leaf Aabb than the shape Aabb that is stored in the leaf. This is done in order not to update each
     * iteration of the leaf Aabb if the shape has moved slightly.
     *
     * <p>
     * Note that the leaf Aabb will be on each side greater than the shape Aabb by a enlargedAabbCoefficient.
     * The enlargedAabbCoefficient is also set in the tree by passing it to the node constructor.
     */
    public final float enlargedAabbCoefficient;

    /**
     * The node axis aligned bounding box. If the node is leaf, Aabb a slightly larger that shape Aabb
     * (see {@link #enlargedAabbCoefficient}), otherwise Aabb of minimum size that allows to contain
     * children Aabbs inside it.
     */
    public Aabb aabb;

    /** The parent node. */
    public AabbTreeNode parent;

    /** The children node array.
     *
     * <p>
     * Note that the size of the array is always 2. If it is a leaf, the array contains two NULL elements,
     * otherwise it contains references to children.
     */
    public final AabbTreeNode[] children;

    /**
     * The node shape. If it is not a leaf, this field is NULL.
     */
    public final IShape shape;

    /**
     * Instantiates a new {@link AabbTreeNode} instance based on shape. This node is a leaf in the tree.
     *
     * @param shape the leaf shape
     * @param enlargedAabbCoefficient the enlarged Aabb coefficient
     */
    public AabbTreeNode(IShape shape, float enlargedAabbCoefficient) {
        this.checked = false;
        this.enlargedAabbCoefficient = enlargedAabbCoefficient;
        AabbTreeNode.calculateEnlargedAabb(this, shape);
        this.parent = null;
        this.children = new AabbTreeNode[2];
        this.shape = shape;
    }

    /**
     * Instantiates a new {@link AabbTreeNode} instance based on Aabb.
     *
     * <p>
     * This is an auxiliary constructor that is used to create non-leaf
     * elements when inserting new shapes into the tree.
     *
     * @param aabb the node axis aligned bounding box
     * @param enlargedAabbCoefficient the enlarged Aabb coefficient
     */
    protected AabbTreeNode(Aabb aabb, float enlargedAabbCoefficient) {
        this.checked = false;
        this.enlargedAabbCoefficient = enlargedAabbCoefficient;
        this.aabb = aabb;
        this.parent = null;
        this.children = new AabbTreeNode[2];
        this.shape = null;
    }

    /**
     * Calculates the shape Aabb collisions.
     *
     * <p>
     * Note, when this method is called, all shapes from tree have an up-to-date Aabb.
     *
     * @param treeRoot the root of the Aabb tree
     * @return the {@link ShapePair} list in which each item represents
     * a unique shape pair and the Aabb of those shapes intersect
     */
    public static Set<ShapePair> calculateAabbCollisions(AabbTreeNode treeRoot) {
        Set<ShapePair> collisions = new HashSet<>();
        if (treeRoot == null || treeRoot.parent != null) {
            // TODO add log message
            // It is necessary to start from tree root, otherwise the inherited cost will be calculated incorrectly
            return collisions;
        }
        if (treeRoot.isLeaf()) {
            return collisions;
        }

        AabbTreeNode.clearCheckedFlag(treeRoot);
        AabbTreeNode.calculateAabbCollisionsHelper(treeRoot.children[0], treeRoot.children[1], collisions);

        return collisions;
    }

    /**
     * Updates the Aabb tree.
     *
     * <p>
     * The update is performed by deleting and inserting the shape in the tree that went beyond the leaf Aabb.
     *
     * <p>
     * Note that the root of the tree may change, so the actual reference to the tree root is returned from the method.
     *
     * @param treeRoot the tree root
     * @return the new or old tree root depending on how the tree was updated
     */
    public static AabbTreeNode updateTree(AabbTreeNode treeRoot) {
        if (treeRoot == null || treeRoot.parent != null) {
            // TODO add log message
            // It is necessary to start from tree root, otherwise the inherited cost will be calculated incorrectly
            return treeRoot;
        }
        if (treeRoot.isLeaf()) {
            AabbTreeNode.calculateEnlargedAabb(treeRoot, treeRoot.shape);
        }

        List<AabbTreeNode> invalidNodes = getInvalidLeafs(treeRoot);
        for (AabbTreeNode node : invalidNodes) {
            treeRoot = AabbTreeNode.removeNode(treeRoot, node);
            treeRoot = AabbTreeNode.insertLeaf(treeRoot, node.shape);
        }
        return treeRoot;
    }

    /**
     * Inserts shape to the tree.
     *
     * <p>
     * Note that the root of the tree may change, so the actual reference to the tree root is returned from the method.
     * 
     * @param treeRoot the tree root to insert the shape into
     * @param shape the shape to be inserted into the tree
     * @return the new or old tree root depending on how the leaf was inserted
     */
    public static AabbTreeNode insertLeaf(AabbTreeNode treeRoot, IShape shape) {
        if (treeRoot == null || treeRoot.parent != null) {
            // TODO add log message
            // It is necessary to start from tree root, otherwise the inherited cost will be calculated incorrectly
            return treeRoot;
        }
        AabbTreeNode leaf = new AabbTreeNode(shape, treeRoot.enlargedAabbCoefficient);

        // Stage 1: find the best sibling for the new leaf
        AabbTreeNode bestSibling = AabbTreeNode.findBestSibling(treeRoot, leaf);

        // Stage 2: create a new parent
        AabbTreeNode newTreeRoot = AabbTreeNode.createNewParent(treeRoot, leaf, bestSibling);

        // Stage 3: walk back up the tree refitting Aabbs
        AabbTreeNode.refittingTreeRoot(leaf);

        return newTreeRoot;
    }

    /**
     * Removes leaf with current shape from aabb tree.
     *
     * @param treeRoot the tree root to remove the shape into
     * @param shape the shape to be removed into the tree
     * @return the new or old tree root depending on how the leaf
     * was removed, can be null in case when tree root removed
     */
    public static AabbTreeNode removeLeaf(AabbTreeNode treeRoot, IShape shape) {
        if (treeRoot == null || treeRoot.parent != null) {
            // TODO add log message
            // It is necessary to start from tree root, otherwise the inherited cost will be calculated incorrectly
            return treeRoot;
        }

        AabbTreeNode leaf = AabbTreeNode.findLeaf(treeRoot, shape);
        if (leaf == null) {
            return treeRoot;
        }
        if (leaf == treeRoot) {
            return null;
        }
        return AabbTreeNode.removeNode(treeRoot, leaf);
    }

    public boolean isLeaf() {
        return shape != null && children[0] == null && children[1] == null;
    }

    private float[] nodeCost(Aabb shapeAabb) {
        // The first element is the total cost of the node, the
        // second element is the total cost of the node subtree
        float[] resultCosts = new float[2];
        resultCosts[0] = Aabb.union(aabb, shapeAabb).surfaceArea();
        resultCosts[1] = shapeAabb.surfaceArea();
        AabbTreeNode currentNode = this;
        resultCosts[1] += currentNode.aabb.deltaSurfaceArea(shapeAabb);
        currentNode = currentNode.parent;
        while (currentNode != null) {
            float currentCost = currentNode.aabb.deltaSurfaceArea(shapeAabb);
            if (currentCost == 0) {
                // If the cost is 0 for current node, then there is no point in going further along the tree,
                // since the current node contains shapeAabb and the cost of parent nodes will also be 0
                return resultCosts;
            }
            resultCosts[0] += currentCost;
            resultCosts[1] += currentCost;
            currentNode = currentNode.parent;
        }
        return resultCosts;
    }

    private void refitTree() {
        AabbTreeNode currentParent = parent;
        while (currentParent != null) {
            currentParent.aabb = Aabb.union(currentParent.children[0].aabb, currentParent.children[1].aabb);
            currentParent = currentParent.parent;
        }
    }

    private static AabbTreeNode findLeaf(AabbTreeNode treeRoot, final IShape shape) {
        Deque<AabbTreeNode> nodes = new ArrayDeque<>();
        nodes.push(treeRoot);
        while (!nodes.isEmpty()) {
            AabbTreeNode currentNode = nodes.pop();
            if (currentNode.isLeaf()) {
                if (shape.equals(currentNode.shape)) {
                    return currentNode;
                }
            } else {
                nodes.push(currentNode.children[0]);
                nodes.push(currentNode.children[1]);
            }
        }
        return null;
    }

    private static AabbTreeNode removeNode(AabbTreeNode treeRoot, final AabbTreeNode node) {
        AabbTreeNode parent = node.parent;
        AabbTreeNode sibling = parent.children[0] == node ? parent.children[1] : parent.children[0];
        AabbTreeNode grandpa = parent.parent;

        if (grandpa == null) {
            sibling.parent = null;
            treeRoot = sibling;
        } else {
            if (grandpa.children[0] == parent) {
                grandpa.children[0] = sibling;
            } else {
                grandpa.children[1] = sibling;
            }
            sibling.parent = grandpa;
            sibling.refitTree();
        }
        return treeRoot;
    }

    private static AabbTreeNode findBestSibling(AabbTreeNode treeRoot, AabbTreeNode leaf) {
        AabbTreeNode bestSibling = treeRoot;
        float bestCost = Float.MAX_VALUE;
        Deque<AabbTreeNode> priorityNodes = new ArrayDeque<>();
        priorityNodes.push(treeRoot);

        while (!priorityNodes.isEmpty()) {
            AabbTreeNode currentNode = priorityNodes.pop();

            float[] nodeCosts = currentNode.nodeCost(leaf.aabb);
            if (nodeCosts[0] < bestCost) {
                bestSibling = currentNode;
                bestCost = nodeCosts[0];
            }

            if (nodeCosts[1] < bestCost && !currentNode.isLeaf()) {
                priorityNodes.push(currentNode.children[0]);
                priorityNodes.push(currentNode.children[1]);
            }
        }

        return bestSibling;
    }

    private static void clearCheckedFlag(AabbTreeNode node) {
        node.checked = false;
        if (!node.isLeaf()) {
            AabbTreeNode.clearCheckedFlag(node.children[0]);
            AabbTreeNode.clearCheckedFlag(node.children[1]);
        }
    }

    private static void checkChild(AabbTreeNode node, Set<ShapePair> collisions) {
        if (!node.checked) {
            calculateAabbCollisionsHelper(node.children[0], node.children[1], collisions);
            node.checked = true;
        }
    }

    private static void calculateAabbCollisionsHelper(AabbTreeNode first, AabbTreeNode second, Set<ShapePair> collisions) {
        if (first.isLeaf()) {
            if (second.isLeaf()) {
                if (Aabb.isIntersected(first.shape.aabb, second.shape.aabb)) {
                    collisions.add(new ShapePair(first.shape, second.shape));
                }
            } else {
                checkChild(second, collisions);
                if (Aabb.isIntersected(first.aabb, second.aabb)) {
                    calculateAabbCollisionsHelper(first, second.children[0], collisions);
                    calculateAabbCollisionsHelper(first, second.children[1], collisions);
                }
            }
        } else {
            if (second.isLeaf()) {
                checkChild(first, collisions);
                if (Aabb.isIntersected(first.aabb, second.aabb)) {
                    calculateAabbCollisionsHelper(first.children[0], second, collisions);
                    calculateAabbCollisionsHelper(first.children[1], second, collisions);
                }
            } else {
                checkChild(first, collisions);
                checkChild(second, collisions);
                if (Aabb.isIntersected(first.aabb, second.aabb)) {
                    calculateAabbCollisionsHelper(first.children[0], second.children[0], collisions);
                    calculateAabbCollisionsHelper(first.children[0], second.children[1], collisions);
                    calculateAabbCollisionsHelper(first.children[1], second.children[0], collisions);
                    calculateAabbCollisionsHelper(first.children[1], second.children[1], collisions);
                }
            }
        }
    }

    private static List<AabbTreeNode> getInvalidLeafs(AabbTreeNode treeRoot) {
        List<AabbTreeNode> invalidNodes = new ArrayList<>();
        Deque<AabbTreeNode> nodes = new ArrayDeque<>();
        nodes.push(treeRoot);
        while (!nodes.isEmpty()) {
            AabbTreeNode currentNode = nodes.pop();
            if (currentNode.isLeaf()) {
                if (!Aabb.isContained(currentNode.aabb, currentNode.shape.aabb)) {
                    invalidNodes.add(currentNode);
                }
            } else {
                nodes.push(currentNode.children[0]);
                nodes.push(currentNode.children[1]);
            }
        }
        return invalidNodes;
    }

    private static void refittingTreeRoot(AabbTreeNode leaf) {
        AabbTreeNode currentParent = leaf.parent;
        while (currentParent != null) {
            currentParent.aabb = Aabb.union(currentParent.children[0].aabb, currentParent.children[1].aabb);

            AabbTreeNode.attemptToRotate(currentParent);

            currentParent = currentParent.parent;
        }
    }

    private static void attemptToRotate(AabbTreeNode node) {
        if (node.parent == null) {
            return;
        }
        AabbTreeNode sibling = node.parent.children[0] == node ? node.parent.children[1] : node.parent.children[0];

        float currentSa = node.aabb.surfaceArea();

        Aabb firstAabb = Aabb.union(sibling.aabb, node.children[0].aabb);
        Aabb secondAabb = Aabb.union(sibling.aabb, node.children[1].aabb);
        float firstChildRotateSa = firstAabb.surfaceArea();
        float secondChildRotateSa = secondAabb.surfaceArea();
        float bestRotateSa = Math.min(firstChildRotateSa, secondChildRotateSa);
        if (bestRotateSa < currentSa) {
            AabbTreeNode child;
            if (firstChildRotateSa < secondChildRotateSa) {
                child = node.children[1];
                node.children[1] = sibling;
                node.aabb = firstAabb;
            } else {
                child = node.children[0];
                node.children[0] = sibling;
                node.aabb = secondAabb;
            }
            sibling.parent = node;

            if (node.parent.children[0] == node) {
                node.parent.children[1] = child;
            } else {
                node.parent.children[0] = child;
            }
            child.parent = node.parent;
        }
    }

    private static AabbTreeNode createNewParent(AabbTreeNode treeRoot, AabbTreeNode leaf, AabbTreeNode bestSibling) {
        AabbTreeNode oldParent = bestSibling.parent;
        AabbTreeNode newParent = new AabbTreeNode(Aabb.union(leaf.aabb, bestSibling.aabb), treeRoot.enlargedAabbCoefficient);
        newParent.parent = oldParent;

        if (oldParent != null) {
            // The sibling was not the root
            if (oldParent.children[0] == bestSibling) {
                oldParent.children[0] = newParent;
            } else {
                oldParent.children[1] = newParent;
            }
            newParent.children[0] = bestSibling;
            newParent.children[1] = leaf;
            bestSibling.parent = newParent;
            leaf.parent = newParent;
            return treeRoot;
        } else {
            // The sibling was the root
            newParent.children[0] = bestSibling;
            newParent.children[1] = leaf;
            bestSibling.parent = newParent;
            leaf.parent = newParent;
            return newParent;
        }
    }

    private static void calculateEnlargedAabb(AabbTreeNode node, IShape shape) {
        node.aabb = new Aabb();
        float width = shape.aabb.max.x - shape.aabb.min.x;
        float height = shape.aabb.max.y - shape.aabb.min.y;
        node.aabb.min.set(shape.aabb.min.x - node.enlargedAabbCoefficient * width,
                shape.aabb.min.y - node.enlargedAabbCoefficient * height);
        node.aabb.max.set(shape.aabb.max.x + node.enlargedAabbCoefficient * width,
                shape.aabb.max.y + node.enlargedAabbCoefficient * height);
    }
}
