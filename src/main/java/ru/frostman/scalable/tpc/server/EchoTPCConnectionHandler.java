package ru.frostman.scalable.tpc.server;

import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class EchoTPCConnectionHandler implements Runnable {
    private static final Logger log = Logger.getLogger(EchoTPCConnectionHandler.class);
    private static final AtomicInteger instances = new AtomicInteger();
    private final int instanceId = instances.getAndIncrement();
    private final Socket socket;

    public EchoTPCConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            log.info("New connection accepted, handler #" + instanceId);

            while (true) {
                int read = dis.readInt();
                dos.writeInt(read);

                log.trace("Connection handler #" + instanceId + " receive and send " + read);
            }
        } catch (EOFException e) {
            log.debug("Client close connection, handler #" + instanceId);
        } catch (Exception e) {
            log.trace("Exception in connection handler# " + instanceId, e);
        } finally {
            log.info("Connection closed, handler #" + instanceId);

            try {
                socket.close();
            } catch (IOException e) {
                // no operation
            }
        }
    }
}
