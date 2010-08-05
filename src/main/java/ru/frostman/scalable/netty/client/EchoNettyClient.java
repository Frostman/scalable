package ru.frostman.scalable.netty.client;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
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
    private static final Logger log = Logger.getLogger(EchoNettyClient.class);

    private String host;
    private int port, messageSize, connections;

    public EchoNettyClient(String host, int port, int messageSize, int connections) {
        this.host = host;
        this.port = port;
        this.messageSize = messageSize;
        this.connections = connections;
    }

    public void start() {
        ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new EchoNettyClientHandler(messageSize));
            }
        });

        log.info("Netty client started");

        for (int i = 0; i < connections; i++) {
            bootstrap.connect(new InetSocketAddress(host, port));
            log.info("connection started: " + i);

        }
        //future.getChannel().getCloseFuture().awaitUninterruptibly();
        //bootstrap.releaseExternalResources();
    }
}
