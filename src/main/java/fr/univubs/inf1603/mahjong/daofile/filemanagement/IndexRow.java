package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cette classe répresente un tuple qui encapsule un index.
 *
 * @author aliyou, nesrine
 * @version 1.2.5
 */
public class IndexRow extends AbstractRow<Index> {

    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(IndexRow.class.getName());

    /**
     * Taille d'un index en octet.
     */
    static final int INDEX_SIZE = 24;
    /**
     * Taille du tuple d'index.
     */
    static final int INDEX_ROW_SIZE = AbstractRow.ROW_HEADER_SIZE + INDEX_SIZE;

    /**
     * Constructeur avec l'identifiant du tuple, l'index et le pointeur de tuple
     *
     * @param rowID Identifiant d'un tuple
     * @param data Index encapsulé dans le tuple.
     * @param rowPointer Pointeur du tuple.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException s'il y'a une erreur lors de l'instanciation.
     */
    public IndexRow(int rowID, Index data, long rowPointer) throws DAOFileException {
        super(rowID, data, INDEX_SIZE, rowPointer);
    }

    /**
     * Constructeur avec un processus qui éffectue des opérations
     * d'entrée/sortie sur un fichier <code>writer</code> et un pointeur de
     * tuple <code>rowPointer</code>..
     *
     * @param writer Processus qui éffectue des opérations d'entrée/sortie sur
     * un fichier.
     * @param rowPointer Pointeur d'un tuple.
     * @throws DAOFileException s'il y'a une erruer lors de la lecture d'un index.
     */
    public IndexRow(DAOFileWriter writer, long rowPointer) throws DAOFileException {
        super(writer, INDEX_SIZE, rowPointer);
    }

    /**
     * Constructeur avec un tampon d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets à partir duquel un index
     * <code>IndexRow.Index</code> est lu.
     * @param rowPointer Pointeur d'un tuple.
     * @throws DAOFileException s'il y'a une erruer lors de la lecture d'un index.
     */
    IndexRow(ByteBuffer buffer, long rowPointer) throws DAOFileException {
        super(buffer, INDEX_SIZE, rowPointer);
    }

    /**
     * Renvoie un index <code>IndexRow.Index</code> lu à partir d'un tampon d'octets
     * <code>buffer</code>.
     *
     * @param buffer Tampon d'octets à partir duquel un index
     * <code>IndexRow.Index</code> est lu.
     * @return L'index lu.
     */
    @Override
    protected Index readData(ByteBuffer buffer) {
        if (buffer.remaining() < INDEX_SIZE) {
            return null;
        }
        UUID dataID = new UUID(buffer.getLong(), buffer.getLong());
        long dataPointer = buffer.getLong();
        Index data = new Index(dataID, dataPointer);
        return data;
    }

    /**
     * Ecrit un index dans un tampon d'octet.
     *
     * @param buffer Tampon d'octet.
     * @return Le nombre d'octets écrits.
     * @throws DAOFileException s'il y'a une errue lors de l'écriture.
     */
    @Override
    protected int writeData(ByteBuffer buffer) throws DAOFileException {
        if (buffer.remaining() < INDEX_SIZE) {
            return -1;
        }
        try {
            int startPosition = buffer.position();
            DAOFileWriter.writeUUID(buffer, getData().getUUID());
            buffer.putLong(getData().getPointer());
            return buffer.position() - startPosition;
        } catch (DAOFileWriterException ex) {
            throw new DAOFileException(ex.getMessage(), ex);
        }
    }

}
