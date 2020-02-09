package com.wds.util;

import org.junit.jupiter.api.Test;

import java.util.List;

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
}