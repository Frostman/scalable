package ru.frostman.scalable.reactor.utils.impl;

import ru.frostman.scalable.reactor.events.Event;
import ru.frostman.scalable.reactor.utils.EventExecutor;
import ru.frostman.scalable.reactor.utils.EventQueue;
import ru.frostman.scalable.reactor.utils.impl.array.ArraySynchronizedEventQueue;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class StaticThreadEventExecutor implements EventExecutor {
    private final Worker[] workers;
    private final EventQueue eventQueue;

    public StaticThreadEventExecutor(int threadsCount, int queueCapacity) {
        eventQueue = new ArraySynchronizedEventQueue(queueCapacity);
        workers = new Worker[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            workers[i] = new Worker();
        }
    }

    @Override
    public void execute(Event event) {
        eventQueue.addEvent(event);
    }

    private final class Worker implements Runnable {
        private Thread workerThread;

        private Worker() {
            workerThread = new Thread(this);
            workerThread.setDaemon(true);
            workerThread.start();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Event event = eventQueue.takeEvent();
                    event.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
