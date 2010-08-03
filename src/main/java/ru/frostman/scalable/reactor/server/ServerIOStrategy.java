package ru.frostman.scalable.reactor.server;

import ru.frostman.scalable.reactor.Connection;
import ru.frostman.scalable.reactor.strategies.IOStrategy;

import java.nio.channels.SelectionKey;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ServerIOStrategy implements IOStrategy {

    @Override
    public boolean beforeRead(Connection connection) {
//        boolean flag = connection.getDataQueue().getFree() > 0;
//
//        if (flag) {
//            connection.setReadBuffer(ByteBuffer.allocate(connection.getPacketSize()));
//        }
//
//        return flag;
        return false;
    }

    @Override
    public void afterRead(Connection connection) {
//        connection.getDataQueue().add(connection.getReadBuffer());
    }

    @Override
    public boolean beforeWrite(Connection connection) {
//        ByteBuffer buffer = connection.getDataQueue().tryTake();
//        connection.setWriteBuffer(buffer);
//
//        if (buffer != null) {
//            buffer.position(0);
//        }
//
//        return buffer != null;
        return false;
    }

    @Override
    public void afterWrite(Connection connection) {
        // no operation
    }

    @Override
    public int getInitiateInterest() {
        return SelectionKey.OP_READ | SelectionKey.OP_WRITE;
    }
}
