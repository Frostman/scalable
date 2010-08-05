package ru.frostman.scalable.reactor.impl;

import ru.frostman.scalable.reactor.handlers.IOStrategy;
import ru.frostman.scalable.reactor.io.ConnectionHandler;
import ru.frostman.scalable.reactor.io.ExtSelector;
import ru.frostman.scalable.reactor.utils.DataQueuePool;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Asynchronized read and write to socket. Internally used
 * selector and data queue to save some data.
 * Read/Write operations specified by IOStrategy implementation.
 *
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class Connection extends ConnectionHandler {

    /**
     * Creates new connection handler with specified arguments.
     *
     * @param selector      to change interest ops.
     * @param socket        to read/write.
     * @param ioStrategy    to control read/write.
     * @param dataQueuePool external DataQueues pool.
     */
    public Connection(ExtSelector selector, SocketChannel socket, IOStrategy ioStrategy, DataQueuePool dataQueuePool) {
        super(selector, socket, ioStrategy, dataQueuePool);
    }

    /**
     * This method invokes when channel ready to read.
     */
    public void doRead() {
        if (!socket.isConnected()) {
            return;
        }

        if (readBuffer == null) {
            if (!ioStrategy.beforeRead(this)) {
                addReadInterest();
                return;
            }
        }

        try {
            while (readBuffer.hasRemaining()) {
                int read = socket.read(readBuffer);

                if (read == 0) {
                    break;
                }

                if (read == -1) {
                    log.info("Connection closed: " + socket);
                    close();
                    break;
                }
            }
        } catch (Exception e) {
            log.info("Connection closed: " + socket);
            log.trace("Exception in Connection.doRead", e);
            close();
        }

        if (!readBuffer.hasRemaining()) {
            ioStrategy.afterRead(this);
            readBuffer = null;
        }

        addReadInterest();
    }

    /**
     * This method invokes when channel ready to write.
     */
    public void doWrite() {
        if (!socket.isConnected()) {
            return;
        }

        if (writeBuffer == null) {
            if (!ioStrategy.beforeWrite(this)) {
                addWriteInterest();
                return;
            }
        }

        try {
            while (writeBuffer.hasRemaining()) {
                int write = socket.write(writeBuffer);

                if (write <= 0) {
                    break;
                }
            }
        } catch (Exception e) {
            log.info("Connection closed: " + socket);
            log.trace("Exception in Connection.doWrite", e);
            close();
        }

        if (!writeBuffer.hasRemaining()) {
            ioStrategy.afterWrite(this);
            writeBuffer = null;
        }

        addWriteInterest();
    }

    /**
     * Add to selector interest to read for current channel.
     */
    @Override
    public void addReadInterest() {
        addChannelInterest(SelectionKey.OP_READ);
    }

    /**
     * Add to selector interest to write for current channel.
     */
    @Override
    public void addWriteInterest() {
        addChannelInterest(SelectionKey.OP_WRITE);
    }
}
