package com.cib.roundforest.csv;

import com.cib.roundforest.InputRecord;
import com.cib.roundforest.pipes.Source;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author Yury Altukhou
 */
public class CSVSource implements Source<InputRecord>{
    private final Iterator<CSVRecord> iterator;
    private final CSVParser parser;

    public CSVSource(File file) throws IOException {
        this.parser = new CSVParser(new BufferedReader(new FileReader(file)), CSVFormat.DEFAULT);
        this.iterator = parser.iterator();
        if(iterator.hasNext()) {
            iterator.next();
        }
    }

    @Override
    public synchronized InputRecord getData() {
        InputRecord record;
        if(iterator.hasNext()) {
            CSVRecord csvRecord = iterator.next();
            record = new InputRecord();
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
        } else {
            try {
                parser.close();
            } catch (IOException ex) {
            }
            record = null;
        }
        return record;
    }
    
}