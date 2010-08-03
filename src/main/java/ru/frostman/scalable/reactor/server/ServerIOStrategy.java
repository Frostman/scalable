package ru.frostman.scalable.reactor.server;

import ru.frostman.scalable.reactor.io.Connection;
import ru.frostman.scalable.reactor.strategies.IOStrategy;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ServerIOStrategy implements IOStrategy {

    @Override
    public boolean beforeRead(Connection connection) {
        ByteBuffer buffer = connection.getDataQueue().getFreeBuffer();
        connection.setReadBuffer(buffer);

        return buffer != null;
    }

    @Override
    public void afterRead(Connection connection) {
        connection.getDataQueue().fillBuffer();
    }

    @Override
    public boolean beforeWrite(Connection connection) {
        ByteBuffer buffer = connection.getDataQueue().getFilledBuffer();
        connection.setWriteBuffer(buffer);

        if (buffer != null) {
            buffer.position(0);
        }

        return buffer != null;
    }

    @Override
    public void afterWrite(Connection connection) {
        connection.getDataQueue().freeBuffer();   
    }

    @Override
    public int getInitiateInterest() {
        return SelectionKey.OP_READ | SelectionKey.OP_WRITE;
    }
}
