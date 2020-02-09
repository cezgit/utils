package com.wds.util;

import java.util.stream.Stream;

import static com.wds.util.BooleanUtils.Status.STATUS_GREEN;
import static com.wds.util.BooleanUtils.Status.STATUS_RED;

public class BooleanUtils {

    public static Status getCombinedStatuses(Boolean... statuses) {
        return Stream.of(statuses).allMatch(status -> status == true) ? STATUS_GREEN : STATUS_RED;
    }

    enum Status {
        STATUS_GREEN,
        STATUS_RED
    }

}
