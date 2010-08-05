package ru.frostman.scalable.reactor.impl.handlers.server;

import ru.frostman.scalable.reactor.handlers.AcceptHandler;
import ru.frostman.scalable.reactor.io.ConnectionHandler;
import ru.frostman.scalable.reactor.handlers.IOStrategy;
import ru.frostman.scalable.reactor.impl.Connection;
import ru.frostman.scalable.reactor.io.ExtSelector;
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
public class Acceptor extends AcceptHandler {

    /**
     * Creates Acceptor from specified parameters.
     *
     * @param selector      to dispatch i/o events.
     * @param host          to listen.
     * @param port          to listen.
     * @param ioStrategy    to i/o process.
     * @param dataQueuePool pool of DataQueue.
     */
    public Acceptor(ExtSelector selector, String host, int port, IOStrategy ioStrategy, DataQueuePool dataQueuePool) {
        super(selector, host, port, ioStrategy, dataQueuePool);        
    }

    /**
     * Starts ServerSocket and bind to socket, register to selector.
     */
    @Override
    public void start() {
        try {
            ssc = ServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(host, port);
            ssc.socket().bind(address, 150);

            registerChannel(ssc, SelectionKey.OP_ACCEPT, this);            
        } catch (IOException e) {
            //TODO log it
            e.printStackTrace();
        }
    }

    /**
     * Closes the server socket.
     */
    @Override
    public void shutdown() {
        try {
            ssc.close();
        } catch (Exception e) {
            // no operation
        }
    }

    /**
     * This method called by selector to accept incoming connection.
     */
    @Override
    public void doAccept() {
        SocketChannel socket = null;
        try {
            socket = ssc.accept();
        } catch (IOException e) {
            log.error("Exception in doAccept", e);
        }
        if (socket != null) {
            try {
                //TODO set buffers with normal freeBuffersCount
                socket.socket().setReceiveBufferSize(2 * dataQueuePool.getDataQueueSize() * dataQueuePool.getPacketSize());
                socket.socket().setSendBufferSize(2 * dataQueuePool.getDataQueueSize() * dataQueuePool.getPacketSize());
                ConnectionHandler connection = new Connection(selector, socket, ioStrategy, dataQueuePool);
                registerChannel(socket, ioStrategy.getInitiateInterest(), connection);                
            } catch (IOException e) {
                log.debug("Exception in changing buffer's size", e);
            }

            log.info("Connection established: " + socket.toString());
        }
    }
}
