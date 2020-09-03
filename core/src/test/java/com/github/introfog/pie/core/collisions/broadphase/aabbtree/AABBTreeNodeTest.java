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

import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.core.shape.AABB;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AABBTreeNodeTest extends PIETest {
    @Test
    public void aabbConstructorTest() {
        AABB aabb = new AABB();
        aabb.min.set(0, 0);
        aabb.max.set(10, 20);
        AABBTreeNode node = new AABBTreeNode(aabb, 0.5f);

        Assert.assertFalse(node.checked);
        Assert.assertEquals(0.5f, node.enlargedAABBCoefficient, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertSame(aabb, node.aabb);
        Assert.assertNull(node.parent);
        Assert.assertArrayEquals(new AABBTreeNode[2], node.children);
        Assert.assertNull(node.shape);
    }

    @Test
    public void shapeConstructorTest() {
        IShape shape = new Circle(5, 0, 5, MathPIE.STATIC_BODY_DENSITY, 0);
        AABBTreeNode node = new AABBTreeNode(shape, 0.3f);

        Assert.assertFalse(node.checked);
        Assert.assertEquals(0.3f, node.enlargedAABBCoefficient, PIETest.FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(new Vector2f(-8, -3), node.aabb.min);
        Assert.assertEquals(new Vector2f(8, 13), node.aabb.max);
        Assert.assertNull(node.parent);
        Assert.assertArrayEquals(new AABBTreeNode[2], node.children);
        Assert.assertSame(shape, node.shape);
    }

    @Test
    public void enlargedAABBCoefficientInTreeTest() {
        IShape shape = new Circle(5, 0, 5, MathPIE.STATIC_BODY_DENSITY, 0);
        AABBTreeNode root = new AABBTreeNode(shape, 0.3f);

        shape = new Circle(7, 5, 5, MathPIE.STATIC_BODY_DENSITY, 0);
        root = AABBTreeNode.insertLeaf(root, shape);
        Assert.assertEquals(0.3f, root.children[0].enlargedAABBCoefficient, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.3f, root.children[1].enlargedAABBCoefficient, FLOAT_EPSILON_COMPARISON);
    }
}
