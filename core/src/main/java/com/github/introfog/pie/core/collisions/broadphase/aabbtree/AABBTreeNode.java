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

import com.github.introfog.pie.core.shape.AABB;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * The class is an AABB node of a AABB tree that is binary (each non-leaf element has
 * exactly 2 children) and the node AABB completely contains the AABBs of its children.
 *
 * @see AABBTreeMethod
 */
public class AABBTreeNode {
    /** The constant DEFAULT_ENLARGED_AABB_COEFFICIENT. */
    public static final float DEFAULT_ENLARGED_AABB_COEFFICIENT = 0.15f;

    /**
     * Auxiliary flag that is used when calculating collisions and is used to
     * mark that the children of the marked node have already been viewed.
     */
    public boolean checked;

    /**
     * The enlarged AABB coefficient that is used when initializing a tree leaf and is used to create a larger
     * leaf AABB than the shape AABB that is stored in the leaf. This is done in order not to update each
     * iteration of the leaf AABB if the shape has moved slightly.
     *
     * <p>
     * Note that the leaf AABB will be on each side greater than the shape AABB by a enlargedAABBCoefficient.
     * The enlargedAABBCoefficient is also set in the tree by passing it to the node constructor.
     */
    public final float enlargedAABBCoefficient;

    /**
     * The node axis aligned bounding box. If the node is leaf, AABB a slightly larger that shape AABB
     * (see {@link #enlargedAABBCoefficient}), otherwise AABB of minimum size that allows to contain
     * children AABBs inside it.
     */
    public AABB aabb;

    /** The parent node. */
    public AABBTreeNode parent;

    /** The children node array.
     *
     * <p>
     * Note that the size of the array is always 2. If it is a leaf, the array contains two NULL elements,
     * otherwise it contains references to children.
     */
    public final AABBTreeNode[] children;

    /**
     * The node shape. If it is not a leaf, this field is NULL.
     */
    public final IShape shape;

    /**
     * Instantiates a new {@link AABBTreeNode} instance based on shape. This node is a leaf in the tree.
     *
     * @param shape the leaf shape
     * @param enlargedAABBCoefficient the enlarged AABB coefficient
     */
    public AABBTreeNode(IShape shape, float enlargedAABBCoefficient) {
        this.checked = false;
        this.enlargedAABBCoefficient = enlargedAABBCoefficient;
        AABBTreeNode.calculateEnlargedAabb(this, shape);
        this.parent = null;
        this.children = new AABBTreeNode[2];
        this.shape = shape;
    }

    /**
     * Instantiates a new {@link AABBTreeNode} instance based on AABB.
     *
     * <p>
     * This is an auxiliary constructor that is used to create non-leaf
     * elements when inserting new shapes into the tree.
     *
     * @param aabb the node axis aligned bounding box
     * @param enlargedAABBCoefficient the enlarged AABB coefficient
     */
    protected AABBTreeNode(AABB aabb, float enlargedAABBCoefficient) {
        this.checked = false;
        this.enlargedAABBCoefficient = enlargedAABBCoefficient;
        this.aabb = aabb;
        this.parent = null;
        this.children = new AABBTreeNode[2];
        this.shape = null;
    }

    /**
     * Calculates the shape AABB collisions.
     *
     * <p>
     * Note, when this method is called, all shapes from tree have an up-to-date AABB.
     *
     * @param treeRoot the root of the AABB tree
     * @return the {@link ShapePair} list in which each item represents
     * a unique shape pair and the AABB of those shapes intersect
     */
    public static List<ShapePair> calculateAabbCollisions(AABBTreeNode treeRoot) {
        List<ShapePair> collisions = new ArrayList<>();
        if (treeRoot == null || treeRoot.parent != null) {
            // TODO add log message
            // It is necessary to start from tree root, otherwise the inherited cost will be calculated incorrectly
            return collisions;
        }
        if (treeRoot.isLeaf()) {
            return collisions;
        }

        AABBTreeNode.clearCheckedFlag(treeRoot);
        AABBTreeNode.calculateAabbCollisionsHelper(treeRoot.children[0], treeRoot.children[1], collisions);

        return collisions;
    }

