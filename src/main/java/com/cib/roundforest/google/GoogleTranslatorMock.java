package com.cib.roundforest.google;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yury.altukhou
 */
public class GoogleTranslatorMock implements GoogleTranslator {

    private static final long EXECUTION_TIME = 10;
    private static final long CONCURENT_TASK_LIMIT = 100;
    private static final long REQUEST_SIZE_LIMIT = 1000;

    private static class GoogleTranslationRequest {
        String text;
        String translation;
        GoogleTranslationCallback callback;
        long time;
    }


    private final List<GoogleTranslationRequest> requestList = new LinkedList<>();
    private final Runnable worker = new Runnable(){
        @Override
        public void run() {
            try {
                for(;;) {
                    GoogleTranslationRequest request;
                    synchronized(requestList){
                        if(requestList.isEmpty()){
                            requestList.wait();
                        }
                        long currentTime = System.currentTimeMillis();
                        request = requestList.get(0);
                        if(request.translation == null) {
                            request.translation = translate(request.text);
                        }
                        long passedTime = currentTime - request.time;
                        if(passedTime >= EXECUTION_TIME) {
                            requestList.remove(0);
                        } else {
                            request = null;
                            requestList.wait(EXECUTION_TIME-passedTime);
                        }
                    }
                    if(request != null) {
                        request.callback.translated(request.translation);
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(GoogleTranslatorMock.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    };

    public GoogleTranslatorMock(){
        Thread thread = new Thread(worker);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void translate(String text, GoogleTranslationCallback callback) {
        GoogleTranslationRequest request = new GoogleTranslationRequest();
        request.text = text;
        request.callback = callback;
        request.time = System.currentTimeMillis();
        addRequest(request);
    }


    private void addRequest(GoogleTranslationRequest request) {
        if(request.text.length() > REQUEST_SIZE_LIMIT){
            throw new RuntimeException("Too large request. request max size=" + REQUEST_SIZE_LIMIT+", message: "+request.text);
        }
        synchronized(requestList){
            if(requestList.size() >= CONCURENT_TASK_LIMIT){
                throw new RuntimeException("Too many concurent tasks. Task limit = " + CONCURENT_TASK_LIMIT);
            }
            requestList.add(request);
            requestList.notify();
        }
    }

    private String translate(String text) {
        Gson gson = new Gson(); 
        InputTranslationRecord[] request = gson.fromJson(text, InputTranslationRecord[].class);
        List<OutputTranslationRecord> result = new ArrayList<>(request.length);
        for(InputTranslationRecord record:request) {
            OutputTranslationRecord out = new OutputTranslationRecord();
            out.text = "(("+record.text+"))";
            result.add(out);
        }
        return gson.toJson(result);
    }

    //{input_lang: ‘en’, output_lang: ‘fr’, text: “Hello John, how are you?”}
    private final static class  InputTranslationRecord {
        String input_lang;
        String output_lang;
        String text;
    }

    private final static class  OutputTranslationRecord {
        String text;
    }

}
