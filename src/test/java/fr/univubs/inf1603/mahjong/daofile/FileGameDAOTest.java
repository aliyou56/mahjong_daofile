package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.Wind;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
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
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Test;

/**
 *
 * @author aliyou
 */
public class FileGameDAOTest extends FileDAOMahjongTest<Game> {

    public FileGameDAOTest() {
        System.out.println("FileGameDAOTest");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of save method, of class FileGameDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws fr.univubs.inf1603.mahjong.engine.rule.RulesException
     * @throws fr.univubs.inf1603.mahjong.engine.game.MoveException
     */
    @Test
    public void testSave() throws DAOException, RulesException, GameException, DAOFileException {
        try {
            Game game1 = createGame(new UUID(0, 451));
            Game game2 = createGame(new UUID(0, 452));
            Game game3 = createGame(new UUID(0, 453));
            Game game4 = createGame(new UUID(0, 454));
            
            FileGameDAO dao = FileGameDAO.getInstance(rootDir);

            super.testSave(dao, game1);
            super.testSave(dao, game2);
            super.testSave(dao, game3);
            super.testSave(dao, game4);

            if (TEST_WITH_FILE_WRITING) {
                Thread.sleep(14000);
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Test of delete method, of class FileZoneDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testDelete() throws DAOException, DAOFileException {
        try {
            FileGameDAO dao = FileGameDAO.getInstance(rootDir);
            super.testDelete(dao, new UUID(0, 453));
            super.testDelete(dao, new UUID(0, 452));
            super.testDelete(dao, new UUID(0, 451));
            super.testDelete(dao, new UUID(0, 454));
            if (TEST_WITH_FILE_WRITING) {
                Thread.sleep(6000);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.out);
        }
    }

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
    
    @Override
    protected boolean compare(Game obj1, Game obj2) {
        if ((obj1 == null && obj2 != null)
                || (obj1 != null && obj2 == null)) {
            return false;
        } else if ((obj1 == null && obj2 == null)) {
            return true;
        } else {
            if (obj1.getUUID().compareTo(obj2.getUUID()) != 0) {
                return false;
            }
            try {
                if (obj1.getCurrentwind().toString().equals(obj2.getCurrentwind().toString())  ) {
                    return false;
                }
            } catch (GameException ex) {
                Logger.getLogger(FileGameDAOTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (obj1.getPlayingTime() != obj2.getPlayingTime()) {
                return false;
            }
            if (obj1.getStealingTime() != obj2.getStealingTime()) {
                return false;
            }
            if (obj1.getRule().getName().equals(obj2.getRule().getName())) {
                return false;
            }
            return true;
        }
    }
}
