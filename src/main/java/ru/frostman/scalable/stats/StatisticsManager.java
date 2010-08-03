package ru.frostman.scalable.stats;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class StatisticsManager implements Runnable {
    private static final Logger log = Logger.getLogger(StatisticsManager.class);
    private static Thread current;
    private static int reportingTimeout = 1;
    private static OutputStream out;
    private final AtomicLong bytesCounter = new AtomicLong();
    private long lastStatsWrite;
    private static volatile boolean work = true;

    private StatisticsManager() {
    }

    public static StatisticsManager getInstance() {
        return Internal.INSTANCE;
    }

    public static void init(int repTimeout, OutputStream outputStream) {
        reportingTimeout = repTimeout;
        out = outputStream;

        log.info("StatisticsManager initialized successfully");
        log.info("Reporting timeout = " + reportingTimeout);
    }

    public static void setWork(boolean work) {
        StatisticsManager.work = work;
    }

    @Override
    public void run() {
        lastStatsWrite = System.nanoTime();
        reportingTimeout *= 1000000000;

        while (work) {
            long currentTime = System.nanoTime();

            if (currentTime - lastStatsWrite >= reportingTimeout) {
                try {
                    writeStats(bytesCounter.getAndSet(0), currentTime);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    lastStatsWrite = currentTime;
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                log.debug("sleep interrupted in stats main loop", e);
            }
        }
    }

    public void increment() {
        bytesCounter.incrementAndGet();
    }

    private void writeStats(long count, long currentTime) throws IOException {
        StringBuilder sb = new StringBuilder();
        long t = count / ((currentTime - lastStatsWrite) / 1000000000);     
        sb.append("Statistics: ").append(t).append(" tps\n");
        out.write(sb.toString().getBytes());
    }

    private static final class Internal {
        private static final StatisticsManager INSTANCE = new StatisticsManager();

        static {
            current = new Thread(INSTANCE);
            current.setPriority(Thread.MAX_PRIORITY - 1);
            current.start();
        }
    }


}
