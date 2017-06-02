package com.cib.roundforest.pipes;

/**
 *
 * @author Yury Altukhou
 */
public abstract class Worker<I,O> {

    private Sink<O> sink;
    private Source<I> source;
    private Thread thread;

    protected Worker(Sink<O> sink, Source<I> source) {
        this.sink = sink;
        this.source = source;
    }

    protected Worker() {
        this(null, null);
    }

    public void start() {
        if(thread != null) {
            return;
        }
        if(sink == null || source == null) {
            throw new NullPointerException("sink and source can not be null");
        }
        this.thread = new Thread(()->{
            run();
        });
        this.thread.setDaemon(true);
        this.thread.start();
    }

    public void setSink(Sink<O> sink) {
        this.sink = sink;
    }

    public void setSource(Source<I> source) {
        this.source = source;
    }

    public void join() throws InterruptedException {
        thread.join();
    }

    protected Sink<O> getSink() {
        return sink;
    }

    protected Source<I> getSource() {
        return source;
    }

    protected O process(I input) {
        return null;
    };

    protected void run() {
        I input = source.getData();
        while(input != null) {
            O output = process(input);
            sink.putData(output);
            input = source.getData();
        }
    }

}
