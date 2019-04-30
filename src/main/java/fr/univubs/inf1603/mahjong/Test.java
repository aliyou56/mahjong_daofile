
package fr.univubs.inf1603.mahjong;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.dao.SapiGameDAO;
import fr.univubs.inf1603.mahjong.daofile.FileDAOManager;
import fr.univubs.inf1603.mahjong.daofile.FileZoneDAO;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.game.Game;
import fr.univubs.inf1603.mahjong.engine.game.GameException;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.GameTileInterface;
import fr.univubs.inf1603.mahjong.engine.game.MahjongBoard;
import fr.univubs.inf1603.mahjong.engine.game.MahjongGame;
import fr.univubs.inf1603.mahjong.engine.game.MahjongTileZone;
import fr.univubs.inf1603.mahjong.engine.game.Move;
import fr.univubs.inf1603.mahjong.engine.game.MoveException;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import fr.univubs.inf1603.mahjong.engine.game.TileZoneIdentifier;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile;
import fr.univubs.inf1603.mahjong.engine.rule.GameRule;
import fr.univubs.inf1603.mahjong.engine.rule.GameRuleFactory;
import fr.univubs.inf1603.mahjong.engine.rule.RulesException;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile;
import fr.univubs.inf1603.mahjong.engine.rule.SimpleHonor;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor;
import fr.univubs.inf1603.mahjong.sapi.Difficulty;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author aliyou
 */
public class Test {

    public static void main(String args[]) throws InterruptedException, DAOFileException, RulesException, MoveException, GameException {
        try {
            DAOManager daoManager = FileDAOManager.getInstance();
            SapiGameDAO dao = daoManager.getSapiGameDao();
            List<String> persistedNames = dao.loadPersistedNames();
         
               
//            DAO<Game> gameDao = daoManager.getGameDao();
//            DAO<TileZone> zoneDao = daoManager.getZoneDao();
//            DAO<GameTileInterface> tileDao = daoManager.getTileDao();
//            GameRuleFactory ruleFactory = new GameRuleFactory();
//            GameRule rule = ruleFactory.create("INTERNATIONAL");
//            MahjongBoard board = new MahjongBoard(Wind.WEST);
////            MahjongBoard board = rule.getBoardRule().distributeTiles(rule.getBoardRule().buildWall());
//            HashMap<Integer, TileZoneIdentifier> path = new HashMap<>();
//            path.put(2, TileZoneIdentifier.Wall);
//            HashMap<Integer, Boolean> publicalyVisible = new HashMap<>();
//            publicalyVisible.put(2, true);
//            Move lastPlayedMove = new Move(Wind.WEST, 0, path, publicalyVisible);
//            int[] playerPoints = {4, 8, 16, 32};
//            UUID gameID = new UUID(0, 1);
//            Wind[] playerWind = Wind.values();
//
//            Game game = new MahjongGame(rule, board, lastPlayedMove, Duration.ofMillis(4000), Duration.ofMillis(4000),
//                    playerPoints, gameID, playerWind);
//            gameDao.save(game);
//           
//            
//            Thread.sleep(8000);
//            
//            gameDao.delete(game);
//            Thread.sleep(8000);

        } catch (DAOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    static private ArrayList<GameTileInterface> createRandomTiles(int nb) {
        ArrayList<GameTileInterface> tiles = new ArrayList<>();
        for (int i = 0; i < nb; i++) {
            tiles.add(getRandomTile());
        }
        return tiles;
    }
    
    static private GameTileInterface getRandomTile() {
        GameTileInterface tile1 = new GameTile(1, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE));
        GameTileInterface tile2 = new GameTile(2, new CommonTile(CommonTile.Family.DOT, CommonTile.Number.TWO));
        GameTileInterface tile3 = new GameTile(3, new CommonTile(CommonTile.Family.CHARACTER, CommonTile.Number.THREE));
        GameTileInterface tile4 = new GameTile(4, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.FOUR));
        GameTileInterface tile5 = new GameTile(5, new CommonTile(CommonTile.Family.CHARACTER, CommonTile.Number.FIVE));
        GameTileInterface tile6 = new GameTile(6, new CommonTile(CommonTile.Family.DOT, CommonTile.Number.SIX));
        GameTileInterface tile7 = new GameTile(7, new CommonTile(CommonTile.Family.CHARACTER, CommonTile.Number.SEVEN));
        GameTileInterface tile8 = new GameTile(8, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.EIGHT));
        GameTileInterface tile9 = new GameTile(9, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE));
        GameTileInterface tile10 = new GameTile(10, new SuperiorHonor(SuperiorHonor.Dragon.GREEN));
        GameTileInterface tile11 = new GameTile(11, new SuperiorHonor(SuperiorHonor.Dragon.RED));
        GameTileInterface tile12 = new GameTile(12, new SuperiorHonor(SuperiorHonor.Dragon.WHITE));
        GameTileInterface tile13 = new GameTile(13, new SimpleHonor(Wind.WEST));
        GameTileInterface tile14 = new GameTile(14, new SimpleHonor(Wind.SOUTH));
        GameTileInterface tile15 = new GameTile(15, new SimpleHonor(Wind.EAST));
        GameTileInterface tile16 = new GameTile(16, new SimpleHonor(Wind.NORTH));
        GameTileInterface tile17 = new GameTile(17, new FlowerTile(FlowerTile.Flower.PLUM));
        GameTileInterface tile18 = new GameTile(18, new FlowerTile(FlowerTile.Flower.BAMBOO));
        GameTileInterface tile19 = new GameTile(19, new FlowerTile(FlowerTile.Flower.CHRYSANTHEMUM));
        GameTileInterface tile20 = new GameTile(20, new FlowerTile(FlowerTile.Flower.ORCHID));
        GameTileInterface tile21 = new GameTile(21, new SeasonTile(SeasonTile.Season.SPRING));
        GameTileInterface tile22 = new GameTile(22, new SeasonTile(SeasonTile.Season.AUTUMN));
        GameTileInterface tile23 = new GameTile(23, new SeasonTile(SeasonTile.Season.SUMMER));
        GameTileInterface tile24 = new GameTile(24, new SeasonTile(SeasonTile.Season.WINTER));
        GameTileInterface[] tiles = {tile1, tile2, tile3, tile4, tile5, tile6, tile7, tile8, tile9, tile10, tile11, tile12,
                                     tile13, tile14, tile15, tile16, tile17, tile18, tile19, tile20, tile21, tile22, tile23, tile24};
        int index = ThreadLocalRandom.current().nextInt(0, 23 + 1);
        return tiles[index];
    }
    
}