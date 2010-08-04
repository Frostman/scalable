package ru.frostman.scalable.reactor.utils.impl.array;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ArraySynchronizedDataQueueTest {
    @Test
    public void testInitialization() throws Exception {
        ArraySynchronizedDataQueue queue = new ArraySynchronizedDataQueue(32, 4);
        queue.getFilledBuffer();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializationException() throws Exception {
        ArraySynchronizedDataQueue queue = new ArraySynchronizedDataQueue(-1, 4);
        queue.getFilledBuffer();
    }

    @Test
    public void testSingleThreadUsing() throws Exception {
        ArraySynchronizedDataQueue queue = new ArraySynchronizedDataQueue(32, 4);

        Assert.assertEquals(32, queue.freeBuffersCount());
        Assert.assertEquals(0, queue.filledBuffersCount());
        Assert.assertNotNull(queue.getFreeBuffer());
        Assert.assertNull(queue.getFilledBuffer());
        queue.fillBuffer();
        Assert.assertEquals(31, queue.freeBuffersCount());

        for (int i = 1; i < 32; i++) {
            Assert.assertNotNull(queue.getFreeBuffer());
            queue.fillBuffer();
        }

        Assert.assertNull(queue.getFreeBuffer());

        for (int i = 0; i < 32; i++) {
            Assert.assertNotNull(queue.getFilledBuffer());
            queue.freeBuffer();
        }

        Assert.assertNull(queue.getFilledBuffer());
        Assert.assertEquals(0, queue.filledBuffersCount());
        Assert.assertEquals(32, queue.freeBuffersCount());
    }

    @Test
    public void testMultiThreadUsing() throws Exception {
        final ArraySynchronizedDataQueue dataQueue = new ArraySynchronizedDataQueue(32, 4);
    }
}
