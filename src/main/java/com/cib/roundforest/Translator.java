package com.cib.roundforest;

/**
 *
 * @author yury.altukhou
 */
public interface Translator {

    interface TranslationCallback {

        void translated(String text);
        
    }


    public void translate(String text, TranslationCallback callback);

}
