/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univubs.inf1603.mahjong.daofile;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public abstract class AbstractRowManagerTest {
    
    public AbstractRowManagerTest() {
    }

    /**
     * Test of readRow method, of class AbstractRowManager.
     */
    @Test
    public void testReadRow() {
        System.out.println("readRow");
        ByteBuffer buffer = null;
        long rowPointer = 0L;
        AbstractRowManager instance = null;
        AbstractRow expResult = null;
        AbstractRow result = instance.readRow(buffer, rowPointer);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addRow method, of class AbstractRowManager.
     */
    @Test
    public void testAddRow() throws Exception {
        System.out.println("addRow");
        AbstractRow newRow = null;
        AbstractRowManager instance = null;
        instance.addRow(newRow);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeRow method, of class AbstractRowManager.
     */
    @Test
    public void testRemoveRow() throws Exception {
        System.out.println("removeRow");
        UUID dataID = null;
        AbstractRowManager instance = null;
        AbstractRow expResult = null;
        AbstractRow result = instance.removeRow(dataID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRow method, of class AbstractRowManager.
     */
    @Test
    public void testGetRow() {
        System.out.println("getRow");
        UUID dataID = null;
        AbstractRowManager instance = null;
        AbstractRow expResult = null;
        AbstractRow result = instance.getRow(dataID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRowPosition method, of class AbstractRowManager.
     */
    @Test
    public void testGetRowPosition() {
        System.out.println("getRowPosition");
        UUID dataID = null;
        AbstractRowManager instance = null;
        int expResult = 0;
        int result = instance.getRowPosition(dataID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRowNumber method, of class AbstractRowManager.
     */
    @Test
    public void testGetRowNumber() {
        System.out.println("getRowNumber");
        AbstractRowManager instance = null;
        int expResult = 0;
        int result = instance.getRowNumber();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNextRowID method, of class AbstractRowManager.
     */
    @Test
    public void testGetNextRowID() {
        System.out.println("getNextRowID");
        AbstractRowManager instance = null;
        int expResult = 0;
        int result = instance.getNextRowID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNextRowPointer method, of class AbstractRowManager.
     */
    @Test
    public void testGetNextRowPointer() {
        System.out.println("getNextRowPointer");
        AbstractRowManager instance = null;
        long expResult = 0L;
        long result = instance.getNextRowPointer();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRows method, of class AbstractRowManager.
     */
    @Test
    public void testGetRows() {
        System.out.println("getRows");
        AbstractRowManager instance = null;
        List expResult = null;
        List result = instance.getRows();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
