package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;

/**
 * Cette classe répresente un tuple de données. Un tuple de données est un tuple
 * qui encapsuler un objet à persister. ex : tuple de tuile
 * {@code FileTileDAO.TileRow}
 *
 * @author aliyou
 * @version 1.3
 * @param <T> Obet à persister.
 */
public abstract class DataRow<T extends Persistable> extends AbstractRow<T> {

    /**
     * Indique si le tuple a déjà été écrit dans un fichier de données.
     */
    private boolean writedInFile;

    /**
     * {@inheritDoc}
     */
    protected DataRow(int rowID, T data, int dataSize, long rowPointer) {
        super(rowID, data, dataSize, rowPointer);
        this.writedInFile = false;
    }

    /**
     * {@inheritDoc}
     */
    protected DataRow(DAOFileWriter writer, int dataSize, long rowPointer) throws DAOFileException {
        super(writer, dataSize, rowPointer);
        this.writedInFile = true;
    }

    /**
     * Indique si un tuple a déjà été écrit dans un fichier de données.
     *
     * @return {@code true} si un si le tuple a déjà été écrit dans un fichier
     * de données.
     */
    protected boolean isWritedInFile() {
        return writedInFile;
    }

    protected void setWritedInFile(boolean writedInFile) {
        this.writedInFile = writedInFile;
    }
}
