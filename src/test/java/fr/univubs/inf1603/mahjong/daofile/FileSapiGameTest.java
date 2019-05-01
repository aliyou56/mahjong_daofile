package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.Wind;
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
 * Classe de test pour le SapiGameDAO.
 *
 * @author aliyou
 * @version 1.2.5
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

        dao.save(sapiGame1);
        dao.save(sapiGame2);
        dao.save(sapiGame3);
        dao.save(sapiGame4);
        if (TEST_WITH_FILE_WRITING) {
            Thread.sleep(10000);
        }
        testSapiGame(sapiGame1, dao.find(sapiGame1.getName()));
        testSapiGame(sapiGame2, dao.find(sapiGame2.getUUID()));
        testSapiGame(sapiGame3, dao.find(sapiGame3.getName()));
        testSapiGame(sapiGame4, dao.find(sapiGame4.getUUID()));
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
        if (TEST_WITH_FILE_WRITING) {
            Thread.sleep(7000);
        }
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
        if (TEST_WITH_FILE_WRITING) {
            Thread.sleep(10000); // Attendre que les données s'écrivent sur le disk.
        }

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
        if (TEST_WITH_FILE_WRITING) {
            Thread.sleep(5000);
        }
    }

    /**
     * Test of loadPersistedUUIDs method, of class FileSimpleGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testLoadPersistedUUIDs() throws DAOException, RulesException, GameException, InterruptedException {
        System.out.println("loadPersistedUUIDs");

        DAOManager manager = FileDAOManager.getInstance(rootDir);
        SapiGameDAO dao = manager.getSapiGameDao();

        assertEquals(new ArrayList<>(), dao.loadPersistedUUIDs());

        UUID id1 = new UUID(0, 357);
        UUID id2 = new UUID(0, 359);
        UUID id3 = new UUID(0, 328);
        SapiGame sapiGame1 = new SapiGame("testLoadPersistedUUIDsGame1", Difficulty.MEDIUM, createGame(id1));
        SapiGame sapiGame2 = new SapiGame("testLoadPersistedUUIDsGame2", Difficulty.HARD, createGame(id2));
        SapiGame sapiGame3 = new SapiGame("testLoadPersistedUUIDsGame3", Difficulty.SILLY, createGame(id3));
        dao.save(sapiGame1);
        dao.save(sapiGame2);
        dao.save(sapiGame3);
        if (TEST_WITH_FILE_WRITING) {
            Thread.sleep(10000); // Attendre que les données s'écrivent sur le disk.
        }

        List<UUID> expResult = new ArrayList<>();
        expResult.add(id1);
        expResult.add(id2);
        expResult.add(id3);
        List<UUID> result = dao.loadPersistedUUIDs();

        assertTrue(expResult.size() == result.size());
        expResult.forEach(uuid -> {
            assertTrue(result.contains(uuid));
        });

        dao.delete(sapiGame1);
        dao.delete(sapiGame2);
        dao.delete(sapiGame3);
        if (TEST_WITH_FILE_WRITING) {
            Thread.sleep(5000);
        }
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
        if (TEST_WITH_FILE_WRITING) {
            Thread.sleep(10000); // Attendre que les données s'écrivent sur le disk.
        }

        testSapiGame(sapiGame1, dao.find(name1));
        testSapiGame(sapiGame2, dao.find(name2));

        dao.delete(sapiGame1);
        dao.delete(sapiGame2);
        if (TEST_WITH_FILE_WRITING) {
            Thread.sleep(5000);
        }
    }

    private void testSapiGame(SapiGame sp1, SapiGame sp2) {
        assertEquals(sp1.getUUID(), sp2.getUUID());
        assertEquals(sp1.getName(), sp2.getName());
        assertEquals(sp1.getSurrenderDifficulty(), sp2.getSurrenderDifficulty());
        assertEquals(sp1.getGame().getUUID(), sp2.getGame().getUUID());
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
        if (TEST_WITH_FILE_WRITING) {
            Thread.sleep(10000); // Attendre que les données s'écrivent sur le disk.
        }

        dao.delete(name1);
        dao.delete(name2);
        if (TEST_WITH_FILE_WRITING) {
            Thread.sleep(5000);
        }

        assertNull(dao.find(name1));
        assertNull(dao.find(name2));

    }

    private MahjongGame createGame(UUID gameID) throws RulesException, MoveException, GameException {
        GameRuleFactory ruleFactory = new GameRuleFactory();
        GameRule rule = ruleFactory.create("INTERNATIONAL");
//        MahjongBoard board = new MahjongBoard(Wind.WEST);
            MahjongBoard board = rule.getBoardRule().distributeTiles(rule.getBoardRule().buildWall());
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
