
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.engine.MahjongTileZone;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import fr.univubs.inf1603.mahjong.engine.game.TileZoneIdentifier;
import fr.univubs.inf1603.mahjong.engine.rule.AbstractTile;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author aliyou, nesrine
 */
public class FileZoneDAOTest extends FileDAOMahJongTest<TileZone> {
    
    static DAO<TileZone> zoneDao;

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
            TileZone zone0 = new MahjongTileZone(new UUID(0, 10), createRandomTiles(4), TileZoneIdentifier.Wall); //91
            super.testSave(zoneDao, zone0);
            TileZone zone1 = new MahjongTileZone(new UUID(0, 11), createRandomTiles(3), TileZoneIdentifier.HandEast);
            super.testSave(zoneDao, zone1);
            TileZone zone2 = new MahjongTileZone(new UUID(0, 12), createRandomTiles(2), TileZoneIdentifier.HandNorth);
            super.testSave(zoneDao, zone2);
            TileZone zone3 = new MahjongTileZone(new UUID(0, 13), createRandomTiles(1), TileZoneIdentifier.HandSouth);
            super.testSave(zoneDao, zone3);
            TileZone zone4 = new MahjongTileZone(new UUID(0, 14), createRandomTiles(5), TileZoneIdentifier.HandWest);
            super.testSave(zoneDao, zone4);
            Thread.sleep(6000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    ArrayList<GameTile> createRandomTiles(int nb, int id) {
        ArrayList<GameTile> tiles = new ArrayList<>();
        for (int i = 0; i < nb; i++) {
            AbstractTile absTile = new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE);
            GameTile tile = new GameTile(i+1, absTile, new UUID(0, id));
            tiles.add(tile);
            id++;
        }
        return tiles;
    }
    
    ArrayList<GameTile> createRandomTiles(int nb) {
        ArrayList<GameTile> tiles = new ArrayList<>();
        for (int i = 0; i < nb; i++) {
            AbstractTile absTile = new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE);
            GameTile tile = new GameTile(i+1, absTile, UUID.randomUUID());
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
            super.testDelete(zoneDao, new UUID(0, 11));
            super.testDelete(zoneDao, new UUID(0, 13));
            super.testDelete(zoneDao, new UUID(0, 10));
            super.testDelete(zoneDao, new UUID(0, 14));
            super.testDelete(zoneDao, new UUID(0, 12));
//            for(TileZone gt : zoneDao.findAll()) {
//                System.err.println(gt.getUUID());
//            }
            Thread.sleep(6000);
        } catch (/*DAOException | */InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
}