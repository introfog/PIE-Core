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

import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.AABB;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.core.shape.Polygon;
import com.github.introfog.pie.core.util.ShapePair;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class QuadTreeNodeTest extends PIETest {
    private static final QuadTreeNode[] QUAD_TREE_NODE_NULL_ARRAY = new QuadTreeNode[] {null, null, null, null};

    @Test
    public void constructorTest() {
        QuadTreeNode node = new QuadTreeNode(new AABB(new Vector2f(-1, -10), new Vector2f(14, 10)), null);
        Assert.assertEquals(new Vector2f(-1, -10), node.boundingBox.min);
        Assert.assertEquals(new Vector2f(19, 10), node.boundingBox.max);

        QuadTreeNode temp = new QuadTreeNode(new AABB(), null);
        node = new QuadTreeNode(new AABB(new Vector2f(-1, -10), new Vector2f(24, 2)), temp);
        Assert.assertEquals(new Vector2f(-1, -10), node.boundingBox.min);
        Assert.assertEquals(new Vector2f(24, 15), node.boundingBox.max);

        Assert.assertSame(temp, node.parent);
        Assert.assertNull(temp.parent);

        Assert.assertNotNull(node.shapes);
        Assert.assertTrue(node.shapes.isEmpty());

        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children);

        AABB[] childrenAABB = new AABB[] {
                new AABB(new Vector2f(11.5f, 2.5f), new Vector2f(24, 15)),
                new AABB(new Vector2f(-1, 2.5f), new Vector2f(11.5f, 15)),
                new AABB(new Vector2f(-1, -10), new Vector2f(11.5f, 2.5f)),
                new AABB(new Vector2f(11.5f, -10), new Vector2f(24, 2.5f))
        };
        for (int i = 0; i < node.childrenAABB.length; i++) {
            Assert.assertEquals(childrenAABB[i].min, node.childrenAABB[i].min);
            Assert.assertEquals(childrenAABB[i].max, node.childrenAABB[i].max);
        }
    }

    @Test
    public void clearTest() {
        QuadTreeNode parent = new QuadTreeNode(new AABB(), null);
        parent.shapes.add(new Circle(0, 0, 0, 0, 0));

        QuadTreeNode firstChild = new QuadTreeNode(new AABB(), null);
        firstChild.shapes.add(new Circle(0, 0, 0, 0, 0));
        parent.children[0] = firstChild;

        QuadTreeNode secondChild = new QuadTreeNode(new AABB(), null);
        secondChild.shapes.add(new Circle(0, 0, 0, 0, 0));
        parent.children[1] = secondChild;

        QuadTreeNode grandson = new QuadTreeNode(new AABB(), null);
        grandson.shapes.add(new Circle(0, 0, 0, 0, 0));
        secondChild.children[3] = grandson;

        parent.clear();

        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, parent.children);
        Assert.assertTrue(parent.shapes.isEmpty());

        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, firstChild.children);
        Assert.assertTrue(firstChild.shapes.isEmpty());

        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, secondChild.children);
        Assert.assertTrue(secondChild.shapes.isEmpty());

        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, grandson.children);
        Assert.assertTrue(grandson.shapes.isEmpty());
    }

    @Test
    public void isEmptyTest() {
        QuadTreeNode node = new QuadTreeNode(new AABB(), null);
        Assert.assertTrue(node.isEmpty());

        node.shapes.add(new Circle(0, 0, 0, 0, 0));
        Assert.assertFalse(node.isEmpty());

        node = new QuadTreeNode(new AABB(), null);
        node.children[0] = new QuadTreeNode(new AABB(), null);
        Assert.assertFalse(node.isEmpty());
    }

    @Test
    public void insertDownNotIntersectedTest() {
        QuadTreeNode node = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);

        IShape shape = new Circle(1, 6 + MathPIE.EPSILON, 0, 0, 0);
        node.insertDown(shape);
        Assert.assertTrue(node.isEmpty());

        shape = new Circle(1, 0, 11 + MathPIE.EPSILON, 0, 0);
        node.insertDown(shape);
        Assert.assertTrue(node.isEmpty());

        shape = new Circle(1, -11 - MathPIE.EPSILON, 0, 0, 0);
        node.insertDown(shape);
        Assert.assertTrue(node.isEmpty());

        shape = new Circle(1, 0, -6 - MathPIE.EPSILON, 0, 0);
        node.insertDown(shape);
        Assert.assertTrue(node.isEmpty());
    }

    @Test
    public void insertDownIntersectedTest() {
        QuadTreeNode node = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);

        IShape shape = new Circle(1, 6 - MathPIE.EPSILON, 0, 0, 0);
        node.insertDown(shape);
        Assert.assertEquals(1, node.shapes.size());
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children);
        node.clear();

        shape = new Circle(1, 0, 11 - MathPIE.EPSILON, 0, 0);
        node.insertDown(shape);
        Assert.assertEquals(1, node.shapes.size());
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children);
        node.clear();

        shape = new Circle(1, -11 + MathPIE.EPSILON, 0, 0, 0);
        node.insertDown(shape);
        Assert.assertEquals(1, node.shapes.size());
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children);
        node.clear();

        shape = new Circle(1, 0, -6 + MathPIE.EPSILON, 0, 0);
        node.insertDown(shape);
        Assert.assertEquals(1, node.shapes.size());
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children);
        node.clear();
    }

    @Test
    public void insertDownContainedTest() {
        QuadTreeNode node = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);

        // All quarters
        IShape shape = new Circle(6, 2.5f, -2.5f, 0, 0);
        node.insertDown(shape);
        Assert.assertEquals(1, node.shapes.size());
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children);
        node.clear();

        // First quarter
        shape = new Circle(1, 3f, 5f, 0, 0);
        node.insertDown(shape);
        Assert.assertEquals(1, node.shapes.size());
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children);
        node.clear();

        // Second quarter
        shape = new Circle(1, -5f, 5f, 0, 0);
        node.insertDown(shape);
        Assert.assertEquals(1, node.shapes.size());
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children);
        node.clear();

        // Third quarter
        shape = new Circle(1, -5f, -3f, 0, 0);
        node.insertDown(shape);
        Assert.assertEquals(1, node.shapes.size());
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children);
        node.clear();

        // Fourth quarter
        shape = new Circle(1, 3f, -3f, 0, 0);
        node.insertDown(shape);
        Assert.assertEquals(1, node.shapes.size());
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children);
        node.clear();
    }

    @Test
    public void insertDownShapeListTest() {
        QuadTreeNode node = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);

        List<IShape> shapes = new ArrayList<>();
        shapes.add(new Circle(6, 2.5f, -2.5f, 0, 0));
        shapes.add(new Circle(1, 6 + MathPIE.EPSILON, 0, 0, 0));
        shapes.add(new Circle(6, 2.5f, -2.5f, 0, 0));
        node.insertDown(shapes);

        Assert.assertEquals(2, node.shapes.size());
        Assert.assertEquals(shapes.get(0), node.shapes.get(0));
        Assert.assertEquals(shapes.get(2), node.shapes.get(1));
    }

    @Test
    public void sameShapesInQuadTreeTest() {
        QuadTreeNode node = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);
        IShape shape = new Circle(6, 7.5f, 7.5f, 0, 0);
        node.insertDown(shape);
        node.insertDown(shape);
        node.insertDown(shape);
        Assert.assertEquals(3, node.shapes.size());
        Assert.assertTrue(
                node.shapes.get(0) == node.shapes.get(1)
                && node.shapes.get(1) == node.shapes.get(2)
                && node.shapes.get(0) == node.shapes.get(2));
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children);
    }

    @Test
    public void maxShapesContainsInOneNodeTest() {
        QuadTreeNode node = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);

        int maxShapesInOneNode = 2;
        node.setMaxShapesInLeaf(maxShapesInOneNode);
        IShape[] shapes = new IShape[maxShapesInOneNode + 1];
        for (int i = 0; i < maxShapesInOneNode + 1; i++) {
            shapes[i] = new Circle(6, 2.5f, -2.5f, 0, 0);
            node.insertDown(shapes[i]);
        }

        Assert.assertEquals(maxShapesInOneNode, node.shapes.size());

        IShape lastShape = shapes[maxShapesInOneNode];
        for (int i = 0; i < node.children.length; i++) {
            Assert.assertNotNull(node.children[i]);
            Assert.assertEquals(1, node.children[i].shapes.size());
            Assert.assertSame(lastShape, node.children[i].shapes.get(0));
            Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children[i].children);
        }

        for (int i = 0; i < maxShapesInOneNode; i++) {
            Assert.assertSame(shapes[i], node.shapes.get(i));
        }
    }

    @Test
    public void maxShapesIntersectsInOneNodeTest() {
        QuadTreeNode node = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);

        int maxShapesInOneNode = 2;
        node.setMaxShapesInLeaf(maxShapesInOneNode);
        int shapesAmount = maxShapesInOneNode + 1;
        IShape[] shapes = new IShape[shapesAmount];
        for (int i = 0; i < shapesAmount; i++) {
            shapes[i] = new Circle(1, 6 - MathPIE.EPSILON, 0, 0, 0);
            node.insertDown(shapes[i]);
        }

        Assert.assertEquals(maxShapesInOneNode, node.shapes.size());

        Assert.assertNull(node.children[0]);
        Assert.assertNull(node.children[1]);
        Assert.assertNull(node.children[2]);
        Assert.assertNotNull(node.children[3]);

        Assert.assertEquals(1, node.children[3].shapes.size());
        Assert.assertSame(shapes[shapesAmount - 1], node.children[3].shapes.get(0));
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children[3].children);

        for (int i = 0; i < maxShapesInOneNode; i++) {
            Assert.assertSame(shapes[i], node.shapes.get(i));
        }
    }

    @Test
    public void maxShapesIntersectsInOneNodeDoubleNestingTest() {
        QuadTreeNode node = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);

        int maxShapesInOneNode = 2;
        node.setMaxShapesInLeaf(maxShapesInOneNode);
        int shapesAmount = 2 * maxShapesInOneNode + 1;
        IShape[] shapes = new IShape[shapesAmount];
        for (int i = 0; i < shapesAmount; i++) {
            shapes[i] = new Circle(1, 6 - MathPIE.EPSILON, 0, 0, 0);
            node.insertDown(shapes[i]);
        }

        Assert.assertEquals(maxShapesInOneNode, node.shapes.size());

        Assert.assertNull(node.children[0]);
        Assert.assertNull(node.children[1]);
        Assert.assertNull(node.children[2]);
        Assert.assertNotNull(node.children[3]);

        Assert.assertEquals(maxShapesInOneNode, node.children[3].shapes.size());

        Assert.assertNotNull(node.children[3].children[0]);
        Assert.assertNull(node.children[3].children[1]);
        Assert.assertNull(node.children[3].children[2]);
        Assert.assertNull(node.children[3].children[3]);

        Assert.assertEquals(1, node.children[3].children[0].shapes.size());
        Assert.assertSame(shapes[shapesAmount - 1], node.children[3].children[0].shapes.get(0));
    }

    @Test
    public void minBoundingBoxTest() {
        AABB boundingBox = new AABB(new Vector2f(0, 0),
                new Vector2f(2 * QuadTreeNode.DEFAULT_MIN_BOUNDING_BOX_SIZE - MathPIE.EPSILON, 2 * QuadTreeNode.DEFAULT_MIN_BOUNDING_BOX_SIZE - MathPIE.EPSILON));
        QuadTreeNode node = new QuadTreeNode(boundingBox, null);
        IShape[] shapes = new IShape[QuadTreeNode.DEFAULT_MAX_SHAPES_IN_ONE_NODE + 1];
        for (int i = 0; i < QuadTreeNode.DEFAULT_MAX_SHAPES_IN_ONE_NODE + 1; i++) {
            shapes[i] = new Circle(6, 2.5f, -2.5f, 0, 0);
            node.insertDown(shapes[i]);
        }
        Assert.assertEquals(QuadTreeNode.DEFAULT_MAX_SHAPES_IN_ONE_NODE + 1, node.shapes.size());
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, node.children);


        boundingBox = new AABB(new Vector2f(0, 0),
                new Vector2f(2 * QuadTreeNode.DEFAULT_MIN_BOUNDING_BOX_SIZE + MathPIE.EPSILON, 2 * QuadTreeNode.DEFAULT_MIN_BOUNDING_BOX_SIZE + MathPIE.EPSILON));
        node = new QuadTreeNode(boundingBox, null);
        shapes = new IShape[QuadTreeNode.DEFAULT_MAX_SHAPES_IN_ONE_NODE + 1];
        for (int i = 0; i < QuadTreeNode.DEFAULT_MAX_SHAPES_IN_ONE_NODE + 1; i++) {
            shapes[i] = new Circle(6, 2.5f, -2.5f, 0, 0);
            node.insertDown(shapes[i]);
        }
        Assert.assertEquals(QuadTreeNode.DEFAULT_MAX_SHAPES_IN_ONE_NODE, node.shapes.size());
        for (QuadTreeNode child : node.children) {
            Assert.assertEquals(1, child.shapes.size());
        }
    }

    @Test
    public void insertUpTest() {
        QuadTreeNode parent = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);
        IShape shape = new Circle(1, -11 - MathPIE.EPSILON, 0, 0, 0);
        parent.insertUp(shape);
        Assert.assertTrue(parent.isEmpty());

        QuadTreeNode child = new QuadTreeNode(new AABB(new Vector2f(0, 0), new Vector2f(5, 5)), parent);
        shape = new Circle(1, 0, 11 - MathPIE.EPSILON, 0, 0);
        child.insertUp(shape);
        Assert.assertTrue(child.isEmpty());
        Assert.assertFalse(parent.isEmpty());

        parent.setMaxShapesInLeaf(1);
        parent.clear();

        parent.insertDown(shape);
        child.insertUp(shape);
        Assert.assertEquals(1, parent.shapes.size());
        Assert.assertNotNull(parent.children[0]);
        Assert.assertEquals(1, parent.children[0].shapes.size());
        Assert.assertTrue(child.isEmpty());
    }

    @Test
    public void setMaxShapesInOneNodeTest() {
        QuadTreeNode parent = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);
        QuadTreeNode child = new QuadTreeNode(new AABB(new Vector2f(-2.5f, 2.5f), new Vector2f(5, 10)), parent);
        parent.children[2] = child;

        int value = -100;
        parent.setMaxShapesInLeaf(value);
        Assert.assertEquals(value, parent.getMaxShapesInLeaf());
        Assert.assertEquals(value, child.getMaxShapesInLeaf());
    }

    @Test
    public void setMinBoundingBoxSizeTest() {
        QuadTreeNode parent = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);
        QuadTreeNode child = new QuadTreeNode(new AABB(new Vector2f(-2.5f, 2.5f), new Vector2f(5, 10)), parent);
        parent.children[3] = child;

        int value = -100;
        parent.setMinBoundingBoxSize(value);
        Assert.assertEquals(value, parent.getMinBoundingBoxSize(), PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(value, child.getMinBoundingBoxSize(), PIETest.FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void updateTest() {
        QuadTreeNode parent = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);
        QuadTreeNode child = new QuadTreeNode(new AABB(new Vector2f(-2.5f, 2.5f), new Vector2f(5, 10)), parent);
        parent.children[0] = child;
        IShape shape = new Circle(1, 0, -6 + MathPIE.EPSILON, 0, 0);

        child.shapes.add(shape);
        child.update();
        Assert.assertTrue(child.isEmpty());
        Assert.assertEquals(1, parent.shapes.size());

        parent.update();
        Assert.assertEquals(1, parent.shapes.size());
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, parent.children);

        parent.clear();
        child.shapes.add(shape);
        parent.children[0] = child;
        parent.update();
        Assert.assertTrue(child.isEmpty());
        Assert.assertEquals(1, parent.shapes.size());
        Assert.assertArrayEquals(QUAD_TREE_NODE_NULL_ARRAY, parent.children);
    }

    @Test
    public void calculateAabbCollisionBetweenParentAndChildTest() {
        QuadTreeNode parent = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);
        parent.setMaxShapesInLeaf(2);

        IShape firstShape = Polygon.generateRectangle(-2.5f, 6.25f, 12, 6, 0, 0);
        parent.insertDown(firstShape);
        IShape secondShape = new Circle(0.05f, -9, -4, 0, 0);
        parent.insertDown(secondShape);

        IShape thirdShape = Polygon.generateRectangle(1.25f, 6.25f, 6, 6, 0, 0);
        parent.insertDown(thirdShape);

        Set<ShapePair> result = parent.calculateAllAabbCollision();
        Assert.assertEquals(1, result.size());
        ShapePair pair = new ShapePair(firstShape, thirdShape);
        Assert.assertEquals(pair, result.iterator().next());
    }

    @Test
    public void calculateAabbCollisionInParentTest() {
        QuadTreeNode parent = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);

        IShape firstShape = Polygon.generateRectangle(-2.5f, 6.25f, 12, 6, 0, 0);
        parent.insertDown(firstShape);

        IShape secondShape = Polygon.generateRectangle(1.25f, 6.25f, 6, 6, 0, 0);
        parent.insertDown(secondShape);

        Set<ShapePair> result = parent.calculateAllAabbCollision();
        Assert.assertEquals(1, result.size());
        ShapePair pair = new ShapePair(firstShape, secondShape);
        Assert.assertEquals(pair, result.iterator().next());
    }

    @Test
    public void calculateAabbCollisionInChildTest() {
        QuadTreeNode parent = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);
        parent.setMaxShapesInLeaf(2);

        IShape firstShape = new Circle(0.05f, -9, -4, 0, 0);
        parent.insertDown(firstShape);
        IShape secondShape = new Circle(0.05f, -8, -4, 0, 0);
        parent.insertDown(secondShape);

        IShape thirdShape = Polygon.generateRectangle(-2.5f, 6.25f, 12, 6, 0, 0);
        parent.insertDown(thirdShape);
        IShape fourthShape = Polygon.generateRectangle(1.25f, 6.25f, 6, 6, 0, 0);
        parent.insertDown(fourthShape);

        Set<ShapePair> result = parent.calculateAllAabbCollision();
        Assert.assertEquals(1, result.size());
        ShapePair pair = new ShapePair(thirdShape, fourthShape);
        Assert.assertEquals(pair, result.iterator().next());
    }

    @Test
    public void calculateAabbCollisionBetweenParentAndGrandsonTest() {
        QuadTreeNode parent = new QuadTreeNode(new AABB(new Vector2f(-10, -5), new Vector2f(5, 10)), null);
        parent.setMaxShapesInLeaf(2);

        IShape firstShape = Polygon.generateRectangle(-2, 3, 0.1f, 0.1f, 0, 0);
        parent.insertDown(firstShape);
        IShape shape = new Circle(0.05f, -9, -4, 0, 0);
        parent.insertDown(shape);

        shape = new Circle(0.05f, 3, 9.8f, 0, 0);
        parent.insertDown(shape);
        shape = new Circle(0.05f, 4, 9.8f, 0, 0);
        parent.insertDown(shape);

        IShape fourthShape = Polygon.generateRectangle(-2.05f, 3, 0.1f, 0.1f, 0, 0);
        parent.insertDown(fourthShape);

        Set<ShapePair> result = parent.calculateAllAabbCollision();
        Assert.assertEquals(1, result.size());
        ShapePair pair = new ShapePair(firstShape, fourthShape);
        Assert.assertEquals(pair, result.iterator().next());
    }
}