    /**
     * Update the AABB tree.
     *
     * <p>
     * The update is performed by deleting and inserting the shape in the tree that went beyond the leaf AABB.
     *
     * <p>
     * Note that the root of the tree may change, so the actual reference to the tree root is returned from the method.
     *
     * @param treeRoot the tree root
     * @return the new or old tree root depending on how the tree was updated
     */
    public static AABBTreeNode updateTree(AABBTreeNode treeRoot) {
        if (treeRoot == null || treeRoot.parent != null) {
            // TODO add log message
            // It is necessary to start from tree root, otherwise the inherited cost will be calculated incorrectly
            return treeRoot;
        }
        if (treeRoot.isLeaf()) {
            AABBTreeNode.calculateEnlargedAabb(treeRoot, treeRoot.shape);
        }

        List<AABBTreeNode> invalidNodes = getInvalidLeafs(treeRoot);
        for (AABBTreeNode node : invalidNodes) {
            AABBTreeNode parent = node.parent;
            AABBTreeNode sibling = parent.children[0] == node ? parent.children[1] : parent.children[0];
            AABBTreeNode grandpa = parent.parent;

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
            treeRoot = AABBTreeNode.insertLeaf(treeRoot, node.shape);
        }
        return treeRoot;
    }

    /**
     * Insert shape to the tree.
     *
     * <p>
     * Note that the root of the tree may change, so the actual reference to the tree root is returned from the method.
     * 
     * @param treeRoot the tree root to insert the shape into
     * @param shape the shape to be inserted into the tree
     * @return the new or old tree root depending on how the leaf was inserted
     */
    public static AABBTreeNode insertLeaf(AABBTreeNode treeRoot, IShape shape) {
        if (treeRoot == null || treeRoot.parent != null) {
            // TODO add log message
            // It is necessary to start from tree root, otherwise the inherited cost will be calculated incorrectly
            return treeRoot;
        }
        AABBTreeNode leaf = new AABBTreeNode(shape, treeRoot.enlargedAABBCoefficient);

        // Stage 1: find the best sibling for the new leaf
        AABBTreeNode bestSibling = AABBTreeNode.findBestSibling(treeRoot, leaf);

        // Stage 2: create a new parent
        AABBTreeNode newTreeRoot = AABBTreeNode.createNewParent(treeRoot, leaf, bestSibling);

        // Stage 3: walk back up the tree refitting AABBs
        AABBTreeNode.refittingTreeRoot(leaf);

        return newTreeRoot;
    }

    private boolean isLeaf() {
        return shape != null;
    }

    private float[] nodeCost(AABB shapeAabb) {
        // The first element is the total cost of the node, the
        // second element is the total cost of the node subtree
        float[] resultCosts = new float[2];
        resultCosts[0] = AABB.union(aabb, shapeAabb).surfaceArea();
        resultCosts[1] = shapeAabb.surfaceArea();
        AABBTreeNode currentNode = this;
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
        AABBTreeNode currentParent = parent;
        while (currentParent != null) {
            currentParent.aabb = AABB.union(currentParent.children[0].aabb, currentParent.children[1].aabb);
            currentParent = currentParent.parent;
        }
    }

