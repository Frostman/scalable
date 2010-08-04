package ru.frostman.scalable.reactor.handlers;

import ru.frostman.scalable.reactor.io.ConnectionHandler;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public interface IOStrategy {
    public boolean beforeRead(ConnectionHandler connection);
    public void afterRead(ConnectionHandler connection);

    public boolean beforeWrite(ConnectionHandler connection);
    public void afterWrite(ConnectionHandler connection);

    public int getInitiateInterest();
}
