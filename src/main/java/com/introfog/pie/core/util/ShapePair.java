package com.introfog.pie.core.util;

import com.introfog.pie.core.shape.IShape;

import java.util.Objects;

public class ShapePair {
    public IShape first;
    public IShape second;

    public ShapePair(IShape first, IShape second) {
        this.first = first;
        this.second = second;
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
