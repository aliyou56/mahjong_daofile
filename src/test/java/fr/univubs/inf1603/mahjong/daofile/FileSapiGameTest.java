package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
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
import static org.junit.Assert.*;
import fr.univubs.inf1603.mahjong.dao.SapiGameDAO;
import fr.univubs.inf1603.mahjong.sapi.Difficulty;
import fr.univubs.inf1603.mahjong.sapi.impl.SapiGame;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aliyou
 */
public class FileSapiGameTest extends FileDAOMahJongTest<SapiGame> {

    public FileSapiGameTest() {
        System.out.println("FileSapiGameDAOTest");
    }
    
    @Test
    public void testSave() throws DAOException, RulesException, MoveException, GameException, InterruptedException {
        SapiGame sapiGame1 = new SapiGame("Game1", Difficulty.EASY, createGame(new UUID(0, 245)));
        SapiGame sapiGame2 = new SapiGame("Game2", Difficulty.HARD, createGame(new UUID(0, 255)));
        SapiGame sapiGame3 = new SapiGame("Game3", Difficulty.MEDIUM, createGame(new UUID(0, 278)));
        SapiGame sapiGame4 = new SapiGame("Game4", Difficulty.SILLY, createGame(new UUID(0, 293)));

        DAOManager manager = FileDAOManager.getInstance(rootDir);
        SapiGameDAO dao = manager.getSapiGameDao();

        super.testSave(dao, sapiGame1);
        super.testSave(dao, sapiGame2);
        super.testSave(dao, sapiGame3);
        super.testSave(dao, sapiGame4);
        Thread.sleep(10000);
    }

    /**
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testDelete() throws DAOException, InterruptedException {
        DAOManager manager = FileDAOManager.getInstance(rootDir);
        SapiGameDAO dao = manager.getSapiGameDao();
        super.testDelete(dao, new UUID(0, 278));
        super.testDelete(dao, new UUID(0, 293));
        super.testDelete(dao, new UUID(0, 245));
        super.testDelete(dao, new UUID(0, 255));
        Thread.sleep(7000);
    }
    
    /**
     * Test of loadPersistedNames method, of class FileSimpleGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testLoadPersistedNames() throws DAOException, RulesException, GameException, InterruptedException {
        System.out.println("loadPersistedNames");
        
        DAOManager manager = FileDAOManager.getInstance(rootDir);
        SapiGameDAO dao = manager.getSapiGameDao();
        
        assertEquals(new ArrayList<>(), dao.loadPersistedNames());
        
        String name1 = "testGame1";
        String name2 = "testGame2";
        String name3 = "testGame3";
        SapiGame sapiGame1 = new SapiGame(name1, Difficulty.SILLY, createGame(new UUID(0, 165)));
        SapiGame sapiGame2 = new SapiGame(name2, Difficulty.MEDIUM, createGame(new UUID(0, 512)));
        SapiGame sapiGame3 = new SapiGame(name3, Difficulty.HARD, createGame(new UUID(0, 125)));
        dao.save(sapiGame1);
        dao.save(sapiGame2);
        dao.save(sapiGame3);
        Thread.sleep(10000); // Attendre que les données s'écrivent sur le disk.
        
        List<String> expResult = new ArrayList<>();
        expResult.add(name1);
        expResult.add(name2);
        expResult.add(name3);
        List<String> result = dao.loadPersistedNames();
        
        assertTrue(expResult.size() == result.size());
        expResult.forEach(name -> {
            assertTrue(result.contains(name));
        });
        
        dao.delete(sapiGame1);
        dao.delete(sapiGame2);
        dao.delete(sapiGame3);
        Thread.sleep(5000);
    }
    
    /**
     * Test of find method, of class FileSimpleGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testFind_String() throws DAOException, RulesException, GameException, InterruptedException {
        System.out.println("find_String");
        
        DAOManager manager = FileDAOManager.getInstance(rootDir);
        SapiGameDAO dao = manager.getSapiGameDao();
        
        String name1 = "testFindGameByName1";
        String name2 = "testFindGameByName2";
        SapiGame sapiGame1 = new SapiGame(name1, Difficulty.SILLY, createGame(new UUID(0, 965)));
        SapiGame sapiGame2 = new SapiGame(name2, Difficulty.MEDIUM, createGame(new UUID(0, 912)));
        dao.save(sapiGame1);
        dao.save(sapiGame2);
        Thread.sleep(10000); // Attendre que les données s'écrivent sur le disk.
        
        assertEquals(sapiGame1, dao.find(name1));
        assertEquals(sapiGame2, dao.find(name2));
        
        dao.delete(sapiGame1);
        dao.delete(sapiGame2);
        Thread.sleep(5000);
    }
    
    @Test
    public void testDelete_String() throws DAOException, RulesException, GameException, InterruptedException {
        System.out.println("fdelete_String");
        
        DAOManager manager = FileDAOManager.getInstance(rootDir);
        SapiGameDAO dao = manager.getSapiGameDao();
        
        String name1 = "testDeleteGameByName1";
        String name2 = "testDeleteGameByName2";
        SapiGame sapiGame1 = new SapiGame(name1, Difficulty.HARD, createGame(new UUID(0, 711)));
        SapiGame sapiGame2 = new SapiGame(name2, Difficulty.EASY, createGame(new UUID(0, 722)));
        dao.save(sapiGame1);
        dao.save(sapiGame2);
        Thread.sleep(10000); // Attendre que les données s'écrivent sur le disk.
        
        dao.delete(name1);
        dao.delete(name2);
        Thread.sleep(5000);
        
        assertNull(dao.find(name1));
        assertNull(dao.find(name2));
        
    }
        
    private MahjongGame createGame(UUID gameID) throws RulesException, MoveException, GameException {
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
        Wind[] playerWind = Wind.values();

        MahjongGame game = new MahjongGame(rule, board, lastPlayedMove, Duration.ofMillis(4000), Duration.ofMillis(4000),
                playerPoints, gameID, playerWind);
        return game;
    }
}
