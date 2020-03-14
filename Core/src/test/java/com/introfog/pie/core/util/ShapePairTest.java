package com.introfog.pie.core.util;

import com.introfog.pie.core.math.MathPIE;
import com.introfog.pie.core.shape.Circle;
import com.introfog.pie.core.shape.IShape;

import org.junit.Assert;
import org.junit.Test;

public class ShapePairTest {
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
    public void hashCodeTest() {

    }
}
