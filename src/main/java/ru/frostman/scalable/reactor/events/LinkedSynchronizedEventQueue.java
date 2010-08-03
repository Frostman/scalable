package ru.frostman.scalable.reactor.events;

import ru.frostman.scalable.reactor.io.Connection;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class LinkedSynchronizedEventQueue {
    private LinkedBlockingQueue<Event> queue;

    public LinkedSynchronizedEventQueue() {
        // TODO may be bad impl, profile it
        queue = new LinkedBlockingQueue<Event>();
    }

    public void addReadEvent(Connection connection) {
        queue.add(connection.getReadEvent());
    }

    public void addWriteEvent(Connection connection) {
        queue.add(connection.getWriteEvent());
    }

    public Event takeEvent() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }
}
