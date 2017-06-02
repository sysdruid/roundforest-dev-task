package com.cib.roundforest;

import com.cib.roundforest.pipes.Worker;
import com.cib.roundforest.pipes.Source;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Yury Altukhou
 */
public class DataReader extends Worker<InputRecord,List<InputRecord>>{

    private int chunkSize = 10;

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    private boolean readFullChunk() {
        Source<InputRecord> source = getSource();
        List<InputRecord> result = new ArrayList<>(chunkSize);
        while(result.size() < chunkSize ) {
            InputRecord item = source.getData();
            if(item == null) {
                break;
            }
            result.add(item);
        }
        getSink().putData(result);
        return result.size() == chunkSize;
    }

    @Override
    protected void run() {
        while(readFullChunk()) {
        }
        try {
            getSink().close();
        } catch (IOException ex) {
            //do nothing
        }
    }



    

}
