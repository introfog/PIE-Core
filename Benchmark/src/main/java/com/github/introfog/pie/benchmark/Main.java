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
package com.github.introfog.pie.benchmark;

import com.github.introfog.pie.core.Context;
import com.github.introfog.pie.core.World;
import com.github.introfog.pie.core.collisions.broadphase.SweepAndPruneMethod;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Main {
    public static final boolean ENABLE_DEBUG_DRAW = false;
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 700;
    public static final float SHAPES_OFFSET_X = 100f;
    public static final float SHAPES_OFFSET_Y = 100f;

    public static World world;

    public static void main(String[] args) throws IOException {
        world = new World(new Context().setBroadPhase(new SweepAndPruneMethod()));

        JFrame frame = new JFrame("PIE test viewer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.add(new Display());
        frame.setVisible(true);
    }
}