package ru.frostman.scalable.tpc.client;

import org.apache.log4j.Logger;
import ru.frostman.scalable.stats.StatisticsManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class EchoTPCClientHandler implements Runnable {
    private static final Logger log = Logger.getLogger(EchoTPCClientHandler.class);
    private static final AtomicInteger instances = new AtomicInteger();
    private final int instanceId = instances.getAndIncrement();
    private String host;
    private int port, frequency;
    private volatile boolean work = true;

    public EchoTPCClientHandler(String host, int port, int frequency) {
        this.host = host;
        this.port = port;
        this.frequency = frequency;

        log.trace("Client handler #" + instanceId + "initialized");
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    @Override
    public void run() {
        Socket socket = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            socket = new Socket(host, port);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (SecurityException e) {
            log.error("Security error", e);
        } catch (UnknownHostException e) {
            log.error("Unknown host", e);
        } catch (Exception e) {
            log.debug("Exception in client #" + instanceId, e);
        }

        if (socket != null && dos != null) {
            log.info("Client #" + instanceId + " start successfully");

            StatisticsManager stats = StatisticsManager.getInstance();
            long timeToSleep = 1000 / frequency;
            int counter = 0;
            try {
                while (work) {
                    int send = counter++;
                    dos.writeInt(send);

                    Thread.sleep(timeToSleep);
                    
                    int receive = dis.readInt();
                    log.trace("Client #" + instanceId + " send " + send + " and receive " + receive);
                    stats.increment();

                    if (send != receive) {
                        log.fatal("sent bytes != receive bytes (" + send + " != " + receive + ")");
                        break;
                    }
                }
            } catch (InterruptedException e) {
                log.trace("Interruption in client #" + instanceId, e);
            } catch (Exception e) {
                log.trace("Exception in client #" + instanceId, e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // no operation
                }
            }

            log.info("Client #" + instanceId + " stop");
        } else {
            log.info("Client #" + instanceId + " start failed");
        }
    }
}
