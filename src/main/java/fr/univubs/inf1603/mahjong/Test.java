
package fr.univubs.inf1603.mahjong;

import fr.univubs.inf1603.mahjong.ai.Difficulty;
import java.util.UUID;
import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.dao.SimpleGameDAO;
import fr.univubs.inf1603.mahjong.daofile.FileDAOManager;
import fr.univubs.inf1603.mahjong.engine.game.Game;
import fr.univubs.inf1603.mahjong.engine.game.GameException;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import fr.univubs.inf1603.mahjong.engine.rule.Wind;
import fr.univubs.inf1603.mahjong.sapi.Player;
import fr.univubs.inf1603.mahjong.sapi.SapiManager;
import fr.univubs.inf1603.mahjong.sapi.SimpleGame;
import fr.univubs.inf1603.mahjong.sapi.SimpleRule;
import fr.univubs.inf1603.mahjong.sapi.impl.HumanPlayerImpl;
import fr.univubs.inf1603.mahjong.sapi.impl.SapiManagerImpl;
import fr.univubs.inf1603.mahjong.sapi.impl.SimpleGameImpl;
import fr.univubs.inf1603.mahjong.sapi.impl.SimpleRuleImpl;

/**
 *
 * @author aliyou
 */
public class Test {
    
    public static void main(String args[]) throws GameException {
        try {
            DAOManager daoManager = FileDAOManager.getInstance();
            SimpleGameDAO simpleGameDao = daoManager.getSimpleGameDao();
//            DAO<Player> playerDao = daoManager.getPlayerDao();
//            DAO<Game> gameeDao = daoManager.getGameDao();
//            DAO<TileZone> zoneeDao = daoManager.getZoneDao();
//            DAO<GameTile> tileDao = daoManager.getTileDao();

              SapiManagerImpl sapiManager = new SapiManagerImpl();
              SimpleRule rule = new SimpleRuleImpl("ruleName", "desc");
              Player p1 = new HumanPlayerImpl("Player1", new UUID(0, 1));
              Player p2 = new HumanPlayerImpl("Player2", new UUID(0, 2));
              Player p3 = new HumanPlayerImpl("Player3", new UUID(0, 3));
              Player p4 = new HumanPlayerImpl("Player4", new UUID(0, 4));
              Player[] players = {p1, p2, p3, p4};
              SimpleGame gq = new SimpleGameImpl(players, rule, new UUID(0,1), "simpleGamteTest", sapiManager, 4, 5, Difficulty.SILLY, null);
//            Player p = new HumanPlayerImpl("Player1", new UUID(0, 1));
//            p.setWind(Wind.WEST);
//            Player bot = new BotFactory().createBot("bot1", BotDifficulties.Difficulty.SILLY, new SimpleRuleImpl("rule", ""), new UUID(0, 1));
//            playerDao.save(p);
//               Player pp = playerDao.find(new UUID(0, 1));
//            System.out.println(pp.getUUID());
//            System.out.println(pp.getName());

        } catch (DAOException ex) {
            ex.printStackTrace(System.out);
        } 
    }

}