package ru.frostman.scalable.reactor;

import ru.frostman.scalable.reactor.handlers.ConnectionCreationHandler;
import ru.frostman.scalable.reactor.handlers.IOStrategy;
import ru.frostman.scalable.reactor.impl.handlers.server.Acceptor;
import ru.frostman.scalable.reactor.impl.handlers.server.ServerIOStrategy;
import ru.frostman.scalable.reactor.utils.impl.array.ArraySynchronizedDataQueuePool;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ReactorFactory {
    private ReactorFactory() {
    }

    public static Reactor createEchoServer(String host, int port, int workerThreads, int dataQueuePoolSize, int dataQueueSize, int packetSize) {
        ArraySynchronizedDataQueuePool dataQueuePool = new ArraySynchronizedDataQueuePool(dataQueuePoolSize, dataQueueSize, packetSize);
        IOStrategy ioStrategy = new ServerIOStrategy();
        Reactor reactor = new Reactor(workerThreads);
        ConnectionCreationHandler acceptor = new Acceptor(reactor.getSelector(), host, port, ioStrategy, dataQueuePool);
        reactor.setConnectionCreationHandler(acceptor);

        return reactor;
    }
}
