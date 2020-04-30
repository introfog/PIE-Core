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

/**
 * This is a generic class for testing. The class contains
 * general methods that may be needed during testing.
 */
public class PIETest {
    /** The constant for comparing double variables for equality. */
    public static final double DOUBLE_EPSILON_COMPARISON = 1E-12;

    /** The constant for comparing float variables for equality. */
    public static final float FLOAT_EPSILON_COMPARISON = 1E-6f;

    /**
     * Creates a folder with a given path, including all necessary nonexistent parent directories.
     * If a folder is already present, no action is performed.
     * @param path the path of the folder to create
     */
    public static void createDestinationFolder(String path) {
        File filePath = new File(path);
        filePath.mkdirs();
    }
}
