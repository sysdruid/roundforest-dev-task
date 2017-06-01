package com.cib.roundforest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author yury.altukhou
 */
public class RecordBuffer implements InputProvider, Runnable{

    private final static int BUFFER_SIZE = 10000;
    private final static int DEFAULT_CHUNK_LIMIT = 1000;
    private final List<InputRecord> buffer = new LinkedList<>();
    private final InputProvider provider;
    private final Thread thread;
    private final Object read = "read";
    private final Object write = "write";
    private boolean everythingRead = false;
    private int chunkLimit = DEFAULT_CHUNK_LIMIT;
    int recordsRead = 0;

    public RecordBuffer(InputProvider provider) {
        this.provider = provider;
        this.thread = new Thread(this);
        this.thread.setDaemon(true);
        this.thread.start();
    }

    @Override
    public void run() {
        for (;;) {
            int size;
            synchronized(read){
                size = buffer.size();
            }
            if( size < BUFFER_SIZE){
                List<InputRecord> records = provider.getRecords();
                recordsRead += records.size();
                System.out.println("read = " + recordsRead);
                if(records == null || records.isEmpty()) {
                        everythingRead = true;
                        notifyAll(write);
                    return;
                }
                    buffer.addAll(records);
                    notifyAll(write);
            } else {
                waitOn(read);
            }
        }
    }
    
    @Override
    public List<InputRecord> getRecords() {
        synchronized(buffer){
            while(buffer.size() < chunkLimit && ! everythingRead){
                waitOn(write);
            }
            int size = Math.min(chunkLimit, buffer.size());
            List<InputRecord> result = new ArrayList<>(buffer.subList(0, size));
            for(int i=0;i<size;i++){
                buffer.remove(0);
            }
            notifyAll(read);

            return result;
        }
    }
    
    void setChunkLimit(int chunkLimit) {
        this.chunkLimit = chunkLimit;
    }
    
    private void waitOn(Object semaphore) {
        System.out.println("waitOn("+semaphore+")");
        try {
            synchronized(semaphore){
                semaphore.wait();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    private void notifyAll(Object semaphore){
        System.out.println("notifyAll("+semaphore+")");
        synchronized(semaphore){
            semaphore.notifyAll();
        }
        
    }
    
}