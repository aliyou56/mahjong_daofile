
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.Zone;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class FileZoneDAOTest {
    
    public FileZoneDAOTest() {
    }

    /**
     * Test of writeToPersistance method, of class FileZoneDAO.
     */
    @Test
    public void testWriteToPersistance() throws Exception {
        System.out.println("writeToPersistance");
        Zone zone = null;
        FileZoneDAO instance = new FileZoneDAO();
        instance.writeToPersistance(zone);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadFromPersistance method, of class FileZoneDAO.
     */
    @Test
    public void testLoadFromPersistance() throws Exception {
        System.out.println("loadFromPersistance");
        UUID uuid = null;
        FileZoneDAO instance = new FileZoneDAO();
        Zone expResult = null;
        Zone result = instance.loadFromPersistance(uuid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteFromPersistance method, of class FileZoneDAO.
     */
    @Test
    public void testDeleteFromPersistance() throws Exception {
        System.out.println("deleteFromPersistance");
        Zone zone = null;
        FileZoneDAO instance = new FileZoneDAO();
        instance.deleteFromPersistance(zone);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of laodAll method, of class FileZoneDAO.
     */
    @Test
    public void testLaodAll() throws Exception {
        System.out.println("laodAll");
        FileZoneDAO instance = new FileZoneDAO();
        List<Zone> expResult = null;
        List<Zone> result = instance.laodAll();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
