package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.UUID;

/**
 * Cette classe définit la notion de l'index. L'index est composé de
 * l'identifiant de l'objet indexé et un pointeur de donné pointant sur l'objet
 * dans le fichier. Il est utilisé pour accelerer l'accès aux données
 * persistées.
 * 
 * @version 1.2.5
 */
public class Index implements Persistable {

    public static final String DATA_POINTER_PROPERTY = "dirty";
    
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
    private long dataPointer;

    /**
     * Constructeur avec l'identifiant de indexé et le pointeur de donné.
     *
     * @param dataID Identifiant de l'objet indexé
     * @param pointer Pointeur de donné pointant sur l'objet indexé.
     */
    public Index(UUID dataID, long pointer) {
        this.dataID = dataID;
        this.dataPointer = pointer;
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
    public long getDataPointer() {
        return dataPointer;
    }

    /**
     * Modifie le pointeur de donné d'un index.
     *
     * @param dataPointer Nouvelle valeur du pointeur de donnée.
     */
    public void setDataPointer(long dataPointer) { 
        if (this.dataPointer != dataPointer) {
            long oldValue = this.dataPointer;
            this.dataPointer = dataPointer;
            this.pcs.firePropertyChange(DATA_POINTER_PROPERTY, oldValue, this.dataPointer);
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
        return "Index{" + "dataID=" + dataID + ", dataPointer=" + dataPointer + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.dataID);
        hash = 13 * hash + (int) (this.dataPointer ^ (this.dataPointer >>> 32));
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
        if (this.dataPointer != other.dataPointer) {
            return false;
        }
        return this.dataID.compareTo(other.dataID) == 0;
    }
}
