package ru.frostman.scalable.tpc.client;

import org.apache.log4j.Logger;
import ru.frostman.scalable.app.Startable;
import ru.frostman.scalable.stats.StatisticsManager;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class EchoTPCClient implements Startable {
    private static final Logger log = Logger.getLogger(EchoTPCClient.class);
    private String host;
    private int port, threads, frequency;

    public EchoTPCClient(String host, int port, int threads, int frequency) {
        this.host = host;
        this.port = port;
        this.threads = threads;
        this.frequency = frequency;
    }

    public void start() {
        log.info("Start clients initialization ");

        Thread[] clients = new Thread[(frequency == 0 ? 2 : 1) * threads];

        boolean ok = true;
        if (frequency != 0) {
            try {
                for (int i = 0; i < threads; i++) {
                    clients[i] = new Thread(new EchoTPCClientHandler(host, port, frequency));
                    clients[i].start();
                }
            } catch (Exception e) {
                log.error(e);
                ok = false;
            }
        } else {
            try {
                for (int i = 0; i < threads; i++) {
                    EchoTPCFloodSenderHandler sender = new EchoTPCFloodSenderHandler(host, port);
                    clients[i] = new Thread(sender);
                    clients[i].start();

                    clients[threads + i] = new Thread(
                            new EchoTPCFloodReceiverHandler(sender.getSocket(), sender.getDis()));
                    clients[threads + i].start();
                }
            } catch (Exception e) {
                log.error(e);
                ok = false;
            }
        }

        if(ok) {
            log.info("All clients started successfully");
        }else {
            log.warn("Some troubles in starting clients");
        }

        try {
            for (Thread client : clients) {
                client.join();
            }
        } catch (InterruptedException e) {
            log.error("Clients join interrupted", e);
        }

        StatisticsManager.setWork(false);
    }
}
