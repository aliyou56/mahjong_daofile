
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class AbstractRowManagerTest {
//    
//    public AbstractRowManagerTest() {
//    }
//    
//    @AfterClass
//    public static void tearDownClass() {
//    }
//
//    /**
//     * Test of getRowList method, of class AbstractRowManager.
//     */
//    @Test
//    public void testGetRowList() {
//        System.out.println("getRowList");
//        List<? extends Persistable> dataList = null;
//        AbstractRowManager instance = null;
//        List expResult = null;
//        List result = instance.getRowList(dataList);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSingleRemoveList method, of class AbstractRowManager.
//     */
//    @Test
//    public void testGetSingleRemoveList() {
//        System.out.println("getSingleRemoveList");
//        AbstractRowManager instance = null;
//        List expResult = null;
//        List result = instance.getSingleRemoveList(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of createRow method, of class AbstractRowManager.
//     */
//    @Test
//    public void testCreateRow() throws Exception {
//        System.out.println("createRow");
//        ByteBuffer buffer = null;
//        long rowPointer = 0L;
//        AbstractRowManager instance = null;
//        AbstractRow expResult = null;
//        AbstractRow result = instance.createRow(buffer, rowPointer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addRow method, of class AbstractRowManager.
//     */
//    @Test
//    public void testAddRow() throws Exception {
//        System.out.println("addRow");
//        AbstractRow newRow = null;
//        AbstractRowManager instance = null;
//        instance.addRow(newRow);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of removeRow method, of class AbstractRowManager.
//     */
//    @Test
//    public void testRemoveRow_UUID() throws Exception {
//        System.out.println("removeRow");
//        UUID dataID = null;
//        AbstractRowManager instance = null;
//        AbstractRow expResult = null;
//        AbstractRow result = instance.removeRow(dataID);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of removeRow method, of class AbstractRowManager.
//     */
//    @Test
//    public void testRemoveRow_GenericType() throws Exception {
//        System.out.println("removeRow");
//        AbstractRow row = null;
//        AbstractRowManager instance = null;
//        boolean expResult = false;
//        boolean result = instance.removeRow(row);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of updateRowsPointer method, of class AbstractRowManager.
//     */
//    @Test
//    public void testUpdateRowsPointer() {
//        System.out.println("updateRowsPointer");
//        long posisiton = 0L;
//        int offset = 0;
//        AbstractRowManager instance = null;
//        instance.updateRowsPointer(posisiton, offset);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of removeRowFromRowsList method, of class AbstractRowManager.
//     */
//    @Test
//    public void testRemoveRowFromRowsList() {
//        System.out.println("removeRowFromRowsList");
//        AbstractRow row = null;
//        AbstractRowManager instance = null;
//        instance.removeRowFromRowsList(row);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRow method, of class AbstractRowManager.
//     */
//    @Test
//    public void testGetRow() {
//        System.out.println("getRow");
//        UUID dataID = null;
//        AbstractRowManager instance = null;
//        AbstractRow expResult = null;
//        AbstractRow result = instance.getRow(dataID);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRowNumber method, of class AbstractRowManager.
//     */
//    @Test
//    public void testGetRowNumber() {
//        System.out.println("getRowNumber");
//        AbstractRowManager instance = null;
//        int expResult = 0;
//        int result = instance.getRowNumber();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNextRowID method, of class AbstractRowManager.
//     */
//    @Test
//    public void testGetNextRowID() {
//        System.out.println("getNextRowID");
//        AbstractRowManager instance = null;
//        int expResult = 0;
//        int result = instance.getNextRowID();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNextRowPointer method, of class AbstractRowManager.
//     */
//    @Test
//    public void testGetNextRowPointer() {
//        System.out.println("getNextRowPointer");
//        AbstractRowManager instance = null;
//        long expResult = 0L;
//        long result = instance.getNextRowPointer();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRowsSortedByRowPointer method, of class AbstractRowManager.
//     */
//    @Test
//    public void testGetRowsSortedByRowPointer() {
//        System.out.println("getRowsSortedByRowPointer");
//        AbstractRowManager instance = null;
//        List expResult = null;
//        List result = instance.getRowsSortedByRowPointer();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRowsSortedByUUID method, of class AbstractRowManager.
//     */
//    @Test
//    public void testGetRowsSortedByUUID() {
//        System.out.println("getRowsSortedByUUID");
//        AbstractRowManager instance = null;
//        List expResult = null;
//        List result = instance.getRowsSortedByUUID();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    public class AbstractRowManagerImpl extends AbstractRowManager {
//
//        public AbstractRowManagerImpl() throws Exception {
//            super(null, 0);
//        }
//
//        public T createRow(ByteBuffer buffer, long rowPointer) throws DAOException {
//            return null;
//        }
//    }
    
}
