package com.wds.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompareUtilsTest {

    @Test
    public void levenshteinDistance() {
        int perc = CompareUtils.levenshteinDistance("MC SAR!", "MC SAR");
        assertEquals(85, perc);

        perc = CompareUtils.levenshteinDistance("BIGGER", "BIG");
        assertEquals(50, perc);

        perc = CompareUtils.levenshteinDistance("BIG ONE", "BIG");
        assertEquals(42, perc);

        perc = CompareUtils.levenshteinDistance("Another Brick In The Wall [Us and Them - Symphonic Pink Floyd]", "ANOTHER BRICK IN THE WALL");
        assertEquals(14, perc);

        perc = CompareUtils.levenshteinDistance("SHAKY BONNIE", "SHAKIN' STEVENS BONNIE TYLER");
        assertEquals(39, perc);
    }

    @Test
    public void longestSubstring() {
        assertEquals("JUAN MARTIN",
                CompareUtils.longestSubstring("JUAN MARTIN", "JUAN MARTIN ROYAL PHILHARMONIC ORCHESTRA MANCINI LOUIS CLARK"));

        assertEquals("TAKE ME OUT",
                CompareUtils.longestSubstring("TAKE ME OUT", "TAKE ME OUTSIDE"));
    }
}
