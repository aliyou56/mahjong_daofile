
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import java.beans.PropertyChangeSupport;
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

    /**
     * Test of getPropertyChangeSupport method, of class FileHeader.
     */
//    @Test
//    public void testGetPropertyChangeSupport() {
//        System.out.println("getPropertyChangeSupport");
//        FileHeader instance = null;
//        PropertyChangeSupport expResult = null;
//        PropertyChangeSupport result = instance.getPropertyChangeSupport();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of toString method, of class FileHeader.
//     */
//    @Test
//    public void testToString() {
//        System.out.println("toString");
//        FileHeader instance = null;
//        String expResult = "";
//        String result = instance.toString();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of hashCode method, of class FileHeader.
//     */
//    @Test
//    public void testHashCode() {
//        System.out.println("hashCode");
//        FileHeader instance = null;
//        int expResult = 0;
//        int result = instance.hashCode();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of equals method, of class FileHeader.
//     */
//    @Test
//    public void testEquals() {
//        System.out.println("equals");
//        Object obj = null;
//        FileHeader instance = null;
//        boolean expResult = false;
//        boolean result = instance.equals(obj);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
