package com.wds.util;

import org.javatuples.Pair;

public class HighestPair {

    Integer highValue = Integer.MIN_VALUE;
    Integer lowValue = Integer.MIN_VALUE;

    private boolean between(int i, int minValueInclusive, int maxValueExclusive) {
        return (i >= minValueInclusive && i < maxValueExclusive);
    }

    public Pair<Integer, Integer> get() {
        return Pair.with(lowValue, highValue);
    }

    public void evaluate(Integer val) {
        if(val > highValue)
            highValue = val;
        else if(between(val, lowValue, highValue))
            lowValue = val;
    }
}