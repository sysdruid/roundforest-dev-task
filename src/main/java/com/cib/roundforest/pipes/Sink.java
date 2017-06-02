package com.cib.roundforest.pipes;

import java.io.Closeable;

/**
 *
 * @author Yury Altukhou
 */
public interface Sink<T> extends Closeable{

    void putData(T record);

}
