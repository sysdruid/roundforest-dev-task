package com.cib.roundforest;


import java.io.File;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Yury Altukhou
 */


@Test()
public class CSVReaderTest {
    private CSVInputProvider provider;


    @BeforeMethod
    public void createProvider() throws Exception{
        this.provider = new CSVInputProvider(new File("src/test/resources/small.csv"));
    }

    @Test
    public void testReadAll() throws Exception {
        List<InputRecord> records = provider.getRecords();
        Assert.assertEquals(records.size(), 20, "records.size()");
    }

    @Test()
    public void testRecord() {
        List<InputRecord> records = provider.getRecords();
        InputRecord record = records.get(0);
        Assert.assertEquals(record.getId(), 1, "record(0).id");
        Assert.assertEquals(record.getProductId(), "B001E4KFG0", "record(0).productId");
        Assert.assertEquals(record.getUserId(), "A3SGXH7AUHU8GW", "record(0).userId");
        Assert.assertEquals(record.getProfileName(), "delmartian", "record(0).profileName");
        Assert.assertEquals(record.getHelpfulnessNumerator(),1, "record(0).helpfulnessNumerator");
        Assert.assertEquals(record.getHelpfulnessDenominator(),1, "record(0).helpfulnessDenominator");
        Assert.assertEquals(record.getScore(),5, "record(0).score");
        Assert.assertEquals(record.getTime(),1303862400, "record(0).time");
        Assert.assertEquals(record.getSummary(),"Good Quality Dog Food", "record(0).summary");
        Assert.assertEquals(record.getText(),"I have bought several of the Vitality canned dog food products and have found them all to be of good quality. The product looks more like a stew than a processed meat and it smells better. My Labrador is finicky and she appreciates this product better than  most.", "record(0).text");
        record = records.get(1);
        Assert.assertEquals(record.getId(), 2, "record(1).id");
        Assert.assertEquals(record.getProductId(), "B00813GRG4", "record(1).productId");
        Assert.assertEquals(record.getUserId(), "A1D87F6ZCVE5NK", "record(1).userId");

    }

    @Test
    void testChunk() throws Exception {
        provider.setChunkLimit(10);
        List<InputRecord> records = provider.getRecords();
        Assert.assertEquals(records.size(), 10, "records.size()");
        records = provider.getRecords();
        Assert.assertEquals(records.size(), 10, "records.size()");
        records = provider.getRecords();
        Assert.assertEquals(records.size(), 0, "records.size()");
    }


}
