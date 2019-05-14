package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.FileDAOUtilities;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import fr.univubs.inf1603.mahjong.engine.persistence.MahjongObservable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe abstraite {@code AbstractRow} définit la notion de tuple. Un
 * tuple encapsule un objet de type {@code MahJongObservable}. Il répresente la
 * plus petite unité de données écrite/lue sur/depuis un fichier. Il écoute
 * également l'objet qu'il encapsule. Dès qu'il est notifié d'un changement il
 * notifie à son tour le processus qui éffectue les opérations d'entrée/sortie
 * sur un fichier. Ce dernier le mets dans sa liste d'attente pour écriture sur
 * disque.
 *
 * <pre>
 *
 *    format d'un tuple dans un fichier:
 *
 *         ---------------------------------------
 *         | rowID = x |         data            |
 *         ---------------------------------------
 * </pre>
 *
 * @author aliyou
 * @version 1.3
 * @param <T> Objet à persister
 */
public abstract class AbstractRow<T extends MahjongObservable> implements MahjongObservable, PropertyChangeListener {

    public static final String DIRTY_PROPERTY = "dirty";
    
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
    public final static int ROW_HEADER_SIZE = 4;
    /**
     * Identifiant d'un tuple.
     */
    private int rowID;
    /**
     * Pointeur d'un tuple.
     */
    private long rowPointer;
    /**
     * Objet encapsulé dans un tuple.
     */
    private T data;
    /**
     * Taille d'un objet encapsulé dans un tuple.
     */
    private final int dataSize;
    /**
     * Indique si l'état d'un tuple a changé ou pas.
     */
    private boolean dirty;

    /**
     * Constructeur privé qui initialise la taille d'un objet encapsulé
     * <code>dataSize</code> et un pointeur de tuple <code>rowPointer</code>.
     *
     * @param dataSize Taille d'un objet encapsulé dans un tuple. DOIT ETRE
     * SUPERIEUR A 0.
     * @param rowPointer Pointeur d'un tuple. DOIT ETRE POSITIF.
     */
    private AbstractRow(int dataSize, long rowPointer) {
        if (dataSize < 1) {
            throw new IllegalArgumentException("AbstractRow -> dataSize : " + dataSize + " must be greater than zero.");
        }
        if (rowPointer < -1) {
            throw new IllegalArgumentException("rowPointer : " + rowPointer + " must be greater or equal to zero");
        }
        this.dataSize = dataSize;
        this.rowPointer = rowPointer;
        this.dirty = false;
        this.pcs = new PropertyChangeSupport(this);
    }

    /**
     * Constructeur avec un identifiant de tuple <code>rowID</code>, un objet
     * encapsulé <code>data</code>, la taille d'un objet encapsulé
     * <code>dataSize</code> et un pointeur de tuple <code>rowPointer</code>.
     *
     * @param rowID Identifiant d'un tuple. DOIT ETRE POSITIF.
     * @param data Objet encapsulé dans un tuple. NE DOIT PAS ETRE NULL.
     * @param dataSize Taille d'un objet encapsulé dans un tuple. DOIT ETRE
     * SUPERIEUR A 0.
     * @param rowPointer Pointeur d'un tuple. Pointe sur le début du tuple dans
     * un fichier. DOIT ETRE POSITIF.
     */
    protected AbstractRow(int rowID, T data, int dataSize, long rowPointer) {
        this(dataSize, rowPointer);
        if (rowID < 0) {
            throw new IllegalArgumentException("AbstractRow -> rowID : " + rowID + " must be greater or equal to zero.");
        }
        FileDAOUtilities.checkNotNull("AbstractRow -> data", data);
        this.rowID = rowID;
        this.data = data;
        this.dirty = true;
        this.data.addPropertyChangeListener(this);
    }
    
