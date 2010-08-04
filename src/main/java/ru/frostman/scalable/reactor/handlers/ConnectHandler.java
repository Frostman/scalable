package ru.frostman.scalable.reactor.handlers;

import ru.frostman.scalable.reactor.io.ExtSelector;
import ru.frostman.scalable.reactor.utils.DataQueuePool;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public abstract class ConnectHandler extends ConnectionCreationHandler{

    /**
     * Creates ConnectHandler from specified parameters.
     *
     * @param selector      to dispatch i/o events.
     * @param host          to connect.
     * @param port          to connect.
     * @param ioStrategy    to i/o process.
     * @param dataQueuePool pool of DataQueue.
     */
    protected ConnectHandler(ExtSelector selector, String host, int port, IOStrategy ioStrategy, DataQueuePool dataQueuePool) {
        super(selector, host, port, ioStrategy, dataQueuePool);
    }

    public abstract void doConnect();
}
