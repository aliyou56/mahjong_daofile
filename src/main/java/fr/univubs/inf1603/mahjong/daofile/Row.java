
package fr.univubs.inf1603.mahjong.daofile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * La classe abstraite {@code Row} définit la notion de tuple. Elle encapsule les
 * objets qui vont étre écrit/lu sur/depuis un fichier.
 * 
 * @author aliyou
 * @param <T> Objet à persister
 */
public abstract class Row<T> implements PropertyChangeListener {
    /**
     * 
     */
    protected final static int ROW_HEADER_SIZE = 4;
    /**
     * 
     */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    /**
     * Identifiant du tuple.
     */
    private final int id;
    /**
     * Stock le pointeur du tuple.
     */
    protected long rowPointer;
    /**
     * Indique si l'état du tuple a changé.
     */
    private boolean changed;
    /**
     * L'objet encapsuler dans le tuple.
     */
    private T data;
    /**
     * Taille de l'objet encapsuler dans le tuple.
     */
    private final int dataSize;
    
    /**
     * Constructeur avec l'identifiant, l'objet encapsulé, la taille de l'objet
     * encapsulé et le pointeur du tuple.
     * @param id Identifiant du tuple.
     * @param data Objet encapsuler dans le tuple.
     * @param dataSize Taille de l'objet encapsuler dans le tuple.
     * @param rowPointer Pointeur du tuple
     */
    Row(int id, T data, int dataSize, long rowPointer) {
        if(data == null)
            throw new NullPointerException("data == null");
        this.id = id;
        this.data = data;
        propertyChangeListener(this.data, "addPropertyChangeListener");
        this.dataSize = dataSize;
        this.rowPointer = rowPointer;
        this.changed = false;
    }
    
    private void propertyChangeListener(T data, String methodName) {
        try {
            Class<?> cl = Class.forName(data.getClass().getName());
            Method method = cl.getDeclaredMethod(methodName);
            method.invoke(data, this);
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                IllegalAccessException | ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
        } 
    }
    
    /**
     * 
     * @param fileChannel
     * @throws IOException
     */
    void write(FileChannel fileChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(this.getRowSize());
        buffer.putInt(id);
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
     * 
     * @param buffer 
     */
    protected abstract void writeData(ByteBuffer buffer);
    
    /**
     * Rétourne la taille du tuple, correspond à la taille de l'en-tete du tuple
     * plus la taille de l'objet encapsuler dans le tuple.
     * @return Taille du tuple.
     */
    final int getRowSize() {
        return ROW_HEADER_SIZE + getDataSize();
    }
    
    /**
     * 
     * @return 
     */
    boolean hasChanged() {
        return changed;
    }
    
    /**
     * 
     * @param changed 
     */
    private void setChanged(boolean changed) {
        boolean oldValue = this.changed;
        this.changed = changed;
        this.pcs.firePropertyChange("changed", oldValue, changed);
    }
    
    /**
     * Rétoiurne l'identifiant du tuple.
     * @return Identifiant du tiple
     */
    int getId() {
        return id;
    }

    /**
     * Rétourne le pointeur du tuple.
     * @return Pointeur du tuple.
     */
    long getRowPointer() {
        return rowPointer;
    }

    /**
     * Modifie le pointeur du tuple.
     * @param rowPointer Nouveau pointeur d'un tuple.
     */
    void setRowPointer(long rowPointer) {
        if(this.rowPointer != rowPointer) {
            this.rowPointer = rowPointer;
            setChanged(true);
        }
    }

    /**
     * Rétourne l'objet encapsuler dans un tuple.
     * @return Objet encapsuler dans un tuple.
     */
    T getData() {
        return data;
    }

    /**
     * Modifie l'objet encapsuler dans un tuple.
     * @param data Nouvel Objet
     */
//    void setData(T data) {
//        if (this.data != data) {
//            propertyChangeListener(data, "removePropertyChangeListener");
//            this.data = data;
//            if (this.data != null) {
//                propertyChangeListener(this.data, "addPropertyChangeListener");
//            }
//        }
//    }
    
    /**
     * Rétourne la taille de l'objet encapsuler
     * @return Taille de l'objet encapsuler
     */
    int getDataSize() {
        return this.dataSize;
    }

    /**
     * 
     * @param evt 
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setChanged(true);
    }

    /**
     * 
     * @param listener 
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
     * 
     * @param listener 
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        return "Row {" + "id=" + id + ", rowPointer=" + rowPointer + ", changed=" + changed + 
                  "\n\tdataSize=" + dataSize + "\n\tdata=" + data + '}';
    }
}