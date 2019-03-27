package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.FileHeaderRow.FileHeader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;

/**
 *
 * @author aliyou
 */
public class FileUtilities {

    /**
     *
     * @param fc
     * @return
     * @throws IOException
     */
    static FileHeaderRow loadFileHeader(FileChannel fc) throws IOException {
        FileHeaderRow fhr;
        ByteBuffer buff = ByteBuffer.allocate(FileHeaderRow.FILE_HEADER_ROW_SIZE);
        if (fc.size() != 0) {
            fc.position(0);
            fc.read(buff);
            buff.flip();
            fhr = FileHeaderRow.readFromBuffer(buff);
            if (fhr == null) {
                fhr = new FileHeaderRow(new FileHeader(0, 0));
            }
        } else {
            fhr = new FileHeaderRow(new FileHeader(0, 0));
        }
        return fhr;
    }

    /**
     * 
     * @param buffer
     * @param str 
     */
    static void writeUUID(ByteBuffer buffer, UUID uuid) {
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
    }

    /**
     * 
     * @param buffer
     * @param str 
     */
    static void writeString(ByteBuffer buffer, String str) {
        buffer.putInt(str.length());
        buffer.put(str.getBytes());
    }

    /**
     * 
     * @param buffer
     * @return 
     */
    static String readString(ByteBuffer buffer) {
        int lenght = buffer.getInt();
        byte buf[] = new byte[lenght];
        buffer.get(buf);
        return new String(buf);
    }
}
