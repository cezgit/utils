package com.wds.util;


import com.google.common.collect.Lists;
import org.javatuples.Pair;

import java.util.List;

public class ListUtils {


    public Pair<Integer, Integer> maxTwo(List<Integer> list) {
        HighestPair highestPair = new HighestPair();
        list.forEach(highestPair::evaluate);
        return highestPair.get();
    }

    public String[] listToArray(List<String> list) {
        return list.stream().toArray(String[]::new);
    }

    /**
     * https://www.baeldung.com/java-list-split
     * use Guava
     * @param list
     * @param buckets
     * @return
     */
    public List<List<Object>> partition(List list, int buckets) {
        return Lists.partition(list, buckets);
    }
}
