package com.cib.roundforest.translator;

/**
 *
 * @author Yury Altukhou
 */
public interface Translator {

    interface TranslatorCallback<T> {

        void translated(T key, String text);
        
    }

    public <T> void translate(T key, String text,  TranslatorCallback<T> callback);

}