package com.cib.roundforest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author Yury Altukhou
 */
public class CSVInputProvider implements InputProvider{
    private int chunkLimit = 1000;
    private final Iterator<CSVRecord> iterator;
    private final CSVParser parser;
    private int maxTextSize = 0;
    private int maxTextId = 0;
    private double avgTextSize = 0;
    private int count = 0;


    public void setChunkLimit(int chunkLimit) {
        this.chunkLimit = chunkLimit;
    }

    public CSVInputProvider(File file) throws IOException {
        this.parser = new CSVParser(new BufferedReader(new FileReader(file)), CSVFormat.DEFAULT);
        this.iterator = parser.iterator();
        if(iterator.hasNext()) {
            iterator.next();
        }
    }

    @Override
    public synchronized List<InputRecord> getRecords() {
        List<InputRecord> result = new ArrayList<>();
        for(;iterator.hasNext() && result.size() < chunkLimit;) {
            CSVRecord csvRecord = iterator.next();
            InputRecord record = new InputRecord();
            //Id,ProductId,UserId,ProfileName,HelpfulnessNumerator,HelpfulnessDenominator,Score,Time,Summary,Text
            record.setId(Integer.parseInt(csvRecord.get(0)));
            record.setProductId(csvRecord.get(1));
            record.setUserId(csvRecord.get(2));
            record.setProfileName(csvRecord.get(3));
            record.setHelpfulnessNumerator(Integer.parseInt(csvRecord.get(4)));
            record.setHelpfulnessDenominator(Integer.parseInt(csvRecord.get(5)));
            record.setScore(Integer.parseInt(csvRecord.get(6)));
            record.setTime(Long.parseLong(csvRecord.get(7)));
            record.setSummary(csvRecord.get(8));
            record.setText(csvRecord.get(9));
            result.add(record);
            int size = record.getText().length();
            if (maxTextSize< size) {
                maxTextSize = size;
                maxTextId = record.getId();
            }
            avgTextSize += size;
            count++;
        }
        if(!iterator.hasNext() && count != 0) {
            avgTextSize = avgTextSize/count;
            count = 0;
            System.out.println("maxTextSize: "+maxTextSize);
            System.out.println("maxTextId: "+maxTextId);
            System.out.println("avgTextSize: "+avgTextSize);
        }
        return result;
    }

}
