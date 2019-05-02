package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.Wind;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import static fr.univubs.inf1603.mahjong.daofile.FileDAOMahjongTest.rootDir;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DAOFileWriter;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.GameTileInterface;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile;
import fr.univubs.inf1603.mahjong.engine.rule.SimpleHonor;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author aliyou, nesrine
 */
public class FileTileDAOTest /*extends FileDAOMahjongTest<GameTileInterface>*/ {

    protected static Path rootDir;

    public FileTileDAOTest() throws IOException {
        System.out.println("FileTileDAOTest");
        rootDir = Paths.get("/ubs/home/etud/2017/e1704274/Bureau", "dao");
    }

    /**
     * Test of writeToPersistence method, of class FileTileDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testWriteToPersistence() throws DAOException, DAOFileWriterException, InterruptedException, DAOFileException {
        GameTileInterface tile1 = new GameTile(1, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE), new UUID(0, 1), true, Wind.EAST);
        GameTileInterface tile2 = new GameTile(2, new CommonTile(CommonTile.Family.CHARACTER, CommonTile.Number.ONE), new UUID(0, 5), true, Wind.WEST);
        GameTileInterface tile3 = new GameTile(3, new CommonTile(CommonTile.Family.DOT, CommonTile.Number.FOUR), new UUID(0, 9), true, Wind.NORTH);
//        GameTileInterface tile4 = new GameTile(4, new SuperiorHonor(SuperiorHonor.Dragon.GREEN), new UUID(0, 3), true, Wind.SOUTH);
//        GameTileInterface tile5 = new GameTile(5, new SimpleHonor(Wind.NORTH), new UUID(0, 2), true, Wind.WEST);
//        GameTileInterface tile6 = new GameTile(6, new FlowerTile(FlowerTile.Flower.PLUM), new UUID(0, 8), true, Wind.SOUTH);
//        GameTileInterface tile7 = new GameTile(7, new SeasonTile(SeasonTile.Season.SPRING), new UUID(0, 7), true, Wind.NORTH);
        DAOFileWriter dAOFileWriter = new DAOFileWriter(
                rootDir.resolve("tile.data")
        ) {
            @Override
            protected void done() {
                notify();
            }
        };

        FileDAOMahjong dao = FileTileDAO.getInstance(rootDir, dAOFileWriter);

        dao.writeToPersistence(tile1);
        dao.writeToPersistence(tile2);
        dao.writeToPersistence(tile3);

        synchronized (dAOFileWriter) {
            dAOFileWriter.wait(6000);
        }

        // Attendre que l'écriture soit effective
        compare(tile1, (GameTileInterface) dao.loadFromPersistence(tile1.getUUID()));
        compare(tile2, (GameTileInterface) dao.loadFromPersistence(tile2.getUUID()));
        compare(tile3, (GameTileInterface) dao.loadFromPersistence(tile3.getUUID()));

        //success
//            if (TEST_WITH_FILE_WRITING) {
//                Thread.sleep(6000);
//            }
    }

    protected boolean compare(GameTileInterface obj1, GameTileInterface obj2) {
        Assert.assertEquals(obj1.getUUID(), obj2.getUUID());
        Assert.assertEquals(obj1.getGameID(), obj2.getGameID());
        Assert.assertEquals(obj1.getOrientation(), obj2.getOrientation());
        Assert.assertEquals(obj1.getTile().toNormalizedName(), obj2.getTile().toNormalizedName());
        return false;
    }

    /**
     * Test of loadFromPersistence method, of class FileTileDAO.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
//    @Test
//    public void testLoadFromPersistence() throws DAOFileException {
//        GameTileInterface tile1 = new GameTile(6, new FlowerTile(FlowerTile.Flower.PLUM), new UUID(0, 8), true, Wind.SOUTH);
//        GameTileInterface tile2 = new GameTile(7, new SeasonTile(SeasonTile.Season.SPRING), new UUID(0, 7), true, Wind.NORTH);
//
//        FileTileDAO dao = FileTileDAO.getInstance(rootDir);
//
//        super.testLoadFromPersistence(dao, tile1);
//        super.testLoadFromPersistence(dao, tile2);
//    }
//
//    /**
//     * Test of laodAll method, of class FileTileDAO.
//     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
//     */
////    @Test
////    public void testLoadAll() throws DAOFileException {
////        GameTileInterface tile3 = new GameTile(3, new CommonTile(CommonTile.Family.DOT, CommonTile.Number.FOUR), new UUID(0, 9), true, Wind.NORTH);
////        GameTileInterface tile4 = new GameTile(4, new SuperiorHonor(SuperiorHonor.Dragon.GREEN), new UUID(0, 3), true, Wind.SOUTH);
////        GameTileInterface tile5 = new GameTile(5, new SimpleHonor(Wind.NORTH), new UUID(0, 2), true, Wind.WEST);
////
////        FileTileDAO dao = FileTileDAO.getInstance(rootDir);
////        List<GameTileInterface> list = new ArrayList<>();
////        list.add(tile3);
////        list.add(tile4);
////        list.add(tile5);
////        super.testLaodAll(dao, list);
////    }
//    
//    /**
//     * Test of delete method, of class FileTileDAO.
//     *
//     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
//     * @throws fr.univubs.inf1603.mahjong.dao.DAOException
//     */
//    @Test
//    public void testDelete() throws DAOFileException, DAOException {
//        System.out.println("delete");
//        try {
//            FileTileDAO dao = FileTileDAO.getInstance(rootDir);
//            for (GameTileInterface t : dao.findAll()) {
//                System.out.println(t);
//            }
//            super.testDelete(dao, new UUID(0, 3));
//            super.testDelete(dao, new UUID(0, 8));
//            super.testDelete(dao, new UUID(0, 2));
//            super.testDelete(dao, new UUID(0, 5));
//            super.testDelete(dao, new UUID(0, 9));
//            super.testDelete(dao, new UUID(0, 1));
//            super.testDelete(dao, new UUID(0, 7));
//            if (TEST_WITH_FILE_WRITING) {
//                Thread.sleep(4000);
//            }
//        } catch (InterruptedException ex) {
//            ex.printStackTrace(System.out);
//        }
//    }
//    @Override
}
