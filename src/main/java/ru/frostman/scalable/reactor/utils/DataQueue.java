package ru.frostman.scalable.reactor.utils;

import java.nio.ByteBuffer;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public interface DataQueue {
    
    public ByteBuffer getFreeBuffer();
    public void fillBuffer();

    public ByteBuffer getFilledBuffer();
    public void freeBuffer();

    public void clearBuffers();

    public int freeBuffersCount();
    public int filledBuffersCount();

}
