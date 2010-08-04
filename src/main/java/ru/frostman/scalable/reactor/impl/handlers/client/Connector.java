package ru.frostman.scalable.reactor.impl.handlers.client;

import ru.frostman.scalable.reactor.handlers.ConnectHandler;
import ru.frostman.scalable.reactor.io.ExtSelector;
import ru.frostman.scalable.reactor.handlers.IOStrategy;
import ru.frostman.scalable.reactor.utils.DataQueuePool;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class Connector extends ConnectHandler {

    /**
     * Creates Connector from specified parameters.
     *
     * @param selector      to dispatch i/o events.
     * @param host          to connect.
     * @param port          to connect.
     * @param ioStrategy    to i/o process.
     * @param dataQueuePool pool of DataQueue.
     */
    protected Connector(ExtSelector selector, String host, int port, IOStrategy ioStrategy, DataQueuePool dataQueuePool) {
        super(selector, host, port, ioStrategy, dataQueuePool);
    }

    @Override
    public void start() {
        
    }

    @Override
    public void shutdown() {

    }
    
    @Override
    public void doConnect() {
    }
}
