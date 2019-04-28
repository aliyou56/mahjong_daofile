
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.engine.game.Game;
import fr.univubs.inf1603.mahjong.engine.game.GameException;
import fr.univubs.inf1603.mahjong.engine.game.MahjongBoard;
import fr.univubs.inf1603.mahjong.engine.game.MahjongGame;
import fr.univubs.inf1603.mahjong.engine.game.Move;
import fr.univubs.inf1603.mahjong.engine.game.MoveException;
import fr.univubs.inf1603.mahjong.engine.game.TileZoneIdentifier;
import fr.univubs.inf1603.mahjong.engine.rule.GameRule;
import fr.univubs.inf1603.mahjong.engine.rule.GameRuleFactory;
import fr.univubs.inf1603.mahjong.engine.rule.RulesException;
import fr.univubs.inf1603.mahjong.engine.rule.Wind;
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import org.junit.Test;

/**
 *
 * @author aliyou
 */
public class FileGameDAOTest extends FileDAOMahJongTest<Game>{
    
    public FileGameDAOTest() {
        System.out.println("FileGameDAOTest");
    }
    
    /**
     * Test of save method, of class FileGameDAO.
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.MoveException
     */
    @Test
    public void testSave() throws DAOException, RulesException, MoveException, GameException {
        try {
            GameRuleFactory ruleFactory = new GameRuleFactory();
            GameRule rule = ruleFactory.create("INTERNATIONAL");
            MahjongBoard board = new MahjongBoard(Wind.WEST);
//            MahjongBoard board = rule.getBoardRule().distributeTiles(rule.getBoardRule().buildWall());
            HashMap<Integer, TileZoneIdentifier> path = new HashMap<>();
            path.put(2, TileZoneIdentifier.Wall);
            HashMap<Integer, Boolean> publicalyVisible = new HashMap<>();
            publicalyVisible.put(2, true);
            Move lastPlayedMove = new Move(Wind.WEST, 0, path, publicalyVisible);
            int[] playerPoints = {4, 8, 16, 32};
            UUID gameID = new UUID(0, 19);
            Wind[] playerWind = Wind.values();
            
            Game game = new MahjongGame(rule, board, lastPlayedMove, Duration.ofMillis(4000), Duration.ofMillis(4000),
                    playerPoints, gameID, playerWind);
            
            DAOManager manager = FileDAOManager.getInstance(rootDir);
            DAO<Game> dao = manager.getGameDao();
            
            super.testSave(dao, game);
//            super.testSave(dao, game2);
            
            Thread.sleep(12000);
            
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    /**
     * Test of delete method, of class FileZoneDAO.
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testDelete() throws DAOException {
        try {
            DAOManager manager = FileDAOManager.getInstance(rootDir);
            DAO<Game> dao = manager.getGameDao();
            super.testDelete(dao, new UUID(0, 19));
            Thread.sleep(6000);
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.out);
        }
    }

//    /**
//     * Test of setLinkManager method, of class FileGameDAO.
//     */
//    @Test
//    public void testSetLinkManager() {
//        System.out.println("setLinkManager");
//        LinkManager<TileZone> zoneToGameLinkManager = null;
//        FileGameDAO instance = null;
//        instance.setLinkManager(zoneToGameLinkManager);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDataRow method, of class FileGameDAO.
//     */
//    @Test
//    public void testGetDataRow_3args() throws Exception {
//        System.out.println("getDataRow");
//        int rowID = 0;
//        Game data = null;
//        long pointer = 0L;
//        FileGameDAO instance = null;
//        DataRow<Game> expResult = null;
//        DataRow<Game> result = instance.getDataRow(rowID, data, pointer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDataRow method, of class FileGameDAO.
//     */
//    @Test
//    public void testGetDataRow_DAOFileWriter_long() throws Exception {
//        System.out.println("getDataRow");
//        DAOFileWriter writer = null;
//        long pointer = 0L;
//        FileGameDAO instance = null;
//        DataRow<Game> expResult = null;
//        DataRow<Game> result = instance.getDataRow(writer, pointer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of deleteFromPersistance method, of class FileGameDAO.
//     */
//    @Test
//    public void testDeleteFromPersistance() throws Exception {
//        System.out.println("deleteFromPersistance");
//        Game game = null;
//        FileGameDAO instance = null;
//        instance.deleteFromPersistance(game);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
