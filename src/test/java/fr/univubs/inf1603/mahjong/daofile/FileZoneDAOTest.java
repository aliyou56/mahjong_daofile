
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.fake_engine.GameTile;
import fr.univubs.inf1603.mahjong.dao.fake_engine.TileZone;
import fr.univubs.inf1603.mahjong.dao.fake_engine.Zone;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author aliyou, nesrine
 */
public class FileZoneDAOTest extends FileDAOMahJongTest {
    
    static DAO<Zone> zoneDao;

    public FileZoneDAOTest() {
        try {
            System.out.println("FileZoneDAOTest");
            zoneDao = daoManager.getZoneDao();
        } catch (DAOException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of save method, of class FileZoneDAO.
     */
    @Test
    public void testSave() {
        try {
//            UUID zoneID = new UUID(0, 5);
            TileZone zone1 = new TileZone(new UUID(0, 1), "tileZoneTest1", createRandomTiles(4), null);
//            TileZone zone2 = new TileZone(new UUID(0, 2), "tileZoneTest2", createRandomTiles(14), null);
//            TileZone zone3 = new TileZone(new UUID(0, 3), "tileZoneTest3", createRandomTiles(14), null);
//            TileZone zone4 = new TileZone(new UUID(0, 4), "tileZoneTest4", createRandomTiles(14), null);
            super.testSave(zoneDao, zone1);
//            super.testSave(zoneDao, zone2);
//            super.testSave(zoneDao, zone3);
//            super.testSave(zoneDao, zone4);
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    ArrayList<GameTile> createRandomTiles(int nb) {
        ArrayList<GameTile> tiles = new ArrayList<>();
        for (int i = 0; i < nb; i++) {
            UUID tileID = UUID.randomUUID();
            GameTile tile = new GameTile(tileID, "Bamboo", String.valueOf(i + 1));
            tiles.add(tile);
        }
        return tiles;
    }

    /**
     * Test of delete method, of class FileZoneDAO.
     */
    @Test
    public void testDelete() {
        try {
//            UUID zoneID = new UUID(0, 5);
            super.testDelete(zoneDao, new UUID(0, 1));
//            super.testDelete(zoneDao, new UUID(0, 2));
//            super.testDelete(zoneDao, new UUID(0, 3));
//            super.testDelete(zoneDao, new UUID(0, 4));
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
