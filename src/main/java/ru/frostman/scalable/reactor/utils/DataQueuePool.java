package ru.frostman.scalable.reactor.utils;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class DataQueuePool {
    private static final Logger log = Logger.getLogger(DataQueuePool.class);
    private final LinkedList<ArraySynchronizedDataQueue> dataQueues = new LinkedList<ArraySynchronizedDataQueue>();
    private final ReentrantLock lock = new ReentrantLock();
    private int dataQueueSize;
    private int packetSize;

    public DataQueuePool(int poolSize, int dataQueueSize, int packetSize) {
        double size = 1L * poolSize * dataQueueSize * packetSize / (1024. * 1024.);
        log.info(String.format("Start initializing DataQueuePool (size: %.3f Mb)", size));
        this.dataQueueSize = dataQueueSize;
        this.packetSize = packetSize;

        for (int i = 0; i < poolSize; i++) {
            dataQueues.add(initDataQueue());
        }

        log.info("DataQueuePool initialized successfully");
    }

    private ArraySynchronizedDataQueue initDataQueue() {
        return new ArraySynchronizedDataQueue(dataQueueSize, packetSize);
    }

    public ArraySynchronizedDataQueue acquireDataQueue() {
        lock.lock();
        try {
            if (dataQueues.size() == 0)
                return initDataQueue();
            else {
                ArraySynchronizedDataQueue dataQueue = dataQueues.remove();
                dataQueue.clearBuffers();

                return dataQueue;
            }
        } finally {
            lock.unlock();
        }
    }

    public void releaseDataQueue(ArraySynchronizedDataQueue dataQueue) {
        lock.lock();
        try {
            dataQueues.add(dataQueue);
        } finally {
            lock.unlock();
        }
    }

    public int getDataQueueSize() {
        return dataQueueSize;
    }

    public int getPacketSize() {
        return packetSize;
    }
}
