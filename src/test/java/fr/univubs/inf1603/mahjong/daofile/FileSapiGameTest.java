package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.Wind;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.FileSapiGameDAO.SapiGameRow;
import fr.univubs.inf1603.mahjong.engine.game.GameException;
import fr.univubs.inf1603.mahjong.engine.game.MahjongBoard;
import fr.univubs.inf1603.mahjong.engine.game.MahjongGame;
import fr.univubs.inf1603.mahjong.engine.game.Move;
import fr.univubs.inf1603.mahjong.engine.game.TileZoneIdentifier;
import fr.univubs.inf1603.mahjong.engine.rule.GameRule;
import fr.univubs.inf1603.mahjong.engine.rule.GameRuleFactory;
import fr.univubs.inf1603.mahjong.engine.rule.RulesException;
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.sapi.Difficulty;
import fr.univubs.inf1603.mahjong.sapi.impl.SapiGame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;

/**
 * Classe de test pour le FileSapiGameDAO.
 * 
 * Avec les attentes d'écriture sur les fichiers, le test dure environ 35 sécondes.
 * 
 *  !!! Le test a pris 35.122s sur mon pc (hp core i7) !!!
 *
 * @author aliyou
 * @version 1.3
 */
public class FileSapiGameTest extends FileDAOMahjongTest<SapiGame> {

    public FileSapiGameTest() {
        System.out.println("\nFileSapiGameDAOTest");
    }

    /**
     * Test of save method, of class FileSapiGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.MoveException
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testWriteToPersistence() throws DAOFileException, DAOException, RulesException, GameException {
        SapiGame sapiGame = new SapiGame("testWriteToPersistenceSapiGame", Difficulty.EASY, createGame(new UUID(0, 1)));

        FileSapiGameDAO dao = FileSapiGameDAO.getInstance(rootDir);

        super.testWriteToPersistence(dao, sapiGame);

        dao.deleteFromPersistence(sapiGame);
    }

    /**
     * Test of loadFromPersistence method, of class FileGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     */
    @Test
    public void loadFromPersistence() throws DAOFileException, DAOException, RulesException, GameException {
        SapiGame sapiGame = new SapiGame("testLoadFromPersistenceSapiGame", Difficulty.MEDIUM, createGame(new UUID(0, 2)));

        FileSapiGameDAO dao = FileSapiGameDAO.getInstance(rootDir);

        super.testWriteToPersistence(dao, sapiGame);

        dao.deleteFromPersistence(sapiGame);
    }

    /**
     * Test of laodAll method, of class FileGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     */
    @Test
    public void testLoadAll() throws DAOFileException, RulesException, GameException {
        SapiGame sapiGame1 = new SapiGame("testLoadAllSapiGame1", Difficulty.HARD, createGame(new UUID(0, 3)));
        SapiGame sapiGame2 = new SapiGame("testLoadAllSapiGame2", Difficulty.MEDIUM, createGame(new UUID(0, 4)));

        FileSapiGameDAO dao = FileSapiGameDAO.getInstance(rootDir);

        List<SapiGame> list = new ArrayList<>();
        list.add(sapiGame1);
        list.add(sapiGame2);

        super.testLaodAll(dao, list);
    }

    /**
     * Test of delete method, of class FileGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     */
    @Test
    public void testDelete() throws DAOFileException, RulesException, GameException {
        SapiGame sapiGame1 = new SapiGame("testDeleteSapiGame1", Difficulty.HARD, createGame(new UUID(0, 6)));
        SapiGame sapiGame2 = new SapiGame("testDeleteSapiGame2", Difficulty.MEDIUM, createGame(new UUID(0, 7)));
        List<SapiGame> list = new ArrayList<>();
        list.add(sapiGame1);
        list.add(sapiGame2);

        FileSapiGameDAO dao = FileSapiGameDAO.getInstance(rootDir);

        super.testDelete(dao, list);
    }

    /**
     * Test of deleteFromPersistence method, of class FileGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws java.lang.InterruptedException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     */
    @Test
    public void testDeleteFromPersistence() throws DAOFileException, DAOException, InterruptedException, RulesException, GameException {
        System.out.println("deleteFromPersistence");
        UUID gameID = new UUID(0, 9);
        SapiGame sapiGame = new SapiGame("testDeleteFromPersistenceSapiGame", Difficulty.MEDIUM, createGame(gameID));

        FileSapiGameDAO dao = FileSapiGameDAO.getInstance(rootDir);

        dao.writeToPersistence(sapiGame);
        // Attendre que l'écriture soit effective
        synchronized (dao) {
            dao.wait(10000);
        }

        dao.deleteFromPersistence(sapiGame);

        Assert.assertNull(dao.loadFromPersistence(gameID));
    }

