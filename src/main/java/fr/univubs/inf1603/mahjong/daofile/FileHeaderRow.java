package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.engine.persistence.MahjongObservable;
import fr.univubs.inf1603.mahjong.daofile.FileHeaderRow.FileHeader;
import java.beans.PropertyChangeSupport;
import java.nio.ByteBuffer;

/**
 * Cette classe répresente un conteneur qui encapsule un une en-tete de fichier.
 *
 * @author aliyou
 * @version 1.0.0
 */
public class FileHeaderRow extends AbstractRow<FileHeader> {

    /**
     * Taille d'une en-tete de fichier en octet
     */
    private static final int FILE_HEADER_SIZE = 8;

    /**
     * Taille d'un tuple contenant une en-tete de fichier
     */
    static final int FILE_HEADER_ROW_SIZE = ROW_HEADER_SIZE + FILE_HEADER_SIZE;

    /**
     * Constructeur avec une en-tete de fichier {@codeFileHeaderRow.FileHeader}. L'identifiant d'un tuple
     * encapsulant un en-tete est toujours égal à 0.
     *
     * @param data En-tete de fichier.
     */
    FileHeaderRow(FileHeader data) {
        super(0, data, FILE_HEADER_SIZE, 0);
    }

    /**
     * Constructeur avec un processus qui éffectue des opérations d'entrée/sortie 
     * sur un fichier <code>writer</code>.
     *
     * @param writer Processus qui éffectue des opérations d'entrée/sortie 
     * sur un fichier
     * @throws DAOException s'il y'a une erruer lors de la lecture de l'en-tete.
     */
    FileHeaderRow(DAOFileWriter writer) throws DAOException {
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

    /**
     * Cette classe répresente une en-tete de fichier. Elle est composée du
     * nombre total de tuples dans un fichier et le prochain identifiant de tuple
     * d'un fichier.
     */
    static class FileHeader implements MahjongObservable {

        /**
         * Support d'écoute
         */
        private final PropertyChangeSupport pcs;

        /**
         * Nombre total de tuple dans le fichier
         */
        private int rowNumber;
        /**
         * Identifiant du dernier tuple dans le fichier
         */
        private int nextRowID;

        /**
         * Constructeur avec le nombre total de tuples <code>rowNumber</code> et 
         * le prochain identifiant de tuple <code>nextRowID</code>.
         *
         * @param rowNumber Nombre total de tuples dans un fichier.
         * @param nextRowID Prochain identifiant de tuple.
         */
        FileHeader(int rowNumber, int nextRowID) {
            this.rowNumber = rowNumber;
            this.nextRowID = nextRowID < 1 ? 1 : nextRowID;
            this.pcs = new PropertyChangeSupport(this);
        }

        /**
         * Rétourne le nombre total de tuples dans un fichier.
         *
         * @return Nombre total de tuples dans un fichier.
         */
        synchronized int getRowNumber() {
            return rowNumber;
        }

        /**
         * Rétourne le prochain identifiant de tuple.
         *
         * @return Prochain identifiant de tuple.
         */
        synchronized int getNextRowID() {
            this.nextRowID += 1;
            this.pcs.firePropertyChange("rowLastId", this.nextRowID - 1, this.nextRowID);
            return nextRowID - 1;
        }

        /**
         * Incrémente le valeur du nombre total de tuple dans le fichier
         */
        synchronized void incrementRowNumber() {
            increment(1);
        }

        /**
         * Décrémente le valeur du nombre total de tuple dans le fichier
         */
        synchronized void decrementRowNumber() {
            increment(-1);
            if (rowNumber == 0) {
                nextRowID = 0;
            }
        }

        private synchronized void increment(int i) {
            this.rowNumber += i;
            this.pcs.firePropertyChange("rowNumber", this.rowNumber - 1, this.rowNumber);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PropertyChangeSupport getPropertyChangeSupport() {
            return this.pcs;
        }

        /**
         * Rétourne une description textuelle d'un lien
         *
         * @return Description textuelle d'un lien.
         */
        @Override
        public String toString() {
            return "FileHeader {" + "rowNumber=" + rowNumber + ", nextRowID=" + nextRowID + '}';
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + this.rowNumber;
            hash = 79 * hash + this.nextRowID;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final FileHeader other = (FileHeader) obj;
            if (this.rowNumber != other.rowNumber) {
                return false;
            }
            if (this.nextRowID != other.nextRowID) {
                return false;
            }
            return true;
        }
    }
}