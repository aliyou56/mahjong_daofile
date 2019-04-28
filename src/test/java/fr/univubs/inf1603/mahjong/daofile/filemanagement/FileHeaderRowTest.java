
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import org.junit.Test;

/**
 *
 * @author aliyou
 */
public class FileHeaderRowTest extends AbstractRowTest {
    
    public FileHeaderRowTest() {
    }
    
    /**
     * Test of testWrite method, of class FileHeaderRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testWrite() throws DAOFileException {
        FileHeader data = new FileHeader(12, 15);
        FileHeaderRow instance = new FileHeaderRow(data);
        super.testWrite(instance, FileHeaderRow.FILE_HEADER_ROW_SIZE);
    }

    /**
     * Test of readData method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testReadData() throws DAOFileException {
        FileHeader data = new FileHeader(12, 15);
        FileHeaderRow instance = new FileHeaderRow(data);
        super.testReadData(instance);
    }

    /**
     * Test of writeData method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testWriteData() throws DAOFileException {
        FileHeader data = new FileHeader(12, 15);
        FileHeaderRow instance = new FileHeaderRow(data);
        super.testWriteData(instance);
    }   
    
    /**
     * Test of getRowSize method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetRowSize() throws DAOFileException {
        FileHeader data = new FileHeader(12, 15);
        FileHeaderRow instance = new FileHeaderRow(data);
        super.testGetRowSize(instance, FileHeaderRow.FILE_HEADER_ROW_SIZE);
    }

    /**
     * Test of hasChanged method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testHasChanged() throws DAOFileException {
        FileHeader data = new FileHeader(12, 15);
        FileHeaderRow instance = new FileHeaderRow(data);
        super.testHasChanged(instance, false);
        // TODO change pointer
    }

    /**
     * Test of setChanged method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testSetChanged() throws DAOFileException {
        FileHeader data = new FileHeader(12, 15);
        FileHeaderRow instance = new FileHeaderRow(data);
        super.testSetChanged(instance);
    }

    /**
     * Test of geRowID method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGeRowID() throws DAOFileException {
        int rowID = 0;
        FileHeader data = new FileHeader(12, 15);
        FileHeaderRow instance = new FileHeaderRow(data);
        super.testGeRowID(instance, rowID);
    }

    /**
     * Test of getRowPointer method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetRowPointer() throws DAOFileException {
        long rowPointer = 0;
        FileHeader data = new FileHeader(12, 15);
        FileHeaderRow instance = new FileHeaderRow(data);
        super.testGetRowPointer(instance, rowPointer);
    }

    /**
     * Test of setRowPointer method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testSetRowPointer() throws DAOFileException {
        FileHeader data = new FileHeader(12, 15);
        FileHeaderRow instance = new FileHeaderRow(data);
        super.testSetRowPointer(instance);
    }

    /**
     * Test of getData method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetData() throws DAOFileException {
        FileHeader data = new FileHeader(12, 15);
        FileHeaderRow instance = new FileHeaderRow(data);
        super.testGetData(instance, data);
    }

    /**
     * Test of getDataSize method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetDataSize() throws DAOFileException {
        FileHeader data = new FileHeader(12, 15);
        FileHeaderRow instance = new FileHeaderRow(data);
        super.testGetDataSize(instance, FileHeaderRow.FILE_HEADER_ROW_SIZE - AbstractRow.ROW_HEADER_SIZE);
    }

    /**
     * Test of getPropertyChangeSupport method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetPropertyChangeSupport() throws DAOFileException {
        FileHeader data = new FileHeader(12, 15);
        FileHeaderRow instance = new FileHeaderRow(data);
        super.testGetPropertyChangeSupport(instance);
    }
}
