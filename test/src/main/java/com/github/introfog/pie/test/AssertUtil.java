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

import org.junit.Assert;

/**
 * Utilities class for assertion operation.
 */
public class AssertUtil {
    private AssertUtil() {
        // Empty constructor
    }

    /**
     * Asserts that {@link Executor#execute()} method call doesn't produce any
     * {@link Exception} otherwise test will fail by throwing {@link AssertionError}.
     *
     * @param executor the instance of {@link Executor} whose
     * {@link Executor#execute()} method will be checked for exception throwing
     */
    public static void doesNotThrow(Executor executor) {
        try {
            executor.execute();
        } catch (Exception ex) {
            Assert.fail();
        }
    }

    /**
     * Asserts that {@link Executor#execute()} method call doesn't produce any
     * {@link Exception} otherwise test will fail by throwing {@link AssertionError}.
     *
     * @param executor the instance of {@link Executor} whose
     * {@link Executor#execute()} method will be checked for exception throwing
     * @param message the identifying message for the {@link AssertionError} may be null
     */
    public static void doesNotThrow(Executor executor, String message) {
        try {
            executor.execute();
        } catch (Exception ex) {
            Assert.fail(message);
        }
    }
}
