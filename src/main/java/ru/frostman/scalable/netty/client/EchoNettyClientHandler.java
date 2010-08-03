package ru.frostman.scalable.netty.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import ru.frostman.scalable.stats.StatisticsManager;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class EchoNettyClientHandler extends SimpleChannelUpstreamHandler {
    private final ChannelBuffer firstMessage;
    private final StatisticsManager stats;

    public EchoNettyClientHandler(int messageSize) {
        if (messageSize <= 0) {
            throw new IllegalArgumentException("messageSize: " + messageSize);
        }

        stats =StatisticsManager.getInstance();
        
        firstMessage = ChannelBuffers.buffer(messageSize);
        for (int i = 0; i < firstMessage.capacity(); i++) {
            firstMessage.writeByte((byte) i);
        }
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        e.getChannel().write(firstMessage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        //((ChannelBuffer) e.getMessage()).readableBytes();
        e.getChannel().write(e.getMessage());
        stats.increment();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getChannel().close();
    }
}
