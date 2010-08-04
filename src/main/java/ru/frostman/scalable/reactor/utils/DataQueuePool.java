package ru.frostman.scalable.reactor.utils;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public interface DataQueuePool {

    public DataQueue acquireDataQueue();
    public void releaseDataQueue(DataQueue dataQueue);

    public int availableDataQueuesCount();
    
    public int getDataQueueSize();
    public int getPacketSize();
}
