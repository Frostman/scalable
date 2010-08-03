package ru.frostman.scalable.reactor;

import ru.frostman.scalable.reactor.server.Acceptor;
import ru.frostman.scalable.reactor.server.ServerIOStrategy;
import ru.frostman.scalable.reactor.strategies.IOStrategy;
import ru.frostman.scalable.reactor.utils.DataQueuePool;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ReactorFactory {
    private ReactorFactory() {
    }

    public static Reactor createEchoServer(String host, int port, int workerThreads, int dataQueuePoolSize, int dataQueueSize, int packetSize) {
        DataQueuePool dataQueuePool = new DataQueuePool(dataQueuePoolSize, dataQueueSize, packetSize);
        IOStrategy ioStrategy = new ServerIOStrategy();
        Reactor reactor = new Reactor(workerThreads);
        Acceptor acceptor = new Acceptor(reactor.getSelector(), host, port, ioStrategy, dataQueuePool);
        reactor.setConnectionCreationHandler(acceptor);

        return reactor;
    }
}
