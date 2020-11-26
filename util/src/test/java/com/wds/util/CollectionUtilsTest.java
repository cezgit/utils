package com.wds.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class CollectionUtilsTest {

    @Test
    void removeDuplicates() {
        List<String> list = asList("george", "michael", "george");
        List<String> uniqueList = CollectionUtils.removeDuplicates(list);
        assertThat(uniqueList.size(), is(2));
        assertThat(uniqueList, hasItems("george", "michael"));
    }

    @Test
    void mergeSets() {
        Set<String> s1 = Set.of("a", "b");
        Set<String> s2 = Set.of("c", "a");
        assertThat(CollectionUtils.mergeSets(s1, s2), is(Set.of("a", "b", "c")));
    }
}