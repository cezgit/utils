package com.wds.util;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class CollectionUtils {

    public static Set<String> asLinkedHashSet(String... s) {
        return Sets.newLinkedHashSet((asList(s)));
    }

    public static <T> List<T> removeDuplicates(List<T> list) {
        return list.stream().distinct().collect(Collectors.toList());
    }

    public static Collector<String, ?, Set> toLinkedHashSetCollection() {
        return Collectors.toCollection(LinkedHashSet::new);
    }

    public static Map<String, Integer> sortByKey(Map<String, Integer> m) {
        Map<String, Integer> sortedMap = m.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return sortedMap;
    }

    public static void sortByHighestTokenCountFirst(List<String> ls) {
        Collections.sort(ls, Comparator.comparing(a -> Long.valueOf(countTokens(a))));
        Collections.reverse(ls);
    }

    public static Set<String> getFirst(Set<String> ss, int max) {
        return ss.stream().limit(max).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static int countTokens(String s) {
        return StringUtils.isBlank(s) ? 0 : s.split(" ").length;
    }

    public static String shortestWord(List<String> set) {
        return Collections.min(set, Comparator.comparing(String::length));
    }
}
