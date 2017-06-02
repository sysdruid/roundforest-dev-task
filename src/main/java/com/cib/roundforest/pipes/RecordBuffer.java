package com.cib.roundforest.pipes;

import com.cib.roundforest.pipes.Source;
import com.cib.roundforest.pipes.Sink;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yury.altukhou
 */
public class RecordBuffer<I> implements Source<I>, Sink<I>{

    private int bufferSize = 10;
    private final List<I> buffer = new LinkedList<>();
    private boolean closed = false;

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public I getData() {
        synchronized(buffer) {
            while(buffer.isEmpty() && ! closed) {
                waitOn(buffer);
            }
            I result = closed && buffer.isEmpty() ? null : buffer.remove(0);
            notifyAll(buffer);
            return result;
        }
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        notifyAll(buffer);
    }

    @Override
    public void putData(I item) {
        synchronized(buffer) {
            while(buffer.size() >= bufferSize) {
                if(closed) {
                    throw new RuntimeException("buffer is closed.");
                }
                waitOn(buffer);
            }
            if(closed) {
                throw new RuntimeException("buffer is closed.");
            }
            buffer.add(item);
            notifyAll(buffer);
        }
    }

    private void notifyAll(Object semaphore) {
        synchronized(semaphore) {
            semaphore.notifyAll();
        }
    }

    private void waitOn(Object semaphore) {
        synchronized(semaphore) {
            try {
                semaphore.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(RecordBuffer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}