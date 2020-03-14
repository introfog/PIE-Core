package com.introfog.pie.core.util;

import com.introfog.pie.core.shape.IShape;

import java.util.Objects;

/**
 * Utility class for storing a pair of shapes. Used to store pairs of shapes that possibly collide or collide.
 */
public class ShapePair {
    /**
     * The shape of the pair with the smaller hash code.
     */
    public IShape first;

    /**
     * The shape of the pair with the bigger hash code.
     */
    public IShape second;

    /**
     * Instantiates a new {@link ShapePair} instance based on two {@link IShape} objects.
     *
     * The {@link #first} variable always stores a shape with a smaller hash code, this is necessary for
     * the comparison of a pair of shapes and calculation of the hash code of the pair independent
     * of collision search method (that is, which shape is the first and which is the second).
     *
     * @param first the shape
     * @param second the shape
     */
    public ShapePair(IShape first, IShape second) {
        if (first.hashCode() < second.hashCode()) {
            this.first = first;
            this.second = second;
        } else {
            this.first = second;
            this.second = first;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShapePair shapePair = (ShapePair) o;
        return first.equals(shapePair.first) &&
                second.equals(shapePair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
