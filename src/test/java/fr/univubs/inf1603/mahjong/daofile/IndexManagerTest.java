
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class IndexManagerTest {

//    static Path rootDir;
//
//    public IndexManagerTest() {
//        rootDir = Paths.get("/tmp/mahjong", "dao");
//    }
//
//    /**
//     * Destruction de l'environnement de test
//     */
//    @AfterClass
//    public static void tearClass() {
//        System.out.println("deleting test files ... ");
//        clean(rootDir.getParent());
//    }
//     /**
//     * Supprime récurssivement les fichiers de la base de données.
//     * @param path Chemin du parent.
//     */
//    private static void clean(Path path) {
//        File pathFile = path.toFile();
//        if (pathFile.isDirectory()) {
//            for (File file : pathFile.listFiles()) {
//                clean(file.toPath());
//            }
//        }
//        System.out.print( pathFile.delete() ? "[OK] " : "[NOK] ");
//        System.out.println(pathFile.getAbsolutePath());
//    }
//    
//    /**
//     * Test of readRowFromBuffer method, of class IndexManager.
//     */
//    @Test
//    public void testReadRow() {
//        try {
//            System.out.println("readRow");
//            Path indexFilePath = Paths.get(rootDir.toString(), "index.test");
//            IndexManager instance = new IndexManager(indexFilePath);
//
//            long rowPointer = 245;
//            Index index = new Index(new UUID(0, 548), 589);
//            int rowID = 29;
//            IndexRow expResult = new IndexRow(rowID, index, rowPointer);
//
//            ByteBuffer buffer = ByteBuffer.allocate(IndexRow.INDEX_ROW_SIZE);
//            buffer.putInt(rowID);
//            expResult.writeData(buffer);
//            buffer.flip();
//
//            IndexRow result = instance.readRowFromBuffer(buffer, rowPointer);
//            assertEquals(expResult, result);
//
//            buffer.clear();
//            expResult.writeData(buffer);
//            buffer.flip();
//            result = instance.readRowFromBuffer(buffer, rowPointer);
//            assertEquals(null, result);
//
//        } catch (IOException ex) {
//            Logger.getLogger(IndexManagerTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    /**
//     * Test of addIndex method, of class IndexManager.
//     */
//    @Test
//    public void testAddIndex() {
//        try {
//            System.out.println("addIndex");
//            Path indexFilePath = Paths.get(rootDir.toString(), "index.test");
//            IndexManager instance = new IndexManager(indexFilePath);
//            
//            UUID dataID = UUID.randomUUID();
//            Index result = new Index(dataID, 589);
//            instance.addIndex(result);
////            Thread.sleep(6000);
//            Index expResult = instance.getRow(dataID).getData();
//            assertEquals(expResult, result);
////            System.out.print( indexFilePath.toFile().delete() ? "[OK] " : "[NOK] ");
//        } catch (IOException ex) {
//            Logger.getLogger(IndexManagerTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    /**
//     * Test of removeIndex method, of class IndexManager.
//     */
//    @Test
//    public void testRemoveIndex() {
//        try {
//            System.out.println("removeIndex");
//            Path indexFilePath = Paths.get(rootDir.toString(), "index.test");
//            IndexManager instance = new IndexManager(indexFilePath);
//            
//            UUID dataID = UUID.randomUUID();
//            Index index = new Index(dataID, 589);
//            instance.addIndex(index);
//            Thread.sleep(4000);
//            instance.removeIndex(dataID, 10);
//            Thread.sleep(4000);
//            IndexRow indexRow = instance.getRow(dataID);
//            Index expResult = indexRow != null ? instance.getRow(dataID).getData() : null;
//            assertEquals(expResult, null);
//        } catch (IOException | InterruptedException ex) {
//            Logger.getLogger(IndexManagerTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    /**
//     * Test of getDataRowPointer method, of class IndexManager.
//     */
//    @Test
//    public void testGetDataRowPointer() {
////        System.out.println("getDataRowPointer");
////        UUID indexID = null;
////        IndexManager instance = null;
////        long expResult = 0L;
////        long result = instance.getDataRowPointer(indexID);
////        assertEquals(expResult, result);
////        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
//    }

}
