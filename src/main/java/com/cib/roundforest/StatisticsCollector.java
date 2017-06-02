package com.cib.roundforest;

import com.cib.roundforest.pipes.Worker;
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
public class StatisticsCollector extends Worker<List<InputRecord>,StatisticsRecord> {
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
        //STOP_WORDS.add("s");
        //STOP_WORDS.add("t");
    }

    public StatisticsCollector() {
    }

    private void collectWords(StatisticsRecord result, String text) {
        String[] words = text.split("<[^>]*>|[\\W&&[^`']]|[`'](?!\\w)|(?<!\\w)[`']");
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

    @Override
    protected StatisticsRecord process(List<InputRecord> records) {
        StatisticsRecord result = new StatisticsRecord();
        records.forEach((record) -> {
            result.addProduct(record.getProductId(), 1);
            result.addUser(record.getUserId(), 1);
            collectWords(result, record.getSummary());
            collectWords(result, record.getText());
        });
        return result;
    }

}
