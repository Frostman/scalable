package ru.frostman.scalable.reactor.events;

import ru.frostman.scalable.reactor.io.ConnectionHandler;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public abstract class Event implements Runnable{
    protected ConnectionHandler connection;

    protected Event(ConnectionHandler connection) {
        this.connection = connection;
    }
}
