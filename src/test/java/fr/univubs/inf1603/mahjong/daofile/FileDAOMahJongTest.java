
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

    protected FileDAOMahJongTest() {
        System.out.println("FileDAOMahJongTest");
        rootDir = Paths.get("/tmp", "mahjong", "dao");
        System.out.println(" rootDir : " + rootDir);
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

    /**
     * Test of writeToPersistance method, of class FileZoneDAO.
     *
     * @param dao
     * @param object
     */
    protected void testSave(DAO<T> dao, T object) {
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
}
