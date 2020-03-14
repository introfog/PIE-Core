package com.introfog.pie.benchmark;

import com.introfog.pie.core.Context;
import com.introfog.pie.core.World;
import com.introfog.pie.core.collisions.broadphase.SweepAndPruneMethod;

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