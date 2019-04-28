
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class DAOFileWriterTest {
    
    /**
     * Chemin du repertoire racine.
     */
    static Path rootDir;
    
    public DAOFileWriterTest() {
        rootDir = Paths.get("/tmp/mahjong/dao");
        if (!rootDir.toFile().exists()) {
            rootDir.toFile().mkdirs();
        }
    }
    
 /**
     * Test of read method, of class DAOFileWriter.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException
     * @throws java.io.IOException
     */
    @Test
    public void testRead() throws  IOException, DAOFileWriterException {
        System.out.println("read");
        Path filePath = rootDir.resolve("fileWriter_read.test");
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            DAOFileWriter instance = new DAOFileWriter(filePath);
            assertEquals(null, instance.read(0, 2));

            raf.write("This is the file content".getBytes());
            long pos = 7;
            int lenght = 10;
            ByteBuffer result = instance.read(pos, lenght);

            ByteBuffer expectedResult = ByteBuffer.allocate(lenght);
            expectedResult.put(" the file ".getBytes());
            expectedResult.flip();
            assertEquals(expectedResult, result);
        }
        filePath.toFile().delete();
    }

    /**
     * Test of write method, of class DAOFileWriter.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException
     * @throws java.io.IOException
     */
    @Test
    public void testWrite() throws DAOFileWriterException, IOException {
        System.out.println("write");
        Path filePath = rootDir.resolve("fileWriter_write.test");
        DAOFileWriter instance = new DAOFileWriter(filePath);
        ByteBuffer buffer = ByteBuffer.allocate(40);
        String str = "This is a file content";
        buffer.put(str.getBytes());
        int expectedResult = str.length();
        int result = instance.write(0, buffer);
        assertEquals(expectedResult, result);
        filePath.toFile().delete();
    }

    /**
     * Test of addRowToMultipleWritingList method, of class DAOFileWriter.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException
     * @throws java.io.IOException
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testAddRowToMultipleWritingList() throws DAOFileWriterException, IOException, DAOFileException {
        System.out.println("addRowToMultipleWritingList");
        Path filePath = rootDir.resolve("fileWriterAddRowToMultipleWritingList.test");
        DAOFileWriter instance = new DAOFileWriter(filePath);
        AbstractRow row = new IndexRow(0, new Index(UUID.randomUUID(), 250), 0);
        boolean result = instance.addRowToMultipleWritingList(row);
        boolean expectedResult = true;
        assertEquals(expectedResult, result);
        filePath.toFile().delete();
    }

    /**
     * Test of addRowToSingleWritingList method, of class DAOFileWriter.
     *
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws java.io.IOException
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException
     */
    @Test
    public void testAddRowToSingleWritingList() throws DAOFileException, IOException, DAOFileWriterException {
        System.out.println("addRowToSingleWritingList");
        Path filePath = rootDir.resolve("fileWriterAddRowToSingleWritingList.test");
        DAOFileWriter instance = new DAOFileWriter(filePath);
        AbstractRow row = new IndexRow(12, new Index(UUID.randomUUID(), 250), 0);
        boolean result = instance.addRowToSingleWritingList(row);
        boolean expectedResult = true;
        assertEquals(expectedResult, result);
        filePath.toFile().delete();
    }
    
    /**
     * Test of loadFileHeader method, of class DAOFileWriter.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws java.io.IOException
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException
     */
    @Test
    public void testLoadFileHeader() throws DAOFileException, IOException, DAOFileWriterException {
        System.out.println("loadFileHeader");
        Path filePath = rootDir.resolve("fileWriterLoadFileHeader.test");
        DAOFileWriter instance = new DAOFileWriter(filePath);
        
        FileHeaderRow expResult1 = new FileHeaderRow(new FileHeader(0, 0));
        FileHeaderRow result1 = instance.loadFileHeader();
        assertEquals(expResult1, result1);
        
        FileHeaderRow expResult = new FileHeaderRow(new FileHeader(12, 13));
        ByteBuffer buffer = ByteBuffer.allocate(FileHeaderRow.FILE_HEADER_ROW_SIZE);
        expResult.write(buffer);
//        instance.write(0, buffer);

        instance.write(0, buffer);
        FileHeaderRow result = instance.loadFileHeader();
        assertEquals(expResult.getData(), result.getData());
        filePath.toFile().delete();
    }

    /**
     * Test of deleteFromFile method, of class DAOFileWriter.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     */
    @Test
    public void testDeleteFromFile() throws DAOFileException, DAOFileWriterException {
        System.out.println("deleteFromFile");
        try {
            String str = "That is a deleting test on a file.";
            
            System.out.println("At the begining");
            deleteFromFileTest(str, 0, 10, "deleting test on a file.");
            
            System.out.println("In the middle");
            deleteFromFileTest(str, 10, 9, "That is a test on a file.");
            
            System.out.println("At the end");
            deleteFromFileTest(str, 23, 11, "That is a deleting test");
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    private void deleteFromFileTest(String str, int position, int size, String expResult) throws DAOFileException, IOException, DAOFileWriterException {
        String result;
        Path filePath = rootDir.resolve("deleteFromFile.test");
        try (RandomAccessFile file = new RandomAccessFile(filePath.toString(), "rw")) {
            FileChannel fc = file.getChannel();
            ByteBuffer buff = ByteBuffer.allocate(str.length());
            buff.put(str.getBytes());
            buff.flip();
            while (buff.hasRemaining()) {
                fc.write(buff);
            }

            DAOFileWriter instance = new DAOFileWriter(filePath);
            instance.deleteFromFile(position, size);

            buff.clear();
            fc.position(0);
            int lenght = fc.read(buff);
            buff.flip();
            byte b[] = new byte[lenght];
            buff.get(b);
            result = new String(b);
        }
        filePath.toFile().delete();
        assertEquals(expResult, result);
    }
 
    /**
     * Test of writeUUID method, of class DAOFileWriter.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException
     */
    @Test
    public void testWriteUUID() throws DAOFileException, DAOFileWriterException {
        System.out.println("writeUUID");
        UUID uuidToWrite = UUID.randomUUID();
        int expResult = Long.BYTES * 2;
        ByteBuffer buffer = ByteBuffer.allocate(expResult);
        int result = DAOFileWriter.writeUUID(buffer, uuidToWrite);
        assertEquals(expResult, result);
    }

    /**
     * Test of writeString method, of class DAOFileWriter.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException
     */
    @Test
    public void testWriteString() throws DAOFileException, DAOFileWriterException {
        System.out.println("writeString");
        String stringToWrite = "This is the string to be writed in the buffer.";
        int expResult = Integer.BYTES + stringToWrite.length();
        ByteBuffer buffer = ByteBuffer.allocate(expResult);
        int result = DAOFileWriter.writeString(buffer, stringToWrite);
        assertEquals(expResult, result);
    }

    /**
     * Test of readString method, of class DAOFileWriter.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException
     */
    @Test
    public void testReadString_ByteBuffer() throws DAOFileException, DAOFileWriterException {
        System.out.println("readString");
        ByteBuffer buffer = ByteBuffer.allocate(50);
        String str = "This is a test";
        buffer.putInt(str.length());
        buffer.put(str.getBytes());
        buffer.flip();
        String expResult = str;
        String result = DAOFileWriter.readString(buffer);
        assertEquals(expResult, result);
    }

    /**
     * Test of readString method, of class DAOFileWriter.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException
     */
    @Test
    public void testReadString_ByteBuffer_int() throws DAOFileException, DAOFileWriterException {
        System.out.println("readString");
        ByteBuffer buffer = ByteBuffer.allocate(50);
        String str = "This is a test";
        buffer.put(str.getBytes());
        buffer.flip();
        String expResult = str;
        String result = DAOFileWriter.readString(buffer, str.length());
        assertEquals(expResult, result);
    }
}
