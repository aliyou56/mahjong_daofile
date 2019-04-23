
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import java.beans.PropertyChangeSupport;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class IndexTest {
    
    public IndexTest() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getUUID method, of class Index.
     */
    @Test
    public void testGetUUID() {
        System.out.println("getUUID");
        UUID dataID = UUID.randomUUID();
        Index instance = new Index(dataID, 25);
        UUID expResult = dataID;
        UUID result = instance.getUUID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPointer method, of class Index.
     */
    @Test
    public void testGetPointer() {
        System.out.println("getPointer");
        long rowPointer = 12;
        Index instance = new Index(UUID.randomUUID(), rowPointer);
        long expResult = rowPointer;
        long result = instance.getPointer();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPointer method, of class Index.
     */
    @Test
    public void testSetPointer() {
        System.out.println("setPointer");
        Index instance = new Index(UUID.randomUUID(), 12);
        long pointer = 24;
        long expResult = pointer;
        instance.setPointer(pointer);
        assertEquals(expResult, instance.getPointer());
    }

    /**
     * Test of getPropertyChangeSupport method, of class Index.
     */
//    @Test
//    public void testGetPropertyChangeSupport() {
//        System.out.println("getPropertyChangeSupport");
//        Index instance = null;
//        PropertyChangeSupport expResult = null;
//        PropertyChangeSupport result = instance.getPropertyChangeSupport();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of toString method, of class Index.
     */
//    @Test
//    public void testToString() {
//        System.out.println("toString");
//        Index instance = new Index(UUID.randomUUID(), 12);
//        String expResult = "";
//        String result = instance.toString();
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of hashCode method, of class Index.
//     */
//    @Test
//    public void testHashCode() {
//        System.out.println("hashCode");
//        Index instance = null;
//        int expResult = 0;
//        int result = instance.hashCode();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of equals method, of class Index.
//     */
//    @Test
//    public void testEquals() {
//        System.out.println("equals");
//        Object obj = null;
//        Index instance = null;
//        boolean expResult = false;
//        boolean result = instance.equals(obj);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
