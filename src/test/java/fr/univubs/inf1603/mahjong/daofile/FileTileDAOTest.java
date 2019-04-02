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
public class FileTileDAOTest extends FileDAOMahJongTest<GameTile> {

    static DAO<GameTile> tileDao;

    public FileTileDAOTest() {
        try {
            System.out.println("FileTileDAOTest");
            tileDao = daoManager.getTileDao();
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
            GameTile tile1 = new GameTile(new UUID(0, 1), "Bamboo", "1");
            super.testSave(tileDao, tile1);
            GameTile tile2 = new GameTile(new UUID(0, 2), "Dot", "5");
            super.testSave(tileDao, tile2);
            GameTile tile3 = new GameTile(new UUID(0, 3), "Character", "9");
            super.testSave(tileDao, tile3);
            GameTile tile5 = new GameTile(new UUID(0, 5), "Dragon", "Red");
            super.testSave(tileDao, tile5);
            GameTile tile4 = new GameTile(new UUID(0, 4), "Wind", "East");
            super.testSave(tileDao, tile4);
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
            super.testDelete(tileDao, new UUID(0, 1));
            super.testDelete(tileDao, new UUID(0, 3));
            super.testDelete(tileDao, new UUID(0, 2));
            super.testDelete(tileDao, new UUID(0, 5));
            super.testDelete(tileDao, new UUID(0, 4));
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
