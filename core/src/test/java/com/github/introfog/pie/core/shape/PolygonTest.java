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
package com.github.introfog.pie.core.shape;

import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.math.Vector2f;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PolygonTest {
    @Test
    public void staticGenerateRectangleTest() {
        Polygon rectangle = Polygon.generateRectangle(0, 0, 15, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        Assert.assertEquals(4, rectangle.vertexCount);
        Assert.assertEquals(4, rectangle.normals.length);
        Assert.assertEquals(4, rectangle.vertices.length);

        Vector2f vec = new Vector2f(7.5f, -5.0f);
        Assert.assertEquals(vec, rectangle.vertices[0]);
        vec.set(7.5f, 5.0f);
        Assert.assertEquals(vec, rectangle.vertices[1]);
        vec.set(-7.5f, 5.0f);
        Assert.assertEquals(vec, rectangle.vertices[2]);
        vec.set(-7.5f, -5.0f);
        Assert.assertEquals(vec, rectangle.vertices[3]);

        vec.set(1, -0.0f);
        Assert.assertEquals(vec, rectangle.normals[0]);
        vec.set(0, 1);
        Assert.assertEquals(vec, rectangle.normals[1]);
        vec.set(-1, -0.0f);
        Assert.assertEquals(vec, rectangle.normals[2]);
        vec.set(0, -1);
        Assert.assertEquals(vec, rectangle.normals[3]);

        vec.set(-7.5f, -5.0f);
        Assert.assertEquals(vec, rectangle.aabb.min);
        vec.set(7.5f, 5.0f);
        Assert.assertEquals(vec, rectangle.aabb.max);

        System.out.println();
    }
}
