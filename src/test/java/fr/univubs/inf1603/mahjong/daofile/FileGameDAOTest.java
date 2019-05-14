package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.Wind;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.FileGameDAO.GameRow;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.engine.game.Game;
import fr.univubs.inf1603.mahjong.engine.game.GameException;
import fr.univubs.inf1603.mahjong.engine.game.MahjongBoard;
import fr.univubs.inf1603.mahjong.engine.game.MahjongGame;
import fr.univubs.inf1603.mahjong.engine.game.Move;
import fr.univubs.inf1603.mahjong.engine.game.TileZoneIdentifier;
import fr.univubs.inf1603.mahjong.engine.rule.GameRule;
import fr.univubs.inf1603.mahjong.engine.rule.GameRuleFactory;
import fr.univubs.inf1603.mahjong.engine.rule.RulesException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * Classe de test pour le FileGameDAO.
 *
 * @author aliyou
 * @version 1.3
 */
public class FileGameDAOTest extends FileDAOMahjongTest<Game> {

    public FileGameDAOTest() {
        System.out.println("\nFileGameDAOTest");
    }

    /**
     * Test of save method, of class FileGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.MoveException
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testWriteToPersistence() throws DAOFileException, DAOException, RulesException, GameException { 
        Game game = createGame(new UUID(0, 1));

        FileGameDAO dao = FileGameDAO.getInstance(rootDir);
        
        super.testWriteToPersistence(dao, game);
        
        dao.deleteFromPersistence(game);
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
        Game game = createGame(new UUID(0, 2));
        
        FileGameDAO dao = FileGameDAO.getInstance(rootDir);

        super.testLoadFromPersistence(dao, game);
        
        dao.deleteFromPersistence(game);
    }

    /**
     * Test of laodAll method, of class FileGameDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     */
    @Test
    public void testLoadAll() throws DAOFileException, RulesException, GameException {
        Game game1 = createGame(new UUID(0, 11));
        Game game2 = createGame(new UUID(0, 12));
//        Game game3 = createGame(new UUID(0, 13));

        FileGameDAO dao = FileGameDAO.getInstance(rootDir);
        
        List<Game> list = new ArrayList<>();
        list.add(game1);
        list.add(game2);
//        list.add(game3);
        
        super.testLaodAll(dao, list);
    }
    
    /**
     * Test of delete method, of class FileGameDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     */
    @Test
    public void testDelete() throws DAOFileException, RulesException, GameException {
        Game game1 = createGame(new UUID(0, 23));
        Game game2 = createGame(new UUID(0, 21));
//        Game game3 = createGame(new UUID(0, 22));
        List<Game> list = new ArrayList<>();
        list.add(game1);
        list.add(game2);
//        list.add(game3);
        
        FileGameDAO dao = FileGameDAO.getInstance(rootDir);
        
        super.testDelete(dao, list);
    }
    
    /**
     * Test of deleteFromPersistence method, of class FileGameDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws java.lang.InterruptedException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     */
    @Test
    public void testDeleteFromPersistence() throws DAOFileException, DAOException, InterruptedException, RulesException, GameException {
        System.out.println("deleteFromPersistence");
        UUID dataID = new UUID(0, 223);
        Game game = createGame(dataID);

        FileGameDAO dao = FileGameDAO.getInstance(rootDir);
        // Ecrire la zone dans le fichier de données
        dao.writeToPersistence(game);
        // Attendre que l'écriture soit effective
        synchronized (dao) {
            dao.wait(10000);
        }
        // Supprimer la zone.
        dao.deleteFromPersistence(game);
        // Vérifier si la zone bien été supprimée
        Assert.assertNull(dao.loadFromPersistence(dataID));
    }

    /**
     * Test of getDataRow method, of class FileGameDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     */
    @Test
    public void testGetDataRow_3args() throws DAOFileException, RulesException, GameException {
        FileGameDAO dao = FileGameDAO.getInstance(rootDir);
        
        Game game = createGame(new UUID(0, 41));
        DataRow<Game> expResult = new GameRow(41, game, 79);
        
        super.testGetDataRow_3args(dao, expResult);
    }

    /**
     * Test of getDataRow method, of class FileGameDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.GameException
     */
    @Test
    public void testGetDataRow_long() throws DAOFileException, DAOException, RulesException, GameException {
        FileGameDAO dao = FileGameDAO.getInstance(rootDir);
      
        Game game = createGame(new UUID(0, 51));
        
        super.testGetDataRow_long(dao, game);
        
        dao.deleteFromPersistence(game);
    }

    /**
     * Test of getInstance method, of class FileGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetInstance() throws DAOFileException {
        System.out.println("getInstance");
        FileGameDAO expResult = FileGameDAO.getInstance(rootDir);
        FileGameDAO result = FileGameDAO.getInstance(rootDir);
        Assert.assertEquals(expResult, result);
    }
    
//    }
//    @Test
//    public void testUpdate() throws DAOException, InterruptedException, RulesException, GameException {
//        System.out.println("testUpdate");
//        MahjongGame game = createGame(new UUID(0, 110));
//
//        DAOManager manager = FileDAOManager.getInstance(rootDir);
//        DAO<Game> dao = manager.getGameDao();
//        super.testSave(dao, game);
//        if (TEST_WITH_FILE_WRITING) {
//            Thread.sleep(8000);
//        }
//
//        MahjongBoard board = (MahjongBoard) game.getBoard();
//        board.setWind(Wind.EAST);
//        if (TEST_WITH_FILE_WRITING) {
//            Thread.sleep(7000);
//        }
//
//        dao.delete(game);
//        if (TEST_WITH_FILE_WRITING) {
//            Thread.sleep(6000);
//        }
//    }
    
    private MahjongGame createGame(UUID gameID) throws RulesException, GameException {
        GameRuleFactory ruleFactory = new GameRuleFactory();
        GameRule rule = ruleFactory.create("INTERNATIONAL");
//        MahjongGame game = new MahjongGame(gameID, rule, Duration.ofSeconds(4), Duration.ofSeconds(4));
//        game.launchGame();
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
    protected void assertTest(Game game1, Game game2) {
        TestUtilities.assertTest(game1, game2);
    }

    @Override
    protected Comparator<Game> getComparator() {
        return (Game game1, Game game2) -> {
            return game1.getUUID().compareTo(game2.getUUID());
        };
    }
}
