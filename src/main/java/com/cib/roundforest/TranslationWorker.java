package com.cib.roundforest;

import com.cib.roundforest.pipes.Worker;
import com.cib.roundforest.translator.Translator;
import com.cib.roundforest.translator.Translator.TranslatorCallback;
import com.cib.roundforest.translator.TranslatorImpl;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Yury Altukhou
 */
public class TranslationWorker extends Worker<List<InputRecord>,List<InputRecord>>{
    Translator translator = new TranslatorImpl();

    @Override
    protected List<InputRecord> process(List<InputRecord> input) {
        List<TranslationCallback> callbackList = new ArrayList<>(input.size());
        List<InputRecord> result = new ArrayList<>(input.size());
        for(InputRecord item:input) {
            item = item.copy();
            String text = item.getText();
            String summary = item.getSummary();

            TextTranslationCallback callback = new TextTranslationCallback(item);
            callbackList.add(callback);
            translator.translate(text, text, callback);
            SummaryTranslationCallback summaryCallback = new SummaryTranslationCallback(item);
            callbackList.add(summaryCallback);
            translator.translate(summary, summary, summaryCallback);
            result.add(item);
        }
        for(TranslationCallback callback:callbackList) {
            callback.waitTranslation();
        }
        return result;
    }

    private static class TextTranslationCallback extends TranslationCallback{
        TextTranslationCallback(InputRecord record) {
            super(record);
        }

        @Override
        protected void setText(String text) {
            record.setText(text);
        }
    }

    private static class SummaryTranslationCallback extends TranslationCallback{
        SummaryTranslationCallback(InputRecord record) {
            super(record);
        }

        @Override
        protected void setText(String text) {
            record.setSummary(text);
        }
    }

    private static abstract class TranslationCallback implements TranslatorCallback{
        protected final InputRecord record;
        private boolean translated = false;

        TranslationCallback(InputRecord record) {
            this.record = record;
        }

        @Override
        public synchronized void translated(Object key, String text) {
            setText(text);
            this.translated = true;
            this.notifyAll();
        }

        public synchronized void waitTranslation() {
            if(!translated) {
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        protected abstract void setText(String text);

    }

}
