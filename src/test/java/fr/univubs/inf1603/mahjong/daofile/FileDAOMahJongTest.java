
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.junit.AfterClass;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 * @param <T>
 */
public abstract class FileDAOMahJongTest<T extends Persistable> {

    protected static Path rootDir;
    protected static DAOManager daoManager;

    protected FileDAOMahJongTest() {
        System.out.println("FileDAOMahJongTest");
        rootDir = Paths.get("/tmp", "mahjong", "dao");
        System.out.println(" rootDir : " + rootDir);
        daoManager = FileDAOManager.getInstance(rootDir);
    }

    /**
     * Destruction de l'environnement de test
     */
    @AfterClass
    public static void tearClass() {
        System.out.println("deleting test files ... ");
//        clean(rootDir.getParent());
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
     * Test of writeToPersistance method, of class FileZoneDAO.
     *
     * @param dao
     * @param object
     */
    protected void testSave(DAO<T> dao, T object) {
        try {
            dao.save(object);
//            Thread.sleep(4000);
            assertEquals(object, dao.find(object.getUUID()));
        } catch (DAOException /*| InterruptedException*/ ex) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Test of writeToPersistance method, of class FileZoneDAO.
     *
     * @param dao
     * @param objectID
     */
    protected void testDelete(DAO<T> dao, UUID objectID) {
        try {
//            System.out.println("delete");
            dao.delete(objectID);
//            Thread.sleep(4000);
            assertEquals(null, dao.find(objectID));
        } catch (DAOException /*| InterruptedException*/ ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    /**
     * Test of write method, of class FileDAOMahJong.
     */
//    @Test
//    public void testWrite() throws Exception {
//        System.out.println("write");
//        UUID uuid = null;
//        AbstractRow row = null;
//        FileDAOMahJong instance = null;
//        instance.write(uuid, row);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of load method, of class FileDAOMahJong.
     */
//    @Test
//    public void testLoad() throws Exception {
//        System.out.println("load");
//        UUID uuid = null;
//        int rowSize = 0;
//        long pointer = 0L;
//        FileDAOMahJong instance = null;
//        ByteBuffer expResult = null;
//        ByteBuffer result = instance.load(uuid, rowSize, pointer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of remove method, of class FileDAOMahJong.
     */
//    @Test
//    public void testRemove() throws Exception {
//        System.out.println("remove");
//        UUID uuid = null;
//        int rowSize = 0;
//        FileDAOMahJong instance = null;
//        boolean expResult = false;
//        boolean result = instance.remove(uuid, rowSize);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of getRowNumber method, of class FileDAOMahJong.
     */
//    @Test
//    public void testGetRowNumber() {
//        System.out.println("getRowNumber");
//        FileDAOMahJong instance = null;
//        int expResult = 0;
//        int result = instance.getRowNumber();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of getNexRowID method, of class FileDAOMahJong.
     */
//    @Test
//    public void testGetNexRowID() {
//        System.out.println("getNexRowID");
//        FileDAOMahJong instance = null;
//        int expResult = 0;
//        int result = instance.getNexRowID();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of getNextRowPointer method, of class FileDAOMahJong.
     */
//    @Test
//    public void testGetNextRowPointer() {
//        System.out.println("getNextRowPointer");
//        int recordSize = 0;
//        FileDAOMahJong instance = null;
//        long expResult = 0L;
//        long result = instance.getNextRowPointer(recordSize);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
