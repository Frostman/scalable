package ru.frostman.scalable.reactor.events;

import ru.frostman.scalable.reactor.io.Connection;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class WriteEvent implements Event {
    private Connection connection;

    public WriteEvent(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            connection.handleWrite();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
