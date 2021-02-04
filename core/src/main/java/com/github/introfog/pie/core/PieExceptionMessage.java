package com.github.introfog.pie.core;

public final class PieExceptionMessage {
    public static final String COLLISION_SOLVE_ITERATION_MUST_NOT_BE_NEGATIVE = "The number of collision solve iterations must not be negative.";

    public static final String INVALID_SHAPES_TYPE_FOR_NARROW_PHASE_HANDLER = "Invalid type of shapes for narrow phase handler.";

    public static final String SAME_SHAPES_PASSED_TO_SHAPE_PAIR_CONSTRUCTOR = "Can't create a ShapePair instance with the same shapes.";

    private PieExceptionMessage() {
        // Empty constructor
    }
}
