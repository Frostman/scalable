package ru.frostman.scalable.reactor;

import org.apache.log4j.Logger;
import ru.frostman.scalable.app.Startable;
import ru.frostman.scalable.reactor.handlers.AcceptHandler;
import ru.frostman.scalable.reactor.handlers.ConnectHandler;
import ru.frostman.scalable.reactor.handlers.ConnectionCreationHandler;
import ru.frostman.scalable.reactor.io.ExtSelector;

import java.io.IOException;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class Reactor implements Startable {
    private static final Logger log = Logger.getLogger(Reactor.class);
    private ExtSelector selector;
    private ConnectionCreationHandler connectionCreationHandler;
    private boolean ready = false;   

    public Reactor(int workerThreads) {
        try {
            selector = new ExtSelector(workerThreads);
        } catch (IOException e) {
            throw new ReactorException(e);
        }
    }

    public void setConnectionCreationHandler(ConnectionCreationHandler connectionCreationHandler) {
        this.connectionCreationHandler = connectionCreationHandler;
        ready = true;
    }

    public ExtSelector getSelector() {
        return selector;
    }

    @Override
    public void start() {
        if (!ready)
            throw new IllegalStateException();

        if (connectionCreationHandler instanceof AcceptHandler) {
            log.info("Reactor mode: server");
        } else if(connectionCreationHandler instanceof ConnectHandler) {
            log.info("Reactor mode: client");
        }

        connectionCreationHandler.start();
        selector.start();                
    }
}
