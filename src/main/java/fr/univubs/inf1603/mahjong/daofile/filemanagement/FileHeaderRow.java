package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import java.nio.ByteBuffer;

/**
 * Cette classe répresente un conteneur qui encapsule un une en-tete de fichier.
 *
 * @author aliyou
 * @version 1.1.0
 */
public class FileHeaderRow extends AbstractRow<FileHeader> {

    /**
     * Taille d'une en-tete de fichier en octet
     */
    private static final int FILE_HEADER_SIZE = 8;

    /**
     * Taille d'un tuple contenant une en-tete de fichier
     */
    public static final int FILE_HEADER_ROW_SIZE = ROW_HEADER_SIZE + FILE_HEADER_SIZE;

    /**
     * Constructeur avec une en-tete de fichier {@codeFileHeaderRow.FileHeader}. L'identifiant d'un tuple
     * encapsulant un en-tete est toujours égal à 0.
     *
     * @param data En-tete de fichier.
     * @throws DAOFileException s'il y'a une erruer lors de la lecture de l'en-tete.
     */
    FileHeaderRow(FileHeader data) throws DAOFileException {
        super(0, data, FILE_HEADER_SIZE, 0);
    }

    /**
     * Constructeur avec un processus qui éffectue des opérations d'entrée/sortie 
     * sur un fichier <code>writer</code>.
     *
     * @param writer Processus qui éffectue des opérations d'entrée/sortie 
     * sur un fichier
     * @throws DAOFileException s'il y'a une erruer lors de la lecture de l'en-tete.
     */
    FileHeaderRow(DAOFileWriter writer) throws DAOFileException {
        super(writer, FILE_HEADER_SIZE, 0);
    }

    /**
     * Lis une en-tete de fichier <code>FileHeader</code> à partir d'un tampon
     * d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets à partir duquel l'en-tete
     * <code>FileHeader</code> est lue.
     * @return Tuple d'en-tete de fichier <code>FileHeader</code> lu.
     */
    @Override
    protected FileHeader readData(ByteBuffer buffer) {
//        if (buffer.remaining() >= getDataSize() - 1) {
        int rowNumber = buffer.getInt();
        int rowLastID = buffer.getInt();
        FileHeader data = new FileHeader(rowNumber, rowLastID);
        return data;
//        }
    }

    /**
     * Ecrit une en-tete de fichier dans un tampon d'octet.
     *
     * @param buffer Tampon d'octet
     */
    @Override
    protected void writeData(ByteBuffer buffer) {
        buffer.putInt(getData().getRowNumber());
        buffer.putInt(getData().getNextRowID());
    }
}