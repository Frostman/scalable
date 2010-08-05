package ru.frostman.scalable.netty.server;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class EchoNettyServerHandler extends SimpleChannelUpstreamHandler {
    private static final Logger log = Logger.getLogger(EchoNettyServerHandler.class);

    private final AtomicLong transferredBytes = new AtomicLong();

    public long getTransferredBytes() {
        return transferredBytes.get();
    }

    @Override
    public void messageReceived(
            ChannelHandlerContext ctx, MessageEvent e) {
        // Send back the received message to the remote peer.
        transferredBytes.addAndGet(((ChannelBuffer) e.getMessage()).readableBytes());
        e.getChannel().write(e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        if (e.getCause() instanceof IOException) {
            log.info("Connection closed");
            e.getChannel().close();
            return;
        }
        // Close the connection when an exception is raised.
        log.warn("Unexpected exception from downstream: ", e.getCause());
        e.getChannel().close();
    }
}