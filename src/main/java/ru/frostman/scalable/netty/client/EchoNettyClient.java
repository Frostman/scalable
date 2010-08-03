package ru.frostman.scalable.netty.client;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import ru.frostman.scalable.app.Startable;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class EchoNettyClient implements Startable {
    private static final int DEFAULT_PORT = 7;
    private static final int DEFAULT_MESSAGE_SIZE = 4;
    private static String DEFAULT_HOST = "localhost";

    private String host;
    private int port, messageSize;

    public EchoNettyClient() {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_MESSAGE_SIZE);
    }

    public EchoNettyClient(String host, int port) {
        this(host, port, DEFAULT_MESSAGE_SIZE);
    }

    public EchoNettyClient(String host, int port, int messageSize) {
        this.host = host;
        this.port = port;
        this.messageSize = messageSize;
    }

    public void start() {
        ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new EchoNettyClientHandler(messageSize));
            }
        });

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        future.getChannel().getCloseFuture().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
    }
}
