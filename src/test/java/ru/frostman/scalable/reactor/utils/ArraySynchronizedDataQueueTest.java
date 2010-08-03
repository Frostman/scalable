package ru.frostman.scalable.reactor.utils;

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
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializationException() throws Exception {
        ArraySynchronizedDataQueue queue = new ArraySynchronizedDataQueue(-1, 4);
    }

    @Test
    public void testSingleThreadUsing() throws Exception {
        ArraySynchronizedDataQueue queue = new ArraySynchronizedDataQueue(32, 4);

        Assert.assertNotNull(queue.getFreeBuffer());
        Assert.assertEquals(0, queue.size());
        Assert.assertNull(queue.getFilledBuffer());
        queue.fillBuffer();
        Assert.assertEquals(1, queue.size());

        for (int i = 1; i < 32; i++) {
            Assert.assertNotNull(queue.getFreeBuffer());
            queue.fillBuffer();
        }               

        Assert.assertNull(queue.getFreeBuffer());
        Assert.assertNotNull(queue.getFilledBuffer());
    }
}
