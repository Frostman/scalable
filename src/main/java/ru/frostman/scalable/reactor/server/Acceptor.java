package ru.frostman.scalable.reactor.server;

import org.apache.log4j.Logger;
import ru.frostman.scalable.reactor.io.Connection;
import ru.frostman.scalable.reactor.io.ExtSelector;
import ru.frostman.scalable.reactor.handlers.AcceptHandler;
import ru.frostman.scalable.reactor.strategies.IOStrategy;
import ru.frostman.scalable.reactor.utils.DataQueuePool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Listen for specified host and port and accepting incoming
 * connections. Work with ExtSelector.
 *
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class Acceptor implements AcceptHandler{
    /**
     * Logging handler.
     */
    private static final Logger log = Logger.getLogger(Acceptor.class);

    /**
     * ServerSocket to receive incoming connections.
     */

    private ServerSocketChannel ssc;

    /**
     * ExtSelector instance to dispatch i/o events.
     */
    private final ExtSelector selector;

    /**
     * Host to listen.
     */
    private String host;

    /**
     * Port to listen.
     */
    private int port;

    /**
     * IOStrategy instance, that provides i/o actions.
     */
    private IOStrategy ioStrategy;

    /**
     * Pool of DataQueues.
     */
    private DataQueuePool dataQueuePool;

    /**
     * Creates acceptor from specified parameters.
     *
     * @param selector
     * @param host
     * @param port
     * @param ioStrategy
     * @param dataQueuePool
     */
    public Acceptor(ExtSelector selector, String host, int port, IOStrategy ioStrategy, DataQueuePool dataQueuePool) {
        this.selector = selector;
        this.host = host;
        this.port = port;
        this.ioStrategy = ioStrategy;
        this.dataQueuePool = dataQueuePool;
    }

    /**
     * Starts ServerSocket and bind to socket, register to selector.
     */
    public void start() {
        try {
            ssc = ServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(host, port);
            ssc.socket().bind(address, 150);

            selector.registerChannelLater(ssc, SelectionKey.OP_ACCEPT, this);
        } catch (IOException e) {
            //TODO log it
            e.printStackTrace();
        }
    }

    /**
     * This method called by selector to accept incoming connection.
     */
    public void doAccept() {
        SocketChannel socket = null;
        try {
            socket = ssc.accept();
            selector.addChannelInterestNow(ssc, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            log.error("Exception in doAccept", e);
        }
        if (socket != null) {
            try {
                //TODO set buffers with normal size
                socket.socket().setReceiveBufferSize(2 * 1024);
                socket.socket().setSendBufferSize(2 * 1024);                
                Connection connection = new Connection(selector, socket, ioStrategy, dataQueuePool);
                selector.registerChannelNow(socket, ioStrategy.getInitiateInterest(), connection);
            } catch (IOException e) {
                log.debug("Exception in changing buffer's size", e);
            }

            log.info("Connection established: " + socket.toString());
        }
    }

    /**
     * Closes the server socket. Locks while closing.
     */
    public void close() {
        try {
            selector.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        ssc.close();
                    } catch (Exception e) {
                        // no operation
                    }
                }
            });
        } catch (InterruptedException e) {
            // no operation
        }
    }
}
