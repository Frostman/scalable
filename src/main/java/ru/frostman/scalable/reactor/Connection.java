package ru.frostman.scalable.reactor;

import org.apache.log4j.Logger;
import ru.frostman.scalable.reactor.events.Event;
import ru.frostman.scalable.reactor.events.ReadEvent;
import ru.frostman.scalable.reactor.events.WriteEvent;
import ru.frostman.scalable.reactor.handlers.SelectorAttachment;
import ru.frostman.scalable.reactor.strategies.IOStrategy;
import ru.frostman.scalable.reactor.utils.ArraySynchronizedDataQueue;
import ru.frostman.scalable.reactor.utils.DataQueuePool;

import java.nio.ByteBuffer;
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
public class Connection implements SelectorAttachment {
    /**
     * Logging handler.
     */
    private static final Logger log = Logger.getLogger(Connection.class);

    /**
     * Used to provide changing interestOps on channels.
     */
    private final ExtSelector selector;

    /**
     * SocketChannel to read/write.
     */
    private final SocketChannel socket;

    /**
     * Strategy of reads/writes.
     */
    private final IOStrategy ioStrategy;

    /**
     * Internal data queue.
     */
    private final ArraySynchronizedDataQueue dataQueue;    

    /**
     * ReadEvent instance
     */
    private Event readEvent = new ReadEvent(this);

    /**
     * WriteEvent instance
     */
    private Event writeEvent = new WriteEvent(this);

    /**
     * Some processing buffers.
     */
    private ByteBuffer readBuffer, writeBuffer, tmpBuffer;

    /**
     * Creates new connection handler with specified arguments.
     *
     * @param selector      to change interest ops.
     * @param socket        to read/write.
     * @param ioStrategy    to control read/write.
     * @param dataQueuePool external DataQueues pool.
     */
    public Connection(ExtSelector selector, SocketChannel socket, IOStrategy ioStrategy, DataQueuePool dataQueuePool) {
        this.selector = selector;
        this.socket = socket;
        this.ioStrategy = ioStrategy;
        dataQueue = dataQueuePool.acquireDataQueue();
    }

    /**
     * This method invokes when channel ready to read.
     */
    public void handleRead() {
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
            log.trace("Exception in Connection.handleRead", e);
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
    public void handleWrite() {
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
            log.trace("Exception in Connection.handleWrite", e);
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
    public void addReadInterest() {
        selector.addChannelInterestLater(socket, SelectionKey.OP_READ);
    }

    /**
     * Add to selector interest to write for current channel.
     */
    public void addWriteInterest() {
        selector.addChannelInterestLater(socket, SelectionKey.OP_WRITE);
    }

    /**
     * Safe socket channel close.
     */
    private void close() {
        try {
            socket.close();
        } catch (Exception e) {
            // no operation
        }
    }

    public ArraySynchronizedDataQueue getDataQueue() {
        return dataQueue;
    }

    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }

    public void setReadBuffer(ByteBuffer readBuffer) {
        this.readBuffer = readBuffer;
    }

    public ByteBuffer getWriteBuffer() {
        return writeBuffer;
    }

    public void setWriteBuffer(ByteBuffer writeBuffer) {
        this.writeBuffer = writeBuffer;
    }

    public ByteBuffer getTmpBuffer() {
        return tmpBuffer;
    }

    public void setTmpBuffer(ByteBuffer tmpBuffer) {
        this.tmpBuffer = tmpBuffer;
    }

    public Event getReadEvent() {
        return readEvent;
    }

    public Event getWriteEvent() {
        return writeEvent;
    }
}
