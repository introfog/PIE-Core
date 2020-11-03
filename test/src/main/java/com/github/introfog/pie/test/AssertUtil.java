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
