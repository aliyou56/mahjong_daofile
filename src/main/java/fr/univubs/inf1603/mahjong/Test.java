
package fr.univubs.inf1603.mahjong;

import java.util.UUID;
import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.daofile.FileDAOManager;
import fr.univubs.inf1603.mahjong.daofile.FileTileDAO;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile.Season;
import fr.univubs.inf1603.mahjong.engine.rule.SimpleHonor;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor;
import fr.univubs.inf1603.mahjong.engine.rule.Wind;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author aliyou
 */
public class Test {
    
    public static void main(String args[]) {
        try {
            DAOManager daoManager = FileDAOManager.getInstance();
            DAO<GameTile> dao = daoManager.getTileDao();
//            DAO<TileZone> daoZone = daoManager.getZoneDao();
//            dao.delete(UUID.fromString("0225b707-d37c-4df6-a480-c5fc000c65ca"));
//            int nb = 10;
//            GameTileOld tiles[] = new GameTileOld[nb];
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
//            System.out.println(dao.find(new UUID(0, 1)));
             GameTile tile1 = new GameTile(1, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE), new UUID(0, 1));
            dao.save(tile1);
//            GameTile tile2 = new GameTile(2, new CommonTile(CommonTile.Family.CHARACTER, CommonTile.Number.ONE), new UUID(0, 5));
//            dao.save(tile2);
//            GameTile tile3 = new GameTile(3, new CommonTile(CommonTile.Family.DOT, CommonTile.Number.FOUR), new UUID(0, 9));
//            dao.save(tile3);
//            GameTile tile4 = new GameTile(4, new SuperiorHonor(SuperiorHonor.Dragon.GREEN), new UUID(0, 3));
//            dao.save(tile4);
//            GameTile tile5 = new GameTile(5, new SimpleHonor(Wind.NORTH), new UUID(0, 2));
//            dao.save(tile5);
//            GameTile tile6 = new GameTile(6, new FlowerTile(FlowerTile.Flower.PLUM), new UUID(0, 8));
//            dao.save(tile6);
//            GameTile tile7 = new GameTile(7, new SeasonTile(SeasonTile.Season.SPRING), new UUID(0, 7));
//            dao.save(tile7);
//            for(GameTile gt : dao.findAll()) {
//                System.out.println(gt + " -> " + gt.getTile().toNormalizedName()+ " : " +gt.getTile().getClass().getSimpleName());
//            }
        } catch (DAOException ex) {
            ex.printStackTrace(System.out);
        } 
    }
    
}