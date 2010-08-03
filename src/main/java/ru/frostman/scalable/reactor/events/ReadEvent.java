package ru.frostman.scalable.reactor.events;

import ru.frostman.scalable.reactor.Connection;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ReadEvent implements Event {
    private Connection connection;

    public ReadEvent(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            connection.handleRead();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
