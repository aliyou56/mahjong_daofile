
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.junit.AfterClass;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 * @param <T>
 */
public abstract class FileDAOMahjongTest<T extends Persistable> {

    /**
     * Si {@code true} les données sont écrites dans les fichiers de données
     */
    protected static final boolean TEST_WITH_FILE_WRITING = true;
    
    protected static Path rootDir;

    protected FileDAOMahjongTest() {
        System.out.println("\n *** TEST_WITH_FILE_WRITING = "+TEST_WITH_FILE_WRITING+" *** \n");
        rootDir = Paths.get("/tmp", "mahjong", "dao");
        System.out.println("rootDir : " + rootDir);
    }

    /**
     * Destruction de l'environnement de test
     */
    @AfterClass
    public static void tearClass() {
        System.out.println("\n *** deleting test files *** \n ");
        clean(rootDir.getParent());
    }

    /**
     * Supprime récurssivement les fichiers de la base de données.
     *
     * @param path Chemin du parent.
     */
    protected static void clean(Path path) {
        File pathFile = path.toFile();
        if (pathFile.isDirectory()) {
            for (File file : pathFile.listFiles()) {
                clean(file.toPath());
            }
        }
        System.out.print(pathFile.delete() ? "[OK] " : "[NOK] ");
        System.out.println(pathFile.getAbsolutePath());
    }

    abstract protected boolean compare(T obj1, T obj2);
    
    /**
     * Test of writeToPersistence method, of class FileDAOMahjong.
     * @param dao
     * @param expResult
     */
    protected void testWriteToPersistence(FileDAOMahjong<T> dao, T expResult) {
        System.out.println("writeToPersistence");
        try {
            dao.writeToPersistence(expResult);
            Thread.sleep(3000);
            T result = dao.loadFromPersistence(expResult.getUUID());
            compare(expResult, result);
        } catch (DAOException | InterruptedException  ex) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Test of loadFromPersistence method, of class FileDAOMahjong.
     * @param dao
     * @param expResult
     */
    protected void testLoadFromPersistence(FileDAOMahjong<T> dao, T expResult) {
        System.out.println("loadFromPersistence");
        try {
            dao.writeToPersistence(expResult);
            Thread.sleep(4000);
            T result = dao.loadFromPersistence(expResult.getUUID());
            compare(expResult, result);
        } catch (DAOException | InterruptedException  ex) {
            ex.printStackTrace(System.out);
        } 
    }

    /**
     * Test of laodAll method, of class FileDAOMahjong.
     * @param dao
     * @param expResult
     */
    protected void testLaodAll(FileDAOMahjong<T> dao, List<T> expResult) {
        System.out.println("laodAll");
        try {
            for(T object : expResult) {
                dao.writeToPersistence(object);
            }
            for(T object : dao.laodAll()) {
                boolean res = expResult.contains(object);
                assertTrue(res);
            }
        } catch (DAOException  ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    /**
     * Test of writeToPersistance method, of class FileZoneDAO.
     *
     * @param dao
     * @param object
     */
    protected void testSave(FileDAOMahjong<T> dao, T object) {
        try {
            dao.save(object);
            assertEquals(object, dao.find(object.getUUID()));
        } catch (DAOException  ex) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Test of writeToPersistance method, of class FileZoneDAO.
     *
     * @param dao
     * @param objectID
     */
    protected void testDelete(/*FileDAOMahjong*/DAO<T> dao, UUID objectID) {
        try {
            dao.delete(objectID);
            assertNull(dao.find(objectID));
        } catch (DAOException ex) {
            ex.printStackTrace(System.out);
        }
    }

//    /**
//     * Test of getDataRow method, of class FileDAOMahjong.
//     */
//    public void testGetDataRow_3args() throws Exception {
//        System.out.println("getDataRow");
//        int rowID = 0;
//        Object data = null;
//        long pointer = 0L;
//        FileDAOMahjong instance = null;
//        DataRow expResult = null;
//        DataRow result = instance.getDataRow(rowID, data, pointer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDataRow method, of class FileDAOMahjong.
//     */
//    public void testGetDataRow_long() throws Exception {
//        System.out.println("getDataRow");
//        long rowPointer = 0L;
//        FileDAOMahjong instance = null;
//        DataRow expResult = null;
//        DataRow result = instance.getDataRow(rowPointer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of removeDataRow method, of class FileDAOMahjong.
//     */
//    public void testRemoveDataRow() throws Exception {
//        System.out.println("removeDataRow");
//        UUID dataID = null;
//        FileDAOMahjong instance = null;
//        boolean expResult = false;
//        boolean result = instance.removeDataRow(dataID);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of delete method, of class FileDAOMahjong.
//     */
//    public void testDelete() throws Exception {
//        System.out.println("delete");
//        FileDAOMahjong instance = null;
//        instance.delete(null);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getRowNumber method, of class FileDAOMahjong.
     * @param instance
     * @param expResult
     */
    protected void testGetRowNumber(FileDAOMahjong<T> instance, int expResult) {
        System.out.println("getRowNumber");
        int result = instance.getRowNumber();
        assertEquals(expResult, result);
    }
}
