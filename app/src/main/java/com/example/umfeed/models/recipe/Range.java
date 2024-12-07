package com.example.umfeed.models.recipe;

public class Range {
    private float min;
    private float max;

    public Range(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public float getMin() { return min; }
    public float getMax() { return max; }
}
