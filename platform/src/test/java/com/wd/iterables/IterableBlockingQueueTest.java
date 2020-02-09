package com.wd.iterables;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.wd.util.ThreadUtils.sleep;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class IterableBlockingQueueTest {

    @Test
    public void theIteratorWillWaitWhenItsEmptyAndNotDone() throws InterruptedException {

        final IterableBlockingQueue<Integer> queue = new IterableBlockingQueue<>();
        final int messagesToSend = 5;
        final List<Integer> messages = IntStream.range(0, messagesToSend)
                .mapToObj(i -> i).collect(Collectors.toList());

        final CountDownLatch producerFinished = new CountDownLatch(1);

        final Runnable slowProducer = () -> {
            try {
                sleep(10);
                messages.forEach(msg -> {
                    queue.add(msg);
                    sleep(2);
                });
                sleep(10);
            } finally {
                producerFinished.countDown();
            }
        };

        final List<Integer> captured = new ArrayList<>();
        final CountDownLatch consumerFinished = new CountDownLatch(messagesToSend);

        final AtomicBoolean exited = new AtomicBoolean(false);
        final Runnable fastConsumer = () -> {
            queue.forEach(msg -> {
                try {
                    captured.add(msg);
                } finally {
                    consumerFinished.countDown();
                }
            });
            exited.set(true);
        };

        final ExecutorService exec = Executors.newCachedThreadPool();
        exec.submit(slowProducer);
        exec.submit(fastConsumer);

        producerFinished.await();
        consumerFinished.await(10, TimeUnit.SECONDS);
        exec.shutdownNow();

        // consumer should not exit iteration"
        assertThat(exited.get()).isFalse();

        // consumer should receive all messages
        assertThat(messages).isEqualTo(captured);
    }
}