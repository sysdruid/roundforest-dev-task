package com.cib.roundforest;

import com.cib.roundforest.csv.CSVSink;
import com.cib.roundforest.csv.CSVSource;
import com.cib.roundforest.pipes.RecordBuffer;
import com.cib.roundforest.pipes.SinkReplicator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yury Altukhou
 */
public class Main {
    private InmemoryStatisticsConsumer statisticResults;

    private File inputFile;
    private int chunkSize = 100;
    private int printLimit = 10;
    private boolean translate = false;
    private int workerCount = 4;

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
        this.chunkSize = chunkLimit;
    }

    public static void main(String[] args) throws Exception{
        Main main = new Main();
        File file = new File("src/test/resources/small.csv");
        for(String arg: args) {
            if(arg.startsWith("translate=")) {
                arg = arg.substring(10).toLowerCase();
                main.setTranslate("true".equals(arg));
            } else {
                file = new File(arg);
            }
        }
        main.setInputFile(file);
        //main.setInputFile(new File("Reviews.csv"));
        main.setChunkLimit(200);
        main.setPrintLimit(1000);
        main.setWorkerCount(4);
        main.run();
    }

    public void run() throws Exception {
        if(!inputFile.exists()) {
            throw new FileNotFoundException("Can not find input file: "+inputFile);
        }
        long start = System.currentTimeMillis();
        CSVSource csvReader = new CSVSource(inputFile);
        this.statisticResults = new InmemoryStatisticsConsumer();
        DataReader reader = new DataReader();
        CSVSink csvSink = new CSVSink(new File("target/Translated.csv"));

        RecordBuffer<List<InputRecord>> buffer = new RecordBuffer<>();
        RecordBuffer<List<InputRecord>> buffer2 = new RecordBuffer<>();
        SinkReplicator<List<InputRecord>> replicator = translate ? 
                                                       new SinkReplicator<>(buffer, buffer2)
                                                       : new SinkReplicator<>(buffer) ;
        TranslationWorker translatorWorker = new TranslationWorker();
        translatorWorker.setSource(buffer2);
        translatorWorker.setSink(csvSink);

        reader.setChunkSize(chunkSize);
        buffer.setBufferSize(workerCount*2);
        buffer2.setBufferSize(workerCount*2);
        statisticResults.setPrintLimit(printLimit);

        reader.setSource(csvReader);
        reader.setSink(replicator);
        

        reader.start();
        if(translate) {
            translatorWorker.start();
        }

        List<StatisticsCollector> collectorList = new ArrayList<>();
        for(int i=0; i<workerCount; i++) {
            StatisticsCollector collector = new StatisticsCollector();
            collector.setSource(buffer);
            collector.setSink(statisticResults);
            collectorList.add(collector);
            collector.start();
        }
        collectorList.forEach((collector) -> {
            try {
                collector.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        if(translate) {
            translatorWorker.join();
        }
        csvSink.close();
        statisticResults.print(System.out);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Time running: %ss", (end-start)/1000));
    }

    public InmemoryStatisticsConsumer getStatisticResults() {
        return statisticResults;
    }

    public void setTranslate(boolean translate) {
        this.translate = translate;
    }

}
