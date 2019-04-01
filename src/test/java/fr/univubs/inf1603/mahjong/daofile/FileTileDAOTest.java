package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.fake_engine.GameTile;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author aliyou, nesrine
 */
public class FileTileDAOTest extends FileDAOMahJongTest {

    static DAO<GameTile> gameDao;

    public FileTileDAOTest() {
        try {
            System.out.println("FileTileDAOTest");
            gameDao = daoManager.getTileDao();
        } catch (DAOException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of save method, of class FileTileDAO.
     */
    @Test
    public void testSave() {
        try {
            //        UUID tileID1 = new UUID(0, 1);
//        UUID tileID2 = new UUID(0, 2);
//        UUID tileID3 = new UUID(0, 3);
            GameTile tile1 = new GameTile(new UUID(0, 1), "Bamboo", "1");
            super.testSave(gameDao, tile1);
            GameTile tile2 = new GameTile(new UUID(0, 2), "Dot", "5");
            super.testSave(gameDao, tile2);
            GameTile tile3 = new GameTile(new UUID(0, 3), "Character", "9");
            super.testSave(gameDao, tile3);
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of delete method, of class FileTileDAO.
     */
    @Test
    public void testDelete() {
        try {
            super.testDelete(gameDao, new UUID(0, 1));
            super.testDelete(gameDao, new UUID(0, 3));
            super.testDelete(gameDao, new UUID(0, 2));
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
