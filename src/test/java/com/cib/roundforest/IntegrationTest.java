package com.cib.roundforest;

import java.io.File;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Yury Altukhou
 */
@Test()
public class IntegrationTest {


    @Test
    public void testMain() throws Exception {
        Main main = new Main();
        main.setTranslate(false);
        main.setInputFile(new File("src/test/resources/small.csv"));
        main.setChunkLimit(200);
        main.setPrintLimit(20);
        main.setWorkerCount(4);
        main.run();
        InmemoryStatisticsConsumer results = main.getStatisticResults();
        StatisticsRecord storage = results.getStorage();
        Map<String, Integer> products = storage.getProductCounts();
        Assert.assertEquals(products.size(), 10, "products.size");
        Assert.assertEquals(products.get("B001GVISJM"), new Integer(7), "products(B001GVISJM)");
        Assert.assertEquals(products.get("B006K2ZZ7K"), new Integer(4), "products(B006K2ZZ7K)");

        Map<String, Integer> users = storage.getUserCounts();
        Assert.assertEquals(users.size(), 20, "users.size");
        Assert.assertEquals(users.get("A1MZYO9TZK0BBI"), new Integer(1), "users(A1MZYO9TZK0BBI)");
        Assert.assertEquals(users.get("A1CZX3CP8IKQIJ"), new Integer(1), "users(A1CZX3CP8IKQIJ)");

        Map<String, Integer> words = storage.getWordCounts();
        Assert.assertEquals(words.size(), 360, "words.size");
        Assert.assertEquals(words.get("food"), new Integer(15), "words(food)");
        Assert.assertEquals(words.get("taffy"), new Integer(11), "words(taffy)");

    }

}
