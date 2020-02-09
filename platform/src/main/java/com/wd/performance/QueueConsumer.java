package com.wd.performance;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.wd.iterables.IterableBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class QueueConsumer<T> {

    private Consumer<List> consumer;
    final IterableBlockingQueue<T> queue = new IterableBlockingQueue<>();
    private static Logger logger = LogManager.getLogger(QueueConsumer.class);
    private static Integer CONSUMING_BATCH_SIZE = 100;

    public QueueConsumer(Consumer<List> queueConsumer) {
        this.consumer = queueConsumer;
    }

    public QueueConsumer(Consumer<List> queueConsumer, Integer consumingBatchSize) {
        this(queueConsumer);
        this.CONSUMING_BATCH_SIZE = consumingBatchSize;
    }

    public void queueAndConsume(Stream<T> sourceStream) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        AtomicInteger counter = new AtomicInteger(1);
        Future future = executor.submit(() -> {
            try {
                sourceStream.forEach(e -> queueElement(counter.getAndIncrement(), e));
            } catch (RuntimeException e) {
                logger.info("consume exception: ", e);
            } finally {
                sourceStream.close();
                queue.done();
            }
        });

        executor.shutdown();
        Iterator<T> queueIterator = queue.iterator();

        while(!future.isDone() || queueIterator.hasNext()){
            consumer.accept(Lists.newArrayList(Iterators.limit(queueIterator, CONSUMING_BATCH_SIZE)));
        }
    }

    private void queueElement(Integer counter, T t) {
        queue.add(t);
        if(counter % CONSUMING_BATCH_SIZE == 0) {
            logger.info(counter + " ads added to queue. queue size=" + queue.size());
        }
    }
}
