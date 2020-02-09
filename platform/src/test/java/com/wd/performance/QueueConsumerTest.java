package com.wd.performance;

import com.wd.util.ThreadUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class QueueConsumerTest {

    private static Logger logger = LogManager.getLogger(QueueConsumerTest.class);

    @Test
    void queueAndConsume() {
        Consumer<List> consumer = list -> {
            ThreadUtils.sleep(500);
            logger.info(list);
        };
        Stream<Integer> sourceStream = IntStream.rangeClosed(1,1000).boxed();

        QueueConsumer<Integer> queueConsumer = new QueueConsumer<>(consumer);
        queueConsumer.queueAndConsume(sourceStream);
    }
}