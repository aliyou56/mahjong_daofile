
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class LinkManagerTest {
    
    Path rootDir;
    
    public LinkManagerTest() {
        rootDir = Paths.get("/tmp/mahjong", "dao");
        if(!rootDir.toFile().exists()) {
            rootDir.toFile().mkdirs();
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of setDAO method, of class LinkManager.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
//    @Test
//    public void testSetDAO() throws DAOFileException {
//        System.out.println("setDAO");
//        Path filePath = rootDir.resolve("linkManager.test");
//        LinkManager instance = new LinkManager(filePath);
//        instance.setDAO(null);
//    }

    /**
     * Test of createRow method, of class LinkManager.
     */
//    @Test
//    public void testCreateRow() throws Exception {
//        System.out.println("createRow");
//        Path filePath = Paths.get(first, more)
//        ByteBuffer buffer = null;
//        long rowPointer = 0L;
//        LinkManager instance = new LinkManager(filePath);
//        LinkRow expResult = null;
//        LinkRow result = instance.createRow(buffer, rowPointer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addChildren method, of class LinkManager.
//     */
//    @Test
//    public void testAddChildren() throws Exception {
//        System.out.println("addChildren");
//        LinkManager instance = null;
//        instance.addChildren(null);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of removeChildren method, of class LinkManager.
//     */
//    @Test
//    public void testRemoveChildren() throws Exception {
//        System.out.println("removeChildren");
//        LinkManager instance = null;
//        instance.removeChildren(null);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of loadChildren method, of class LinkManager.
//     */
//    @Test
//    public void testLoadChildren() throws Exception {
//        System.out.println("loadChildren");
//        UUID parentID = null;
//        LinkManager instance = null;
//        ArrayList expResult = null;
//        ArrayList result = instance.loadChildren(parentID);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    
}
