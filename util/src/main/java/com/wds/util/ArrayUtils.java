package com.wds.util;

import java.util.Arrays;

public class ArrayUtils {

    public static <T> boolean arrayHasMatch(T[] array, T match) {
        return Arrays.stream(array).anyMatch(match::equals);
    }

    public static boolean strContainsAny(String s, String[] values) {
        return Arrays.stream(values).parallel().anyMatch(s::contains);
    }

}
