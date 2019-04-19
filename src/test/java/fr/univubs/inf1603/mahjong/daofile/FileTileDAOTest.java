package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile;
import fr.univubs.inf1603.mahjong.engine.rule.SimpleHonor;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor;
import fr.univubs.inf1603.mahjong.engine.rule.Wind;
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
        System.out.println("save");
        try {
            GameTile tile1 = new GameTile(1, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE), new UUID(0, 1));
            super.testSave(tileDao, tile1);
            GameTile tile2 = new GameTile(2, new CommonTile(CommonTile.Family.CHARACTER, CommonTile.Number.ONE), new UUID(0, 5));
            super.testSave(tileDao, tile2);
            GameTile tile3 = new GameTile(3, new CommonTile(CommonTile.Family.DOT, CommonTile.Number.FOUR), new UUID(0, 9));
            super.testSave(tileDao, tile3);
            GameTile tile4 = new GameTile(4, new SuperiorHonor(SuperiorHonor.Dragon.GREEN), new UUID(0, 3));
            super.testSave(tileDao, tile4);
            GameTile tile5 = new GameTile(5, new SimpleHonor(Wind.NORTH), new UUID(0, 2));
            super.testSave(tileDao, tile5);
            GameTile tile6 = new GameTile(6, new FlowerTile(FlowerTile.Flower.PLUM), new UUID(0, 8));
            super.testSave(tileDao, tile6);
            GameTile tile7 = new GameTile(7, new SeasonTile(SeasonTile.Season.SPRING), new UUID(0, 7));
            super.testSave(tileDao, tile7);
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of delete method, of class FileTileDAO.
     */
    @Test
    public void testDelete() {
        System.out.println("delete");
        try {
            super.testDelete(tileDao, new UUID(0, 3));
            super.testDelete(tileDao, new UUID(0, 8));
            super.testDelete(tileDao, new UUID(0, 2));
            super.testDelete(tileDao, new UUID(0, 5));
            super.testDelete(tileDao, new UUID(0, 9));
            super.testDelete(tileDao, new UUID(0, 1));
            super.testDelete(tileDao, new UUID(0, 7));
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
