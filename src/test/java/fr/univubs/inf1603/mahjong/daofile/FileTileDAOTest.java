package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.Wind;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.FileTileDAO.TileRow;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.LinkManager;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.GameTileInterface;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile;
import fr.univubs.inf1603.mahjong.engine.rule.SimpleHonor;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

/**
 * Classe de test pour le FileTileDAO.
 * 
 * @author aliyou
 * @version 1.3
 */
public class FileTileDAOTest extends FileDAOMahjongTest<GameTileInterface> {

    /**
     * Constructeur vide.
     */
    public FileTileDAOTest() {
        System.out.println("\nFileTileDAOTest");
    }

    /**
     * Test of getRowNumber method, of class FileTileDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetRowNumber() throws DAOFileException {
        FileTileDAO instance = FileTileDAO.getInstance(rootDir);
        super.testGetRowNumber(instance, 0);
    }
    
    /**
     * Test of writeToPersistence method, of class FileTileDAO.
     * 
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testWriteToPersistence() throws DAOFileException, DAOException {
        GameTileInterface tile = new GameTile(95, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE), new UUID(0, 195), true, Wind.EAST);
        
        FileTileDAO dao = FileTileDAO.getInstance(rootDir);

        super.testWriteToPersistence(dao, tile);
        
        dao.deleteFromPersistence(tile);
    }
    
    /**
     * Test of loadFromPersistence method, of class FileTileDAO.
     * 
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void loadFromPersistence() throws DAOFileException, DAOException {
        GameTileInterface tile = new GameTile(15, new FlowerTile(FlowerTile.Flower.CHRYSANTHEMUM), new UUID(0, 14), true, Wind.WEST);
        
        FileTileDAO dao = FileTileDAO.getInstance(rootDir);

        super.testLoadFromPersistence(dao, tile);
        
        dao.deleteFromPersistence(tile);
    }

    /**
     * Test of laodAll method, of class FileTileDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testLoadAll() throws DAOFileException {
        GameTileInterface tile1 = new GameTile(1, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE), new UUID(0, 1), true, Wind.EAST);
        GameTileInterface tile2 = new GameTile(2, new CommonTile(CommonTile.Family.CHARACTER, CommonTile.Number.ONE), new UUID(0, 5), true, Wind.WEST);
        GameTileInterface tile3 = new GameTile(3, new CommonTile(CommonTile.Family.DOT, CommonTile.Number.FOUR), new UUID(0, 9), true, Wind.NORTH);
        GameTileInterface tile4 = new GameTile(4, new SuperiorHonor(SuperiorHonor.Dragon.GREEN), new UUID(0, 3), true, Wind.SOUTH);
        GameTileInterface tile5 = new GameTile(5, new SimpleHonor(Wind.NORTH), new UUID(0, 2), true, Wind.WEST);
        GameTileInterface tile6 = new GameTile(6, new FlowerTile(FlowerTile.Flower.PLUM), new UUID(0, 8), true, Wind.SOUTH);
        GameTileInterface tile7 = new GameTile(7, new SeasonTile(SeasonTile.Season.SPRING), new UUID(0, 7), true, Wind.NORTH);

        FileTileDAO dao = FileTileDAO.getInstance(rootDir);
        
        List<GameTileInterface> list = new ArrayList<>();
        list.add(tile1);
        list.add(tile2);
        list.add(tile3);
        list.add(tile4);
        list.add(tile5);
        list.add(tile6);
        list.add(tile7);
        
        super.testLaodAll(dao, list);
    }
    
    /**
     * Test of delete method, of class FileDAOMahjong.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testDelete() throws DAOFileException {
        GameTileInterface tile1 = new GameTile(144, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE), new UUID(0, 452), true, Wind.EAST);
        GameTileInterface tile2 = new GameTile(143, new SuperiorHonor(SuperiorHonor.Dragon.GREEN), new UUID(0, 845), true, Wind.SOUTH);
        GameTileInterface tile3 = new GameTile(142, new SimpleHonor(Wind.NORTH),new UUID(0, 123), true, Wind.WEST);
        GameTileInterface tile4 = new GameTile(141, new FlowerTile(FlowerTile.Flower.PLUM), new UUID(0, 568), true, Wind.SOUTH);
        GameTileInterface tile5 = new GameTile(140, new SeasonTile(SeasonTile.Season.SPRING), new UUID(0, 753), true, Wind.NORTH);
        List<GameTileInterface> list = new ArrayList<>();
        list.add(tile1);
        list.add(tile2);
        list.add(tile3);
        list.add(tile4);
        list.add(tile5);
        
        FileTileDAO dao = FileTileDAO.getInstance(rootDir);
        
        super.testDelete(dao, list);
    }

    /**
     * Test of removeDataRow method, of class FileTileDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testRemoveDataRow() throws DAOFileException {
        FileTileDAO dao = FileTileDAO.getInstance(rootDir);
        GameTileInterface data = new GameTile(142, new SimpleHonor(Wind.NORTH), new UUID(0, 33), true, Wind.WEST);
        super.testRemoveDataRow(dao, data);
    }
    
    /**
     * Test of deleteFromPersistence method, of class FileTileDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testDeleteFromPersistence() throws DAOFileException, DAOException, InterruptedException {
        System.out.println("deleteFromPersistence");
        UUID datID1 = new UUID(0, 223);
        UUID datID2 = new UUID(0, 220);
        GameTileInterface tile1 = new GameTile(144, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE), datID1, true, Wind.EAST);
        GameTileInterface tile2 = new GameTile(143, new SuperiorHonor(SuperiorHonor.Dragon.GREEN), datID2, true, Wind.SOUTH);

        FileTileDAO instance = FileTileDAO.getInstance(rootDir);
        // Ecrire les tuiles dans le fichier de données
        instance.writeToPersistence(tile1);
        instance.writeToPersistence(tile2);
        // Attendre que l'écriture soit effective
        synchronized (instance) {
            instance.wait(3000);
        }
        // Suprimer les tuiles.
        instance.deleteFromPersistence(tile1);
        instance.deleteFromPersistence(tile2);
        // Vérifier si les tuiles ont bien été supprimées
        Assert.assertNull(instance.loadFromPersistence(datID1));
        Assert.assertNull(instance.loadFromPersistence(datID2));
    }

    /**
     * Test of getDataRow method, of class FileTileDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetDataRow_3args() throws DAOFileException {
        FileTileDAO instance = FileTileDAO.getInstance(rootDir);
        
        GameTileInterface tile = new GameTile(142, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE)); 
        DataRow<GameTileInterface> expResult = new TileRow(15, tile, 29);
        
        super.testGetDataRow_3args(instance, expResult);
    }

    /**
     * Test of getDataRow method, of class FileTileDAO.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
     */
    @Test
    public void testGetDataRow_long() throws DAOFileException, DAOException {
        FileTileDAO dao = FileTileDAO.getInstance(rootDir);
      
        UUID dataID = new UUID(0, 100);
        GameTileInterface tile = new GameTile(144, new SeasonTile(SeasonTile.Season.SPRING), dataID, true, Wind.NORTH);
        
        super.testGetDataRow_long(dao, tile);
        
        dao.deleteFromPersistence(tile);
    }

    /**
     * Test of getInstance method, of class FileTileDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetInstance() throws DAOFileException {
        System.out.println("getInstance");
        FileTileDAO expResult = FileTileDAO.getInstance(rootDir);
        FileTileDAO result = FileTileDAO.getInstance(rootDir);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getLinkManager method, of class FileTileDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetLinkManager() throws DAOFileException {
        System.out.println("getLinkManager");
        FileTileDAO instance = FileTileDAO.getInstance(rootDir);
        LinkManager<GameTileInterface> result = instance.getLinkManager();
        Assert.assertNotNull(result);
    }

    @Override
    protected void assertTest(GameTileInterface gti1, GameTileInterface gti2) {
        TestUtilities.assertTest(gti1, gti2);
    }

    @Override
    protected Comparator<GameTileInterface> getComparator() {
        return TestUtilities.tileComparator();
    }
}