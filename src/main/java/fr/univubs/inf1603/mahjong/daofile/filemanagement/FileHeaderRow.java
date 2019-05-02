package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cette classe répresente un conteneur qui encapsule une en-tete de fichier.
 *
 * @author aliyou
 * @version 1.2.5
 */
public class FileHeaderRow extends AbstractRow<FileHeader> {

    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(FileHeaderRow.class.getName());

    /**
     * Taille d'une en-tete de fichier en octet
     */
    private static final int FILE_HEADER_SIZE = 8;

    /**
     * Taille d'un tuple d'en-tete de fichier
     */
    public static final int FILE_HEADER_ROW_SIZE = ROW_HEADER_SIZE + FILE_HEADER_SIZE;

    /**
     * Constructeur avec une en-tete de fichier {@code FileHeader}.
     * L'identifiant d'un tuple encapsulant une en-tete est toujours égal à 0.
     *
     * @param data En-tete d'un fichier.
     * @throws DAOFileException s'il y'a une erruer lors de la lecture de
     * l'en-tete.
     */
    FileHeaderRow(FileHeader data) {
        super(0, data, FILE_HEADER_SIZE, 0);
    }

    /**
     * Constructeur avec un processus qui éffectue des opérations
     * d'entrée/sortie sur un fichier <code>writer</code>.
     *
     * @param writer Processus qui éffectue des opérations d'entrée/sortie sur
     * un fichier
     * @throws DAOFileException s'il y'a une erruer lors de la lecture de
     * l'en-tete.
     */
    FileHeaderRow(DAOFileWriter writer) throws DAOFileException {
        super(writer, FILE_HEADER_SIZE, 0);
    }

    /**
     * Change l'état d'un tuple lorsque l'état de l'objet encapsulé change.
     *
     * @param evt Evenement
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName.equals(FileHeader.ROW_NUMBER_PROPERTY)
                || propertyName.equals(FileHeader.LAST_ROW_ID_PROPERTY)) {
            setDirty(false);
            setDirty(true);
        }
    }
    
    /**
     * Renvoie une en-tete de fichier <code>FileHeader</code> lue à partir d'un
     * tampon d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets à partir duquel l'en-tete
     * <code>FileHeader</code> est lue.
     * @return Tuple d'en-tete de fichier <code>FileHeader</code> lu.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si
     * le nombre d'octets restant dans le tampon d'octets est inférieur à la
     * taille de l'en-tete de fichier.
     */
    @Override
    protected FileHeader readData(ByteBuffer buffer) throws DAOFileException {
        if (buffer.remaining() < FILE_HEADER_SIZE - 1) {
            String message = "FileHader can't be read from the buffer '" + buffer + "'"
                    + "\n\t cause -> remaining bytes '" + buffer.remaining() + "' is less than FileHeader size '" + FILE_HEADER_SIZE + "'";
            LOGGER.log(Level.SEVERE, message);
            throw new DAOFileException(message);
        }
        int rowNumber = buffer.getInt();
        int rowLastID = buffer.getInt();
        FileHeader data = new FileHeader(rowNumber, rowLastID);
        return data;
    }

    /**
     * Ecrit une en-tete de fichier dans un tampon d'octets.
     *
     * @param buffer Tampon d'octet
     * @return Le nombre d'octets écrits dans le tampon d'octets.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si
     * le nombre d'octets restant dans le tampon d'octet est insuffisant pour
     * contenir l'en-tete de fichier.
     */
    @Override
    protected int writeData(ByteBuffer buffer) throws DAOFileException {
        if (buffer.remaining() < FILE_HEADER_SIZE) {
            String message = "Remianing bytes '" + buffer.remaining() + "' is less than FILE_HEADER_SIZE '"
                    + FILE_HEADER_SIZE +"'";
            throw new DAOFileException(message);
        }
        int startPosition = buffer.position();
        buffer.putInt(getData().getRowNumber());
        buffer.putInt(getData().getLastRowID());
        return buffer.position() - startPosition;
    }
}
