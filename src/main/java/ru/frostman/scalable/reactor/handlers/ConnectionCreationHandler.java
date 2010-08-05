package ru.frostman.scalable.reactor.handlers;

import ru.frostman.scalable.app.Startable;
import ru.frostman.scalable.reactor.ReactorException;
import ru.frostman.scalable.reactor.io.ExtSelector;
import ru.frostman.scalable.reactor.utils.DataQueuePool;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public abstract class ConnectionCreationHandler extends SelectorAttachment implements Startable {        
    /**
     * Host to work.
     */
    protected String host;

    /**
     * Port to work.
     */
    protected int port;    

    /**
     * Pool of DataQueues.
     */
    protected DataQueuePool dataQueuePool;

    /**
     * Creates ConnectionCreationHandler from specified parameters.
     *
     * @param selector      to dispatch i/o events.
     * @param host          to work.
     * @param port          to work.
     * @param ioStrategy    to i/o process.
     * @param dataQueuePool pool of DataQueue.
     */
    protected ConnectionCreationHandler(ExtSelector selector, String host, int port, IOStrategy ioStrategy, DataQueuePool dataQueuePool) {
        super(selector, ioStrategy);
        this.host = host;
        this.port = port;
        this.dataQueuePool = dataQueuePool;
    }

    /**
     * Starts ConnectionCreationHandler.
     */
    public abstract void start();

    /**
     * Safely stops ConnectionCreationHandler.
     */
    public abstract void shutdown();

    protected void registerChannel(SelectableChannel channel, int selectionKeys, SelectorAttachment attachment) {
        if (!channel.isOpen()) {
            throw new ReactorException("Channel is not open.");
        }

        try {
            SelectionKey sk;
            if (channel.isRegistered()) {
                // TODO think about sync
                log.error("BIG BIG TROUBLE!!!!!! channel already registered");
                sk = channel.keyFor(selector.getSelector());
                sk.interestOps(selectionKeys);
                sk.attach(attachment);
            } else {
                channel.configureBlocking(false);
                sk = channel.register(selector.getSelector(), selectionKeys, attachment);
            }
            attachment.setSelectionKey(sk);
        } catch (Exception e) {
            //TODO it's bad
            throw new ReactorException("Error in registering channel", e);
        }
    }
}
