package ru.frostman.scalable.reactor.handlers;

import ru.frostman.scalable.reactor.io.ExtSelector;
import ru.frostman.scalable.reactor.utils.DataQueuePool;

import java.nio.channels.ServerSocketChannel;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public abstract class AcceptHandler extends ConnectionCreationHandler {
    /**
     * ServerSocket to receive incoming connections.
     */
    protected ServerSocketChannel ssc;

    /**
     * Creates AcceptHandler from specified parameters.
     *
     * @param selector      to dispatch i/o events.
     * @param host          to listen.
     * @param port          to listen.
     * @param ioStrategy    to i process.
     * @param dataQueuePool pool of DataQueue.
     */
    protected AcceptHandler(ExtSelector selector, String host, int port, IOStrategy ioStrategy, DataQueuePool dataQueuePool) {
        super(selector, host, port, ioStrategy, dataQueuePool);
    }       

    public abstract void doAccept();
}
