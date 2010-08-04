package ru.frostman.scalable.reactor.impl.events;

import ru.frostman.scalable.reactor.events.Event;
import ru.frostman.scalable.reactor.io.ConnectionHandler;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class WriteEvent extends Event {
    
    public WriteEvent(ConnectionHandler connection) {
        super(connection);
    }

    @Override
    public void run() {
        try {
            connection.doWrite();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
