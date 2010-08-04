package ru.frostman.scalable.reactor.utils.impl.array;

import org.junit.Assert;
import org.junit.Test;
import ru.frostman.scalable.reactor.utils.impl.array.ArraySynchronizedDataQueuePool;


/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */

public class ArraySynchronizedDataQueuePoolTest {
    @Test
    public void testInitialization() throws Exception {
        ArraySynchronizedDataQueuePool dataQueuePool = new ArraySynchronizedDataQueuePool(10000, 32, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializationException() throws Exception {
        ArraySynchronizedDataQueuePool dataQueuePool = new ArraySynchronizedDataQueuePool(-1, -1, -1);                
    }

    @Test
    public void testSingleThreadUsing() throws Exception {
        ArraySynchronizedDataQueuePool dataQueuePool = new ArraySynchronizedDataQueuePool(10000, 32, 4);

        Assert.assertEquals(10000, dataQueuePool.availableDataQueuesCount());

        for (int i = 0; i < 15000; i++) {
            Assert.assertNotNull(dataQueuePool.acquireDataQueue());
        }

        Assert.assertEquals(32, dataQueuePool.getDataQueueSize());
        Assert.assertEquals(4, dataQueuePool.getPacketSize());
    }

    @Test
    public void testMultiThreadUsing() throws Exception {
        ArraySynchronizedDataQueuePool dataQueuePool = new ArraySynchronizedDataQueuePool(10000, 32, 4);
    }
}
