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

    public static boolean deleteFromFile(FileChannel fc, int pointerPosition, int size) throws IOException {
//        checkNotNull("fileChannel", fc);
        if(pointerPosition < 0 || size < 0) {
            throw new IllegalArgumentException("pointerPosition="+pointerPosition + " | size="+size);
        }
        System.out.println("pointerPosition : " + pointerPosition);
        System.out.println("before fc.size : " + fc.size());
        if (fc.size() > pointerPosition) {
            int nextPointerPosition = pointerPosition + size;
            System.out.println("nextPointerPosition : " + nextPointerPosition);
            int nbRemainBytes = (int) (fc.size() - nextPointerPosition);
            nbRemainBytes = nbRemainBytes < 0 ? 0 : nbRemainBytes;

            ByteBuffer remainingBytes = ByteBuffer.allocate(nbRemainBytes);
            fc.position(nextPointerPosition);
            fc.read(remainingBytes);
            remainingBytes.flip();
            fc.position(pointerPosition);
            while (remainingBytes.hasRemaining()) {
                fc.write(remainingBytes);
            }
            fc.truncate(pointerPosition + nbRemainBytes);
            System.out.println("after fc.size : " + fc.size());
            return true;
        }
        return false;
    }

    /**
     * Vérifie si un objet est null ou pas. Lève une exception de type
     * <code>IllegalArgumentException</code> si l'ojet est <code>null</code>.
     *
     * @param name Nom de l'objet à tester.
     * @param obj Objet à tester.
     */
    private static void checkNotNull(String name, Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " == null");
        }
    }

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
