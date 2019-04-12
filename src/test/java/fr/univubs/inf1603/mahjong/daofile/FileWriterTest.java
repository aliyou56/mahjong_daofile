/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univubs.inf1603.mahjong.daofile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class FileWriterTest {
    
//    static Path filePath;
    Path rootDir;
    
    public FileWriterTest() {
        rootDir = Paths.get("/tmp/mahjong/dao");
        if (!rootDir.toFile().exists()) {
            rootDir.toFile().mkdirs();
        }
    }
    
    /**
     * Destruction de l'environnement de test
     */
//    @AfterClass
//    public static void tearClass() {
////        System.out.println("deleting test files ... ");
////        System.out.print(filePath.toFile().delete() ? "[OK] " : "[NOK] ");
//    }
 /**
     * Test of read method, of class FileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testRead() throws IOException {
        System.out.println("read");
        Path filePath = Paths.get(rootDir.toString(), "fileWriter_read.test");
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            FileWriter instance = new FileWriter(raf.getChannel());
            assertEquals(null, instance.read(0, 0));

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
     * Test of write method, of class FileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testWrite() throws IOException {
        System.out.println("write");
        Path filePath = Paths.get(rootDir.toString(), "fileWriter_write.test");
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            FileWriter instance = new FileWriter(raf.getChannel());
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
     * Test of addRowToMultipleWritingList method, of class FileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testAddRowToMultipleWritingList() throws IOException {
        System.out.println("addRowToMultipleWritingList");
        Path filePath = Paths.get(rootDir.toString(), "fileWriterAddRowToMultipleWritingList.test");
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            FileWriter instance = new FileWriter(raf.getChannel());
            AbstractRow row = new IndexRow(0, new IndexRow.Index(UUID.randomUUID(), 250), 0);
            boolean result = instance.addRowToMultipleWritingList(row);
            boolean expectedResult = true;
            assertEquals(expectedResult, result);
        }
        System.out.println(filePath.toFile().delete() ? "[OK] test file deleted  : "+filePath.toString() : "[NOK] ");
    }

    /**
     * Test of addRowToSingleWritingList method, of class FileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testAddRowToSingleWritingList() throws IOException {
        System.out.println("addRowToSingleWritingList");
        Path filePath = Paths.get(rootDir.toString(), "fileWriterAddRowToSingleWritingList.test");
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            FileWriter instance = new FileWriter(raf.getChannel());
            AbstractRow row = new IndexRow(12, new IndexRow.Index(UUID.randomUUID(), 250), 0);
            boolean result = instance.addRowToSingleWritingList(row);
            boolean expectedResult = true;
            assertEquals(expectedResult, result);
        }
        System.out.println(filePath.toFile().delete() ? "[OK] test file deleted  : "+filePath.toString() : "[NOK] ");
    }
    
    /**
     * Test of loadFileHeader method, of class FileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testLoadFileHeader() throws IOException {
        System.out.println("loadFileHeader");
        Path filePath = Paths.get(rootDir.toString(), "fileWriterLoadFileHeader.test");
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            FileWriter instance = new FileWriter(raf.getChannel());
//            FileHeaderRow expResult = new FileHeaderRow(new FileHeaderRow.FileHeader(0, 0));
//            FileHeaderRow result = instance.loadFileHeader();
//            assertEquals(expResult, result);
            
            ByteBuffer buffer = ByteBuffer.allocate(12);
            buffer.putInt(0);
            buffer.putInt(12);
            buffer.putInt(13);
            buffer.flip();
            System.out.println(instance.write(0, buffer));
            FileHeaderRow expResult = new FileHeaderRow(new FileHeaderRow.FileHeader(12, 13));
            FileHeaderRow result = instance.loadFileHeader();
            System.out.println(result);
            assertEquals(expResult, result);
        }
        System.out.println(filePath.toFile().delete() ? "[OK] test file deleted  : "+filePath.toString() : "[NOK] ");
    }

//    /**
//     * Test of deleteFromFile method, of class FileWriter.
//     */
//    @Test
//    public void testDeleteFromFile() throws Exception {
//        System.out.println("deleteFromFile");
//        int position = 0;
//        int offset = 0;
//        FileWriter instance = null;
//        boolean expResult = false;
//        boolean result = instance.deleteFromFile(position, offset);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
    /**
     * Test of writeUUID method, of class FileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testWriteUUID() throws IOException {
        System.out.println("writeUUID");
        UUID uuidToWrite = UUID.randomUUID();
        int expResult = Long.BYTES * 2;
        ByteBuffer buffer = ByteBuffer.allocate(expResult);
        int result = FileWriter.writeUUID(buffer, uuidToWrite);
        assertEquals(expResult, result);
    }

    /**
     * Test of writeString method, of class FileWriter.
     * @throws java.io.IOException
     */
    @Test
    public void testWriteString() throws IOException {
        System.out.println("writeString");
        String stringToWrite = "This is the string to be writed in the buffer.";
        int expResult = Integer.BYTES + stringToWrite.length();
        ByteBuffer buffer = ByteBuffer.allocate(expResult);
        int result = FileWriter.writeString(buffer, stringToWrite);
        assertEquals(expResult, result);
    }

    /**
     * Test of readString method, of class FileWriter.
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
        String result = FileWriter.readString(buffer);
        assertEquals(expResult, result);
    }

    /**
     * Test of readString method, of class FileWriter.
     */
    @Test
    public void testReadString_ByteBuffer_int() {
        System.out.println("readString");
        ByteBuffer buffer = ByteBuffer.allocate(50);
        String str = "This is a test";
        buffer.put(str.getBytes());
        buffer.flip();
        String expResult = str;
        String result = FileWriter.readString(buffer, str.length());
        assertEquals(expResult, result);
    }
    
    
    
    
    
    
    
    
    
    
    
    
//    /**
//     * Test of deleteFromFile method, of class FileUtilities.
//     */
//    @Test
//    public void testDeleteFromFile() {
//        System.out.println("deleteFromFile");
//        try {
//            System.out.println("At the begining");
//            String str = "That is a deleting test on a file.";
//            deleteFromFileTest(str, 0, 10, "deleting test on a file.");
//            
//            System.out.println("In the middle");
//            deleteFromFileTest(str, 10, 9, "That is a test on a file.");
//            
//            System.out.println("At the end");
//            deleteFromFileTest(str, 23, 11, "That is a deleting test");
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(FileUtilitiesTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(FileUtilitiesTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    private void deleteFromFileTest(String str, int position, int size, String expResult) throws FileNotFoundException, IOException {
//        String result;
//        try (RandomAccessFile file = new RandomAccessFile(filePath.toString(), "rw")) {
//            FileChannel fc = file.getChannel();
//            ByteBuffer buff = ByteBuffer.allocate(str.length());
//            buff.put(str.getBytes());
//            buff.flip();
//            while (buff.hasRemaining()) {
//                fc.write(buff);
//            }
//
//            FileUtilities.deleteFromFile(fc, position, size);
//
//            buff.clear();
//            fc.position(0);
//            int lenght = fc.read(buff);
//            buff.flip();
//            byte b[] = new byte[lenght];
//            buff.get(b);
//            result = new String(b);
//        }
//        filePath.toFile().delete();
//        assertEquals(expResult, result);
//    }
//
    
    
    
    
    
//    /**
//     * Test of loadFileHeader method, of class FileUtilities.
//     */
////    @Test
////    public void testLoadFileHeader() throws Exception {
////        System.out.println("loadFileHeader");
////        FileChannel fc = null;
////        FileHeaderRow expResult = null;
////        FileHeaderRow result = FileUtilities.loadFileHeader(fc);
////        assertEquals(expResult, result);
////        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
////    }
//    /**
//     * Test of writeString method, of class FileUtilities.
//     */
////    @Test
////    public void testWriteString() {
////        System.out.println("writeString");
////        ByteBuffer buffer = null;
////        String str = "";
////        FileUtilities.writeString(buffer, str);
////        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
////    }
//    /**
//     * Test of readString method, of class FileUtilities.
//     */
////    @Test
////    public void testReadString() {
////        System.out.println("readString");
////        ByteBuffer buffer = null;
////        String expResult = "";
////        String result = FileUtilities.readString(buffer);
////        assertEquals(expResult, result);
////        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
////    }
   
}
