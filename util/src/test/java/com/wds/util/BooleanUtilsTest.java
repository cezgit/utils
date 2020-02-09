package com.wds.util;

import org.junit.jupiter.api.Test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class BooleanUtilsTest {

    @Test
    void getCombinedStatuses() {
        assertThat(BooleanUtils.Status.STATUS_GREEN, is(BooleanUtils.getCombinedStatuses(true, true)));
        assertThat(BooleanUtils.Status.STATUS_RED, is(BooleanUtils.getCombinedStatuses(true, false)));
    }
}