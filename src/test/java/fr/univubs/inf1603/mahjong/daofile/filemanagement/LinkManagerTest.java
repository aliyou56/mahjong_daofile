
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.daofile.FileDAOMahjong;
import fr.univubs.inf1603.mahjong.daofile.FileDAOManager;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import java.nio.file.Path;
import java.nio.file.Paths;
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

}
