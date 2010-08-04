package ru.frostman.scalable.reactor.events;

import ru.frostman.scalable.reactor.io.Connection;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public interface EventQueue {
    public void addReadEvent(Connection connection);

    public void addWriteEvent(Connection connection);

    public Event takeEvent();
}
