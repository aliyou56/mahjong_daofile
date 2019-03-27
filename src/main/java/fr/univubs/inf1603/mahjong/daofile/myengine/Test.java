
package fr.univubs.inf1603.mahjong.daofile.myengine;

import fr.univubs.inf1603.mahjong.dao.AbstractTile;
import java.util.UUID;
import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.Zone;
import fr.univubs.inf1603.mahjong.daofile.FileDAOManager;
import fr.univubs.inf1603.mahjong.engine.TileZoneException;
import java.util.ArrayList;
import java.util.Arrays;
//import fr.univubs.inf1603.mahjong.engine.AbstractTile;


/**
 *
 * @author aliyou
 */
public class Test {
    
    public static void main(String args[]) throws TileZoneException {
        try {
            DAO<AbstractTile> dao = FileDAOManager.getInstance().getTileDao();
            DAO<Zone> daoZone = FileDAOManager.getInstance().getZoneDao();
//            int nb = 5;
//            Tile tiles[] = new Tile[nb];
//            int i=0;
//            while(i < nb) {
////                UUID uuid = new UUID(0, (i+1));
//                UUID uuid = UUID.randomUUID();
//                String type = "Type" + (i+1);
//                String family = "Family" + (i+1);
//                tiles[i] = new Tile(uuid, type, family);
////                dao.save(tiles[i]);
//                i++;
//            }
//            Zone zone = new TileZone("my first zone", new ArrayList<>(Arrays.asList(tiles)));
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
//            System.out.println(dao.find(UUID.fromString("5f5f525a-1703-4522-a396-4607922fd2b4")));
//            System.out.println(dao.find(UUID.fromString("0913f7a0-e0b7-4208-8ca6-72d7c52096bf")));

//              for(AbstractTile t : dao.findAll()) {
//                  Tile tt = (Tile) t;
//                  System.out.println(tt);
//              }
//              248205a1-614b-4cc7-9e99-877b845fbe18
                for(Zone zone : daoZone.findAll()) {
                  Zone zo = (TileZone) zone;
                  System.out.println(zo);
              }
        } catch (DAOException ex) {
            ex.printStackTrace(System.out);
        }
    }
    
}