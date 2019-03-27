package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * La classe abstraite <code>AbstractRow</code> définit la notion de tuple. Elle
 * encapsule des objets qui vont étre écrit/lu sur/depuis un fichier.
 *
 * @author aliyou
 * @version 1.0.0
 * @param <T> Objet à persister
 */
public abstract class AbstractRow<T> implements PropertyChangeListener {

    /**
     * Support d'écoute
     */
    private final PropertyChangeSupport pcs;

    /**
     * Taille de l'en-tete d'un tuple.
     */
    protected final static int ROW_HEADER_SIZE = 4;

    /**
     * Identifiant du tuple.
     */
    private final int rowID;
    /**
     * Stock le pointeur du tuple.
     */
    protected long rowPointer;
    /**
     * Indique si l'état du tuple a changé.
     */
    private boolean changed;
    /**
     * L'objet encapsulé dans le tuple.
     */
    private T data;
    /**
     * Taille de l'objet encapsulé dans le tuple.
     */
    private final int dataSize;

    /**
     * Constructeur avec l'identifiant du tuple, l'objet encapsulé, la taille de l'objet
     * encapsulé et le pointeur du tuple.
     *
     * @param rowID Identifiant du tuple.
     * @param data Objet encapsulé dans le tuple.
     * @param dataSize Taille de l'objet encapsulé dans le tuple.
     * @param rowPointer Pointeur du tuple.
     */
    AbstractRow(int rowID, T data, int dataSize, long rowPointer) {
        if (data == null) {
            throw new NullPointerException("data == null");
        }
        this.rowID = rowID;
        this.data = data;
        this.dataSize = dataSize;
        this.rowPointer = rowPointer;
        this.changed = false;
        this.pcs = new PropertyChangeSupport(this);
//        propertyChangeListener(this.data, "addPropertyChangeListener");
    }

    private void propertyChangeListener(T data, String methodName) {
        try {
            Class<?> cl = Class.forName(data.getClass().getName());
            Method method = cl.getDeclaredMethod(methodName);
            method.invoke(data, this);
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException
                | IllegalAccessException | ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Ecrit un tuple dans un fichier.
     * 
     * @param fileChannel Fichier
     * @throws IOException s'il y'a une erreur lors de l'écriture.
     */
    void write(FileChannel fileChannel) throws IOException, DAOException {
        ByteBuffer buffer = ByteBuffer.allocate(this.getRowSize());
        buffer.putInt(rowID);
        writeData(buffer);
        buffer.flip();
        if (this.rowPointer > -1) {
            fileChannel.position(this.rowPointer);
            while (buffer.hasRemaining()) {
                fileChannel.write(buffer);
                System.out.println(" writed -> " + this);
            }
        }
        this.changed = false;
    }

    /**
     * Ecrit le contenu de l'objet encapsulé dans un tampon d'octet.
     * 
     * @param buffer Tampon d'octet.
     * @throws DAOException s'il y'a une erreur lors de l'écriture.
     */
    protected abstract void writeData(ByteBuffer buffer) throws DAOException;

    /**
     * Rétourne la taille d'un tuple, correspond à la taille de l'en-tete du tuple
     * plus la taille de l'objet encapsulé dans le tuple.
     *
     * @return Taille d'un tuple.
     */
    final int getRowSize() {
        return ROW_HEADER_SIZE + getDataSize();
    }

    /**
     * Rétourne <code>true</code> si l'état du tuple a changé sinon <code>false</code>
     * 
     * @return <code>true</code> si l'état du tuple a changé sinon <code>false</code>
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
     * Rétourne l'identifiant du tuple.
     *
     * @return Identifiant du tiple
     */
    int geRowID() {
        return rowID;
    }

    /**
     * Rétourne le pointeur du tuple.
     *
     * @return Pointeur du tuple.
     */
    long getRowPointer() {
        return rowPointer;
    }

    /**
     * Modifie le pointeur du tuple.
     *
     * @param rowPointer Nouveau pointeur d'un tuple.
     */
    void setRowPointer(long rowPointer) {
        if (this.rowPointer != rowPointer) {
            this.rowPointer = rowPointer;
            setChanged(true);
        }
    }

    /**
     * Rétourne l'objet encapsulé dans un tuple.
     *
     * @return Objet encapsulé dans un tuple.
     */
    T getData() {
        return data;
    }

    /**
     * Rétourne la taille de l'objet encapsulé.
     *
     * @return Taille de l'objet encapsulé
     */
    int getDataSize() {
        return this.dataSize;
    }

    /**
     * Change l'état du tuple lorsque l'état de l'objet encapulé change.
     * 
     * @param evt Evenement
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setChanged(true);
    }

    /**
     * Ajoute un écouteur
     *
     * @param listener Ecouteur à rajouter.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
     * Supprime un écouteur
     *
     * @param listener Ecouteur à supprimer
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
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
}
