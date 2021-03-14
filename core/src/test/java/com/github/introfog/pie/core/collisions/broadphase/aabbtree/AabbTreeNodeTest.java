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

import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.Aabb;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.test.PieTest;
import com.github.introfog.pie.test.annotations.UnitTest;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AabbTreeNodeTest extends PieTest {
    @Test
    public void aabbConstructorTest() {
        Aabb aabb = new Aabb();
        aabb.min.set(0, 0);
        aabb.max.set(10, 20);
        AabbTreeNode node = new AabbTreeNode(aabb, 0.5f);

        Assert.assertFalse(node.checked);
        Assert.assertEquals(0.5f, node.enlargedAabbCoefficient, PieTest.FLOAT_EPSILON_COMPARISON);
        Assert.assertSame(aabb, node.aabb);
        Assert.assertNull(node.parent);
        Assert.assertArrayEquals(new AabbTreeNode[2], node.children);
        Assert.assertNull(node.shape);
    }

    @Test
    public void shapeConstructorTest() {
        IShape shape = new Circle(5, 0, 5, MathPie.STATIC_BODY_DENSITY, 0);
        AabbTreeNode node = new AabbTreeNode(shape, 0.3f);

        Assert.assertFalse(node.checked);
        Assert.assertEquals(0.3f, node.enlargedAabbCoefficient, PieTest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(new Vector2f(-8, -3), node.aabb.min);
        Assert.assertEquals(new Vector2f(8, 13), node.aabb.max);
        Assert.assertNull(node.parent);
        Assert.assertArrayEquals(new AabbTreeNode[2], node.children);
        Assert.assertSame(shape, node.shape);
    }

    @Test
    public void isLeafTest() {
        IShape shape = new Circle(5, 0, 5, MathPie.STATIC_BODY_DENSITY, 0);
        AabbTreeNode root = new AabbTreeNode(shape, 0);

        Assert.assertTrue(root.isLeaf());
        root.children[0] = new AabbTreeNode(shape, 0);
        Assert.assertFalse(root.isLeaf());
        root.children[0] = null;
        root.children[1] = new AabbTreeNode(shape, 0);
        Assert.assertFalse(root.isLeaf());

        root = AabbTreeNode.insertLeaf(root, shape);
        Assert.assertFalse(root.isLeaf());
    }

    @Test
    public void enlargedAabbCoefficientInTreeTest() {
        IShape shape = new Circle(5, 0, 5, MathPie.STATIC_BODY_DENSITY, 0);
        AabbTreeNode root = new AabbTreeNode(shape, 0.3f);

        shape = new Circle(7, 5, 5, MathPie.STATIC_BODY_DENSITY, 0);
        root = AabbTreeNode.insertLeaf(root, shape);
        Assert.assertEquals(0.3f, root.children[0].enlargedAabbCoefficient, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.3f, root.children[1].enlargedAabbCoefficient, FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void invalidTreeRootCalculateCollisionTest() {
        Assert.assertTrue(AabbTreeNode.calculateAabbCollisions(null).isEmpty());

        AabbTreeNode node = new AabbTreeNode(new Aabb(), 0.5f);
        node.parent = node;
        Assert.assertTrue(AabbTreeNode.calculateAabbCollisions(node).isEmpty());

        IShape circle = new Circle(0, 0, 0, 0, 0);
        node = new AabbTreeNode(circle, 0.5f);
        Assert.assertTrue(AabbTreeNode.calculateAabbCollisions(node).isEmpty());
    }

    @Test
    public void invalidTreeRootUpdateTreeTest() {
        Assert.assertNull(AabbTreeNode.updateTree(null));

        AabbTreeNode node = new AabbTreeNode(new Aabb(), 0.5f);
        node.parent = node;
        Assert.assertSame(node, AabbTreeNode.updateTree(node));
    }

    @Test
    public void leafRootNotMovedShapeUpdateRootTest() {
        IShape circle = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0);
        AabbTreeNode node = new AabbTreeNode(circle, 0.2f);
        Aabb aabb = node.aabb;
        circle.getBody().getPosition().add(new Vector2f(3.9f, 0));

        AabbTreeNode outputNode = AabbTreeNode.updateTree(node);
        Aabb outputAabb = outputNode.aabb;
        Assert.assertSame(outputNode, node);
        Assert.assertNotSame(outputAabb, aabb);
        Assert.assertEquals(new Vector2f(-14, -14), outputAabb.min);
        Assert.assertEquals(new Vector2f(14, 14), outputAabb.max);
    }

    @Test
    public void movedLeftLeafAndNullGrandpaUpdateRootTest() {
        IShape c1 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0);
        AabbTreeNode root = new AabbTreeNode(c1, 0.1f);

        IShape c2 = new Circle(10, 10, 0, MathPie.STATIC_BODY_DENSITY, 0);
        root = AabbTreeNode.insertLeaf(root, c2);

        c1.getBody().getPosition().add(new Vector2f(15f, 0));
        c1.computeAabb();
        // Additional bBox size equal to 2, and if we move circle for 1.9, leaf shouldn't update
        c2.getBody().getPosition().add(new Vector2f(1.9f, 0));
        c2.computeAabb();
        AabbTreeNode newRoot = AabbTreeNode.updateTree(root);

        Assert.assertNotSame(newRoot, root);
        Assert.assertNotNull(newRoot.children);
        Assert.assertSame(newRoot.children[1].shape, c1);
        Assert.assertEquals(new Vector2f(3, -12), newRoot.children[1].aabb.min);
        Assert.assertEquals(new Vector2f(27, 12), newRoot.children[1].aabb.max);

        Assert.assertEquals(new Vector2f(-2, -12), newRoot.children[0].aabb.min);
        Assert.assertEquals(new Vector2f(22, 12), newRoot.children[0].aabb.max);
    }

    @Test
    public void movedRightLeafAndNullGrandpaUpdateRootTest() {
        IShape c1 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0);
        AabbTreeNode root = new AabbTreeNode(c1, 0.1f);

        IShape c2 = new Circle(10, 10, 0, MathPie.STATIC_BODY_DENSITY, 0);
        root = AabbTreeNode.insertLeaf(root, c2);

        IShape c3 = new Circle(10, 20, 0, MathPie.STATIC_BODY_DENSITY, 0);
        root = AabbTreeNode.insertLeaf(root, c3);

        // Additional bBox size equal to 2, and if we move circle for 3, leaf should update
        c2.getBody().getPosition().add(new Vector2f(3f, 0));
        c2.computeAabb();
        AabbTreeNode newRoot = AabbTreeNode.updateTree(root);

        Assert.assertSame(newRoot, root);
        Assert.assertNotNull(newRoot.children);
        Assert.assertNotNull(newRoot.children[1].children);
        AabbTreeNode child = newRoot.children[1].children[1];
        Assert.assertSame(child.shape, c2);
        Assert.assertEquals(new Vector2f(1, -12), child.aabb.min);
        Assert.assertEquals(new Vector2f(25, 12), child.aabb.max);
    }

    @Test
    public void movedLeftLeafAndUpdateRootTest() {
        IShape c1 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0);
        AabbTreeNode root = new AabbTreeNode(c1, 0.1f);

        IShape c2 = new Circle(10, 10, 0, MathPie.STATIC_BODY_DENSITY, 0);
        root = AabbTreeNode.insertLeaf(root, c2);

        IShape c3 = new Circle(10, 20, 0, MathPie.STATIC_BODY_DENSITY, 0);
        root = AabbTreeNode.insertLeaf(root, c3);

        IShape c4 = new Circle(10, 30, 0, MathPie.STATIC_BODY_DENSITY, 0);
        root = AabbTreeNode.insertLeaf(root, c4);

        // Additional bBox size equal to 2, and if we move circle for 5, leaf should update
        c4.getBody().getPosition().add(new Vector2f(5f, 0));
        c4.computeAabb();
        AabbTreeNode newRoot = AabbTreeNode.updateTree(root);

        Assert.assertSame(newRoot, root);
        Assert.assertNotNull(newRoot.children);
        Assert.assertNotNull(newRoot.children[1].children);
        AabbTreeNode child = newRoot.children[1].children[1];
        Assert.assertSame(child.shape, c4);
        Assert.assertEquals(new Vector2f(23, -12), child.aabb.min);
        Assert.assertEquals(new Vector2f(47, 12), child.aabb.max);
    }

    @Test
    public void invalidTreeRootInsertLeafTest() {
        Assert.assertNull(AabbTreeNode.insertLeaf(null, null));

        AabbTreeNode node = new AabbTreeNode(new Aabb(), 0.5f);
        node.parent = node;
        Assert.assertSame(node, AabbTreeNode.insertLeaf(node, null));
    }

    @Test
    public void treeRotationsTest() {
        IShape c1 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0);
        AabbTreeNode root = new AabbTreeNode(c1, 0.1f);

        for (int i = 1; i < 100; i++) {
            IShape circle = new Circle(10,i * 10, 0, MathPie.STATIC_BODY_DENSITY, 0);
            root = AabbTreeNode.insertLeaf(root, circle);
            int height = AabbTreeNodeTest.calculateTreeHeight(root);
            int expectedHeight = (int) Math.ceil(Math.log(i + 1) / Math.log(2)) + 1;
            Assert.assertEquals(expectedHeight, height);

        }
    }

    @Test
    public void insertLeafTest() {
        IShape c1 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0);
        AabbTreeNode root = new AabbTreeNode(c1, 0.1f);

        for (int i = 1; i < 3; i++) {
            IShape circle = new Circle(10,i * 10, 0, MathPie.STATIC_BODY_DENSITY, 0);
            root = AabbTreeNode.insertLeaf(root, circle);
        }
        IShape c2 = new Circle(1, 20, 0, MathPie.STATIC_BODY_DENSITY, 0);
        root = AabbTreeNode.insertLeaf(root, c2);

        Assert.assertNotNull(root.children);
        Assert.assertNotNull(root.children[1].children);
        Assert.assertSame(c2, root.children[1].children[1].shape);
        Assert.assertNotNull(root.children[1].children[0].shape);
        Assert.assertEquals(new Vector2f(20, 0), root.children[1].children[0].shape.getBody().getPosition());
    }

    @Test
    public void calculateCollisionsTest() {
        IShape c1 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0);
        AabbTreeNode root = new AabbTreeNode(c1, 0.1f);

        for (int i = 1; i < 100; i++) {
            IShape circle = new Circle(10,i * 10.1f, 0, MathPie.STATIC_BODY_DENSITY, 0);
            root = AabbTreeNode.insertLeaf(root, circle);
            Assert.assertEquals(i, AabbTreeNode.calculateAabbCollisions(root).size());
        }
    }

    @Test
    public void removeLeafTest() {
        IShape c1 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0);
        AabbTreeNode root = new AabbTreeNode(c1, 0.1f);

        IShape c2 = new Circle(10, 10, 0, MathPie.STATIC_BODY_DENSITY, 0);
        root = AabbTreeNode.insertLeaf(root, c2);

        IShape c3 = new Circle(10, 20, 0, MathPie.STATIC_BODY_DENSITY, 0);
        root = AabbTreeNode.insertLeaf(root, c3);

        root = AabbTreeNode.removeLeaf(root, c1);
        Assert.assertFalse(root.isLeaf());
        Assert.assertNotNull(root.children[0]);
        Assert.assertNotNull(root.children[1]);
        Assert.assertTrue(root.children[0].isLeaf());
        Assert.assertTrue(root.children[1].isLeaf());
        Assert.assertEquals(c2, root.children[0].shape);
        Assert.assertEquals(c3, root.children[1].shape);

        root = AabbTreeNode.removeLeaf(root, c2);
        Assert.assertTrue(root.isLeaf());
        Assert.assertEquals(c3, root.shape);

        root = AabbTreeNode.removeLeaf(root, c2);
        Assert.assertTrue(root.isLeaf());
        Assert.assertEquals(c3, root.shape);

        root = AabbTreeNode.removeLeaf(root, c3);
        Assert.assertNull(root);
    }

    @Test
    public void invalidTreeRootRemoveLeafTest() {
        Assert.assertNull(AabbTreeNode.removeLeaf(null, null));

        AabbTreeNode node = new AabbTreeNode(new Aabb(), 0.5f);
        node.parent = node;
        Assert.assertSame(node, AabbTreeNode.removeLeaf(node, null));
    }

    private static int calculateTreeHeight(AabbTreeNode treeRoot) {
        Deque<AabbTreeNode> nodes = new ArrayDeque<>();
        nodes.push(treeRoot);
        Map<AabbTreeNode, Integer> mapHeights = new HashMap<>();
        mapHeights.put(treeRoot, 1);
        while (!nodes.isEmpty()) {
            AabbTreeNode currentNode = nodes.pop();
            if (!currentNode.isLeaf())  {
                mapHeights.put(currentNode.children[0], mapHeights.get(currentNode) + 1);
                mapHeights.put(currentNode.children[1], mapHeights.get(currentNode) + 1);
                nodes.push(currentNode.children[0]);
                nodes.push(currentNode.children[1]);
            }
        }
        return mapHeights.values().stream().max(Comparator.naturalOrder()).orElse(-1);
    }
}
