package ru.frostman.scalable.reactor.utils.impl.array;

import org.apache.log4j.Logger;
import ru.frostman.scalable.reactor.utils.DataQueue;
import ru.frostman.scalable.reactor.utils.DataQueuePool;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ArraySynchronizedDataQueuePool implements DataQueuePool {
    private static final Logger log = Logger.getLogger(ArraySynchronizedDataQueuePool.class);
    private final LinkedList<DataQueue> dataQueues = new LinkedList<DataQueue>();
    private final ReentrantLock lock = new ReentrantLock();
    private int dataQueueSize;
    private int packetSize;

    public ArraySynchronizedDataQueuePool(int poolSize, int dataQueueSize, int packetSize) {
        if (poolSize <= 0 || dataQueueSize <= 0 || packetSize <= 0) {
            throw new IllegalArgumentException();
        }

        double size = 1L * poolSize * dataQueueSize * packetSize / (1024. * 1024.);
        log.info(String.format("Start initializing ArraySynchronizedDataQueuePool (freeBuffersCount: %.3f Mb)", size));
        this.dataQueueSize = dataQueueSize;
        this.packetSize = packetSize;

        for (int i = 0; i < poolSize; i++) {
            dataQueues.add(initDataQueue());
        }

        log.info("ArraySynchronizedDataQueuePool initialized successfully");
    }

    private DataQueue initDataQueue() {
        return new ArraySynchronizedDataQueue(dataQueueSize, packetSize);
    }

    public DataQueue acquireDataQueue() {
        lock.lock();
        try {
            if (dataQueues.size() == 0)
                return initDataQueue();
            else {
                DataQueue dataQueue = dataQueues.remove();
                dataQueue.clearBuffers();

                return dataQueue;
            }
        } finally {
            lock.unlock();
        }
    }

    public void releaseDataQueue(DataQueue dataQueue) {
        lock.lock();
        try {
            dataQueues.add(dataQueue);
        } finally {
            lock.unlock();
        }
    }

    public int availableDataQueuesCount() {
        lock.lock();
        try {
            return dataQueues.size();
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
