package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.Persistable;
import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

/**
 * Cette classe répresente un conteneur qui encapsule un index. 
 * 
 * @author aliyou
 * @version 1.0.0
 */
public class IndexRow extends AbstractRow<Index> {

    /**
     * Taille d'un index en octet.
     */
    static final int INDEX_SIZE = 24;
    /**
     * Taille du tuple encapulant un index.
     */
    static final int INDEX_ROW_SIZE = AbstractRow.ROW_HEADER_SIZE + INDEX_SIZE;

    /**
     * Constructeur avec l'identifiant du tuple, l'index et le pointeur de
     * tuple
     *
     * @param rowID Identifiant du tuple
     * @param data Index encapsulé dans le tuple
     * @param recordPointer Pointeur du tuple
     */
    IndexRow(int rowID, Index data, long rowPointer) {
        super(rowID, data, INDEX_SIZE, rowPointer);
    }

    /**
     * Ecrit un index dans un tampon d'octet.
     *
     * @param buffer Tampon d'octet
     */
    @Override
    protected void writeData(ByteBuffer buffer) {
        FileUtilities.writeUUID(buffer, getData().getUUID());
        buffer.putLong(getData().getPointer());
    }

    /**
     * Lis un tuple contenant un index à partir d'un tampon d'octets. Rétourne
     * le tuple lu si les données dans le tampon sont cohérentes sinon
     * <code>null</code>.
     *
     * @param buffer Tampon d'octets
     * @param rowPointer Pointeur de tuple
     * @return Tuple contenant un Index si les données lues sont cohérentes sinon
     * <code>null</code>.
     */
    static IndexRow readFromBuffer(ByteBuffer buffer, long rowPointer) {
        if (buffer.remaining() >= INDEX_ROW_SIZE - 1) {
            int rowID = buffer.getInt();
            UUID dataID = new UUID(buffer.getLong(), buffer.getLong());
            long dataPointer = buffer.getLong(); 
            Index data = new Index(dataID, dataPointer);
            return new IndexRow(rowID, data, rowPointer);
        }
        return null;
    }
    
    /**
     * Cette classe définit la notion de l'index.
     * L'index est composé de l'identifiant de l'objet indexé et un 
     * pointeur de donné pointant sur l'objet dans le fichier. Il est utilisé
     * pour accelerer l'accès aux données persistées.
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
            long oldValue = this.pointer;
            this.pointer = pointer;
            this.pcs.firePropertyChange("pointer", oldValue, this.pointer);
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
