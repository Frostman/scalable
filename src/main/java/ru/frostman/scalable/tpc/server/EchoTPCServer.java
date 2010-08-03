package ru.frostman.scalable.tpc.server;

import org.apache.log4j.Logger;
import ru.frostman.scalable.app.Startable;

import java.io.IOException;
import java.net.*;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class EchoTPCServer implements Startable {
    private static final Logger log = Logger.getLogger(EchoTPCServer.class);
    private String host;
    private int port;
    private volatile boolean work = true;

    public EchoTPCServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    public void start() {
        try {
            ServerSocket server = new ServerSocket(port, 150, InetAddress.getByName(host));
            //server.setSoTimeout(1000);

            log.info("Server start successfully");

            while (work && !server.isClosed()) {
                Socket socket = null;
                try {
                    log.debug("Accepting new connection");
                    socket = server.accept();
                } catch (SocketTimeoutException e) {
                    // no operation
                }

                if (!work) {
                    break;
                }

                if (socket == null || socket.isClosed()) {
                    continue;
                }

                EchoTPCConnectionHandler handler = new EchoTPCConnectionHandler(socket);
                Thread handlerThread = new Thread(handler);
                handlerThread.start();
            }

            log.info("Server stop");

            server.close();
        } catch (UnknownHostException e) {
            log.error("UnknownHostException", e);
        } catch (SecurityException e) {
            log.error("SecurityException", e);
        } catch (IOException e) {
            log.debug("Exception in server", e);
        }
    }
}
