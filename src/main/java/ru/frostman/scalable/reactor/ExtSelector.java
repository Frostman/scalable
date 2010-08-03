package ru.frostman.scalable.reactor;

import org.apache.log4j.Logger;
import ru.frostman.scalable.reactor.client.Connector;
import ru.frostman.scalable.reactor.server.Acceptor;
import ru.frostman.scalable.reactor.handlers.SelectorAttachment;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class is I/O Event queue for selector. It provides high level
 * asynchronized methods to manage all operations on the selector, like
 * registering and unregistering channels, or updating the events of
 * interest for each monitored socket.
 *
 * It supports pending invocation of methods. Only thread of ExtSelector
 * can access selector object and invoke methods on it or access all
 * sockets managed by it.
 *
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ExtSelector implements Runnable {
    /**
     * Logging handler.
     */
    private static final Logger log = Logger.getLogger(ExtSelector.class);

    /**
     * Selector used for I/O working.
     */
    private final Selector selector;

    /**
     * The thread associated with selector.
     */
    private final Thread currentThread;

    /**
     * Flag telling that selector must work
     * or gracefully terminated.
     */
    private boolean work = false;

    /**
     * List of tasks for pending invocation.
     */
    private final ArrayList<Runnable> pendingTasks = new ArrayList<Runnable>(128);

    /**
     * Executor for processing handlers.
     */
    private final Executor executor;

    /**
     * Creates a new selector and the associated thread started by this
     * constructor.
     *
     * @param threads number of threads for executor
     *
     * @throws IOException iff Selector.open() crushed.
     */
    public ExtSelector(int threads) throws IOException {
        executor = Executors.newFixedThreadPool(threads);
        selector = Selector.open();
        currentThread = Thread.currentThread();
    }

    /**
     * Add add channel interest to internal pending invocation queue.
     *
     * @param channel  to add interest
     * @param interest to add
     */
    public void addChannelInterestLater(final SelectableChannel channel, final int interest) {
        invokeLater(new Runnable() {
            public void run() {
                try {
                    addChannelInterestNow(channel, interest);
                } catch (Exception e) {
                    log.debug("Exception in addChannelInterestLater");
                    log.trace("Exception in addChannelInterestLater", e);
                }
            }
        });
    }

    /**
     * Add the specified interest to channel.
     *
     * @param channel  to add interest.
     * @param interest to add.
     *
     * @throws IOException iff errors.
     */
    public void addChannelInterestNow(SelectableChannel channel,
                                      int interest) throws IOException {
        if (Thread.currentThread() != currentThread) {
            throw new ReactorException("Method can't be called from non selector thread");
        }
        SelectionKey sk = channel.keyFor(selector);
        changeKeyInterest(sk, sk.interestOps() | interest);
    }

    /**
     * Add remove interest from channel task to internal pending invocation queue.
     *
     * @param channel  to remove interest.
     * @param interest to remove.
     */
    public void removeChannelInterestLater(final SelectableChannel channel, final int interest) {
        invokeLater(new Runnable() {
            public void run() {
                try {
                    removeChannelInterestNow(channel, interest);
                } catch (Exception e) {
                    log.debug("Exception in removeChannelInterestLater");
                    log.trace("Exception in removeChannelInterestLater", e);
                }
            }
        });
    }

    /**
     * Remove the specified interest from channel.
     *
     * @param channel  to remove interest.
     * @param interest interest to remove
     *
     * @throws IOException iff errors.
     */
    public void removeChannelInterestNow(SelectableChannel channel, int interest) throws IOException {
        if (Thread.currentThread() != currentThread) {
            throw new ReactorException("Method can't be called from non selector thread");
        }
        SelectionKey sk = channel.keyFor(selector);
        changeKeyInterest(sk, sk.interestOps() & ~interest);
    }

    /**
     * Set the interestOps of SelectionKey.
     *
     * @param sk             SelectionKey to set InterestOps
     * @param newInterestOps ops to set
     *
     * @throws IOException iff errors
     */
    private void changeKeyInterest(SelectionKey sk, int newInterestOps) throws IOException {
        try {
            sk.interestOps(newInterestOps);
        } catch (CancelledKeyException e) {
            throw new ReactorException("Exception in changeKeyInterest", e);
        }
    }

    /**
     * Add register channel task to internal pending invocation queue.
     *
     * @param channel       to register
     * @param selectionKeys interestOps
     * @param attachment    to channel
     */
    public void registerChannelLater(final SelectableChannel channel, final int selectionKeys, final SelectorAttachment attachment) {
        invokeLater(new Runnable() {
            public void run() {
                try {
                    registerChannelNow(channel, selectionKeys, attachment);
                } catch (IOException e) {
                    log.debug("Exception in registerChannelLater");
                    log.trace("Exception in registerChannelLater", e);
                }
            }
        });
    }

    /**
     * Register SelectableChannel in selector.
     *
     * @param channel       to register
     * @param selectionKeys interestOps
     * @param attachment    to channel
     *
     * @throws IOException iff errors
     */
    public void registerChannelNow(SelectableChannel channel, int selectionKeys, SelectorAttachment attachment) throws IOException {
        if (Thread.currentThread() != currentThread) {
            throw new ReactorException("Method can't be called from non selector thread");
        }

        if (!channel.isOpen()) {
            throw new ReactorException("Channel is not open.");
        }

        try {
            if (channel.isRegistered()) {
                SelectionKey sk = channel.keyFor(selector);
                sk.interestOps(selectionKeys);
                sk.attach(attachment);
            } else {
                channel.configureBlocking(false);
                channel.register(selector, selectionKeys, attachment);
            }
        } catch (Exception e) {
            throw new ReactorException("Error in registering channel", e);
        }
    }

    /**
     * Invoke the task in selector thread. This method is only schedules
     * the task.
     *
     * @param run task to schedule.
     */
    public void invokeLater(Runnable run) {
        synchronized (pendingTasks) {
            pendingTasks.add(run);
        }
        selector.wakeup();
    }

    /**
     * Invoke the task in selector thread. This method returns iff
     * task processed.
     *
     * @param task to invoke
     *
     * @throws InterruptedException iff interrupts
     */
    public void invokeAndWait(final Runnable task) throws InterruptedException {
        if (Thread.currentThread() == currentThread) {
            task.run();
        } else {
            final Object obj = new Object();
            final boolean[] invoked = {false};
            synchronized (obj) {
                this.invokeLater(new Runnable() {
                    public void run() {
                        task.run();
                        invoked[0] = true;
                        obj.notify();
                    }
                });
                while (!invoked[0]) {
                    obj.wait();
                }
            }
        }
    }

    /**
     * Invoke pending tasks. Locks pendingTasks list.
     */
    private void invokePendingTasks() {
        synchronized (pendingTasks) {
            for (Runnable pendingTask : pendingTasks) {
                pendingTask.run();
            }
            pendingTasks.clear();
        }
    }

    /**
     * Starts main loop.
     */
    public void start() {
        if (work) {
            throw new IllegalStateException();
        }
        
        work = true;
        run();
    }

    /**
     * Changes work flag to false. All pending tasks will be executed
     * before dying.
     */
    public void stop() {
        work = false;
        selector.wakeup();
    }

    /**
     * Main loop. This is where i/o events dispatching and executes
     * pending
     */
    @Override
    public void run() {
        try {
            while (true) {
                invokePendingTasks();

                if (!work) {
                    return;
                }

                int selectedKeys;
                try {
                    selectedKeys = selector.select();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    continue;
                }

                if (selectedKeys == 0) {
                    continue;
                }

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey sk = it.next();
                    it.remove();
                    try {
                        int readyOps = sk.readyOps();
                        sk.interestOps(sk.interestOps() & ~readyOps);
                        SelectorAttachment attachment = (SelectorAttachment) sk.attachment();

                        if (sk.isAcceptable()) {
                            ((Acceptor) attachment).doAccept();
                        } else if (sk.isConnectable()) {
                            ((Connector) attachment).doConnect();
                        } else {
                            final Connection connection = (Connection) sk.attachment();
                            if (sk.isValid() && sk.isReadable()) {
                                executor.execute(new Runnable() {
                                    public void run() {
                                        connection.handleRead();
                                    }
                                });
                            }

                            if (sk.isValid() && sk.isWritable()) {
                                executor.execute(new Runnable() {
                                    public void run() {
                                        connection.handleWrite();
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Throwable t) {
            closeSelector();

            log.error("Throwable in main selector loop", t);
        }
    }

    /**
     * Safely close all selection keys and selector.
     */
    private void closeSelector() {
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {
            try {
                key.channel().close();
            } catch (IOException e) {
                // no operation
            }
        }
        try {
            selector.close();
        } catch (IOException e) {
            // no operation
        }
    }
}
