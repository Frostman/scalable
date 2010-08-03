package ru.frostman.scalable.tpc.client;

import org.apache.log4j.Logger;

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
public class EchoTPCFloodSenderHandler implements Runnable {
    private static final Logger log = Logger.getLogger(EchoTPCFloodSenderHandler.class);
    private static final AtomicInteger instances = new AtomicInteger();
    private final int instanceId = instances.getAndIncrement();
    private volatile boolean work = true;
    private Socket socket = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;

    public EchoTPCFloodSenderHandler(String host, int port) {
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

        log.info("Client #" + instanceId + " connected successfully");
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getDis() {
        return dis;
    }

    @Override
    public void run() {
        if (socket != null && dos != null) {
            log.debug("Client #" + instanceId + " sender start successfully");

            int counter = 0;
            try {
                while (work) {
                    int send = counter++;
                    dos.writeInt(send);
                    log.trace("Client #" + instanceId + " send " +send);
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