    /**
     * Constructeur avec le processus qui éffectue les opérations d'entrée/sortie 
     * sur un fichier <code>writer</code>, la taille d'un objet
     * encapsulé <code>dataSize</code> et un pointeur de tuple
     * <code>rowPointer</code>.
     *
     * @param writer Processus qui éffectue les opérations d'entrée/sortie sur un fichier
     * @param dataSize Taille d'un objet encapsulé dans un tuple. DOIT ETRE
     * SUPERIEUR A 0.
     * @param rowPointer Pointeur d'un tuple. DOIT ETRE POSITIF.
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation.
     */
    protected AbstractRow(DAOFileWriter writer, int dataSize, long rowPointer) throws DAOFileException {
        this(dataSize, rowPointer);
        FileDAOUtilities.checkNotNull("AbstractRow -> writer", writer);
        try {
            ByteBuffer buffer = writer.read(rowPointer, getRowSize());
            if (buffer == null) {
                String message = "Row could not be read from the file."
                        +"\n\t\t\t cause -> No data found in the file at the position " + rowPointer + "\n" ;
                LOGGER.log(Level.WARNING, message);
                throw new DAOFileException(message);
            } 
            read(buffer);
            this.addPropertyChangeListener(writer);
        } catch (DAOFileWriterException ex) {
            String message = "Row could not be read from the file."
                    + "\n\t cause -> " + ex.getMessage() + "\n";
            LOGGER.log(Level.WARNING, message);
            throw new DAOFileException(message);
        }
    }

    /**
     * Constructeur avec un tampo d'octets <code>buffer</code>, la taille d'un
     * objet encapsulé <code>dataSize</code> et un pointeur de tuple
     * <code>rowPointer</code>.
     *
     * @param buffer Tampon d'octets à partir duquel un objet encapsulé
     * <code>data</code> est lu. NE DOIT PAS ETRE NULL.
     * @param dataSize Taille d'un objet encapsulé dans un tuple. DOIT ETRE
     * SUPERIEUR A 0.
     * @param rowPointer Pointeur d'un tuple. DOIT ETRE POSITIF.
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation.
     */
    protected AbstractRow(ByteBuffer buffer, int dataSize, long rowPointer) throws DAOFileException {
        this(dataSize, rowPointer);
        FileDAOUtilities.checkNotNull("AbstractRow -> buffer", buffer);
        read(buffer);
    }

    /**
     * Lis un tuple à partir d'un tampon d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets
     * @throws DAOException s'il y'a une erreur lors de la lecture.
     */
    private void read(ByteBuffer buffer) throws DAOFileException {
        LOGGER.log(Level.FINE, " AbstractRow.read -> pointer = {0}", rowPointer);
        int position = buffer.position();
        if (buffer.remaining() < getDataSize()) {
            String message = "Row couldn't be read from the buffer '" + buffer + "'"
                    + "\n\t cause -> Remianing bytes '" + buffer.remaining() + "' is less than ROW_SIZE '" + getRowSize() + "' \n";
            throw new DAOFileException(message);
        }
        try {
            this.rowID = buffer.getInt();
            T dataRead = readData(buffer);
            if (this.data != null) {
                this.data.removePropertyChangeListener(this);
            }
            this.data = dataRead;
            this.data.addPropertyChangeListener(this);
        } catch (DAOFileException ex) {
            buffer.position(position);
//            LOGGER.log(Level.SEVERE, ex.getMessage());
            throw new DAOFileException(ex.getMessage());
        } 
    }

    /**
     * Ecrit un tuple dans un tampon d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets.
     * @throws DAOFileException s'il y'a une erreur lors de l'écriture.
     */
    int write(ByteBuffer buffer) throws DAOFileException {
        int startPosition = buffer.position();
        try {
            if (buffer.remaining() < getDataSize()) {
            String message = this.getClass().getSimpleName() + " data=" + getData() + " couldn't be writed in the buffer '" + buffer + "'"
                    + "\n\t cause -> Remianing bytes '" + buffer.remaining() + "' is less than ROW_SIZE '"+getRowSize()+"' \n";
                throw new DAOFileException(message);
            }
            buffer.putInt(rowID);
            int nbWritedBytes = writeData(buffer);
            if (nbWritedBytes != getDataSize()) {
                int diff = getDataSize() - nbWritedBytes;
                buffer.position(buffer.position() + diff);
            }
            LOGGER.log(Level.FINE, "startPosition : {0}, nbWritedBytes : {1}, endPosition : {2}, rowSize : {3}, dataType : {4}",
                    new Object[]{startPosition, nbWritedBytes, buffer.position(), getRowSize(), this.data.getClass().getSimpleName()});
            dirty = false;
//            setDirty(false);
            return ROW_HEADER_SIZE + nbWritedBytes;
        } catch (DAOFileException ex) {
            buffer.position(startPosition);
//            LOGGER.log(Level.SEVERE, message);
            throw new DAOFileException(ex.getMessage());
        } 
    }

