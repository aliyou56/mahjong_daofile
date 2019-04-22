
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.filemanagement.RowUtilities;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.IndexRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.AbstractRow;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.Index;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test pour la classe RowUtilities
 * 
 * @author aliyou
 */
public class RowUtilitiesTest {
    
    public RowUtilitiesTest() {
    }
    
    /**
     * Test of updateRowsPointer method, of class RowUtilities.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testUpdateRowsPointer() throws DAOFileException {
        System.out.println("updateRowsPointer");
        List<AbstractRow> sortedListByPointer = new ArrayList<>();
        
        int rowSize = 24;
        long pointer1 = rowSize * 1;
        AbstractRow r1 = new IndexRow(1, new Index(UUID.randomUUID(), 15), pointer1);
        long pointer2 = rowSize * 6;
        AbstractRow r2 = new IndexRow(2, new Index(UUID.randomUUID(), 16), pointer2);
        long pointer3 = rowSize * 7;
        AbstractRow r3 = new IndexRow(3, new Index(UUID.randomUUID(), 36), pointer3);
        sortedListByPointer.add(r1);
        sortedListByPointer.add(r2);
        sortedListByPointer.add(r3);
        
        long rowPointer = rowSize;
        int offset = rowSize * 4;
        RowUtilities.updateRowsPointer(sortedListByPointer, rowPointer, offset);
        
        assertEquals(pointer1, sortedListByPointer.get(0).getRowPointer());
        assertEquals(rowSize * 2, sortedListByPointer.get(1).getRowPointer());
        assertEquals(rowSize * 3, sortedListByPointer.get(2).getRowPointer());
    }

    /**
     * Test of getRowFromSortedListByPointer method, of class RowUtilities.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetRowFromSortedListByPointer() throws DAOFileException {
        System.out.println("getRowFromSortedListByPointer");
        List<AbstractRow> sortedListByPointer = new ArrayList<>();
        
        int rowSize = 24;
        long pointer1 = rowSize * 1;
        long pointer2 = rowSize * 6;
        long pointer3 = rowSize * 7;
        AbstractRow r1 = new IndexRow(1, new Index(UUID.randomUUID(), 15), pointer1);
        AbstractRow r2 = new IndexRow(2, new Index(UUID.randomUUID(), 16), pointer2);
        AbstractRow r3 = new IndexRow(3, new Index(UUID.randomUUID(), 36), pointer3);
        sortedListByPointer.add(r1);
        sortedListByPointer.add(r2);
        sortedListByPointer.add(r3);
        
        assertEquals(r2, RowUtilities.getRowFromSortedListByPointer(sortedListByPointer, pointer2));
        assertEquals(r3, RowUtilities.getRowFromSortedListByPointer(sortedListByPointer, pointer3));
        assertEquals(r1, RowUtilities.getRowFromSortedListByPointer(sortedListByPointer, pointer1));
        
        assertEquals(null, RowUtilities.getRowFromSortedListByPointer(sortedListByPointer, pointer2+pointer3));
    }

    /**
     * Test of addRowToSortedListByPointer method, of class RowUtilities.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testAddRowToSortedListByPointer() throws DAOFileException {
        System.out.println("addRowToSortedListByPointer");
        List<AbstractRow> sortedListByPointer = new ArrayList<>();
        
        int rowSize = 24;
        AbstractRow r1 = new IndexRow(1, new Index(UUID.randomUUID(), 15), rowSize * 1);
        AbstractRow r2 = new IndexRow(2, new Index(UUID.randomUUID(), 16), rowSize * 6);
        AbstractRow r3 = new IndexRow(3, new Index(UUID.randomUUID(), 36), rowSize * 18);
        
        RowUtilities.addRowToSortedListByPointer(sortedListByPointer, r2);
        RowUtilities.addRowToSortedListByPointer(sortedListByPointer, r3);
        RowUtilities.addRowToSortedListByPointer(sortedListByPointer, r1);
        
        assertEquals(r1, sortedListByPointer.get(0));
        assertEquals(r2, sortedListByPointer.get(1));
        assertEquals(r3, sortedListByPointer.get(2));
    }

    /**
     * Test of addRowToSortedListByUUID method, of class RowUtilities.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testAddRowToSortedListByUUID() throws DAOFileException {
        System.out.println("addRowToSortedListByUUID");
        List<AbstractRow> sortedListByUUID = new ArrayList<>(); 
        
        int rowSize = 24;
        AbstractRow r1 = new IndexRow(1, new Index(new UUID(0, 29), 15), rowSize * 1);
        AbstractRow r2 = new IndexRow(2, new Index(new UUID(0, 48), 16), rowSize * 6);
        AbstractRow r3 = new IndexRow(3, new Index(new UUID(0, 223), 36), rowSize * 18);
        
        RowUtilities.addRowToSortedListByUUID(sortedListByUUID, r2);
        RowUtilities.addRowToSortedListByUUID(sortedListByUUID, r3);
        RowUtilities.addRowToSortedListByUUID(sortedListByUUID, r1);
        
        assertEquals(r1, sortedListByUUID.get(0));
        assertEquals(r2, sortedListByUUID.get(1));
        assertEquals(r3, sortedListByUUID.get(2));
    }

    /**
     * Test of getRowPositionFormSortedListByPointer method, of class RowUtilities.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetRowPositionFormSortedListByPointer() throws DAOFileException {
        System.out.println("getRowPositionFormSortedListByPointer");
        List<AbstractRow> sortedListByPointer = new ArrayList<>();
        
        long pointer1 = 12;
        AbstractRow r1 = new IndexRow(1, new Index(UUID.randomUUID(), 15), pointer1);
        long pointer2 = 24;
        AbstractRow r2 = new IndexRow(2, new Index(UUID.randomUUID(), 16), pointer2);
        long pointer3 = 36;
        AbstractRow r3 = new IndexRow(3, new Index(UUID.randomUUID(), 36), pointer3);
        sortedListByPointer.add(r1);
        sortedListByPointer.add(r2);
        sortedListByPointer.add(r3);
        
        assertEquals(1, RowUtilities.getRowPositionFormSortedListByPointer(sortedListByPointer, pointer2));
        assertEquals(0, RowUtilities.getRowPositionFormSortedListByPointer(sortedListByPointer, pointer1));
        assertEquals(2, RowUtilities.getRowPositionFormSortedListByPointer(sortedListByPointer, pointer3));
        assertEquals(0, RowUtilities.getRowPositionFormSortedListByPointer(sortedListByPointer, 1));
        assertEquals(0, RowUtilities.getRowPositionFormSortedListByPointer(sortedListByPointer, 4));
        assertEquals(2, RowUtilities.getRowPositionFormSortedListByPointer(sortedListByPointer, 27));
        assertEquals(2, RowUtilities.getRowPositionFormSortedListByPointer(sortedListByPointer, 100));
    }

    /**
     * Test of getRowPositionFromSortedListByUUID method, of class RowUtilities.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetRowPositionFromSortedListByUUID() throws DAOFileException {
        System.out.println("getRowPositionFromSortedListByUUID");
        List<AbstractRow> sortedListByUUID = new ArrayList<>();
        
        UUID dataID1 = new UUID(0, 3);
        AbstractRow r1 = new IndexRow(1, new Index(dataID1, 12), 12);
        UUID dataID2 = new UUID(0, 5);
        AbstractRow r2 = new IndexRow(2, new Index(dataID2, 24), 24);
        UUID dataID3 = new UUID(0, 18);
        AbstractRow r3 = new IndexRow(3, new Index(dataID3, 36), 36);
        sortedListByUUID.add(r1);
        sortedListByUUID.add(r2);
        sortedListByUUID.add(r3);
        
        assertEquals(1, RowUtilities.getRowPositionFromSortedListByUUID(sortedListByUUID, dataID2));
        assertEquals(0, RowUtilities.getRowPositionFromSortedListByUUID(sortedListByUUID, dataID1));
        assertEquals(2, RowUtilities.getRowPositionFromSortedListByUUID(sortedListByUUID, dataID3));
        assertEquals(0, RowUtilities.getRowPositionFromSortedListByUUID(sortedListByUUID, new UUID(0, 1)));
        assertEquals(0, RowUtilities.getRowPositionFromSortedListByUUID(sortedListByUUID, new UUID(0, 4)));
        assertEquals(2, RowUtilities.getRowPositionFromSortedListByUUID(sortedListByUUID, new UUID(0, 6)));
        assertEquals(2, RowUtilities.getRowPositionFromSortedListByUUID(sortedListByUUID, new UUID(0, 100)));
    }
    
}
