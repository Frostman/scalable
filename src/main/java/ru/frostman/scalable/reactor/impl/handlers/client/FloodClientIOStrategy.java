package ru.frostman.scalable.reactor.impl.handlers.client;

import ru.frostman.scalable.reactor.handlers.IOStrategy;
import ru.frostman.scalable.reactor.io.ConnectionHandler;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class FloodClientIOStrategy implements IOStrategy{

    @Override
    public boolean beforeRead(ConnectionHandler connection) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void afterRead(ConnectionHandler connection) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean beforeWrite(ConnectionHandler connection) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void afterWrite(ConnectionHandler connection) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getInitiateInterest() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
