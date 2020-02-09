package com.wds.util;

import java.util.Collections;
import java.util.List;

public class SearchUtils {

    private static int NOT_IN_LIST = -1;

    // return the closest low bound item in the list, closest from value
    public static int closestValue (List<Integer> list, int value) {


        int index = Collections.binarySearch(list,value); // see binarySearch method info
        if(index < 0) {
            // bitwise complement of a value; inverts the bits ie a 0-bit becomes 1-bit and vice versa: ~x equals (-x)-1 so ~-5 becomes 5-1=4
            index = ~index-1;
        }
        return index >= 0 ? list.get(index) : NOT_IN_LIST;
    }
}
