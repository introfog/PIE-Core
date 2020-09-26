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
package com.github.introfog.pie.core.util;

import com.github.introfog.pie.core.math.MathPIE;
import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.core.shape.IShape;
import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ShapePairTest extends PIETest {
    @Test
    public void fieldStoringTest() {
        IShape c1 = new Circle(10, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 0, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);

        ShapePair shapePair = new ShapePair(c1, c2);
        boolean c1First = c1.hashCode() < c2.hashCode();

        Assert.assertTrue("First shape in pair should be have smaller hash code",
                c1First ? shapePair.first == c1 : shapePair.first == c2);

        shapePair = new ShapePair(c2, c1);
        Assert.assertTrue("First shape in pair should be have smaller hash code",
                c1First ? shapePair.first == c1 : shapePair.first == c2);
    }

    @Test
    public void equalsAndHashCodeItselfTest() {
        IShape c1 = new Circle(10, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 0, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        ShapePair shapePair = new ShapePair(c1, c2);

        Assert.assertTrue(shapePair.equals(shapePair));
        Assert.assertEquals(shapePair.hashCode(), shapePair.hashCode());
    }

    @Test
    public void equalsAndHashCodeToAnotherEqualBodyTest() {
        IShape c1 = new Circle(10, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 0, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        ShapePair first = new ShapePair(c1, c2);
        ShapePair second = new ShapePair(c1, c2);

        PIETest.checkEqualsAndHashCodeMethods(first, second, true);
    }

    @Test
    public void equalsAndHashCodeToAnotherNotEqualBodyTest() {
        IShape c1 = new Circle(10, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 0, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        IShape c3 = new Circle(10, 10, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        ShapePair first = new ShapePair(c1, c2);
        ShapePair second = new ShapePair(c1, c3);

        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = new ShapePair(c3, c2);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);
    }

    @Test
    public void equalsToNullTest() {
        IShape c1 = new Circle(10, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 0, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        ShapePair shapePair = new ShapePair(c1, c2);

        Assert.assertFalse(shapePair.equals(null));
    }

    @Test
    public void equalsToAnotherClassTest() {
        IShape c1 = new Circle(10, 10, 10, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 0, 0, MathPIE.STATIC_BODY_DENSITY, 0.2f);
        ShapePair shapePair = new ShapePair(c1, c2);

        Assert.assertFalse(shapePair.equals(""));
    }
}
