package ru.frostman.scalable.reactor.handlers;

import org.apache.log4j.Logger;
import ru.frostman.scalable.app.Startable;
import ru.frostman.scalable.reactor.io.ExtSelector;
import ru.frostman.scalable.reactor.utils.DataQueuePool;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public abstract class ConnectionCreationHandler implements SelectorAttachment, Startable{
    /**
     * Logging handler.
     */
    protected static final Logger log = Logger.getLogger(ConnectionCreationHandler.class);

    /**
     * ExtSelector instance to dispatch i/o events.
     */
    protected final ExtSelector selector;

    /**
     * Host to work.
     */
    protected String host;

    /**
     * Port to work.
     */
    protected int port;

    /**
     * IOStrategy instance, that provides i/o actions.
     */
    protected IOStrategy ioStrategy;

    /**
     * Pool of DataQueues.
     */
    protected DataQueuePool dataQueuePool;

    /**
     * Creates ConnectionCreationHandler from specified parameters.
     *
     * @param selector to dispatch i/o events.
     * @param host to work.
     * @param port to work.
     * @param ioStrategy to i/o process.
     * @param dataQueuePool pool of DataQueue.
     */
    protected ConnectionCreationHandler(ExtSelector selector, String host, int port, IOStrategy ioStrategy, DataQueuePool dataQueuePool) {
        this.selector = selector;
        this.host = host;
        this.port = port;
        this.ioStrategy = ioStrategy;
        this.dataQueuePool = dataQueuePool;
    }

    /**
     * Starts ConnectionCreationHandler.
     */
    public abstract void start();

    /**
     * Safely stops ConnectionCreationHandler.
     */
    public abstract void shutdown();
}
