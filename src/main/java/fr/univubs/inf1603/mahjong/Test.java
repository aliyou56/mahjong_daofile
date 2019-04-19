
package fr.univubs.inf1603.mahjong;

import java.util.UUID;
import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.daofile.FileDAOManager;
import fr.univubs.inf1603.mahjong.daofile.FileTileDAO;
import fr.univubs.inf1603.mahjong.daofile.engine.MahjongTileZone;
import fr.univubs.inf1603.mahjong.daofile.engine.Rule;
import fr.univubs.inf1603.mahjong.engine.game.Game;
import fr.univubs.inf1603.mahjong.engine.game.GameException;
import fr.univubs.inf1603.mahjong.engine.game.MahjongGame;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import fr.univubs.inf1603.mahjong.engine.game.TileZoneIdentifier;
import fr.univubs.inf1603.mahjong.engine.rule.AbstractTile;
import fr.univubs.inf1603.mahjong.engine.rule.BoardRule;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile;
import fr.univubs.inf1603.mahjong.engine.rule.GameRule;
import fr.univubs.inf1603.mahjong.engine.rule.ScoringSystem;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile.Season;
import fr.univubs.inf1603.mahjong.engine.rule.SimpleHonor;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor;
import fr.univubs.inf1603.mahjong.engine.rule.Wind;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliyou
 */
public class Test {
    
    public static void main(String args[]) throws GameException {
        try {
            DAOManager daoManager = FileDAOManager.getInstance();
            DAO<GameTile> dao = daoManager.getTileDao();
//            DAO<TileZone> daoZone = daoManager.getZoneDao();
//            int nb = 10;
//            GameTile tiles[] = new GameTileOld[nb];
//            int i=0;
//            while(i < nb) {
////                UUID uuid = new UUID(0, (i+1));
//                UUID uuid = UUID.randomUUID();
//                String type = "Type" + (i+1);
//                String family = "Family" + (i+1);
//                tiles[i] = new GameTileOld(uuid, type, family);
////                dao.save(tiles[i]);
//                i++;
//            }
//            TileZone zone0 = new MahjongTileZone(new UUID(0, 10), createRandomTiles(4, 1), TileZoneIdentifier.Wall); //91
//            daoZone.save(zone0);
//            TileZone zone1 = new MahjongTileZone(new UUID(0, 11), createRandomTiles(3, 5), TileZoneIdentifier.HandEast);
//            daoZone.save(zone1);
//            TileZone zone2 = new MahjongTileZone(new UUID(0, 12), createRandomTiles(2, 8), TileZoneIdentifier.HandNorth);
//            daoZone.save(zone2);
//            TileZone zone3 = new MahjongTileZone(new UUID(0, 13), createRandomTiles(1, 10), TileZoneIdentifier.HandSouth);
//            daoZone.save(zone3);
//            TileZone zone4 = new MahjongTileZone(new UUID(0, 14), createRandomTiles(5, 11), TileZoneIdentifier.HandWest);
//            daoZone.save(zone4);
            
//            daoZone.delete(new UUID(0, 10));
//            for(GameTile gt : dao.findAll()) {
//                System.err.println(gt.getUUID());
//            }
//                System.err.println("Zone");
//            for(TileZone gt : daoZone.findAll()) {
//                System.err.println(gt.getUUID());
//            }
        } catch (DAOException ex) {
            ex.printStackTrace(System.out);
        } 
    }
    
    static ArrayList<GameTile> createRandomTiles(int nb, int id) {
        ArrayList<GameTile> tiles = new ArrayList<>();
        for (int i = 0; i < nb; i++) {
            AbstractTile absTile = new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE);
            GameTile tile = new GameTile(i+1, absTile, new UUID(0, id));
            tiles.add(tile);
            id++;
        }
        return tiles;
    }
    
    
}