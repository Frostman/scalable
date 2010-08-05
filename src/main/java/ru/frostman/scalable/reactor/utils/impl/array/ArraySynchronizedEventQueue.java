package ru.frostman.scalable.reactor.utils.impl.array;

import ru.frostman.scalable.reactor.events.Event;
import ru.frostman.scalable.reactor.io.ConnectionHandler;
import ru.frostman.scalable.reactor.utils.EventQueue;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ArraySynchronizedEventQueue implements EventQueue {
    private final Event[] events;
    private final Semaphore used;
    private final Semaphore free;
    private final int capacity;
    private int freeIdx = 0;
    private int usedIdx = 0;
    private ReentrantLock addLock = new ReentrantLock();
    private ReentrantLock takeLock = new ReentrantLock();

    @SuppressWarnings({"unchecked"})
    public ArraySynchronizedEventQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }

        this.capacity = capacity;
        events = new Event[capacity];
        used = new Semaphore(0);
        free = new Semaphore(capacity);
    }

    final int inc(int i) {
        return (++i == capacity) ? 0 : i;
    }

    @Override
    public void addEvent(Event event) {
        addLock.lock();
        try {
            free.acquire();
            events[freeIdx] = event;
            freeIdx = inc(freeIdx);
            used.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            addLock.unlock();
        }
    }

    @Override
    public Event takeEvent() {
        takeLock.lock();
        try {
            used.acquire();
            Event event = events[usedIdx];
            usedIdx = inc(usedIdx);
            free.release();
            return event;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            takeLock.unlock();
        }
    }

    @Override
    public void addReadEvent(ConnectionHandler connection) {
        addEvent(connection.getReadEvent());
    }

    @Override
    public void addWriteEvent(ConnectionHandler connection) {
        addEvent(connection.getWriteEvent());
    }
}
