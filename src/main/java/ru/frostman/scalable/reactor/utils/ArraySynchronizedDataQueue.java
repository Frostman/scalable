package ru.frostman.scalable.reactor.utils;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ArraySynchronizedDataQueue {
    private ByteBuffer[] buffers;
    private int freeIdx;
    private int filledIdx;
    private int count;
    private int capacity;
    private final ReentrantLock lock;
    private boolean canFree;
    private boolean canFill;

    public ArraySynchronizedDataQueue(int capacity, int bufferCapacity) {
        if (capacity <= 0 || bufferCapacity <= 0)
            throw new IllegalArgumentException();
        this.capacity = capacity;
        buffers = new ByteBuffer[capacity];
        lock = new ReentrantLock();

        for (int i = 0; i < capacity; i++) {
            buffers[i] = ByteBuffer.allocate(bufferCapacity);
        }
    }

    final int inc(int i) {
        return (++i == buffers.length) ? 0 : i;
    }

    public ByteBuffer getFreeBuffer() {
        lock.lock();
        try {
            if (count == capacity) {
                return null;
            } else {
                canFill = true;
                return buffers[freeIdx];
            }
        } finally {
            lock.unlock();
        }
    }

    public void fillBuffer() {
        lock.lock();
        try {
            if (count == capacity) {
                throw new IllegalStateException();
            }

            if(!canFill) {
                return;
            }

            count++;
            freeIdx = inc(freeIdx);
            canFill = false;
        } finally {
            lock.unlock();
        }
    }

    public ByteBuffer getFilledBuffer() {
        lock.lock();
        try {
            if (count == 0) {
                return null;
            } else {
                canFree = true;
                return buffers[filledIdx];
            }
        } finally {
            lock.unlock();
        }
    }

    public void freeBuffer() {
        lock.lock();
        try {
            if (count == 0) {
                throw new IllegalStateException();
            }

            if (!canFree) {
                return;
            }

            count--;
            filledIdx = inc(filledIdx);
            canFree = false;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }

    public void clearBuffers() {
        lock.lock();
        try {
            for (ByteBuffer buffer : buffers) {
                buffer.clear();
            }
        } finally {
            lock.unlock();
        }
    }

    public String toString() {
        return "DataQueue [count = " + size() + " ]";
    }
}
