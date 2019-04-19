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
 * tuple encapsule un objet <code>MahJongObservable</code>. Il répresente la
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
    private int rowID;
    /**
     * Pointeur d'un tuple.
     */
    private long rowPointer;
    /**
     * Indique si l'état d'un tuple a changé ou pas.
     */
    private boolean changed;
    /**
     * Objet encapsulé dans un tuple.
     */
    private T data;
    /**
     * Taille d'un objet encapsulé dans un tuple.
     */
    private final int dataSize;

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
            throw new IllegalArgumentException("rowID : " + rowID + " must be greater or equal to zero.");
        }
        FileDAOUtilities.checkNotNull("data", data);
        this.rowID = rowID;
        this.data = data;
        this.data.addPropertyChangeListener(this);
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
     * @throws DAOException s'il y'a une erreur lors de l'instanciation.
     */
    protected AbstractRow(ByteBuffer buffer, int dataSize, long rowPointer) throws DAOException {
        this(dataSize, rowPointer);
        FileDAOUtilities.checkNotNull("buffer", buffer);
        read(buffer);
    }

    /**
     * Constructeur avec le writer <code>writer</code>, la taille d'un objet
     * encapsulé <code>dataSize</code> et un pointeur de tuple
     * <code>rowPointer</code>.
     *
     * @param writer Writer
     * @param dataSize Taille d'un objet encapsulé dans un tuple. DOIT ETRE
     * SUPERIEUR A 0.
     * @param rowPointer Pointeur d'un tuple. DOIT ETRE POSITIF.
     * @throws DAOException s'il y'a une erreur lors de l'instanciation.
     */
    protected AbstractRow(DAOFileWriter writer, int dataSize, long rowPointer) throws DAOException {
        this(dataSize, rowPointer);
        try {
            FileDAOUtilities.checkNotNull("writer", writer);
            ByteBuffer buffer = writer.read(rowPointer, getRowSize());
            read(buffer);
        } catch (IOException ioe) {
            throw new DAOException("IO Error : " + ioe.getMessage());
        }
    }

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
            throw new IllegalArgumentException("dataSize : " + dataSize + " must be greater than zero.");
        }
        if (rowPointer < -1) {
            throw new IllegalArgumentException("rowPointer : " + rowPointer + " must be greater or equal to zero");
        }
        this.dataSize = dataSize;
        this.rowPointer = rowPointer;
        this.changed = false;
        this.pcs = new PropertyChangeSupport(this);
    }

    /**
     * Lis un tuple à partir d'un tampon d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets
     * @throws DAOException s'il y'a une erreur lors de la lecture.
     */
    void read(ByteBuffer buffer) throws DAOException {
        System.err.println(" AbstractRow.read -> pointer = " + rowPointer);
        FileDAOUtilities.checkNotNull("buffer", buffer);
//        if (buffer.remaining() >= ROW_HEADER_SIZE - 1) {
        this.rowID = buffer.getInt();
        T dataRead = readData(buffer);
        if (dataRead != null) {
            if (this.data != null) {
                this.data.removePropertyChangeListener(this);
            }
            this.data = dataRead;
            this.data.addPropertyChangeListener(this);
        } else {
            throw new DAOException("L'objet n'a pas pu etre lu depuis le tampon d'octet.");
        }
//        }
    }

    /**
     * Ecrit un tuple dans un tampon d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets.
     * @throws DAOException s'il y'a une erreur lors de l'écriture.
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
//        setChanged(false);
    }

    /**
     * Lis et rétourne un objet encapsulé <code>data</code> depuis un tampon
     * d'octet <code>buffer</code>.
     *
     * @param buffer Tampon d'octets à partir duquel un objet encapsulé
     * <code>data</code> est lu.
     * @return Objet <code>T</code> encapsulé <code>data</code> dans un tuple.
     * @throws DAOException s'il y'a une reeur lors de la lecture.
     */
    protected abstract T readData(ByteBuffer buffer) throws DAOException;

    /**
     * Ecrit un objet encapsulé <code>data</code> dans un tampon d'octet
     * <code>buffer</code>.
     *
     * @param buffer Tampon d'octets dans lequel un objet encapsulé
     * <code>data</code> est écrit.
     * @throws IOException s'il y'a une erreur lors de l'écriture.
     * @throws DAOException s'il y'a une erreur lors d'une opération DAO.
     */
    protected abstract void writeData(ByteBuffer buffer) throws IOException, DAOException;

    /**
     * Rétourne la taille d'un tuple, correspond à la taille de l'en-tete du
     * tuple + la taille d'un objet encapsulé <code>data</code> dans le tuple.
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
    void setChanged(boolean changed) {
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
     * Modifie le pointeur d'un tuple. Si le pointeur de tuple est égal à -1, le
     * tuple n'est pas écrit dans un fichier. La valeur -1 est utilisée pour
     * éviter qu'un tuple présent dans une liste d'attente pour etre écrit dans
     * un fichier ne soit écrit lorsque le tuple est supprimé alors qu'il est
     * toujours dans la liste.
     *
     * @param rowPointer Nouveau pointeur d'un tuple. DOIT ETRE SUPERIEUR OU
     * EGAL A -1.
     * @param notifyWriter si <code>true</code> Notifie le writer du tuple. Et
     * le tuple est écrit dans un fichier.
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
     * Rétourne la taille d'un objet encapsulé <code>data</code> dans un tuple.
     *
     * @return Taille d'un objet encapsulé <code>data</code> dans un tuple.
     */
    int getDataSize() {
        return this.dataSize;
    }

    /**
     * Change l'état d'un tuple lorsque l'état de l'objet encapsulé
     * <code>data</code> change.
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