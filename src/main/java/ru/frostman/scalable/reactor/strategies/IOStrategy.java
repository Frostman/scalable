package ru.frostman.scalable.reactor.strategies;

import ru.frostman.scalable.reactor.io.Connection;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public interface IOStrategy {
    public boolean beforeRead(Connection connection);
    public void afterRead(Connection connection);

    public boolean beforeWrite(Connection connection);
    public void afterWrite(Connection connection);

    public int getInitiateInterest();
}
