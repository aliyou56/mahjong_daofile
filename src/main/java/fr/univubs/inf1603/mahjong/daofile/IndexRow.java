package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Cette classe répresente un tuple qui encapsule un index.
 *
 * @author aliyou, nesrine
 * @version 1.0.0
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
     */
    IndexRow(int rowID, Index data, long rowPointer) {
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
     * @throws DAOException s'il y'a une erruer lors de la lecture d'un index
     * <code>IndexRow.Index</code>.
     */
    IndexRow(DAOFileWriter writer, long rowPointer) throws IOException, DAOException {
        super(writer, INDEX_SIZE, rowPointer);
    }

    /**
     * Constructeur avec un tampon d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets à partir duquel un index
     * <code>IndexRow.Index</code> est lu.
     * @param rowPointer Pointeur d'un tuple.
     * @throws DAOException s'il y'a une erruer lors de la lecture d'un index
     * <code>IndexRow.Index</code>.
     */
    IndexRow(ByteBuffer buffer, long rowPointer) throws DAOException {
        super(buffer, INDEX_SIZE, rowPointer);
    }

    /**
     * Lis un index <code>IndexRow.Index</code> à partir d'un tampon d'octets
     * <code>buffer</code>.
     *
     * @param buffer Tampon d'octets à partir duquel un index
     * <code>IndexRow.Index</code> est lu.
     */
    @Override
    protected Index readData(ByteBuffer buffer) {
        UUID dataID = new UUID(buffer.getLong(), buffer.getLong());
        long dataPointer = buffer.getLong();
        Index data = new Index(dataID, dataPointer);
        return data;
    }

    /**
     * Ecrit un index dans un tampon d'octet.
     *
     * @param buffer Tampon d'octet.
     * @throws DAOException s'il y'a une errue lors de l'écriture.
     */
    @Override
    protected void writeData(ByteBuffer buffer) throws DAOException {
        try {
            DAOFileWriter.writeUUID(buffer, getData().getUUID());
            buffer.putLong(getData().getPointer());
        } catch (IOException ex) {
            throw new DAOException("IO error : " + ex.getMessage());
        }
    }

    /**
     * Cette classe définit la notion de l'index. L'index est composé de
     * l'identifiant de l'objet indexé et un pointeur de donné pointant sur
     * l'objet dans le fichier. Il est utilisé pour accelerer l'accès aux
     * données persistées.
     */
    static class Index implements Persistable {

        /**
         * Support d'écoute
         */
        private final PropertyChangeSupport pcs;

        /**
         * L'identifiant de l'objet pointé
         */
        private final UUID dataID;
        /**
         * Pointeur de donné pointant sur un objet indexé.
         */
        private long pointer;

        /**
         * Constructeur avec l'identifiant de indexé et le pointeur de donné.
         *
         * @param dataID Identifiant de l'objet indexé
         * @param pointer Pointeur de donné pointant sur l'objet indexé.
         */
        Index(UUID dataID, long pointer) {
            this.dataID = dataID;
            this.pointer = pointer;
            this.pcs = new PropertyChangeSupport(this);
        }

        /**
         * Rétourne l'identifiant de l'objet indexé.
         *
         * @return Identifiant de l'objet indexé.
         */
        @Override
        public UUID getUUID() {
            return dataID;
        }

        /**
         * Rétourne le pointeur de donné d'un index.
         *
         * @return Pointeur de données d'un index.
         */
        long getPointer() {
            return pointer;
        }

        /**
         * Modifie le pointeur de donné d'un index.
         *
         * @param parentID Nouvelle valeur du pointeur de donnée.
         */
        void setPointer(long pointer) {
            if (this.pointer != pointer) {
                long oldValue = this.pointer;
                this.pointer = pointer;
                this.pcs.firePropertyChange("pointer", oldValue, this.pointer);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PropertyChangeSupport getPropertyChangeSupport() {
            return this.pcs;
        }

        /**
         * Rétourne une description textuelle d'un index.
         *
         * @return Description textuelle d'un index.
         */
        @Override
        public String toString() {
            return "Index{" + "dataID=" + dataID + ", pointer=" + pointer + '}';
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 13 * hash + Objects.hashCode(this.dataID);
            hash = 13 * hash + (int) (this.pointer ^ (this.pointer >>> 32));
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
            final Index other = (Index) obj;
            if (this.pointer != other.pointer) {
                return false;
            }
            return this.dataID.compareTo(other.dataID) == 0;
        }
    }
}
