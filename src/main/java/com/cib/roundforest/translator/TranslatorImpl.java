package com.cib.roundforest.translator;

import com.cib.roundforest.google.GoogleTranslator;
import com.cib.roundforest.google.GoogleTranslatorMock;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Yury Altukhou
 */
public class TranslatorImpl implements Translator{

    private final static int MAX_REQUEST_COUNT = 100;
    private final GsonBuilder gsonBuilder;

    private GoogleTranslator googleTranslator = new GoogleTranslatorMock();

    private final Object semaphore = new Object();
    private int count = 0 ;

    @Override
    public <T> void translate(T key, String text, TranslatorCallback<T> callback) {
        synchronized(semaphore) {
            while(count == MAX_REQUEST_COUNT) {
                try {
                    semaphore.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            googleTranslator.translate(request(text), (translated) -> {
                TranslationResult[] result =  gson().fromJson(translated, TranslationResult[].class);
                decCount();
                callback.translated(key, result[0].text);
            });
            count++;
        }
    }

    public TranslatorImpl() {
        this.gsonBuilder = new GsonBuilder();
        gsonBuilder.disableHtmlEscaping();
    }

    private synchronized Gson gson() {
        return gsonBuilder.create();
    }

    private void decCount() {
        synchronized(semaphore) {
            count--;
            semaphore.notifyAll();
        }
    }

    private String request(String text) {
        text = text.substring(0, Math.min(900, text.length()));
        text = gson().toJson(text);
        final String template= "[{input_lang:\"en\",output_lang:\"fr\",text:%s}]";
        return String.format(template, text);
    }

    private final static class  TranslationResult {
        String text;
    }
}
