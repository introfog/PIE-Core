package com.introfog.pie.core.util;

import com.introfog.pie.core.shape.IShape;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShapePair {
    @JsonProperty
    public IShape first;
    @JsonProperty
    public IShape second;

    public ShapePair() {}

    public ShapePair(IShape first, IShape second) {
        this.first = first;
        this.second = second;
    }
}
