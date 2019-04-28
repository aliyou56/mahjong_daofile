
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.LinkRow.Link;
import java.util.UUID;
import org.junit.Test;

/**
 * Completed
 * 
 * @author aliyou
 */
public class LinkRowTest extends AbstractRowTest {
    
    public LinkRowTest() {
    }
    
    /**
     * Test of testWrite method, of class FileHeaderRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testWrite() throws DAOFileException {
        Link data = new Link(new UUID(0, 1), new UUID(0, 2));
        LinkRow instance = new LinkRow(29, data, 100);
        super.testWrite(instance, LinkRow.LINK_ROW_SIZE);
    }

    /**
     * Test of readData method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testReadData() throws DAOFileException {
        Link data = new Link(new UUID(0, 1), new UUID(0, 2));
        LinkRow instance = new LinkRow(29, data, 100);
        super.testReadData(instance);
    }

    /**
     * Test of writeData method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testWriteData() throws DAOFileException {
        Link data = new Link(new UUID(0, 1), new UUID(0, 2));
        LinkRow instance = new LinkRow(29, data, 100);
        super.testWriteData(instance);
    }   
    
    /**
     * Test of getRowSize method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetRowSize() throws DAOFileException {
        Link data = new Link(new UUID(0, 1), new UUID(0, 2));
        LinkRow instance = new LinkRow(29, data, 100);
        super.testGetRowSize(instance, LinkRow.LINK_ROW_SIZE);
    }

    /**
     * Test of hasChanged method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testHasChanged() throws DAOFileException {
        Link data = new Link(new UUID(0, 1), new UUID(0, 2));
        LinkRow instance = new LinkRow(29, data, 100);
        super.testHasChanged(instance, false);
    }

    /**
     * Test of setChanged method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testSetChanged() throws DAOFileException {
        Link data = new Link(new UUID(0, 1), new UUID(0, 2));
        LinkRow instance = new LinkRow(29, data, 100);
        super.testSetChanged(instance);
    }

    /**
     * Test of geRowID method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGeRowID() throws DAOFileException {
        int rowID = 29;
        Link data = new Link(new UUID(0, 1), new UUID(0, 2));
        LinkRow instance = new LinkRow(rowID, data, 100);
        super.testGeRowID(instance, rowID);
    }

    /**
     * Test of getRowPointer method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetRowPointer() throws DAOFileException {
        long rowPointer = 100;
        Link data = new Link(new UUID(0, 1), new UUID(0, 2));
        LinkRow instance = new LinkRow(29, data, rowPointer);
        super.testGetRowPointer(instance, rowPointer);
    }

    /**
     * Test of setRowPointer method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testSetRowPointer() throws DAOFileException {
        Link data = new Link(new UUID(0, 1), new UUID(0, 2));
        LinkRow instance = new LinkRow(29, data, 100);
        super.testSetRowPointer(instance);
    }

    /**
     * Test of getData method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetData() throws DAOFileException {
        Link data = new Link(new UUID(0, 1), new UUID(0, 2));
        LinkRow instance = new LinkRow(29, data, 100);
        super.testGetData(instance, data);
    }

    /**
     * Test of getDataSize method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetDataSize() throws DAOFileException {
        Link data = new Link(new UUID(0, 1), new UUID(0, 2));
        LinkRow instance = new LinkRow(29, data, 100);
        super.testGetDataSize(instance, LinkRow.LINK_ROW_SIZE - AbstractRow.ROW_HEADER_SIZE);
    }

    /**
     * Test of getPropertyChangeSupport method, of class AbstractRow.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testGetPropertyChangeSupport() throws DAOFileException {
        Link data = new Link(new UUID(0, 1), new UUID(0, 2));
        LinkRow instance = new LinkRow(29, data, 100);
        super.testGetPropertyChangeSupport(instance);
    }
}
