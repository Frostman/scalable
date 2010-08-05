package ru.frostman.scalable.reactor.utils;

import ru.frostman.scalable.reactor.events.Event;
import ru.frostman.scalable.reactor.io.ConnectionHandler;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public interface EventQueue {

    public void addEvent(Event event);

    public void addReadEvent(ConnectionHandler connection);

    public void addWriteEvent(ConnectionHandler connection);

    public Event takeEvent();
}
