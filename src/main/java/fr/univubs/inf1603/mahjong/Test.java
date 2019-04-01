
package fr.univubs.inf1603.mahjong;

import java.util.UUID;
import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.dao.Persistable;
import fr.univubs.inf1603.mahjong.dao.fake_engine.GameTile;
import fr.univubs.inf1603.mahjong.dao.fake_engine.TileZone;
import fr.univubs.inf1603.mahjong.dao.fake_engine.Zone;
import fr.univubs.inf1603.mahjong.daofile.FileDAOManager;
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
//            DAO<Zone> daoZone = FileDAOManager.getInstance().getZoneDao();
//            int nb = 5;
//            GameTile tiles[] = new GameTile[nb];
//            int i=0;
//            while(i < nb) {
////                UUID uuid = new UUID(0, (i+1));
//                UUID uuid = UUID.randomUUID();
//                String type = "Type" + (i+1);
//                String family = "Family" + (i+1);
//                tiles[i] = new GameTile(uuid, type, family);
////                dao.save(tiles[i]);
//                i++;
//            }
//            Zone zone = new TileZone("my first zone", new ArrayList<>(Arrays.asList(tiles)), null);
//            daoZone.save(zone);
//              System.out.println(zone);
//                System.out.println(daoZone.find((UUID.fromString("c96f224d-175c-4a93-961b-8550bc43609"))));
//            Tile tile2 = new Tile(new UUID(0, 1), "Character", "9");
//            dao.save(tile2);
//            Tile tile = new Tile(new UUID(0, 2), "Circle", "1");
//            dao.save(tile);
//            Tile tile1 = new Tile(new UUID(0, 6), "Bamboo", "5");
//            dao.save(tile1);
//            Tile tile3 = new Tile(new UUID(0, 9), "WinEast", "1");
//            dao.save(tile3);
//            Tile tile4 = new Tile(new UUID(0, 3), "Dragon", "Red");
//            dao.save(tile4);
//            Tile tile5 = new Tile(new UUID(0, 5), "Dragon", "White");
//            dao.save(tile5);
//            Tile tile6 = new Tile(new UUID(0, 8), "Dragon", "Green");
//            dao.save(tile6);
//                dao.delete(new UUID(0, 1));
//                Tile tt = dao.find(new UUID(0, ));
//                System.out.println(tt);
//                tt.setCategory("Bamboo");

//              for(AbstractTile t : dao.findAll()) {
//                  Tile tt = (Tile) t;
//                  System.out.println(tt);
//              }
//                for(Zone zone : daoZone.findAll()) {
//                  Zone zo = (TileZone) zone;
//                  System.out.println(zo);
//              }
        } catch (DAOException ex) {
            ex.printStackTrace(System.out);
        }
    }
    
}