package ru.frostman.scalable.tpc.client;

import org.apache.log4j.Logger;
import ru.frostman.scalable.stats.StatisticsManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class EchoTPCFloodReceiverHandler implements Runnable {
    private static final Logger log = Logger.getLogger(EchoTPCFloodReceiverHandler.class);
    private static final AtomicInteger instances = new AtomicInteger();
    private final int instanceId = instances.getAndIncrement();
    private volatile boolean work = true;
    private Socket socket;
    private DataInputStream dis;

    public EchoTPCFloodReceiverHandler(Socket socket, DataInputStream dis) {
        this.socket = socket;
        this.dis = dis;
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    @Override
    public void run() {
        StatisticsManager stats = StatisticsManager.getInstance();

        if (socket != null && dis != null) {
            log.debug("Client #" + instanceId + " receiver start successfully");

            try {
                while (work) {
                    int receive = dis.readInt();
                    stats.increment();
                    log.trace("Client #" + instanceId + " receive " + receive);
                }
            } catch (Exception e) {
                log.trace("Exception in client #" + instanceId, e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // no operation
                }
            }
        }
    }
}
