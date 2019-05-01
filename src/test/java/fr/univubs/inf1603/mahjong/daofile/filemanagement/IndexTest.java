
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

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
     * Test of getDataPointer method, of class Index.
     */
    @Test
    public void testGetPointer() {
        System.out.println("getPointer");
        long rowPointer = 12;
        Index instance = new Index(UUID.randomUUID(), rowPointer);
        long expResult = rowPointer;
        long result = instance.getDataPointer();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDataPointer method, of class Index.
     */
    @Test
    public void testSetPointer() {
        System.out.println("setPointer");
        Index instance = new Index(UUID.randomUUID(), 12);
        long pointer = 24;
        long expResult = pointer;
        instance.setDataPointer(pointer);
        assertEquals(expResult, instance.getDataPointer());
    }    
}
