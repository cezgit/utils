package com.wds.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayUtilsTest {

    @Test
    void arrayHasMatch() {
        String[] array = new String[] {"george", "michael"};
        assertTrue(ArrayUtils.arrayHasMatch(array, "michael"));
    }

    @Test
    void strContainsAny() {
        String[] array = new String[] {"george", "michael"};
        assertTrue(ArrayUtils.strContainsAny("georges", array));
        assertTrue(ArrayUtils.strContainsAny("george harrison", array));
        assertFalse(ArrayUtils.strContainsAny("harrison", array));
    }
}