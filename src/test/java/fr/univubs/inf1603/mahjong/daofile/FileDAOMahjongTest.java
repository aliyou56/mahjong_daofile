
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.FileHeaderRow;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author aliyou
 * @version 1.3
 * @param <T> Objet à persister
 */
public abstract class FileDAOMahjongTest<T extends Persistable> {
    
    /**
     * Chemin d'accès du repertoire racine.
     */
    protected static Path rootDir;

    protected FileDAOMahjongTest() {
        rootDir = Paths.get("/tmp", "mahjong", "dao");
        if (!rootDir.toFile().exists()) {
            rootDir.toFile().mkdirs();
        }
        System.out.println(" \n **** rootDir : " + rootDir +" *****\n");
    }

//    /**
//     * Préparation de l'envireonnement de test.
//     */
//    @BeforeClass
//    public static void setUpClass() {
//        rootDir = Paths.get("/tmp", "mahjong", "dao");
//        System.out.println(" \n **** rootDir : " + rootDir +" *****\n");
//    }

    /**
     * Destruction de l'environnement de test
     */
    @After
    public void tearDown() {
        System.out.println("\n *** deleting test files *** \n ");
        clean(rootDir.getParent());
    }

    /**
     * Supprime récurssivement les fichiers d'un repertoire.
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

    abstract protected void assertTest(T obj1, T obj2);

    abstract protected Comparator<T> getComparator();
    
    /**
     * Test of writeToPersistence method, of class FileDAOMahjong.
     * @param dao
     * @param expResult
     */
    protected void testWriteToPersistence(FileDAOMahjong<T> dao, T expResult) {
        System.out.println("writeToPersistence");
        try {
            dao.writeToPersistence(expResult);
            // Attendre que l'écriture soit effective
            synchronized (dao) {
                dao.wait(15000);
            }
            T result = dao.loadFromPersistence(expResult.getUUID());
            assertTest(expResult, result);
        } catch (DAOException | InterruptedException ex) {
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
            // Attendre que l'écriture soit effective
            synchronized (dao) {
                dao.wait(10000);
            }
            T result = dao.loadFromPersistence(expResult.getUUID());
            assertTest(expResult, result);
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
            // Attendre que l'écriture soit effective
            synchronized (dao) {
                dao.wait(10000);
            }
            List<T> result = dao.laodAll();
            
            assertEquals(expResult.size(), result.size());
            expResult.sort(getComparator());
            result.sort(getComparator());
            System.err.println("\nexpect : " + expResult);
            System.err.println("result : " + result + "\n");
            for(int i=0; i<expResult.size(); i++) {
                assertTest(expResult.get(i), result.get(i));
            }
            
            //Supprimer tous les objets de la liste.
            dao.delete(expResult);
        } catch (DAOException | DAOFileException | InterruptedException ex) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Test of delete method, of class FileDAOMahjong.
     * @param dao
     * @param list
     */
    protected void testDelete(FileDAOMahjong<T> dao, List<T> list) {
        System.out.println("delete");
        try {
            for (T obj : list) {
                dao.writeToPersistence(obj);
            }
            // Attendre que l'écriture soit effective
            synchronized (dao) {
                dao.wait(10000);
            }
            // on supprime les éléments de la liste
            dao.delete(list);
            // on vérifie 
            for (T obj : list) {
                Assert.assertNull(dao.loadFromPersistence(obj.getUUID()));
            }
        } catch (DAOException | DAOFileException | InterruptedException ex) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Test of removeDataRow method, of class FileDAOMahjong.
     * @param dao
     * @param data
     */
    protected void testRemoveDataRow(FileDAOMahjong<T> dao, T data) {
        try {
            System.out.println("removeDataRow");
            dao.writeToPersistence(data);
            // Attendre que l'écriture soit effective
            synchronized (dao) {
                dao.wait(10000);
            }
            // on vérifie
            assertEquals(true, dao.removeDataRow(data.getUUID()));
            assertEquals(false, dao.removeDataRow(UUID.randomUUID()));
        } catch (DAOException | DAOFileException | InterruptedException ex) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Test of getDataRow method, of class FileDAOMahjong.
     * @param instance
     * @param expResult
     */
    protected void testGetDataRow_3args(FileDAOMahjong<T> instance, DataRow<T> expResult) {
        System.out.println("getDataRow");
        DataRow<T> result = instance.getDataRow(expResult.geRowID(), expResult.getData(), expResult.getRowPointer());
        
        Assert.assertEquals(expResult.geRowID(), result.geRowID());
        Assert.assertEquals(expResult.getRowPointer(), result.getRowPointer());
        assertTest(expResult.getData(), result.getData());
    }

    /**
     * Test of getDataRow method, of class FileDAOMahjong.
     * @param dao
     * @param data
     */
    protected void testGetDataRow_long(FileDAOMahjong<T> dao, T data) {
        try {
            System.out.println("getDataRow");
            dao.writeToPersistence(data);
            // Attendre que l'écriture soit effective
            synchronized (dao) {
                dao.wait(10000);
            }
            
            int rowID = 1;
            long rowPointer = FileHeaderRow.FILE_HEADER_ROW_SIZE; //  = 12
            
            DataRow<T> expResult = dao.getDataRow(rowID, data, rowPointer);
            DataRow<T> result = dao.getDataRow(rowPointer);
            
            Assert.assertEquals(expResult.geRowID(), result.geRowID());
            Assert.assertEquals(expResult.getRowPointer(), result.getRowPointer());
            assertTest(expResult.getData(), result.getData());
        } catch (DAOException | DAOFileException | InterruptedException ex) {
            ex.printStackTrace(System.out);
        }
    }

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