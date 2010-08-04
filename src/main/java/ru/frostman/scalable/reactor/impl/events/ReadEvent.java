package ru.frostman.scalable.reactor.impl.events;

import ru.frostman.scalable.reactor.events.Event;
import ru.frostman.scalable.reactor.io.ConnectionHandler;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ReadEvent extends Event {

    public ReadEvent(ConnectionHandler connection) {
        super(connection);
    }

    @Override
    public void run() {
        try {
            connection.doRead();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
