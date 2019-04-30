package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.Wind;
import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.GameTileInterface;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile;
import fr.univubs.inf1603.mahjong.engine.rule.SimpleHonor;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author aliyou, nesrine
 */
public class FileTileDAOTest extends FileDAOMahJongTest<GameTileInterface> {

    public FileTileDAOTest() {
        System.out.println("FileTileDAOTest");
    }

    /**
     * Test of save method, of class FileTileDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testSave() throws DAOException {
        System.out.println("save");
        try {
            GameTileInterface tile1 = new GameTile(1, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE), new UUID(0, 1), true, Wind.EAST);
            GameTileInterface tile2 = new GameTile(2, new CommonTile(CommonTile.Family.CHARACTER, CommonTile.Number.ONE), new UUID(0, 5), true, Wind.WEST);
            GameTileInterface tile3 = new GameTile(3, new CommonTile(CommonTile.Family.DOT, CommonTile.Number.FOUR), new UUID(0, 9), true, Wind.NORTH);
            GameTileInterface tile4 = new GameTile(4, new SuperiorHonor(SuperiorHonor.Dragon.GREEN), new UUID(0, 3), true, Wind.SOUTH);
            GameTileInterface tile5 = new GameTile(5, new SimpleHonor(Wind.NORTH), new UUID(0, 2), true, Wind.WEST);
            GameTileInterface tile6 = new GameTile(6, new FlowerTile(FlowerTile.Flower.PLUM), new UUID(0, 8), true, Wind.SOUTH);
            GameTileInterface tile7 = new GameTile(7, new SeasonTile(SeasonTile.Season.SPRING), new UUID(0, 7), true, Wind.NORTH);

            DAOManager manager = FileDAOManager.getInstance(rootDir);
            DAO<GameTileInterface> dao = manager.getTileDao();
            super.testSave(dao, tile1);
            super.testSave(dao, tile2);
            super.testSave(dao, tile3);
            super.testSave(dao, tile4);
            super.testSave(dao, tile5);
            super.testSave(dao, tile6);
            super.testSave(dao, tile7);
            Thread.sleep(6000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void findAll() throws DAOException {
        System.out.println("findAll");
        DAOManager manager = FileDAOManager.getInstance(rootDir);
        DAO<GameTileInterface> dao = manager.getTileDao();
        for (GameTileInterface t : dao.findAll()) {
            System.out.println(t);
        }
    }

    /**
     * Test of delete method, of class FileTileDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testDelete() throws DAOException {
        System.out.println("delete");
        try {
            DAOManager manager = FileDAOManager.getInstance(rootDir);
            DAO<GameTileInterface> dao = manager.getTileDao();
            for (GameTileInterface t : dao.findAll()) {
                System.out.println(t);
            }
            super.testDelete(dao, new UUID(0, 3));
            super.testDelete(dao, new UUID(0, 8));
            super.testDelete(dao, new UUID(0, 2));
            super.testDelete(dao, new UUID(0, 5));
            super.testDelete(dao, new UUID(0, 9));
            super.testDelete(dao, new UUID(0, 1));
            super.testDelete(dao, new UUID(0, 7));
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}