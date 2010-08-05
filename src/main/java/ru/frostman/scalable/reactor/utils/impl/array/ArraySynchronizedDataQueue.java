package ru.frostman.scalable.reactor.utils.impl.array;

import ru.frostman.scalable.reactor.utils.DataQueue;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ArraySynchronizedDataQueue implements DataQueue {
    private final ByteBuffer[] buffers;
    private final Semaphore used;
    private final Semaphore free;
    private final int capacity;
    private final int bufferCapacity;
    private int freeIdx = 0;
    private int usedIdx = 0;

    @SuppressWarnings({"unchecked"})
    public ArraySynchronizedDataQueue(int capacity, int bufferCapacity) {
        if (capacity <= 0 || bufferCapacity <= 0) {
            throw new IllegalArgumentException();
        }

        this.capacity = capacity;
        this.bufferCapacity = bufferCapacity;
        buffers = new ByteBuffer[capacity];
        used = new Semaphore(0);
        free = new Semaphore(capacity);

        for (int i = 0; i < capacity; i++) {
            buffers[i] = ByteBuffer.allocate(bufferCapacity);
        }
    }

    @Override
    public ByteBuffer getFreeBuffer() {
        if (!free.tryAcquire()) {
            return null;
        } else {
            buffers[freeIdx].position(0);
            return buffers[freeIdx];
        }
    }

    @Override
    public void fillBuffer() {
        freeIdx = inc(freeIdx);
        used.release();
    }

    @Override
    public ByteBuffer getFilledBuffer() {
        if (!used.tryAcquire())
            return null;
        else {
            buffers[usedIdx].position(0);
            return buffers[usedIdx];
        }
    }

    @Override
    public void freeBuffer() {
        usedIdx = inc(usedIdx);
        free.release();
    }

    @Override
    public void clearBuffers() {
        for (ByteBuffer buffer : buffers) {
            buffer.clear();
        }
    }

    @Override
    public int freeBuffersCount() {
        return free.availablePermits();
    }

    @Override
    public int filledBuffersCount() {
        return used.availablePermits();
    }

    final int inc(int i) {
        return (++i == capacity) ? 0 : i;
    }

    public int getBufferCapacity() {
        return bufferCapacity;
    }
}
