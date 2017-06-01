package com.cib.roundforest;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yury.altukhou
 */
public class TranslatorImpl implements Translator {

    private static final long EXECUTION_TIME = 200;
    private static final long CONCURENT_TASK_LIMIT = 100;
    private static final long REQUEST_SIZE_LIMIT = 1000;

    private static class TranslationRequest {
        String text;
        TranslationCallback callback;
        long time;
    }


    private final List<TranslationRequest> requestList = new LinkedList<>();
    private final Runnable worker = new Runnable(){
        @Override
        public void run() {
            try {
                for(;;) {
                    synchronized(requestList){
                        if(requestList.isEmpty()){
                            requestList.wait();
                        }
                        long currentTime = System.currentTimeMillis();
                        TranslationRequest request = requestList.get(0);
                        long passedTime = currentTime - request.time;
                        if(passedTime >= EXECUTION_TIME) {
                            requestList.remove(0);
                            request.callback.translated(request.text);
                        }
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(TranslatorImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    };

    public TranslatorImpl(){
        Thread thread = new Thread(worker);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public synchronized void translate(String text, TranslationCallback callback) {
        TranslationRequest request = new TranslationRequest();
        request.text = text;
        request.callback = callback;
        request.time = System.currentTimeMillis();
        addRequest(request);
    }


    private void addRequest(TranslationRequest request) {
        if(request.text.length() > REQUEST_SIZE_LIMIT){
            throw new RuntimeException("Too large request. request max size=" + REQUEST_SIZE_LIMIT);
        }
        synchronized(requestList){
            if(requestList.size() >= CONCURENT_TASK_LIMIT){
                throw new RuntimeException("Too many concurent tasks. Task limit = " + CONCURENT_TASK_LIMIT);
            }
            requestList.add(request);
            requestList.notify();
        }
    }

}
