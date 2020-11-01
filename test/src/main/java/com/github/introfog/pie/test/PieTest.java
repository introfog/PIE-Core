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
package com.github.introfog.pie.test;

import java.io.File;

import org.junit.Assert;

/**
 * This is a generic class for testing. The class contains
 * general methods that may be needed during testing.
 */
public abstract class PieTest {
    /** The constant for comparing float variables for equality. */
    public static final float FLOAT_EPSILON_COMPARISON = 1E-6f;

    /**
     * Creates a folder with a given path, including all necessary nonexistent parent directories.
     * If a folder is already present, no action is performed.
     *
     * @param path the path of the folder to create
     */
    public static void createDestinationFolder(String path) {
        File filePath = new File(path);
        filePath.mkdirs();
    }

    /**
     * If checkEqual is true, then the method checks that the objects are symmetrically equal and
     * their hash codes are equal, otherwise it checks that the objects are symmetrically not
     * equal and their hash codes are not equal.
     *
     * @param a the first object
     * @param b the second object
     * @param checkEqual the boolean flag
     */
    public static void checkEqualsAndHashCodeMethods(Object a, Object b, boolean checkEqual) {
        boolean result;
        if (checkEqual) {
            result = a.equals(b);
            Assert.assertTrue(result);
            result = b.equals(a);
            Assert.assertTrue(result);
            Assert.assertEquals(a.hashCode(), b.hashCode());
        } else {
            result = a.equals(b);
            Assert.assertFalse(result);
            result = b.equals(a);
            Assert.assertFalse(result);
            Assert.assertNotEquals(a.hashCode(), b.hashCode());
        }
    }
}
