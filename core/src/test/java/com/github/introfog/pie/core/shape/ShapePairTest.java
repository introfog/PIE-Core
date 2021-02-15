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

import com.github.introfog.pie.core.PieExceptionMessage;
import com.github.introfog.pie.core.math.MathPie;
import com.github.introfog.pie.test.PieTest;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ShapePairTest extends PieTest {
    @Test
    public void tryToCreateShapePairWithSameShapesTest() {
        IShape c1 = new Circle(0, 0, 0, 0, 0);
        Assert.assertThrows(PieExceptionMessage.SAME_SHAPES_PASSED_TO_SHAPE_PAIR_CONSTRUCTOR,
                IllegalArgumentException.class, () -> new ShapePair(c1, c1));
    }

    @Test
    public void fieldStoringTest() {
        IShape c1 = new Circle(10, 10, 10, MathPie.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0.2f);

        ShapePair shapePair = new ShapePair(c1, c2);
        boolean c1First = c1.hashCode() < c2.hashCode();

        Assert.assertTrue("First shape in pair should be have smaller hash code",
                c1First ? shapePair.getFirst() == c1 : shapePair.getFirst() == c2);
        Assert.assertSame(c2, c1First ? shapePair.getSecond() : shapePair.getFirst());

        shapePair = new ShapePair(c2, c1);
        Assert.assertTrue("First shape in pair should be have smaller hash code",
                c1First ? shapePair.getFirst() == c1 : shapePair.getFirst() == c2);
        Assert.assertSame(c2, c1First ? shapePair.getSecond() : shapePair.getFirst());
    }

    @Test
    public void equalsAndHashCodeItselfTest() {
        IShape c1 = new Circle(10, 10, 10, MathPie.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0.2f);
        ShapePair shapePair = new ShapePair(c1, c2);

        Assert.assertTrue(shapePair.equals(shapePair));
        Assert.assertEquals(shapePair.hashCode(), shapePair.hashCode());
    }

    @Test
    public void equalsAndHashCodeToAnotherEqualBodyTest() {
        IShape c1 = new Circle(10, 10, 10, MathPie.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0.2f);
        ShapePair first = new ShapePair(c1, c2);
        ShapePair second = new ShapePair(c1, c2);

        PieTest.checkEqualsAndHashCodeMethods(first, second, true);
    }

    @Test
    public void equalsAndHashCodeToAnotherNotEqualBodyTest() {
        IShape c1 = new Circle(10, 10, 10, MathPie.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0.2f);
        IShape c3 = new Circle(10, 10, 0, MathPie.STATIC_BODY_DENSITY, 0.2f);
        ShapePair first = new ShapePair(c1, c2);
        ShapePair second = new ShapePair(c1, c3);

        PieTest.checkEqualsAndHashCodeMethods(first, second, false);

        second = new ShapePair(c3, c2);
        PieTest.checkEqualsAndHashCodeMethods(first, second, false);
    }

    @Test
    public void equalsToNullTest() {
        IShape c1 = new Circle(10, 10, 10, MathPie.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0.2f);
        ShapePair shapePair = new ShapePair(c1, c2);

        Assert.assertFalse(shapePair.equals(null));
    }

    @Test
    public void equalsToAnotherClassTest() {
        IShape c1 = new Circle(10, 10, 10, MathPie.STATIC_BODY_DENSITY, 0.2f);
        IShape c2 = new Circle(10, 0, 0, MathPie.STATIC_BODY_DENSITY, 0.2f);
        ShapePair shapePair = new ShapePair(c1, c2);

        Assert.assertFalse(shapePair.equals(""));
    }
}
