
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
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
    private static final int WAITING_TIME_BEFORE_WRITE = 1;
    
    /**
     * 
     */
    private FileChannel fileChannel;
    /**
     * 
     */
    private List<AbstractRow> dirty;

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
            for (AbstractRow row : dirty) {
                row.write(fileChannel);
            }
            dirty.clear();
        } catch (IOException | DAOException e) {
            e.printStackTrace(System.out);
        }
    };
    
    /**
     * 
     * @param row 
     */
    synchronized void addRowToDirtyList(AbstractRow row) {
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
        AbstractRow row = (AbstractRow) evt.getSource();
        addRowToDirtyList(row);
    }
}