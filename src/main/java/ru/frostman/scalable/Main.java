package ru.frostman.scalable;

import org.apache.log4j.Logger;
import ru.frostman.scalable.app.AppType;
import ru.frostman.scalable.app.Startable;
import ru.frostman.scalable.log.LoggingLevel;
import ru.frostman.scalable.log.LoggingLevelConfigurator;
import ru.frostman.scalable.netty.client.EchoNettyClient;
import ru.frostman.scalable.netty.server.EchoNettyServer;
import ru.frostman.scalable.reactor.ReactorFactory;
import ru.frostman.scalable.stats.StatisticsManager;
import ru.frostman.scalable.tpc.client.EchoTPCClient;
import ru.frostman.scalable.tpc.server.EchoTPCServer;

import java.io.IOException;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class);

    /**
     * Supported args:
     *
     * -m (mode) client, flood_client, tpc_server, reactor_server
     * -f frequency
     * -n threads
     * -t reporting timeout
     * -s server IP
     * -p server port
     *
     * @param args command line
     */
    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.run(args);
    }

    private void run(String[] args) throws IOException {
        if (args.length % 2 != 0) {
            writeArgs();
        }

        AppType type = AppType.CLIENT;
        int frequency = 10;
        int threads = Runtime.getRuntime().availableProcessors() - 1;
        int reportingTimeout = 1;
        String host = "localhost";
        int port = 7;
        LoggingLevel loggingLevel = LoggingLevel.INFO;

        //TODO see gnu.getopt 

        try {
            for (int i = 0; i < args.length; i += 2) {
                String flag = args[i], value = args[i + 1];
                if ("-m".equals(flag)) {
                    type = AppType.fromString(value);
                } else if ("-f".equals(flag)) {
                    frequency = Integer.parseInt(value);
                } else if ("-n".equals(flag)) {
                    threads = Integer.parseInt(value);
                } else if ("-t".equals(flag)) {
                    reportingTimeout = Integer.parseInt(value);
                } else if ("-s".equals(flag)) {
                    host = value;
                } else if ("-p".equals(flag)) {
                    port = Integer.parseInt(value);
                } else if ("-L".equals(flag)) {
                    loggingLevel = LoggingLevel.getByAbbrev(value);
                }
            }
        } catch (Exception e) {
            System.out.println("Bad arguments\n");
            writeArgs();
            return;
        }

        LoggingLevelConfigurator.configure(loggingLevel);

        log.info("Available processors: " + Runtime.getRuntime().availableProcessors());
        log.info(String.format("Available memory: %.3f Mb", Runtime.getRuntime().freeMemory() / (1024. * 1024.)));

        StringBuilder appConf = new StringBuilder();
        appConf.append("Application configuration:\n")
                .append("\t type: ").append(type).append('\n');

        Startable app = null;
        if (AppType.CLIENT == type) {
            StatisticsManager.init(reportingTimeout, System.out);

            appConf.append("\t host: ").append(host).append('\n')
                    .append("\t port: ").append(port).append('\n')
                    .append("\t threads: ").append(threads).append('\n')
                    .append("\t frequency: ").append(frequency).append('\n');

            app = new EchoTPCClient(host, port, threads, frequency);

        } else if (AppType.NETTY_CLIENT == type) {
            StatisticsManager.init(reportingTimeout, System.out);

            appConf.append("\t host: ").append(host).append('\n')
                    .append("\t port: ").append(port).append('\n');

            app = new EchoNettyClient(host, port, 4, threads);

        } else if (AppType.NETTY_SERVER == type) {
            StatisticsManager.init(reportingTimeout, System.out);

            appConf.append("\t host: ").append(host).append('\n')
                    .append("\t port: ").append(port).append('\n');

            app = new EchoNettyServer(host, port);

        } else if (AppType.FLOOD_CLIENT == type) {
            StatisticsManager.init(reportingTimeout, System.out);

            appConf.append("\t host: ").append(host).append('\n')
                    .append("\t port: ").append(port).append('\n')
                    .append("\t threads: ").append(threads).append('\n')
                    .append("\t frequency: INF\n");

            app = new EchoTPCClient(host, port, threads, 0);

        } else if (AppType.TPC_SERVER == type) {

            appConf.append("\t host: ").append(host).append('\n')
                    .append("\t port: ").append(port).append('\n');

            app = new EchoTPCServer(host, port);

        } else if (AppType.REACTOR_SERVER == type) {

            appConf.append("\t host: ").append(host).append('\n')
                    .append("\t port: ").append(port).append('\n')
                    .append("\t workers: ").append(threads).append('\n');

            //TODO delete hardcode and do accept in selector thread
            app = ReactorFactory.createEchoServer(host, port, threads, 120000, 32, 4);

        } else {
            log.fatal(type + " is not supported mode");
            return;
        }

        log.info(appConf);
        app.start();
    }

    private void writeArgs() {
        System.out.println("Supported args:\n" +
                "     \n" +
                "      -m (mode) client, flood_client, tpc_server, reactor_server\n" +
                "      -f frequency\n" +
                "      -n threads (workers)\n" +
                "      -t reporting timeout\n" +
                "      -s server IP\n" +
                "      -p server port\n +" +
                "      -L logging level (TRACE, DEBUG, INFO, WARN, ERROR, FATAL)");
    }
}
