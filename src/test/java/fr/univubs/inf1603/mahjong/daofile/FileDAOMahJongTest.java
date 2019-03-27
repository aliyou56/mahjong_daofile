/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class FileDAOMahJongTest {
    
    public FileDAOMahJongTest() {
    }

    /**
     * Test of write method, of class FileDAOMahJong.
     */
    @Test
    public void testWrite() throws Exception {
        System.out.println("write");
        UUID uuid = null;
        AbstractRow row = null;
        FileDAOMahJong instance = null;
        instance.write(uuid, row);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of load method, of class FileDAOMahJong.
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("load");
        UUID uuid = null;
        int rowSize = 0;
        long pointer = 0L;
        FileDAOMahJong instance = null;
        ByteBuffer expResult = null;
        ByteBuffer result = instance.load(uuid, rowSize, pointer);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class FileDAOMahJong.
     */
    @Test
    public void testRemove() throws Exception {
        System.out.println("remove");
        UUID uuid = null;
        int rowSize = 0;
        FileDAOMahJong instance = null;
        boolean expResult = false;
        boolean result = instance.remove(uuid, rowSize);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRowNumber method, of class FileDAOMahJong.
     */
    @Test
    public void testGetRowNumber() {
        System.out.println("getRowNumber");
        FileDAOMahJong instance = null;
        int expResult = 0;
        int result = instance.getRowNumber();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNexRowID method, of class FileDAOMahJong.
     */
    @Test
    public void testGetNexRowID() {
        System.out.println("getNexRowID");
        FileDAOMahJong instance = null;
        int expResult = 0;
        int result = instance.getNexRowID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNextRowPointer method, of class FileDAOMahJong.
     */
    @Test
    public void testGetNextRowPointer() {
        System.out.println("getNextRowPointer");
        int recordSize = 0;
        FileDAOMahJong instance = null;
        long expResult = 0L;
        long result = instance.getNextRowPointer(recordSize);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class FileDAOMahJongImpl extends FileDAOMahJong {

        public FileDAOMahJongImpl() throws Exception {
            super(null, "", "");
        }

        @Override
        protected void writeToPersistance(Object object) throws DAOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected Object loadFromPersistance(UUID uuid) throws DAOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void deleteFromPersistance(Object object) throws DAOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected List laodAll() throws DAOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
}
