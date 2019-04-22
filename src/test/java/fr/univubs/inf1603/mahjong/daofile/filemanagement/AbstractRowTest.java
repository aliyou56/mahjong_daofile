
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class AbstractRowTest {
    
//    public AbstractRowTest() {
//    }
//    
//    @AfterClass
//    public static void tearDownClass() {
//    }
//
//    /**
//     * Test of read method, of class AbstractRow.
//     */
//    @Test
//    public void testRead() throws Exception {
//        System.out.println("read");
//        ByteBuffer buffer = null;
//        AbstractRow instance = null;
//        instance.read(buffer);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of write method, of class AbstractRow.
//     */
//    @Test
//    public void testWrite() throws Exception {
//        System.out.println("write");
//        ByteBuffer buffer = null;
//        AbstractRow instance = null;
//        instance.write(buffer);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of readData method, of class AbstractRow.
//     */
//    @Test
//    public void testReadData() throws Exception {
//        System.out.println("readData");
//        ByteBuffer buffer = null;
//        AbstractRow instance = null;
//        Object expResult = null;
//        Object result = instance.readData(buffer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of writeData method, of class AbstractRow.
//     */
//    @Test
//    public void testWriteData() throws Exception {
//        System.out.println("writeData");
//        ByteBuffer buffer = null;
//        AbstractRow instance = null;
//        instance.writeData(buffer);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRowSize method, of class AbstractRow.
//     */
//    @Test
//    public void testGetRowSize() {
//        System.out.println("getRowSize");
//        AbstractRow instance = null;
//        int expResult = 0;
//        int result = instance.getRowSize();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of hasChanged method, of class AbstractRow.
//     */
//    @Test
//    public void testHasChanged() {
//        System.out.println("hasChanged");
//        AbstractRow instance = null;
//        boolean expResult = false;
//        boolean result = instance.hasChanged();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setChanged method, of class AbstractRow.
//     */
//    @Test
//    public void testSetChanged() {
//        System.out.println("setChanged");
//        boolean changed = false;
//        AbstractRow instance = null;
//        instance.setChanged(changed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of geRowID method, of class AbstractRow.
//     */
//    @Test
//    public void testGeRowID() {
//        System.out.println("geRowID");
//        AbstractRow instance = null;
//        int expResult = 0;
//        int result = instance.geRowID();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRowPointer method, of class AbstractRow.
//     */
//    @Test
//    public void testGetRowPointer() {
//        System.out.println("getRowPointer");
//        AbstractRow instance = null;
//        long expResult = 0L;
//        long result = instance.getRowPointer();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRowPointer method, of class AbstractRow.
//     */
//    @Test
//    public void testSetRowPointer() {
//        System.out.println("setRowPointer");
//        long rowPointer = 0L;
//        boolean notifyWriter = false;
//        AbstractRow instance = null;
//        instance.setRowPointer(rowPointer, notifyWriter);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getData method, of class AbstractRow.
//     */
//    @Test
//    public void testGetData() {
//        System.out.println("getData");
//        AbstractRow instance = null;
//        Object expResult = null;
//        Object result = instance.getData();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDataSize method, of class AbstractRow.
//     */
//    @Test
//    public void testGetDataSize() {
//        System.out.println("getDataSize");
//        AbstractRow instance = null;
//        int expResult = 0;
//        int result = instance.getDataSize();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of propertyChange method, of class AbstractRow.
//     */
//    @Test
//    public void testPropertyChange() {
//        System.out.println("propertyChange");
//        PropertyChangeEvent evt = null;
//        AbstractRow instance = null;
//        instance.propertyChange(evt);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPropertyChangeSupport method, of class AbstractRow.
//     */
//    @Test
//    public void testGetPropertyChangeSupport() {
//        System.out.println("getPropertyChangeSupport");
//        AbstractRow instance = null;
//        PropertyChangeSupport expResult = null;
//        PropertyChangeSupport result = instance.getPropertyChangeSupport();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of toString method, of class AbstractRow.
//     */
//    @Test
//    public void testToString() {
//        System.out.println("toString");
//        AbstractRow instance = null;
//        String expResult = "";
//        String result = instance.toString();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of hashCode method, of class AbstractRow.
//     */
//    @Test
//    public void testHashCode() {
//        System.out.println("hashCode");
//        AbstractRow instance = null;
//        int expResult = 0;
//        int result = instance.hashCode();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of equals method, of class AbstractRow.
//     */
//    @Test
//    public void testEquals() {
//        System.out.println("equals");
//        Object obj = null;
//        AbstractRow instance = null;
//        boolean expResult = false;
//        boolean result = instance.equals(obj);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    public class AbstractRowImpl extends AbstractRow {
//
//        public AbstractRowImpl() throws Exception {
//            super(null, 0, 0L);
//        }
//
//        public T readData(ByteBuffer buffer) throws DAOException {
//            return null;
//        }
//
//        public void writeData(ByteBuffer buffer) throws IOException, DAOException {
//        }
//    }
//    
}
