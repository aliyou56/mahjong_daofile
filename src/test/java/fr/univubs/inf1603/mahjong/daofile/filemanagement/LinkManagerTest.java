
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.daofile.FileDAOMahjong;
import fr.univubs.inf1603.mahjong.daofile.FileDAOManager;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * //TODO not completed 
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
     * Test of getDAO method, of class LinkManager.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testGetDAO() throws DAOFileException, DAOException {
        System.out.println("getDAO");
        Path filePath = rootDir.resolve("linkManager.test");
        
        DAOManager daoManager = FileDAOManager.getInstance(rootDir);
        
        LinkManager<TileZone> instance = new LinkManager(filePath);
        FileDAOMahjong expResult = (FileDAOMahjong) daoManager.getTileDao();
        instance.setDAO(expResult);
        FileDAOMahjong result = instance.getDAO();
        
        assertEquals(expResult, result);
        
        filePath.toFile().delete();
    }

    /**
     * Test of setDAO method, of class LinkManager.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testSetDAO() throws DAOFileException, DAOException {
        System.out.println("setDAO");
        Path filePath = rootDir.resolve("linkManager.test");
        
        DAOManager daoManager = FileDAOManager.getInstance(rootDir);
        
        LinkManager<TileZone> instance = new LinkManager(filePath);
        FileDAOMahjong expResult = (FileDAOMahjong) daoManager.getTileDao();
        instance.setDAO(expResult);
        FileDAOMahjong result = instance.getDAO();
        
        assertEquals(expResult, result);
        
        filePath.toFile().delete();
    }
//
//    /**
//     * Test of createRow method, of class LinkManager.
//     */
//    @Test
//    public void testCreateRow_ByteBuffer_long() throws Exception {
//        System.out.println("createRow");
//        ByteBuffer buffer = null;
//        long rowPointer = 0L;
//        LinkManager instance = null;
//        LinkRow expResult = null;
//        LinkRow result = instance.createRow(buffer, rowPointer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of createRow method, of class LinkManager.
//     */
//    @Test
//    public void testCreateRow_DAOFileWriter_long() throws Exception {
//        System.out.println("createRow");
//        DAOFileWriter writer = null;
//        long rowPointer = 0L;
//        LinkManager instance = null;
//        LinkRow expResult = null;
//        LinkRow result = instance.createRow(writer, rowPointer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addChild method, of class LinkManager.
//     */
//    @Test
//    public void testAddChild() throws Exception {
//        System.out.println("addChild");
//        UUID parentID = null;
//        Object child = null;
//        LinkManager instance = null;
//        instance.addChild(parentID, child);
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
//     * Test of updateChildrenLink method, of class LinkManager.
//     */
//    @Test
//    public void testUpdateChildrenLink() throws Exception {
//        System.out.println("updateChildrenLink");
//        LinkManager instance = null;
//        instance.updateChildrenLink(null);
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

}
