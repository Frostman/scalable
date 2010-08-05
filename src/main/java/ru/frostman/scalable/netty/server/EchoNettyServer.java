package ru.frostman.scalable.netty.server;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import ru.frostman.scalable.app.Startable;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class EchoNettyServer implements Startable {
    private static final Logger log = Logger.getLogger(EchoNettyServer.class);

    private String host;
    private int port;

    public EchoNettyServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new EchoNettyServerHandler());
            }
        });

        bootstrap.bind(new InetSocketAddress(host, port));

        log.info("Netty server started.");
    }
}
