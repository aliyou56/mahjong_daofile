
package fr.univubs.inf1603.mahjong.daofile;

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
     * @throws java.io.IOException
     */
    @Test
    public void testRead() throws IOException {
        System.out.println("read");
        Path filePath = Paths.get(rootDir.toString(), "fileWriter_read.test");
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            DAOFileWriter instance = new DAOFileWriter(raf.getChannel());
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
        System.out.println(filePath.toFile().delete() ? "[OK] test file deleted : "+filePath.toString() : "[NOK] ");
    }

    /**
     * Test of write method, of class DAOFileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testWrite() throws IOException {
        System.out.println("write");
        Path filePath = Paths.get(rootDir.toString(), "fileWriter_write.test");
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            DAOFileWriter instance = new DAOFileWriter(raf.getChannel());
            ByteBuffer buffer = ByteBuffer.allocate(40);
            String str = "This is a file content";
            buffer.put(str.getBytes());
            int expectedResult = str.length();
            int result = instance.write(0, buffer);
            assertEquals(expectedResult, result);
        }
        System.out.println(filePath.toFile().delete() ? "[OK] test file deleted  : "+filePath.toString() : "[NOK] ");
    }

    /**
     * Test of addRowToMultipleWritingList method, of class DAOFileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testAddRowToMultipleWritingList() throws IOException {
        System.out.println("addRowToMultipleWritingList");
        Path filePath = Paths.get(rootDir.toString(), "fileWriterAddRowToMultipleWritingList.test");
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            DAOFileWriter instance = new DAOFileWriter(raf.getChannel());
            AbstractRow row = new IndexRow(0, new IndexRow.Index(UUID.randomUUID(), 250), 0);
            boolean result = instance.addRowToMultipleWritingList(row);
            boolean expectedResult = true;
            assertEquals(expectedResult, result);
        }
        System.out.println(filePath.toFile().delete() ? "[OK] test file deleted  : "+filePath.toString() : "[NOK] ");
    }

    /**
     * Test of addRowToSingleWritingList method, of class DAOFileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testAddRowToSingleWritingList() throws IOException {
        System.out.println("addRowToSingleWritingList");
        Path filePath = Paths.get(rootDir.toString(), "fileWriterAddRowToSingleWritingList.test");
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            DAOFileWriter instance = new DAOFileWriter(raf.getChannel());
            AbstractRow row = new IndexRow(12, new IndexRow.Index(UUID.randomUUID(), 250), 0);
            boolean result = instance.addRowToSingleWritingList(row);
            boolean expectedResult = true;
            assertEquals(expectedResult, result);
        }
        System.out.println(filePath.toFile().delete() ? "[OK] test file deleted  : "+filePath.toString() : "[NOK] ");
    }
    
    /**
     * Test of loadFileHeader method, of class DAOFileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testLoadFileHeader() throws IOException {
        System.out.println("loadFileHeader");
        Path filePath = Paths.get(rootDir.toString(), "fileWriterLoadFileHeader.test");
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            FileChannel fc = raf.getChannel();
            DAOFileWriter instance = new DAOFileWriter(fc);
            
            FileHeaderRow expResult1 = new FileHeaderRow(new FileHeaderRow.FileHeader(0, 0));
            FileHeaderRow result1 = instance.loadFileHeader();
            assertEquals(expResult1, result1);
            
            ByteBuffer buffer = ByteBuffer.allocate(12);
            buffer.putInt(0);
            buffer.putInt(12);
            buffer.putInt(13);
            buffer.flip();
            fc.position(0);
            while(buffer.hasRemaining()) {
                fc.write(buffer);
            }
            
            System.out.println(instance.write(0, buffer));
            FileHeaderRow expResult = new FileHeaderRow(new FileHeaderRow.FileHeader(12, 13));
            FileHeaderRow result = instance.loadFileHeader();
            System.out.println(result);
            assertEquals(expResult.getData(), result.getData());
        }
        System.out.println(filePath.toFile().delete() ? "[OK] test file deleted  : "+filePath.toString() : "[NOK] ");
    }

    /**
     * Test of deleteFromFile method, of class DAOFileWriter.
     */
    @Test
    public void testDeleteFromFile() {
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

    private void deleteFromFileTest(String str, int position, int size, String expResult) throws IOException {
        String result;
        Path filePath = Paths.get(rootDir.toString(), "deleteFromFile.test");
        try (RandomAccessFile file = new RandomAccessFile(filePath.toString(), "rw")) {
            FileChannel fc = file.getChannel();
            ByteBuffer buff = ByteBuffer.allocate(str.length());
            buff.put(str.getBytes());
            buff.flip();
            while (buff.hasRemaining()) {
                fc.write(buff);
            }

            DAOFileWriter instance = new DAOFileWriter(fc);
            instance.deleteFromFile(position, size);

            buff.clear();
            fc.position(0);
            int lenght = fc.read(buff);
            buff.flip();
            byte b[] = new byte[lenght];
            buff.get(b);
            result = new String(b);
        }
        System.out.println(filePath.toFile().delete() ? "[OK] test file deleted  : "+filePath.toString() : "[NOK] ");
//        filePath.toFile().delete();
        assertEquals(expResult, result);
    }
 
    /**
     * Test of writeUUID method, of class DAOFileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testWriteUUID() throws IOException {
        System.out.println("writeUUID");
        UUID uuidToWrite = UUID.randomUUID();
        int expResult = Long.BYTES * 2;
        ByteBuffer buffer = ByteBuffer.allocate(expResult);
        int result = DAOFileWriter.writeUUID(buffer, uuidToWrite);
        assertEquals(expResult, result);
    }

    /**
     * Test of writeString method, of class DAOFileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testWriteString() throws IOException {
        System.out.println("writeString");
        String stringToWrite = "This is the string to be writed in the buffer.";
        int expResult = Integer.BYTES + stringToWrite.length();
        ByteBuffer buffer = ByteBuffer.allocate(expResult);
        int result = DAOFileWriter.writeString(buffer, stringToWrite);
        assertEquals(expResult, result);
    }

    /**
     * Test of readString method, of class DAOFileWriter.
     */
    @Test
    public void testReadString_ByteBuffer() {
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
     */
    @Test
    public void testReadString_ByteBuffer_int() {
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
