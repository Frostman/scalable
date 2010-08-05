package ru.frostman.scalable.reactor.handlers;

import org.apache.log4j.Logger;
import ru.frostman.scalable.reactor.io.ExtSelector;

import java.nio.channels.SelectionKey;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public abstract class SelectorAttachment {
    /**
     * Logging handler.
     */
    protected static final Logger log = Logger.getLogger(ConnectionCreationHandler.class);

    /**
     * ExtSelector instance to dispatch i/o events.
     */
    protected final ExtSelector selector;

    /**
     * IOStrategy instance, that provides i/o actions.
     */
    protected final IOStrategy ioStrategy;

    private SelectionKey selectionKey;
    private ReentrantLock selectionKeyLock = new ReentrantLock();

    protected SelectorAttachment(ExtSelector selector, IOStrategy ioStrategy) {
        this.selector = selector;
        this.ioStrategy = ioStrategy;
    }

    protected void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public void addChannelInterest(int interest) {
        selectionKeyLock.lock();
        try {
            selectionKey.interestOps(selectionKey.interestOps() | interest);
            selector.wakeup();
        } catch (Exception e) {
            log.trace(e);
        } finally {
            selectionKeyLock.unlock();
        }
    }

    public void removeChannelInterest(int interest) {
        selectionKeyLock.lock();
        try {
            selectionKey.interestOps(selectionKey.interestOps() & ~interest);
        } catch (Exception e) {
            log.trace(e);
        } finally {
            selectionKeyLock.unlock();
        }
    }
}
