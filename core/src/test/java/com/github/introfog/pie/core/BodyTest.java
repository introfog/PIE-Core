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
package com.github.introfog.pie.core;

import com.github.introfog.pie.test.PIETest;
import com.github.introfog.pie.test.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BodyTest extends PIETest {
    @Test
    public void paramConstructorTest() {
        Body body = new Body(0.1f, 0.2f, 0.3f, 0.4f);

        Assert.assertEquals(0.1f, body.position.x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.2f, body.position.y, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.3f, body.density, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.4f, body.restitution, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.5f, body.staticFriction, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0.3f, body.dynamicFriction, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0, body.torque, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0, body.force.x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0, body.force.y, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0, body.velocity.x, FLOAT_EPSILON_COMPARISON);
        Assert.assertEquals(0, body.velocity.y, FLOAT_EPSILON_COMPARISON);
    }

    @Test
    public void equalsAndHashCodeItselfTest() {
        Body body = new Body(0.1f, 0.2f, 0.3f, 0.4f);

        Assert.assertTrue(body.equals(body));
        Assert.assertEquals(body.hashCode(), body.hashCode());
    }

    @Test
    public void equalsAndHashCodeToAnotherEqualBodyTest() {
        Body first = new Body(0.1f, 0.2f, 0.3f, 0.4f);
        Body second = new Body(0.1f, 0.2f, 0.3f, 0.4f);

        PIETest.checkEqualsAndHashCodeMethods(first, second, true);
    }

    @Test
    public void equalsAndHashCodeToAnotherNotEqualBodyTest() {
        Body first = new Body(0.1f, 0.2f, 0.3f, 0.4f);
        Body second = new Body(0.2f, 0.2f, 0.3f, 0.4f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = new Body(0.1f, 0.3f, 0.3f, 0.4f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = new Body(0.1f, 0.2f, 0.4f, 0.4f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);

        second = new Body(0.1f, 0.2f, 0.3f, 0.5f);
        PIETest.checkEqualsAndHashCodeMethods(first, second, false);
    }

    @Test
    public void equalsToNullTest() {
        Body body = new Body(0.1f, 0.2f, 0.3f, 0.4f);

        Assert.assertFalse(body.equals(null));
    }

    @Test
    public void equalsToAnotherClassTest() {
        Body body = new Body(0.1f, 0.2f, 0.3f, 0.4f);

        Assert.assertFalse(body.equals(""));
    }

    @Test
    public void toStringTest() {
        Body body = new Body(0.1634345f, 0.2f, 0.3f, 0.4f);
        Assert.assertEquals("{position={0.1634345; 0.2}; density=0.3; restitution=0.4}", body.toString());
    }
}
