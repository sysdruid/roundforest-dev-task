package com.cib.roundforest;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Yury Altukhou
 */
public class OutputRecord {

    private final Map<String,Integer> userCounts = new HashMap<>();
    private final Map<String,Integer> productCounts = new HashMap<>();
    private final Map<String,Integer> wordCounts = new HashMap<>();

    public Map<String, Integer> getUserCounts() {
        return userCounts;
    }

    public Map<String, Integer> getProductCounts() {
        return productCounts;
    }

    public Map<String, Integer> getWordCounts() {
        return wordCounts;
    }

    private void addCount(Map<String,Integer> map, String key, int increment) {
        Integer count = map.get(key);
        if(count == null) {
            map.put(key, increment);
        } else {
            map.put(key, count+ increment);
        }
    }

    public void addProduct(String id, int increment) {
        addCount(productCounts, id, increment);
    }

    public void addUser(String id, int increment) {
        addCount(userCounts, id, increment);
    }

    public void addWord(String word, int increment) {
        addCount(wordCounts, word, increment);
    }

}
