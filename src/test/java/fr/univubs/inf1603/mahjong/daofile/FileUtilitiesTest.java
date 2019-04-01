
package fr.univubs.inf1603.mahjong.daofile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class FileUtilitiesTest {
    
    static Path filePath;
    
    public FileUtilitiesTest() {
        filePath = Paths.get("/tmp/mahjong/dao", "file.test");
    }
    
    /**
     * Destruction de l'environnement de test
     */
    @AfterClass
    public static void tearClass() {
        System.out.println("deleting test files ... ");
        System.out.print(filePath.toFile().delete() ? "[OK] " : "[NOK] ");
    }

    /**
     * Test of deleteFromFile method, of class FileUtilities.
     */
    @Test
    public void testDeleteFromFile() {
        try {
            System.out.println("deleteFromFile");
            RandomAccessFile file = new RandomAccessFile(filePath.toString(), "rw");
            FileChannel fc = file.getChannel();
            ByteBuffer buff = ByteBuffer.allocate(30);
            buff.put("That is a file deleting test..".getBytes());
            buff.flip();
            while(buff.hasRemaining()) {
                fc.write(buff);
            }
            
            String expResult = "That deleting test..";
            int pointerPosition = 5;
            int size = 10;
            FileUtilities.deleteFromFile(fc, pointerPosition, size);
            
            fc.position(0);
            buff.clear();
            int lenght = fc.read(buff);
            buff.flip();
            System.out.println(buff);
            byte b[] = new byte[lenght];
            buff.get(b);
            String result = new String(b);
            System.out.println("result : " + result);
            assertEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtilitiesTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileUtilitiesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of loadFileHeader method, of class FileUtilities.
     */
//    @Test
//    public void testLoadFileHeader() throws Exception {
//        System.out.println("loadFileHeader");
//        FileChannel fc = null;
//        FileHeaderRow expResult = null;
//        FileHeaderRow result = FileUtilities.loadFileHeader(fc);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of writeUUID method, of class FileUtilities.
     */
//    @Test
//    public void testWriteUUID() {
//        System.out.println("writeUUID");
//        ByteBuffer buffer = null;
//        UUID uuid = null;
//        FileUtilities.writeUUID(buffer, uuid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of writeString method, of class FileUtilities.
     */
//    @Test
//    public void testWriteString() {
//        System.out.println("writeString");
//        ByteBuffer buffer = null;
//        String str = "";
//        FileUtilities.writeString(buffer, str);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of readString method, of class FileUtilities.
     */
//    @Test
//    public void testReadString() {
//        System.out.println("readString");
//        ByteBuffer buffer = null;
//        String expResult = "";
//        String result = FileUtilities.readString(buffer);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
