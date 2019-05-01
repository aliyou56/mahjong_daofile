
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class FileHeaderTest {
    
    public FileHeaderTest() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getRowNumber method, of class FileHeader.
     */
    @Test
    public void testGetRowNumber() {
        System.out.println("getRowNumber");
        FileHeader instance = new FileHeader(10, 11);
        int expResult = 10;
        int result = instance.getRowNumber();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNextRowID method, of class FileHeader.
     */
    @Test
    public void testGetLastRowID() {
        System.out.println("getLastRowID");
        int lastRowID = 11;
        FileHeader instance = new FileHeader(10, lastRowID);
        int result = instance.getLastRowID();
        assertEquals(lastRowID, result);
    }
    
    /**
     * Test of getNextRowID method, of class FileHeader.
     */
    @Test
    public void testGetNextRowID() {
        System.out.println("getNextRowID");
        int lastRowID = 11;
        FileHeader instance = new FileHeader(10, lastRowID);
        int expResult = lastRowID + 1;
        int result = instance.getNextRowID();
        assertEquals(expResult, result);
    }

    /**
     * Test of incrementRowNumber method, of class FileHeader.
     */
    @Test
    public void testIncrementRowNumber() {
        System.out.println("incrementRowNumber");
        int rowNumber = 8;
        FileHeader instance = new FileHeader(rowNumber, 11);
        instance.incrementRowNumber();
        assertEquals(rowNumber + 1, instance.getRowNumber());
    }

    /**
     * Test of decrementRowNumber method, of class FileHeader.
     */
    @Test
    public void testDecrementRowNumber() {
        System.out.println("decrementRowNumber");
        int rowNumber = 8;
        FileHeader instance = new FileHeader(rowNumber, 11);
        instance.decrementRowNumber();
        assertEquals(rowNumber - 1, instance.getRowNumber());
    }
}
