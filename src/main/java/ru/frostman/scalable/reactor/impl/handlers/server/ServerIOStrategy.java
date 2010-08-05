package ru.frostman.scalable.reactor.impl.handlers.server;

import ru.frostman.scalable.reactor.handlers.IOStrategy;
import ru.frostman.scalable.reactor.io.ConnectionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ServerIOStrategy implements IOStrategy {

    @Override
    public boolean beforeRead(ConnectionHandler connection) {
        ByteBuffer buffer = connection.getDataQueue().getFreeBuffer();
        connection.setReadBuffer(buffer);

        return buffer != null;
    }

    @Override
    public void afterRead(ConnectionHandler connection) {
        connection.getDataQueue().fillBuffer();
    }

    @Override
    public boolean beforeWrite(ConnectionHandler connection) {
        ByteBuffer buffer = connection.getDataQueue().getFilledBuffer();
        connection.setWriteBuffer(buffer);        

        return buffer != null;
    }

    @Override
    public void afterWrite(ConnectionHandler connection) {
        connection.getDataQueue().freeBuffer();   
    }

    @Override
    public int getInitiateInterest() {
        return SelectionKey.OP_READ | SelectionKey.OP_WRITE;
    }
}