    /**
     * Test of getDataRow method, of class FileGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testGetDataRow_3args() throws DAOFileException, RulesException, GameException, DAOException {
        FileSapiGameDAO dao = FileSapiGameDAO.getInstance(rootDir);

        SapiGame sapiGame = new SapiGame("testGetDataRow_3argsSapiGame", Difficulty.HARD, createGame(UUID.randomUUID()));
        DataRow<SapiGame> expResult = new SapiGameRow(41, sapiGame, 79);

        super.testGetDataRow_3args(dao, expResult);
        dao.deleteFromPersistence(sapiGame);
    }

    /**
     * Test of getDataRow method, of class FileGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     */
    @Test
    public void testGetDataRow_long() throws DAOFileException, DAOException, RulesException, GameException {
        FileSapiGameDAO dao = FileSapiGameDAO.getInstance(rootDir);

        SapiGame sapiGame = new SapiGame("testGetDataRow_longSapiGame", Difficulty.SILLY, createGame(UUID.randomUUID()));

        super.testGetDataRow_long(dao, sapiGame);

        dao.deleteFromPersistence(sapiGame);
    }

    /**
     * Test of getInstance method, of class FileZoneDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetInstance() throws DAOFileException {
        System.out.println("getInstance");
        FileSapiGameDAO expResult = FileSapiGameDAO.getInstance(rootDir);
        FileSapiGameDAO result = FileSapiGameDAO.getInstance(rootDir);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of loadPersistedNames method, of class FileSimpleGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     * @throws java.lang.InterruptedException
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testLoadPersistedNames() throws DAOException, RulesException, GameException, InterruptedException, DAOFileException {
        System.out.println("loadPersistedNames");

        FileSapiGameDAO dao = FileSapiGameDAO.getInstance(rootDir);

        assertEquals(new ArrayList<>(), dao.loadPersistedNames());

        String name1 = "loadPersistedNamesSapiGame1";
        String name2 = "loadPersistedNamesSapiGame2";
        SapiGame sapiGame1 = new SapiGame(name1, Difficulty.SILLY, createGame(new UUID(0, 20)));
        SapiGame sapiGame2 = new SapiGame(name2, Difficulty.MEDIUM, createGame(new UUID(0, 21)));
        dao.save(sapiGame1);
        dao.save(sapiGame2);
        synchronized (dao) {
            dao.wait(10000);
        }

        List<String> expResult = new ArrayList<>();
        expResult.add(name1);
        expResult.add(name2);
        Collections.sort(expResult);
        List<String> result = dao.loadPersistedNames();
        System.out.println("\nexpect : " + expResult);
        System.out.println("result : " + result + "\n");

        Assert.assertArrayEquals(expResult.toArray(), result.toArray());

        dao.delete(sapiGame1);
        dao.delete(sapiGame2);
    }

    /**
     * Test of loadPersistedUUIDs method, of class FileSimpleGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     * @throws java.lang.InterruptedException
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testLoadPersistedUUIDs() throws DAOException, RulesException, GameException, InterruptedException, DAOFileException {
        System.out.println("loadPersistedUUIDs");

        FileSapiGameDAO dao = FileSapiGameDAO.getInstance(rootDir);

        assertEquals(new ArrayList<>(), dao.loadPersistedUUIDs());

        UUID id1 = new UUID(0, 31);
        UUID id2 = new UUID(0, 32);
        SapiGame sapiGame1 = new SapiGame("testLoadPersistedUUIDsGame1", Difficulty.MEDIUM, createGame(id1));
        SapiGame sapiGame2 = new SapiGame("testLoadPersistedUUIDsGame2", Difficulty.HARD, createGame(id2));
        dao.save(sapiGame1);
        dao.save(sapiGame2);
        //Attendre que l'écriture soit éffective
        synchronized (dao) {
            dao.wait(10000);
        }

        List<UUID> expResult = new ArrayList<>();
        expResult.add(id1);
        expResult.add(id2);
        Collections.sort(expResult);
        List<UUID> result = dao.loadPersistedUUIDs();
        System.out.println("\nexpect : " + expResult);
        System.out.println("result : " + result + "\n");

        Assert.assertArrayEquals(expResult.toArray(), result.toArray());

        dao.delete(sapiGame1);
        dao.delete(sapiGame2);
    }

    private MahjongGame createGame(UUID gameID) throws RulesException, GameException {
        GameRuleFactory ruleFactory = new GameRuleFactory();
        GameRule rule = ruleFactory.create("INTERNATIONAL");
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

    @Override
    protected void assertTest(SapiGame sp1, SapiGame sp2) {
        assertEquals(sp1.getUUID(), sp2.getUUID());
        assertEquals(sp1.getName(), sp2.getName());
        assertEquals(sp1.getSurrenderDifficulty(), sp2.getSurrenderDifficulty());
        TestUtilities.assertTest(sp1.getGame(), sp2.getGame());
    }

    @Override
    protected Comparator<SapiGame> getComparator() {
        return (SapiGame sapiGame1, SapiGame sapiGame2) -> {
            return sapiGame1.getUUID().compareTo(sapiGame2.getUUID());
        };
    }
}