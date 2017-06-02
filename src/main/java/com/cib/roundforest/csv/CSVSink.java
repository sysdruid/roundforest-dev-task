package com.cib.roundforest.csv;

import com.cib.roundforest.InputRecord;
import com.cib.roundforest.pipes.Sink;
import java.io.*;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 *
 * @author Yury Altukhou
 */
public class CSVSink implements Sink<List<InputRecord>>{
    private final CSVPrinter printer;


    public CSVSink(File file) throws IOException {
        Writer writer = new FileWriter(file);
        writer = new BufferedWriter(writer);
        printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
    }
    
    @Override
    public void close() throws IOException {
        printer.close();
    }
    
    @Override
    public void putData(List<InputRecord> list) {
        try {
            for(InputRecord item:list) {
                printer.print(item.getId());
                printer.print(item.getProductId());
                printer.print(item.getUserId());
                printer.print(item.getProfileName());
                printer.print(item.getHelpfulnessNumerator());
                printer.print(item.getHelpfulnessDenominator());
                printer.print(item.getScore());
                printer.print(item.getTime());
                printer.print(item.getSummary());
                printer.print(item.getText());
                printer.println();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
