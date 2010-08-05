package ru.frostman.scalable.reactor.utils;

import ru.frostman.scalable.reactor.events.Event;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public interface EventExecutor {
    public void execute(Event event);
}
