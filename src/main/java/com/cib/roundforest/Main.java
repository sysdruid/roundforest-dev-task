package com.cib.roundforest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Yury Altukhou
 */
public class Main {

    private File inputFile;
    private int chunkLimit = 100;
    private int printLimit = 10;
    private int workerCount = 8;

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public void setPrintLimit(int printLimit) {
        this.printLimit = printLimit;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public void setChunkLimit(int chunkLimit) {
        this.chunkLimit = chunkLimit;
    }

    public static void main(String[] args) throws Exception{
        Main main = new Main();
        //main.setInputFile(new File("src/test/resources/small.csv"));
        main.setInputFile(new File("Reviews.csv"));
        main.setChunkLimit(1000);
        main.setPrintLimit(20);
        main.setWorkerCount(4);
        main.run();
    }

    public void run() throws IOException {
        long start = System.currentTimeMillis();
        CSVInputProvider provider = new CSVInputProvider(inputFile);
        InmemoryOutputConsumer consumer = new InmemoryOutputConsumer();

        provider.setChunkLimit(chunkLimit);
        consumer.setPrintLimit(printLimit);

        List<StatisticsCollector> collectorList = new ArrayList<>();
        for(int i=0; i<workerCount; i++) {
            StatisticsCollector collector = new StatisticsCollector(provider, consumer);
            collectorList.add(collector);
            collector.start();
        }
        collectorList.forEach((collector) -> {
            try {
                collector.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
            }
        });
        consumer.print(System.out);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Time running: %ss", (end-start)/1000));
    }

}
