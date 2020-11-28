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

    public static <T> Set<T> mergeSets(Set<T> s1, Set<T> s2) {
        Set<T> mergedSet = s1.stream().collect(Collectors.toSet());
        mergedSet.addAll(s2);
        return mergedSet;
    }

    public static <T> Set<T> differenceBetweenSets(final Set<T> setOne, final Set<T> setTwo) {
        Set<T> result = new HashSet<>(setOne);
        result.removeIf(setTwo::contains);
        return result;
    }

    /**
     * get a map's key by value
     * @param map
     * @param value
     * @param <K>
     * @param <V>
     * @return the key corresponding to the value arg
     */
    public static <K, V> Optional<K> getMapKey(Map<K, V> map, V value) {
        return map.entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
