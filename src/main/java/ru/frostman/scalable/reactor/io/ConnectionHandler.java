package ru.frostman.scalable.reactor.io;

import org.apache.log4j.Logger;
import ru.frostman.scalable.reactor.events.Event;
import ru.frostman.scalable.reactor.handlers.IOStrategy;
import ru.frostman.scalable.reactor.handlers.SelectorAttachment;
import ru.frostman.scalable.reactor.impl.events.ReadEvent;
import ru.frostman.scalable.reactor.impl.events.WriteEvent;
import ru.frostman.scalable.reactor.utils.DataQueue;
import ru.frostman.scalable.reactor.utils.DataQueuePool;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public abstract class ConnectionHandler implements SelectorAttachment {
    /**
     * Logging handler.
     */
    protected static final Logger log = Logger.getLogger(ConnectionHandler.class);

    /**
     * Used to provide changing interestOps on channels.
     */
    protected final ExtSelector selector;

    /**
     * SocketChannel to read/write.
     */
    protected final SocketChannel socket;

    /**
     * Strategy of reads/writes.
     */
    protected final IOStrategy ioStrategy;

    /**
     * Internal data queue.
     */
    protected final DataQueue dataQueue;

    /**
     * ReadEvent instance
     */
    protected Event readEvent = new ReadEvent(this);

    /**
     * WriteEvent instance
     */
    protected Event writeEvent = new WriteEvent(this);

    /**
     * Some processing buffers.
     */
    protected ByteBuffer readBuffer, writeBuffer, tmpBuffer;

    /**
     * Creates new connection handler with specified arguments.
     *
     * @param selector      to change interest ops.
     * @param socket        to read/write.
     * @param ioStrategy    to control read/write.
     * @param dataQueuePool external DataQueues pool.
     */
    protected ConnectionHandler(ExtSelector selector, SocketChannel socket, IOStrategy ioStrategy, DataQueuePool dataQueuePool) {
        this.selector = selector;
        this.socket = socket;
        this.ioStrategy = ioStrategy;
        dataQueue = dataQueuePool.acquireDataQueue();
    }

    /**
     * This method invokes when channel ready to read.
     */
    public abstract void doRead();

    /**
     * This method invokes when channel ready to write.
     */
    public abstract void doWrite();

    /**
     * Add to selector interest to read for current channel.
     */
    public abstract void addReadInterest();

    /**
     * Add to selector interest to write for current channel.
     */
    public abstract void addWriteInterest();

    /**
     * Safe socket channel close.
     */
    public void close() {
        try {
            socket.close();
        } catch (Exception e) {
            // no operation
        }
    }

    public DataQueue getDataQueue() {
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
