package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.Wind;
import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import static fr.univubs.inf1603.mahjong.daofile.FileDAOMahJongTest.rootDir;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.GameTileInterface;
import fr.univubs.inf1603.mahjong.engine.game.MahjongTileZone;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import fr.univubs.inf1603.mahjong.engine.game.TileZoneIdentifier;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile;
import fr.univubs.inf1603.mahjong.engine.rule.SimpleHonor;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author aliyou, nesrine
 */
public class FileZoneDAOTest extends FileDAOMahJongTest<TileZone> {

    public FileZoneDAOTest() {
        System.out.println("FileZoneDAOTest");
    }

    /**
     * Test of save method, of class FileZoneDAO.
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testSave() throws DAOException {
        try {
            TileZone zone0 = new MahjongTileZone(createRandomTiles(91), new UUID(0, 10) , TileZoneIdentifier.Wall); //91
            TileZone zone1 = new MahjongTileZone(createRandomTiles(14), new UUID(0, 11), TileZoneIdentifier.HandEast);
            TileZone zone2 = new MahjongTileZone(createRandomTiles(13), new UUID(0, 12), TileZoneIdentifier.HandNorth);
            TileZone zone3 = new MahjongTileZone(createRandomTiles(13), new UUID(0, 13), TileZoneIdentifier.HandSouth);
            TileZone zone4 = new MahjongTileZone(createRandomTiles(13), new UUID(0, 14), TileZoneIdentifier.HandWest);
            
            DAOManager manager = FileDAOManager.getInstance(rootDir);
            DAO<TileZone> dao = manager.getZoneDao();
            super.testSave(dao, zone0);
            super.testSave(dao, zone1);
            super.testSave(dao, zone2);
            super.testSave(dao, zone3);
            super.testSave(dao, zone4);
            Thread.sleep(12000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<GameTileInterface> createRandomTiles(int nb) {
        ArrayList<GameTileInterface> tiles = new ArrayList<>();
        for (int i = 0; i < nb; i++) {
            tiles.add(getRandomTile());
        }
        return tiles;
    }
    
    private GameTileInterface getRandomTile() {
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
    
    /**
     * Test of delete method, of class FileZoneDAO.
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testDelete() throws DAOException {
        try {
            DAOManager manager = FileDAOManager.getInstance(rootDir);
            DAO<TileZone> dao = manager.getZoneDao();
            super.testDelete(dao, new UUID(0, 11));
            super.testDelete(dao, new UUID(0, 13));
            super.testDelete(dao, new UUID(0, 10));
            super.testDelete(dao, new UUID(0, 14));
            super.testDelete(dao, new UUID(0, 12));
            Thread.sleep(6000);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTileDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testUpdate() throws DAOException, InterruptedException {
        System.out.println("testUpdate");
        MahjongTileZone zone0 = new MahjongTileZone(createRandomTiles(4), new UUID(0, 110), TileZoneIdentifier.Wall); //91
        MahjongTileZone zone1 = new MahjongTileZone(createRandomTiles(2), new UUID(0, 111), TileZoneIdentifier.HandEast);

        DAOManager manager = FileDAOManager.getInstance(rootDir);
        DAO<TileZone> dao = manager.getZoneDao();
        super.testSave(dao, zone0);
        super.testSave(dao, zone1);
        Thread.sleep(8000);
        
        GameTile t1 = (GameTile) zone0.getTiles().get(0);
        GameTile t2 = (GameTile) zone0.getTiles().get(1);
        zone0.removeTile(t1);
        zone0.removeTile(t2);
        Thread.sleep(7000);
        
        zone1.addTile(t2);
        zone1.addTile(t1);
        Thread.sleep(10000);
        
        super.testDelete(dao, new UUID(0, 111));
        super.testDelete(dao, new UUID(0, 110));
        Thread.sleep(6000);
    }
}