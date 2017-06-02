package com.cib.roundforest.pipes;

import java.io.IOException;

/**
 *
 * @author Yury Altukhou
 */
public class SinkReplicator<I> implements Sink<I>{

    private final Sink<I>[] sinks;

    public SinkReplicator(Sink<I>... sinks) {
        this.sinks = sinks;
    }

    @Override
    public void close() throws IOException {
        for(Sink<I> sink:sinks) {
            sink.close();
        }
    }

    @Override
    public void putData(I record) {
        for(Sink<I> sink:sinks) {
            sink.putData(record);
        }
    }


}
