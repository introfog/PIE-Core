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
package com.github.introfog.pie.core.collisions.broadphase;

import com.github.introfog.pie.core.shape.Circle;
import com.github.introfog.pie.test.PieTest;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BruteForceMethodTest extends AbstractBroadPhaseTest {
    @Override
    protected AbstractBroadPhase getBroadPhaseMethod() {
        return new BruteForceMethod();
    }

    @Test
    public void newInstanceTest() {
        BruteForceMethod method = (BruteForceMethod) getBroadPhaseMethod();
        method.addShape(new Circle(0, 0, 0, 0, 0));
        BruteForceMethod clone = method.newInstance();
        Assert.assertNotSame(method, clone);
        Assert.assertNotSame(method.shapes, clone.shapes);
        Assert.assertEquals(method.shapes, clone.shapes);
    }
}
