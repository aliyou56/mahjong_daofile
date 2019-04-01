package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.MahJongObservable;
import fr.univubs.inf1603.mahjong.daofile.FileHeaderRow.FileHeader;
import java.beans.PropertyChangeListener;
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
     * Constructeur avec l'en-tete de fichier. L'identifiant d'un tuple
     * encapsulant un en-tete est toujours égal à 0.
     *
     * @param data L'en-tete de fichier.
     */
    FileHeaderRow(FileHeader data) {
        super(0, data, FILE_HEADER_SIZE, 0);
    }

    /**
     * Ecrit une en-tete de fichier dans un tampon d'octet.
     *
     * @param buffer Tampon d'octet
     */
    @Override
    protected void writeData(ByteBuffer buffer) {
        buffer.putInt(getData().getRowNumber());
        buffer.putInt(getData().getRowLastId());
    }

    /**
     * Lis un tuple contenant une en-tete de fichier à partir d'un tampon
     * d'octets. Rétourne le tuple lu si les données dans le tampon sont
     * cohérentes sinon <code>null</code>.
     *
     * @param buffer Tampon d'octets
     * @param rowPointer Pointeur de tuple
     * @return Tuple contenant une en-tete de fichier si les données lues sont
     * cohérentes sinon <code>null</code>.
     */
    static FileHeaderRow readFromBuffer(ByteBuffer buffer) {
        if (buffer.remaining() >= FILE_HEADER_ROW_SIZE) {
            int rowID = buffer.getInt();
            int rowNumber = buffer.getInt();
            int rowLastID = buffer.getInt();
            FileHeader data = new FileHeader(rowNumber, rowLastID);
            return new FileHeaderRow(data);
        }
        return null;
    }

    /**
     * Cette classe répresente une en-tete de fichier. Elle est composé du
     * nombre de tuple total dans le fichier et le dernier identifiant de tuple
     * dans le fichier.
     */
    static class FileHeader implements MahJongObservable {

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
        private int rowLastId;

        /**
         * Constructeur avec le nombre total de tuple et l'identifiant du
         * dernier tuple dans le fichier.
         *
         * @param rowNumber Nombre total de tuple dans le fichier.
         * @param rowLastId Identifiant du dernier tuple dans le fichier
         */
        FileHeader(int rowNumber, int rowLastId) {
            this.rowNumber = rowNumber;
            this.rowLastId = rowLastId;
            this.pcs = new PropertyChangeSupport(this);
        }

        /**
         * Rétourne le nombre total de tuple dans le fichier.
         *
         * @return Nombre total de tuple dans le fichier.
         */
        synchronized int getRowNumber() {
            return rowNumber;
        }

        /**
         * Rétourne l'identifiant du dernier tuple dans le fichier
         *
         * @return Identifiant du dernier tuple dans le fichier.
         */
        synchronized int getRowLastId() {
            return rowLastId;
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
            if(rowNumber == 0) {
                rowLastId = 0;
            }
        }

        private synchronized void increment(int i) {
            this.rowNumber += i;
            this.pcs.firePropertyChange("rowNumber", this.rowNumber - 1, this.rowNumber);
        }

        /**
         * Incrémente l'identifiant du dernier tuple dans le fichier.
         */
        synchronized void updateRowLastId() {
            this.rowLastId += 1;
            this.pcs.firePropertyChange("rowLastId", this.rowLastId - 1, this.rowLastId);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            this.pcs.addPropertyChangeListener(listener);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            this.pcs.removePropertyChangeListener(listener);
        }

        /**
         * Rétourne une description textuelle d'un lien
         *
         * @return Description textuelle d'un lien.
         */
        @Override
        public String toString() {
            return "FileHeader {" + "rowNumber=" + rowNumber + ", rowLastId=" + rowLastId + '}';
        }
    }
}
