package com.github.introfog.pie.test;

/**
 * This class define a functional interface which executes some code and return nothing.
 */
@FunctionalInterface
public interface Executor {
    /**
     * Executes some code.
     */
    void execute() throws Exception;
}
