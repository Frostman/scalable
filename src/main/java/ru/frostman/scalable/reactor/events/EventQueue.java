package ru.frostman.scalable.reactor.events;

import ru.frostman.scalable.reactor.io.ConnectionHandler;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public interface EventQueue {
    public void addReadEvent(ConnectionHandler connection);

    public void addWriteEvent(ConnectionHandler connection);

    public Event takeEvent();
}