    private static AABBTreeNode findBestSibling(AABBTreeNode treeRoot, AABBTreeNode leaf) {
        AABBTreeNode bestSibling = null;
        float bestCost = Float.MAX_VALUE;
        Stack<AABBTreeNode> priorityNodes = new Stack<>();
        priorityNodes.push(treeRoot);

        while (!priorityNodes.empty()) {
            AABBTreeNode currentNode = priorityNodes.pop();

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

    private static void clearCheckedFlag(AABBTreeNode node) {
        node.checked = false;
        if (!node.isLeaf()) {
            AABBTreeNode.clearCheckedFlag(node.children[0]);
            AABBTreeNode.clearCheckedFlag(node.children[1]);
        }
    }

    private static void checkChild(AABBTreeNode node, List<ShapePair> collisions) {
        if (!node.checked) {
            calculateAabbCollisionsHelper(node.children[0], node.children[1], collisions);
            node.checked = true;
        }
    }

    private static void calculateAabbCollisionsHelper(AABBTreeNode first, AABBTreeNode second, List<ShapePair> collisions) {
        if (first.isLeaf()) {
            if (second.isLeaf()) {
                if (AABB.isIntersected(first.shape.aabb, second.shape.aabb)) {
                    collisions.add(new ShapePair(first.shape, second.shape));
                }
            } else {
                checkChild(second, collisions);
                if (AABB.isIntersected(first.aabb, second.aabb)) {
                    calculateAabbCollisionsHelper(first, second.children[0], collisions);
                    calculateAabbCollisionsHelper(first, second.children[1], collisions);
                }
            }
        } else {
            if (second.isLeaf()) {
                checkChild(first, collisions);
                if (AABB.isIntersected(first.aabb, second.aabb)) {
                    calculateAabbCollisionsHelper(first.children[0], second, collisions);
                    calculateAabbCollisionsHelper(first.children[1], second, collisions);
                }
            } else {
                checkChild(first, collisions);
                checkChild(second, collisions);
                if (AABB.isIntersected(first.aabb, second.aabb)) {
                    calculateAabbCollisionsHelper(first.children[0], second.children[0], collisions);
                    calculateAabbCollisionsHelper(first.children[0], second.children[1], collisions);
                    calculateAabbCollisionsHelper(first.children[1], second.children[0], collisions);
                    calculateAabbCollisionsHelper(first.children[1], second.children[1], collisions);
                }
            }
        }
    }

    private static List<AABBTreeNode> getInvalidLeafs(AABBTreeNode treeRoot) {
        List<AABBTreeNode> invalidNodes = new ArrayList<>();
        Stack<AABBTreeNode> nodes = new Stack<>();
        nodes.push(treeRoot);
        while (!nodes.empty()) {
            AABBTreeNode currentNode = nodes.pop();
            if (currentNode.isLeaf()) {
                if (!AABB.isContained(currentNode.aabb, currentNode.shape.aabb)) {
                    invalidNodes.add(currentNode);
                }
            } else {
                nodes.push(currentNode.children[0]);
                nodes.push(currentNode.children[1]);
            }
        }
        return invalidNodes;
    }

    private static void refittingTreeRoot(AABBTreeNode leaf) {
        AABBTreeNode currentParent = leaf.parent;
        while (currentParent != null) {
            currentParent.aabb = AABB.union(currentParent.children[0].aabb, currentParent.children[1].aabb);

            AABBTreeNode.attemptToRotate(currentParent);

            currentParent = currentParent.parent;
        }
    }

    private static void attemptToRotate(AABBTreeNode node) {
        if (node.parent == null) {
            return;
        }
        AABBTreeNode sibling = node.parent.children[0] == node ? node.parent.children[1] : node.parent.children[0];
        if (sibling == null) {
            return;
        }

        float currentSa = node.aabb.surfaceArea();

        AABB firstAabb = AABB.union(sibling.aabb, node.children[0].aabb);
        AABB secondAabb = AABB.union(sibling.aabb, node.children[1].aabb);
        float firstChildRotateSa = firstAabb.surfaceArea();
        float secondChildRotateSa = secondAabb.surfaceArea();
        float bestRotateSa = Math.min(firstChildRotateSa, secondChildRotateSa);
        if (bestRotateSa < currentSa) {
            AABBTreeNode child;
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

    private static AABBTreeNode createNewParent(AABBTreeNode treeRoot, AABBTreeNode leaf, AABBTreeNode bestSibling) {
        AABBTreeNode oldParent = bestSibling.parent;
        AABBTreeNode newParent = new AABBTreeNode(AABB.union(leaf.aabb, bestSibling.aabb), treeRoot.enlargedAABBCoefficient);
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

    private static void calculateEnlargedAabb(AABBTreeNode node, IShape shape) {
        node.aabb = new AABB();
        float width = shape.aabb.max.x - shape.aabb.min.x;
        float height = shape.aabb.max.y - shape.aabb.min.y;
        node.aabb.min.set(shape.aabb.min.x - node.enlargedAABBCoefficient * width,
                shape.aabb.min.y - node.enlargedAABBCoefficient * height);
        node.aabb.max.set(shape.aabb.max.x + node.enlargedAABBCoefficient * width,
                shape.aabb.max.y + node.enlargedAABBCoefficient * height);
    }
}
