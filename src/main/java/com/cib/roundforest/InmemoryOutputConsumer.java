package com.cib.roundforest;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Yury Altukhou
 */
public class InmemoryOutputConsumer implements OutputConsumer{
    private final OutputRecord storage = new OutputRecord();
    private int printLimit = 10;

    @Override
    public synchronized void putOutputRecord(OutputRecord record) {
        record.getProductCounts().entrySet().forEach((entry) -> {
            storage.addProduct(entry.getKey(), entry.getValue());
        });
        record.getUserCounts().entrySet().forEach((entry) -> {
            storage.addUser(entry.getKey(), entry.getValue());
        });
        record.getWordCounts().entrySet().forEach((entry) -> {
            storage.addWord(entry.getKey(), entry.getValue());
        });
    }

    private void sortAndPrint(String name, Map<String,Integer> map, PrintStream out) {
        LinkedList<Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
        out.println(String.format(name + "(%7s):", list.size()));
        list.sort((entry1, entry2)->{
            return entry2.getValue() - entry1.getValue();
        });
        int size = Math.min(list.size(), printLimit);
        list.subList(0, size).forEach((entry)-> {
            out.println(String.format("%-20s %10s", entry.getKey(), entry.getValue()));
        });

    }

    public void print(PrintStream out) {
        sortAndPrint("Products", storage.getProductCounts(), out);
        sortAndPrint("Users", storage.getUserCounts(), out);
        sortAndPrint("Words", storage.getWordCounts(), out);
    }

    void setPrintLimit(int printLimit) {
        this.printLimit = printLimit;
    }

}
