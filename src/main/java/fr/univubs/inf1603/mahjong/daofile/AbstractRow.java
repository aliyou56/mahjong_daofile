package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.engine.persistence.MahjongObservable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe abstraite <code>AbstractRow</code> définit la notion de tuple. Un
 * tuple encapsule un objet <code>MahJongObservable</code>. Il répresente
 * la plus petite unité de données écrite/lue sur/depuis un fichier. Il écoute
 * également l'objet qu'il encapsule. Dès qu'il est notifié d'un changement il
 * notifie à son tour le processus qui écrit dans le fichier. Ce dernier le mets
 * dans sa liste d'attente pour écriture sur disque.
 *
 * <pre>
 * format d'un tuple dans un fichier:
 *         ---------------------------------------
 *         | rowID = x |         data            | 
 *         --------------------------------------- 
 * </pre>
 * 
 * @author aliyou, nesrine
 * @version 1.0.0
 * @param <T> Objet à persister
 */
public abstract class AbstractRow<T extends MahjongObservable> implements MahjongObservable, PropertyChangeListener {

    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractRow.class.getSimpleName());

    /**
     * Support d'écoute
     */
    private final PropertyChangeSupport pcs;
    /**
     * Taille de l'en-tete d'un tuple.
     */
    protected final static int ROW_HEADER_SIZE = 4;
    /**
     * Identifiant d'un tuple.
     */
    private final int rowID;
    /**
     * Stock le pointeur d'un tuple.
     */
    private long rowPointer;
    /**
     * Indique si l'état d'un tuple a changé.
     */
    private boolean changed;
    /**
     * L'objet encapsulé dans un tuple.
     */
    private T data;
    /**
     * Taille de l'objet encapsulé dans un tuple.
     */
    private final int dataSize;

    /**
     * Constructeur avec l'identifiant d'un tuple, l'objet encapsulé, la taille
     * de l'objet encapsulé et le pointeur du tuple.
     *
     * @param rowID Identifiant d'un tuple.
     * @param data Objet encapsulé dans un tuple.
     * @param dataSize Taille de l'objet encapsulé dans un tuple.
     * @param rowPointer Pointeur d'un tuple.
     */
    protected AbstractRow(int rowID, T data, int dataSize, long rowPointer) {
        if (rowID < 0) {
            throw new IllegalArgumentException("rowID : " + rowID + " must be greater or equal to zero.");
        }
        if (data == null) {
            throw new IllegalArgumentException("data == null");
        }
        if (dataSize < 1) {
            throw new IllegalArgumentException("dataSize : " + dataSize + " must be greater than zero.");
        }
        if (rowPointer < -1) {
            throw new IllegalArgumentException("rowPointer : " + rowPointer + " >= -1");
        }
        this.rowID = rowID;
        this.data = data;
        this.dataSize = dataSize;
        this.rowPointer = rowPointer;
        this.changed = false;
        this.pcs = new PropertyChangeSupport(this);
        this.data.addPropertyChangeListener(this);
    }

    /**
     * Ecrit un tuple dans un tampon de données..
     *
     * @param buffer tampon de données
     * @throws IOException s'il y'a une erreur lors de l'écriture.
     */
    void write(ByteBuffer buffer) throws IOException, DAOException {
        long startPosition = buffer.position();
        buffer.putInt(rowID);
        writeData(buffer);
        int nbWritedBytes = (int) (startPosition + getRowSize() - buffer.position());
        if (nbWritedBytes != getRowSize()) {
            buffer.position(buffer.position() + nbWritedBytes);
        }
        LOGGER.log(Level.FINE, "startPosition : {0}, nbWritedBytes : {1}, endPosition : {2}, rowSize : {3}, dataType : {4}",
                new Object[]{startPosition, nbWritedBytes, buffer.position(), getRowSize(), this.data.getClass().getSimpleName()});
        this.changed = false;
    }

    /**
     * Ecrit le contenu d'un objet encapsulé dans un tampon d'octet.
     *
     * @param buffer Tampon d'octet.
     * @throws IOException s'il y'a une erreur lors de l'écriture.
     * @throws fr.univubs.inf1603.mahjong.dao.DAOException s'il y'a une erreur lors d'une opération DAO.
     */
    protected abstract void writeData(ByteBuffer buffer) throws IOException, DAOException;

    /**
     * Rétourne la taille d'un tuple, correspond à la taille de l'en-tete du
     * tuple plus la taille de l'objet encapsulé dans le tuple.
     *
     * @return Taille d'un tuple.
     */
    final int getRowSize() {
        return ROW_HEADER_SIZE + getDataSize();
    }

    /**
     * Rétourne <code>true</code> si l'état du tuple a changé sinon
     * <code>false</code>
     *
     * @return <code>true</code> si l'état du tuple a changé sinon
     * <code>false</code>
     */
    boolean hasChanged() {
        return changed;
    }

    /**
     * Modifie et notifie l'état d'un tuple.
     *
     * @param changed Nouvel état
     */
    private void setChanged(boolean changed) {
        boolean oldValue = this.changed;
        this.changed = changed;
        this.pcs.firePropertyChange("changed", oldValue, changed);
    }

    /**
     * Rétourne l'identifiant d'un tuple.
     *
     * @return Identifiant d'un tuple.
     */
    int geRowID() {
        return rowID;
    }

    /**
     * Rétourne le pointeur d'un tuple.
     *
     * @return Pointeur d'un tuple.
     */
    long getRowPointer() {
        return rowPointer;
    }

    /**
     * Modifie le pointeur d'un tuple.
     *
     * @param rowPointer Nouveau pointeur d'un tuple.
     * @param notifyWriter si <code>true</code> Notifie le writer du tuple.
     */
    void setRowPointer(long rowPointer, boolean notifyWriter) {
        if (this.rowPointer != rowPointer && rowPointer >= -1) {
            this.rowPointer = rowPointer;
            if (notifyWriter) {
                setChanged(true);
            }
        }
    }

    /**
     * Rétourne un objet <code>T</code> encapsulé dans un tuple.
     *
     * @return Objet <code>T</code> encapsulé dans un tuple.
     */
    T getData() {
        return data;
    }

    /**
     * Rétourne la taille d'un objet encapsulé dans un tuple.
     *
     * @return Taille d'un objet encapsulé dans un tuple.
     */
    int getDataSize() {
        return this.dataSize;
    }

    /**
     * Change l'état du tuple lorsque l'état de l'objet encapsulé change.
     *
     * @param evt Evenement
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setChanged(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.pcs;
    }

    /**
     * Rétourne une description textuelle d'un tuple
     *
     * @return Description textuelle d'un tuple.
     */
    @Override
    public String toString() {
        return "Row {" + "id=" + rowID + ", rowPointer=" + rowPointer + ", changed=" + changed
                + "\n\tdataSize=" + dataSize + "\n\tdata=" + data + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.rowID;
        hash = 97 * hash + (int) (this.rowPointer ^ (this.rowPointer >>> 32));
        hash = 97 * hash + Objects.hashCode(this.data);
        hash = 97 * hash + this.dataSize;
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
        final AbstractRow<?> other = (AbstractRow<?>) obj;
        if (this.rowID != other.rowID) {
            return false;
        }
        if (this.rowPointer != other.rowPointer) {
            return false;
        }
        if (this.dataSize != other.dataSize) {
            return false;
        }
        return Objects.equals(this.data, other.data);
    }

}