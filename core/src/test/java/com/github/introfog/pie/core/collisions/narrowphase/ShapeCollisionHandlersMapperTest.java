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
package com.github.introfog.pie.core.collisions.narrowphase;

import com.github.introfog.pie.core.collisions.narrowphase.impl.CircleCircleCollisionHandler;
import com.github.introfog.pie.core.collisions.narrowphase.impl.CirclePolygonCollisionHandler;
import com.github.introfog.pie.core.collisions.narrowphase.impl.PolygonPolygonCollisionHandler;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.Polygon;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ShapeCollisionHandlersMapperTest {
    @Test
    public void defaultConstructorTest() {
        ShapeCollisionHandlersMapper mapper = new ShapeCollisionHandlersMapper();
        Assert.assertNull(mapper.getMapping(Circle.class, Circle.class));
        Assert.assertNull(mapper.getMapping(Circle.class, Polygon.class));
        Assert.assertNull(mapper.getMapping(Polygon.class, Circle.class));
        Assert.assertNull(mapper.getMapping(Polygon.class, Polygon.class));
    }

    @Test
    public void copyConstructorTest() {
        ShapeCollisionHandlersMapper mapper = new ShapeCollisionHandlersMapper();
        IShapeCollisionHandler handler = new CircleCircleCollisionHandler();
        mapper.putMapping(Circle.class, Polygon.class, handler);

        ShapeCollisionHandlersMapper copy = new ShapeCollisionHandlersMapper(mapper);
        Assert.assertSame(handler, copy.getMapping(Circle.class, Polygon.class));

        Assert.assertNull(mapper.getMapping(Circle.class, Circle.class));
        Assert.assertNull(mapper.getMapping(Polygon.class, Circle.class));
        Assert.assertNull(mapper.getMapping(Polygon.class, Polygon.class));
    }

    @Test
    public void defaultMappingTest() {
        ShapeCollisionHandlersMapper mapper = ShapeCollisionHandlersMapper.createAndGetDefaultMapping();
        Assert.assertEquals(CircleCircleCollisionHandler.class, mapper.getMapping(Circle.class, Circle.class).getClass());
        Assert.assertEquals(CirclePolygonCollisionHandler.class, mapper.getMapping(Circle.class, Polygon.class).getClass());
        Assert.assertEquals(CirclePolygonCollisionHandler.class, mapper.getMapping(Polygon.class, Circle.class).getClass());
        Assert.assertEquals(PolygonPolygonCollisionHandler.class, mapper.getMapping(Polygon.class, Polygon.class).getClass());
    }
}
