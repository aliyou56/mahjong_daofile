
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.persistence.MahjongObservable;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cette classe répresente une en-tete de fichier. Elle est composée du nombre
 * total de tuples dans un fichier et le prochain identifiant de tuple d'un
 * fichier.
 * 
 * @author aliyou
 * @version 1.2.5
 */
public class FileHeader implements MahjongObservable {

    public static final String ROW_NUMBER_PROPERTY = "rowNumber",
                               LAST_ROW_ID_PROPERTY = "lastRowID";
    
    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(FileHeader.class.getName());
    
    /**
     * Support d'écoute
     */
    private final PropertyChangeSupport pcs;

    /**
     * Nombre total de tuple dans un fichier
     */
    private int rowNumber;
    /**
     * Identifiant du dernier tuple d'un fichier
     */
    private int lastRowID;

    /**
     * Constructeur avec le nombre total de tuples <code>rowNumber</code> et un
     * identifiant du dernier tuple <code>lastRowID</code>.
     *
     * @param rowNumber Nombre total de tuples dans un fichier.
     * @param lastRowID Identifiant du dernier tuple d'un fichier.
     */
    public FileHeader(int rowNumber, int lastRowID) {
        this.rowNumber = rowNumber;
        this.lastRowID = lastRowID;
        this.pcs = new PropertyChangeSupport(this);
    }

    /**
     * @return Nombre total de tuples dans un fichier.
     */
    synchronized public int getRowNumber() {
        return this.rowNumber;
    }

    /**
     * Modifie la valeur du nombre total de tuples dans un fichier.
     * 
     * @param rowNumber Nouvelle valeur.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * si la nouvelle valeur du nombre total de tuples dans un fichier est négative.
     */
    synchronized public void setRowNumber(int rowNumber) throws DAOFileException {
        if(rowNumber < 0) {
            String message = "rowNumber didin't chnged."
                    + "\n\t cause -> newRowNumber '"+rowNumber+"' is less than 0.";
            throw new DAOFileException(message);
        }
        if (this.rowNumber != rowNumber) {
            int oldValue = this.rowNumber;
            int newValue = rowNumber;
            this.rowNumber = rowNumber;
            if (rowNumber == 0) {
                lastRowID = 0;
            }
            this.pcs.firePropertyChange(ROW_NUMBER_PROPERTY, oldValue, newValue);
        }
    }
    
    /**
     * @return Identifiant du dernier tuple d'un fichier.
     */
    synchronized public int getLastRowID() {
        return this.lastRowID;
    }
    
    synchronized private void setLastRowID(int lastRowID) {
        if(this.lastRowID != lastRowID) {
            int oldValue = this.lastRowID;
            int newValue = lastRowID;
            this.lastRowID = lastRowID;
            this.pcs.firePropertyChange(LAST_ROW_ID_PROPERTY, oldValue, newValue);
        }
    }
    
    /**
     * Rétourne le prochain identifiant de tuple.
     * Incremente la valeur de l'identifiant du dernier tuple d'un fichier {@code lastRowID}.
     *
     * @return Prochain identifiant de tuple.
     */
    synchronized public int getNextRowID() {
        setLastRowID(getLastRowID() + 1);
        return getLastRowID();
    }

    /**
     * Incrémente le valeur du nombre total de tuple dans le fichier
     */
    synchronized public void incrementRowNumber() {
        try {
            setRowNumber(getRowNumber() + 1);
        } catch (DAOFileException ex) { // should never come.
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
    }

    /**
     * Décrémente le valeur du nombre total de tuple dans le fichier
     */
    synchronized public void decrementRowNumber() {
        if(getRowNumber() > 0) {
            try {
                setRowNumber(getRowNumber() - 1);
            } catch (DAOFileException ex) { // should never come.
                LOGGER.log(Level.WARNING, ex.getMessage());
            }
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
     * Rétourne une description textuelle d'un lien
     *
     * @return Description textuelle d'un lien.
     */
    @Override
    public String toString() {
        return "FileHeader {" + "rowNumber=" + rowNumber + ", lastRowID=" + lastRowID + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.rowNumber;
        hash = 79 * hash + this.lastRowID;
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
        if (this.lastRowID != other.lastRowID) {
            return false;
        }
        return true;
    }
}