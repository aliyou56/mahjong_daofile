
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.persistence.MahjongObservable;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class DataRowTest {
    
    public DataRowTest() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getIndex method, of class DataRow.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetIndex() throws Exception {
//        System.out.println("getIndex");
//        UUID dataID = new UUID(0, 1);
//        long rowPointer = 25;
//        DataRow instance = new DataRowImpl(new GameTile(5, new CommonTile(CommonTile.Family.BAMBOO, CommonTile.Number.NINE), dataID, true, Wind.EAST), rowPointer);
//        Index expResult = new Index(dataID, rowPointer);
//        Index result = instance.getIndex();
//        assertEquals(expResult, result);
    }
//
//    public class DataRowImpl extends DataRow {
//
//        public DataRowImpl(Persistable data, long rowPointer) throws Exception {
//            super(2, data, 24, rowPointer);
//        }
//
//        @Override
//        protected MahjongObservable readData(ByteBuffer buffer) throws DAOFileException {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        protected void writeData(ByteBuffer buffer) throws DAOFileException {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//    }
    
}
