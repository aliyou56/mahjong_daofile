
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.engine.persistence.MahjongObservable;
import java.beans.PropertyChangeSupport;

/**
 * Cette classe répresente une en-tete de fichier. Elle est composée du nombre
 * total de tuples dans un fichier et le prochain identifiant de tuple d'un
 * fichier.
 * 
 * @author aliyou
 * @version 1.1.0
 */
public class FileHeader implements MahjongObservable {

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
     * Constructeur avec le nombre total de tuples <code>rowNumber</code> et le
     * prochain identifiant de tuple <code>nextRowID</code>.
     *
     * @param rowNumber Nombre total de tuples dans un fichier.
     * @param nextRowID Prochain identifiant de tuple.
     */
    public FileHeader(int rowNumber, int nextRowID) {
        this.rowNumber = rowNumber;
        this.nextRowID = nextRowID < 1 ? 1 : nextRowID;
        this.pcs = new PropertyChangeSupport(this);
    }

    /**
     * Rétourne le nombre total de tuples dans un fichier.
     *
     * @return Nombre total de tuples dans un fichier.
     */
    synchronized public int getRowNumber() {
        return rowNumber;
    }

    /**
     * Rétourne le prochain identifiant de tuple.
     *
     * @return Prochain identifiant de tuple.
     */
    synchronized public int getNextRowID() {
        this.nextRowID += 1;
        this.pcs.firePropertyChange("rowLastId", this.nextRowID - 1, this.nextRowID);
        return nextRowID - 1;
    }

    /**
     * Incrémente le valeur du nombre total de tuple dans le fichier
     */
    synchronized public void incrementRowNumber() {
        increment(1);
    }

    /**
     * Décrémente le valeur du nombre total de tuple dans le fichier
     */
    synchronized public void decrementRowNumber() {
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