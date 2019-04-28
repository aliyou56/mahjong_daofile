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

/**
 *
 * @author aliyou
 */
public class SapiGameTest extends FileDAOMahJongTest<SapiGame> {

    public SapiGameTest() {
        System.out.println("FileSapiGameDAOTest");
    }

    @Test
    public void testSave() throws DAOException, RulesException, MoveException, GameException {
        try {
            UUID gameID = new UUID(0, 45);
            SapiGame sapiGame = new SapiGame("Game", Difficulty.SILLY, createGame(gameID));

            DAOManager manager = FileDAOManager.getInstance(rootDir);
            SapiGameDAO dao = manager.getSapiGameDao();

            dao.save(sapiGame);

            Thread.sleep(10000);

            assertEquals(sapiGame, dao.find(sapiGame.getUUID()));
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.out);
        }
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
//        UUID gameID = new UUID(0, 1);
        Wind[] playerWind = Wind.values();

        MahjongGame game = new MahjongGame(rule, board, lastPlayedMove, Duration.ofMillis(4000), Duration.ofMillis(4000),
                playerPoints, gameID, playerWind);
        return game;
    }
     
    /**
     * Test of loadPersistedNames method, of class FileSimpleGameDAO.
     */
//    @Test
//    public void testLoadPersistedNames() throws Exception {
//        System.out.println("loadPersistedNames");
//        FileSimpleGameDAO instance = null;
//        List<String> expResult = null;
//        List<String> result = instance.loadPersistedNames();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
    /**
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testDelete() throws DAOException {
        try {
            DAOManager manager = FileDAOManager.getInstance(rootDir);
            SapiGameDAO dao = manager.getSapiGameDao();
            super.testDelete(dao, new UUID(0, 45));
            Thread.sleep(7000);
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.out);
        }
    }
}
