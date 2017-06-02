package com.cib.roundforest.google;

/**
 *
 * @author yury.altukhou
 */
public interface GoogleTranslator {

    interface GoogleTranslationCallback {

        void translated(String text);
        
    }

    public void translate(String text, GoogleTranslationCallback callback);

}
