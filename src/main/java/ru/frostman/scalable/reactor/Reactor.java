package ru.frostman.scalable.reactor;

import ru.frostman.scalable.app.Startable;
import ru.frostman.scalable.reactor.handlers.ConnectionCreationHandler;
import ru.frostman.scalable.reactor.io.ExtSelector;

import java.io.IOException;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class Reactor implements Startable {
    private ExtSelector selector;
    private ConnectionCreationHandler connectionCreationHandler;
    private boolean ready = false;

    private Reactor() {
    }

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
        
        connectionCreationHandler.start();
        selector.start();
    }
}