    /**
     * Renvoie un objet {@code T} encapsulé lu depuis un tampon d'octet <code>buffer</code>.
     *
     * @param buffer Tampon d'octets à partir duquel un objet encapsulé est lu.
     * @return Objet <code>T</code> encapsulé  dans un tuple si les données dans le tampon
     * sont cohérentes.
     * @throws DAOFileException s'il y'a une reeur lors de la lecture.
     */
    protected abstract T readData(ByteBuffer buffer) throws DAOFileException;

    /**
     * Ecrit un objet encapsulé <code>data</code> dans un tampon d'octet
     * <code>buffer</code>.
     *
     * @param buffer Tampon d'octets dans lequel un objet encapsulé est écrit.
     * @return Le nombre d'octes écrit dans le tampon d'octets
     * @throws DAOFileException s'il y'a une erreur lors de l'écriture.
     */
    protected abstract int writeData(ByteBuffer buffer) throws DAOFileException;

    /**
     * Renvoie la taille d'un tuple, correspond à la taille de l'en-tete du
     * tuple {@code +} la taille de l' objet {@code T} encapsulé dans le tuple.
     *
     * @return Taille d'un tuple.
     */
    final public int getRowSize() {
        return ROW_HEADER_SIZE + getDataSize();
    }

    /**
     * Renvoie <code>true</code> si l'état du tuple a changé sinon
     * <code>false</code>
     *
     * @return <code>true</code> si l'état du tuple a changé sinon
     * <code>false</code>
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Modifie et notifie l'état d'un tuple.
     *
     * @param dirty Nouvel état
     */
    public void setDirty(boolean dirty) {
        boolean oldValue = this.dirty;
        this.dirty = dirty;
        this.pcs.firePropertyChange(DIRTY_PROPERTY, oldValue, dirty);
    }

    /**
     * Renvoie l'identifiant d'un tuple.
     *
     * @return Identifiant d'un tuple.
     */
    public int geRowID() {
        return rowID;
    }

    /**
     * Renvoie le pointeur d'un tuple.
     *
     * @return Pointeur d'un tuple.
     */
    public long getRowPointer() {
        return rowPointer;
    }

    /**
     * Modifie le pointeur d'un tuple. 
     *
     * @param rowPointer Nouveau pointeur d'un tuple. DOIT ETRE SUPERIEUR OU
     * EGAL A 0.
     * @param dirty si <code>true</code> Notifie le writer du tuple. Et
     * le tuple est écrit dans un fichier.
     */
    public void setRowPointer(long rowPointer, boolean dirty) {
        if (rowPointer < 0) {
            String message = "Row pointer didn't changed. "
                    + "\n\t cause -> newRowPointer '" + rowPointer + "' is less than 0.";
            LOGGER.log(Level.WARNING, message);
        } else {
            if (this.rowPointer != rowPointer) {
                this.rowPointer = rowPointer;
                if (dirty) {
                    setDirty(true);
                }
            }
        }
    }

    /**
     * Renvoie l'objet {@code T} encapsulé dans un tuple.
     *
     * @return Objet {@code T} encapsulé dans un tuple.
     */
    public T getData() {
        return data;
    }

    /**
     * Renvoie la taille de l'objet {@code T} encapsulé  dans un tuple.
     *
     * @return Taille de l'objet {@code T} encapsulé dans un tuple.
     */
    public int getDataSize() {
        return this.dataSize;
    }

    /**
     * Change l'état d'un tuple lorsque l'état de l'objet encapsulé change.
     *
     * @param evt Evenement
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setDirty(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.pcs;
    }

    /**
     * @return Description textuelle d'un tuple.
     */
    @Override
    public String toString() {
        return "Row {" + "id=" + rowID + ", rowPointer=" + rowPointer + ", dirty=" + dirty
                + "\n\tdataSize=" + dataSize + "\n\tdata=" + data + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.rowID;
        hash = 97 * hash + (int) (this.rowPointer ^ (this.rowPointer >>> 32));
        hash = 97 * hash + Objects.hashCode(this.data);
        hash = 97 * hash + this.dataSize;
        return hash;
    }

    /**
     * {@inheritDoc}
     */
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
