
package fr.univubs.inf1603.mahjong.daofile.myengine;

import fr.univubs.inf1603.mahjong.dao.AbstractTile;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.FileDAOManager;
//import fr.univubs.inf1603.mahjong.engine.AbstractTile;


/**
 *
 * @author aliyou
 */
public class Test {
    
    public static void main(String args[]) {
        try {
            DAO<AbstractTile> dao = FileDAOManager.getInstance().getTileDao();
//            int nb = 100;
//            Tile tiles[] = new Tile[nb];
//            int i=0;
//            while(i < nb) {
////                UUID uuid = new UUID(0, (i+1));
//                UUID uuid = UUID.randomUUID();
//                String type = "Type" + (i+1);
//                String family = "Family" + (i+1);
//                tiles[i] = new Tile(uuid, type, family);
//                dao.save(tiles[i]);
//                i++;
//            }

            Tile tile2 = new Tile(new UUID(0, 1), "Character", "9");
            dao.save(tile2);
            Tile tile = new Tile(new UUID(0, 2), "Circle", "1");
            dao.save(tile);
            Tile tile1 = new Tile(new UUID(0, 6), "Bamboo", "5");
            dao.save(tile1);
            Tile tile3 = new Tile(new UUID(0, 9), "WinEast", "1");
            dao.save(tile3);
            Tile tile4 = new Tile(new UUID(0, 3), "Dragon", "Red");
            dao.save(tile4);
            Tile tile5 = new Tile(new UUID(0, 5), "Dragon", "White");
            dao.save(tile5);
            Tile tile6 = new Tile(new UUID(0, 8), "Dragon", "Green");
            dao.save(tile6);
//                dao.delete(new UUID(0, 1));
//                Tile tt = dao.find(new UUID(0, 2));
//                System.out.println(tt);
//                tt.setCategory("Bamboo");
//            System.out.println(dao.find(new UUID(0, 3)));

              for(AbstractTile t : dao.findAll()) {
                  System.out.println(t);
              }
        } catch (DAOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}