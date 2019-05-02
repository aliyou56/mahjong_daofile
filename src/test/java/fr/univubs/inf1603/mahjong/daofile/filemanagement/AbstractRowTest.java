
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.persistence.MahjongObservable;
import java.beans.PropertyChangeSupport;
import java.nio.ByteBuffer;
import org.junit.AfterClass;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public abstract class AbstractRowTest {
    
    protected AbstractRowTest() {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    /**
     * Test of testWrite method, of class AbstractRow.
     * @param row
     * @param expResult
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    
    protected void testWrite(AbstractRow row, int expResult) throws DAOFileException {
        System.out.println("write");
        ByteBuffer buffer = ByteBuffer.allocate(row.getRowSize());
        int result = row.write(buffer);
        assertEquals(expResult, result);
        ByteBuffer buffer2 = ByteBuffer.allocate(1);
        try {
            row.write(buffer2);
        } catch(DAOFileException ex) {
            System.out.println(ex.getMessage());
            assertTrue(true);
        }
    }

    /**
     * Test of readData method, of class AbstractRow.
     * @param row
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    protected void testReadData(AbstractRow row) throws DAOFileException {
        System.out.println("readData");
        ByteBuffer buffer = ByteBuffer.allocate(row.getRowSize());
        row.writeData(buffer);
        buffer.flip();

        MahjongObservable expResult = row.getData();
        MahjongObservable result = row.readData(buffer);

        assertEquals(expResult, result);
    }

    /**
     * Test of writeData method, of class AbstractRow.
     * @param row
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    protected void testWriteData(AbstractRow row) throws DAOFileException {
        System.out.println("writeData");

        ByteBuffer buffer = ByteBuffer.allocate(row.getRowSize());
        row.writeData(buffer);
        buffer.flip();
        
        MahjongObservable expResult = row.getData();
        MahjongObservable result = row.readData(buffer);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getRowSize method, of class AbstractRow.
     * @param row
     * @param rowSize
     */
    protected void testGetRowSize(AbstractRow row, int rowSize) {
        System.out.println("getRowSize");
        
        int expResult = rowSize;
        int result = row.getRowSize();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of isDirty method, of class AbstractRow.
     * @param row
     * @param expResult
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    protected void testIsDirty(AbstractRow row, boolean expResult) throws DAOFileException {
        System.out.println("isDirty");
//        ByteBuffer buffer = ByteBuffer.allocate(row.getRowSize());
//        row.writeData(buffer);
//        
//        boolean expResult = false;
        boolean result = row.isDirty();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDirty method, of class AbstractRow.
     * @param row
     */
    protected void testSetDirty(AbstractRow row) {
        System.out.println("setDirty");
        boolean expResult = false;
        row.setDirty(expResult);
        boolean result = row.isDirty();
        assertEquals(expResult, result);
        
        expResult = false;
        row.setDirty(expResult);
        result = row.isDirty();
        assertEquals(expResult, result);
    }

    /**
     * Test of geRowID method, of class AbstractRow.
     * @param row
     * @param expResult
     */
    protected void testGeRowID(AbstractRow row, int expResult) {
        System.out.println("geRowID");
        int result = row.geRowID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRowPointer method, of class AbstractRow.
     * @param row
     * @param expResult
     */
    protected void testGetRowPointer(AbstractRow row, long expResult) {
        System.out.println("getRowPointer");
        long result = row.getRowPointer();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRowPointer method, of class AbstractRow.
     * @param row
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    protected void testSetRowPointer(AbstractRow row) throws DAOFileException {
        System.out.println("setRowPointer");
        long expResult = 4578;
        boolean notifyWriter = false;
        row.setRowPointer(expResult, notifyWriter);
        
        long result = row.getRowPointer();
        assertEquals(expResult, result);
        
        row.setRowPointer(-2, notifyWriter);
        assertEquals(row.getRowPointer(), result);
    }

    /**
     * Test of getData method, of class AbstractRow.
     * @param row
     * @param expResult
     */
    protected void testGetData(AbstractRow row, MahjongObservable expResult) {
        System.out.println("getData");
        MahjongObservable result = row.getData();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDataSize method, of class AbstractRow.
     * @param row
     * @param expResult
     */
    protected void testGetDataSize(AbstractRow row, int expResult) {
        System.out.println("getDataSize");
        int result = row.getDataSize();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPropertyChangeSupport method, of class AbstractRow.
     * @param row
     */
    protected void testGetPropertyChangeSupport(AbstractRow row) {
        System.out.println("getPropertyChangeSupport");
        PropertyChangeSupport result = row.getPropertyChangeSupport();
        assertNotNull(result);
    }

    /**
     * Test of propertyChange method, of class AbstractRow.
     */
//    public void testPropertyChange() {
//        System.out.println("propertyChange");
//        PropertyChangeEvent evt = null;
//        AbstractRow instance = null;
//        instance.propertyChange(evt);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
