package com.cib.roundforest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Yury Altukhou
 */
public class StatisticsCollector implements Runnable{
    private static final Set<String> STOP_WORDS = new HashSet<>();
    static {
        try (Stream<String> stream = Files.lines(Paths.get("src/main/resources/stop-words.txt"))) {
            stream.forEach((line)->{
                line = line.trim();
                if(!line.isEmpty()) {
                    STOP_WORDS.add(line.toLowerCase());
                }
            });
        } catch (IOException ex) {
            throw new RuntimeException("Fail to read stop-words.txt", ex);
        }
        STOP_WORDS.add("s");
        STOP_WORDS.add("t");
    }
    private final OutputConsumer consumer;
    private final InputProvider provider;
    private final Thread thread;

    public StatisticsCollector(InputProvider provider, OutputConsumer consumer) {
        this.provider = provider;
        this.thread = new Thread(this);
        this.consumer = consumer;
    }

    @Override
    public void run() {
        List<InputRecord> records = provider.getRecords();
        while(records != null && !records.isEmpty()) {
            OutputRecord output = process(records);
            consumer.putOutputRecord(output);
            records = provider.getRecords();
        }
    }

    public void start() {
        thread.start();
    }
    public void join() throws InterruptedException {
        thread.join();
    }

    private void collectWords(OutputRecord result, String text) {
        String[] words = text.split("<[^>]*>|\\W");
        //String[] words = text.split("\\W");
        for(String word: words) {
            if(word.isEmpty()) {
                continue;
            }
            word = word.toLowerCase();
            if(!STOP_WORDS.contains(word)) {
                result.addWord(word, 1);
            }
        }
    }

    private OutputRecord process(List<InputRecord> records) {
        OutputRecord result = new OutputRecord();
        records.forEach((record) -> {
            result.addProduct(record.getProductId(), 1);
            result.addUser(record.getUserId(), 1);
            collectWords(result, record.getSummary());
            collectWords(result, record.getText());
        });
        return result;
    }

}
