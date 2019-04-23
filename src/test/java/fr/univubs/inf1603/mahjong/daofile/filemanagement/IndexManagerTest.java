package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class IndexManagerTest {

    static Path rootDir;

    public IndexManagerTest() {
        rootDir = Paths.get("/tmp/mahjong", "dao");
        if (!rootDir.toFile().exists()) {
            rootDir.toFile().mkdirs();
        }
    }

    /**
     * Destruction de l'environnement de test
     */
    @AfterClass
    public static void tearClass() {
        System.out.println("deleting test files ... ");
        clean(rootDir.getParent());
    }

    /**
     * Supprime récurssivement les fichiers de la base de données.
     *
     * @param path Chemin du parent.
     */
    private static void clean(Path path) {
        File pathFile = path.toFile();
        if (pathFile.isDirectory()) {
            for (File file : pathFile.listFiles()) {
                clean(file.toPath());
            }
        }
        System.out.print(pathFile.delete() ? "[OK] " : "[NOK] ");
        System.out.println(pathFile.getAbsolutePath());
    }

    /**
     * Test of createRow method, of class IndexManager.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testCreateRow() throws DAOFileException {
        System.out.println("createRow");
        Path indexFilePath = rootDir.resolve("index.test");
        int dataRowSize = 24;
        IndexManager instance = new IndexManager(indexFilePath, dataRowSize);
        
        long rowPointer = 29;
        IndexRow expResult = new IndexRow(4, new Index(UUID.randomUUID(), 45), rowPointer);
        ByteBuffer buffer = ByteBuffer.allocate(IndexRow.INDEX_ROW_SIZE);
        expResult.write(buffer);
        buffer.flip();
        
        IndexRow result = instance.createRow(buffer, rowPointer);
        
        assertEquals(expResult, result);
    }
    
    /**
     * Test of addIndex method, of class IndexManager.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddIndex() throws DAOFileException, InterruptedException {
        System.out.println("addIndex");
        Path indexFilePath = rootDir.resolve("index.test");
        int dataRowSize = 24;
        IndexManager instance = new IndexManager(indexFilePath, dataRowSize);

        UUID dataID = UUID.randomUUID();
        Index result = new Index(dataID, dataRowSize * 2);
        
        instance.addIndex(result);
        Thread.sleep(4000);
        
        assertEquals(instance.getRow(dataID).getData(), result);
        
        indexFilePath.toFile().delete();
    }

    /**
     * Test of removeIndex method, of class IndexManager.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveIndex_UUID() throws DAOFileException, InterruptedException {
        System.out.println("removeIndex");
        Path indexFilePath = rootDir.resolve("index.test");
        int dataRowSize = 24;
        IndexManager instance = new IndexManager(indexFilePath, dataRowSize);
        
        UUID dataID = UUID.randomUUID();
        Index index = new Index(dataID, dataRowSize);
        instance.addIndex(index);
        Thread.sleep(4000);
        
        instance.removeIndex(dataID);
        
        assertEquals(null, instance.getRow(dataID));
        
        indexFilePath.toFile().delete();
    }
    
    /**
     * Test of removeIndex method, of class IndexManager.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveIndex_IndexRow() throws DAOFileException, InterruptedException {
        System.out.println("removeIndex");
        Path indexFilePath = rootDir.resolve("index.test");
        int dataRowSize = 24;
        IndexManager instance = new IndexManager(indexFilePath, dataRowSize);
        
        UUID dataID = UUID.randomUUID();
        Index index = new Index(dataID, dataRowSize);
        instance.addIndex(index);
        Thread.sleep(4000);
        
        IndexRow indexRowToDelete = instance.getRow(dataID);
        
        instance.removeIndex(indexRowToDelete);
        
        assertEquals(null, instance.getRow(dataID));
        
        indexFilePath.toFile().delete();
    }

    /**
     * Test of removeIndex method, of class IndexManager.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveIndex_List() throws DAOFileException, InterruptedException {
        System.out.println("removeIndex");
        Path indexFilePath = rootDir.resolve("index.test");
        int dataRowSize = 24;
        IndexManager instance = new IndexManager(indexFilePath, dataRowSize);
        
        UUID dataID1 = new UUID(0, 1);
        Index index1 = new Index(dataID1, dataRowSize);
        UUID dataID2 = new UUID(0, 2);
        Index index2 = new Index(dataID2, dataRowSize * 2);
        UUID dataID3 = new UUID(0, 3);
        Index index3 = new Index(dataID3, dataRowSize * 3);
        instance.addIndex(index1);
        instance.addIndex(index2);
        instance.addIndex(index3);
        Thread.sleep(4000);
        
        List<IndexRow> indexRowsSortedByPointerToDelete = new ArrayList<>();
        indexRowsSortedByPointerToDelete.add(instance.getRow(dataID1));
        indexRowsSortedByPointerToDelete.add(instance.getRow(dataID2));
        indexRowsSortedByPointerToDelete.add(instance.getRow(dataID3));
        
        instance.removeIndex(indexRowsSortedByPointerToDelete);

        assertEquals(null, instance.getRow(dataID1));
        assertEquals(null, instance.getRow(dataID2));
        assertEquals(null, instance.getRow(dataID3));
        
        indexFilePath.toFile().delete();
    }
}
