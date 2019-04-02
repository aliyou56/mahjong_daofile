package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.FileHeaderRow.FileHeader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliyou
 */
public class FileUtilities {

    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(FileUtilities.class.getName());
    
    /**
     * Supprime d'un fichier <code>size</code> octets à partir de la position
     * <code>position</code>.
     *
     * exemple : Supposons qu'on a "This is a file" dans un fichier, en appelant
     *          cette méthode avec position=5 et size=5, le fichier contiendra
     *          apès l'execution "This file"
     *
     * @param fc Fichier
     * @param position Position à partir de laquelle on supprime.
     * @param size Nombre d'octets à supprimer.
     * @return <code>true</code> si la suppression a été éffectuée sinon <code>false</code>.
     * @throws IOException
     */
    public static boolean deleteFromFile(FileChannel fc, int position, int size) throws IOException {
//        checkNotNull("fileChannel", fc);
        if(position < 0) {
            throw new IllegalArgumentException("FileUtilities.deleteFromFile : position must be greater than 0 : "+position);
        }
        if(size < 0) {
            throw new IllegalArgumentException("FileUtilities.deleteFromFile : size must be greater than 0: "+size);
        }
        long fileSize = fc.size();
        boolean result = false;
        LOGGER.log(Level.INFO, "position={0}, size={1}", new Object[]{position, size});
//        System.out.println("FileUtilities.deleteFromFile : position="+position+", size="+size);
        if (fc.size() > position) {
            int nextPosition = position + size;
            int nbRemaingBytes = (int) (fc.size() - nextPosition);
            nbRemaingBytes = nbRemaingBytes < 0 ? 0 : nbRemaingBytes;
            LOGGER.log(Level.INFO, "nextPosition={0}, nbRemaingBytes={1}", new Object[]{nextPosition, nbRemaingBytes});
//            System.out.println("FileUtilities.deleteFromFile : nextPointerPosition=" +nextPosition);

            ByteBuffer remainingBytes = ByteBuffer.allocate(nbRemaingBytes);
            fc.position(nextPosition);
            fc.read(remainingBytes);
            remainingBytes.flip();
            fc.position(position);
            while (remainingBytes.hasRemaining()) {
                fc.write(remainingBytes);
            }
            fc.truncate(position + nbRemaingBytes);
            result = true;
        }
        LOGGER.log(Level.INFO, "fileSize : {0} -> {1}", new Object[]{fileSize, fc.size()});
//        System.out.println("FileUtilities.deleteFromFile : fileSize : "+fileSize+ " -> "+fc.size());
        return result;
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