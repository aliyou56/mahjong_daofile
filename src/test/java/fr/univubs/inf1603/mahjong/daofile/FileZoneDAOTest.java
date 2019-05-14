package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.Wind;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import static fr.univubs.inf1603.mahjong.daofile.FileDAOMahjongTest.rootDir;
import fr.univubs.inf1603.mahjong.daofile.FileZoneDAO.ZoneRow;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.LinkManager;
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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Assert;
import org.junit.Test;

/**
 * Classe de test pour le FileZoneDAO.
 *
 * @author aliyou
 * @version 1.3
 */
public class FileZoneDAOTest extends FileDAOMahjongTest<TileZone> {

    /**
     * Constructeur vide.
     */
    public FileZoneDAOTest() {
        System.out.println("\nFileZoneDAOTest");
    }

    /**
     * Test of writeToPersistence method, of class FileZoneDAO.
     * 
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testWriteToPersistence() throws DAOFileException, DAOException { 
        TileZone zone = new MahjongTileZone(createRandomTiles(91), new UUID(0, 110), TileZoneIdentifier.Wall); 

        FileZoneDAO dao = FileZoneDAO.getInstance(rootDir);
        
        super.testWriteToPersistence(dao, zone);
        
        dao.deleteFromPersistence(zone);
    }
   
    /**
     * Test of loadFromPersistence method, of class FileZoneDAO.
     * 
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void loadFromPersistence() throws DAOFileException, DAOException {
        TileZone zone = new MahjongTileZone(createRandomTiles(13), new UUID(0, 123), TileZoneIdentifier.HandNorth);
        
        FileZoneDAO dao = FileZoneDAO.getInstance(rootDir);

        super.testLoadFromPersistence(dao, zone);
        
        dao.deleteFromPersistence(zone);
    }

    /**
     * Test of laodAll method, of class FileZoneDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testLoadAll() throws DAOFileException {
        TileZone zone1 = new MahjongTileZone(createRandomTiles(14), new UUID(0, 11), TileZoneIdentifier.HandEast);
        TileZone zone2 = new MahjongTileZone(createRandomTiles(13), new UUID(0, 12), TileZoneIdentifier.HandNorth);
        TileZone zone3 = new MahjongTileZone(createRandomTiles(13), new UUID(0, 13), TileZoneIdentifier.HandSouth);
        TileZone zone4 = new MahjongTileZone(createRandomTiles(13), new UUID(0, 14), TileZoneIdentifier.HandWest);

        FileZoneDAO dao = FileZoneDAO.getInstance(rootDir);
        
        List<TileZone> list = new ArrayList<>();
        list.add(zone1);
        list.add(zone2);
        list.add(zone3);
        list.add(zone4);
        
        super.testLaodAll(dao, list);
    }
    
    /**
     * Test of delete method, of class FileZoneDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testDelete() throws DAOFileException {
        TileZone zone1 = new MahjongTileZone(createRandomTiles(14), UUID.randomUUID(), TileZoneIdentifier.DiscardEast);
        TileZone zone2 = new MahjongTileZone(createRandomTiles(13), UUID.randomUUID(), TileZoneIdentifier.MeldEast0);
        TileZone zone3 = new MahjongTileZone(createRandomTiles(13), UUID.randomUUID(), TileZoneIdentifier.SupremeNorth);
        List<TileZone> list = new ArrayList<>();
        list.add(zone1);
        list.add(zone2);
        list.add(zone3);
        
        FileZoneDAO dao = FileZoneDAO.getInstance(rootDir);
        
        super.testDelete(dao, list);
    }
    
    /**
     * Test of deleteFromPersistence method, of class FileZoneDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testDeleteFromPersistence() throws DAOFileException, DAOException, InterruptedException {
        System.out.println("deleteFromPersistence");
        UUID dataID1 = new UUID(0, 223);
        TileZone zone1 = new MahjongTileZone(createRandomTiles(14), dataID1, TileZoneIdentifier.DiscardSouth);

        FileZoneDAO instance = FileZoneDAO.getInstance(rootDir);
        // Ecrire la zone dans le fichier de données
        instance.writeToPersistence(zone1);
        // Attendre que l'écriture soit effective
        synchronized (instance) {
            instance.wait(10000);
        }
        // Supprimer la zone.
        instance.deleteFromPersistence(zone1);
        // Vérifier si la zone bien été supprimée
        Assert.assertNull(instance.loadFromPersistence(dataID1));
    }

    /**
     * Test of getDataRow method, of class FileZoneDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetDataRow_3args() throws DAOFileException {
        FileZoneDAO instance = FileZoneDAO.getInstance(rootDir);
        
        TileZone zone = new MahjongTileZone(createRandomTiles(13), UUID.randomUUID(), TileZoneIdentifier.HandNorth);
        DataRow<TileZone> expResult = new ZoneRow(41, zone, 79);
        
        super.testGetDataRow_3args(instance, expResult);
    }

    /**
     * Test of getDataRow method, of class FileZoneDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testGetDataRow_long() throws DAOFileException, DAOException {
        FileZoneDAO dao = FileZoneDAO.getInstance(rootDir);
      
        TileZone zone = new MahjongTileZone(createRandomTiles(13), UUID.randomUUID(), TileZoneIdentifier.HandNorth);
        
        super.testGetDataRow_long(dao, zone);
        
        dao.deleteFromPersistence(zone);
    }

    /**
     * Test of getInstance method, of class FileZoneDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetInstance() throws DAOFileException {
        System.out.println("getInstance");
        FileZoneDAO expResult = FileZoneDAO.getInstance(rootDir);
        FileZoneDAO result = FileZoneDAO.getInstance(rootDir);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getLinkManager method, of class FileZoneDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetLinkManager() throws DAOFileException {
        System.out.println("getLinkManager");
        FileZoneDAO instance = FileZoneDAO.getInstance(rootDir);
        LinkManager<TileZone> result = instance.getLinkManager();
        Assert.assertNotNull(result);
    }
    
//    
//    /**
//     * 
//     * @throws DAOException
//     * @throws InterruptedException
//     * @throws DAOFileException 
//     */
//    @Test
//    public void testUpdate() throws DAOException, InterruptedException, DAOFileException {
//        System.out.println("testUpdate");
//        MahjongTileZone zone0 = new MahjongTileZone(createRandomTiles(4), new UUID(0, 110), TileZoneIdentifier.Wall);
//        MahjongTileZone zone1 = new MahjongTileZone(createRandomTiles(2), new UUID(0, 111), TileZoneIdentifier.HandEast);
//
//        FileZoneDAO dao = FileZoneDAO.getInstance(rootDir);
//        
//        dao.writeToPersistence(zone0);
//        dao.writeToPersistence(zone1);
//        // Attendre que l'écriture soit effective
//        synchronized(dao) {
//            dao.wait(10000);
//        }
//
//        GameTile t1 = (GameTile) zone0.getTiles().get(0);
//        GameTile t2 = (GameTile) zone0.getTiles().get(1);
//        zone0.removeTile(t1);
//        zone0.removeTile(t2);
//        // Attendre que l'écriture soit effective
//        synchronized(dao) {
//            dao.wait(10000);
//        }
//        
//        TileZone loadedZone0 = dao.loadFromPersistence(zone0.getUUID());
//        Assert.assertEquals(2, loadedZone0.getTiles().size());
//
//        zone1.addTile(t2);
//        zone1.addTile(t1);
//        // Attendre que l'écriture soit effective
//        synchronized(dao) {
//            dao.wait(10000);
//        }
//        TileZone loadedZone1 = dao.loadFromPersistence(zone1.getUUID());
//        Assert.assertEquals(4, loadedZone1.getTiles().size());
//
//        dao.deleteFromPersistence(zone0);
//        dao.deleteFromPersistence(zone1);
//    }
    
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
    
    @Override
    protected void assertTest(TileZone tz1, TileZone tz2) {
        TestUtilities.assertTest(tz1, tz2);
    }

    @Override
    protected Comparator<TileZone> getComparator() {
        return (TileZone tz1, TileZone tz2) -> {
            return tz1.getUUID().compareTo(tz2.getUUID());
        };
    }
}
