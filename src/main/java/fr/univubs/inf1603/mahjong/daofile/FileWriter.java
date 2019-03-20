
package fr.univubs.inf1603.mahjong.daofile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author aliyou
 */
public class FileWriter implements PropertyChangeListener {

    /**
     * 
     */
    private static final int WAITING_TIME_BEFORE_WRITE = 2;
    
    /**
     * 
     */
    private FileChannel fileChannel;
    /**
     * 
     */
    private List<Row> dirty;

    /**
     * 
     */
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    /**
     * 
     */
    ScheduledFuture<?> future = null;
    
    /**
     * Constructeur avec un FileChannel
     * @param fileChannel FileChannel
     */
    FileWriter(FileChannel fileChannel) {
        if (fileChannel == null) {
            throw new IllegalArgumentException("fileChannel == null");
        }
        this.fileChannel = fileChannel;
        this.dirty = new ArrayList<>();
    }

    /**
     * 
     */
    Runnable writeToDisk = () -> {
        try {
            for (Row row : dirty) {
                row.write(fileChannel);
            }
            dirty.clear();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    };
    
    /**
     * 
     * @param row 
     */
    synchronized void addRecordToDirtyList(Row row) {
        if ( ! dirty.contains(row)) {
            dirty.add(row);
            if (future != null) {
                future.cancel(true);
            }
            future = ses.schedule(writeToDisk, WAITING_TIME_BEFORE_WRITE, TimeUnit.SECONDS);
        }
    }

    /**
     * 
     * @param evt 
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Row row = (Row) evt.getSource();
        addRecordToDirtyList(row);
    }
}