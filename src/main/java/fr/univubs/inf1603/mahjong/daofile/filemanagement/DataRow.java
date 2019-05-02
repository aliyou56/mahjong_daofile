package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;

/**
 * Cette classe répresente un tuple de données. Un tuple de données est un tuple
 * qui a un index.
 *
 * @author aliyou
 * @version 1.2.5
 * @param <T> Obet à persister.
 */
public abstract class DataRow<T extends Persistable> extends AbstractRow<T> {

    /**
     * Index d'un tuple.
     */
    private final Index index;

    /**
     * Indique si le tuple a déjà été écrit dans un fichier de données.
     */
    private boolean writedInFile;

    /**
     * {@inheritDoc}
     */
    protected DataRow(int rowID, T data, int dataSize, long rowPointer) throws DAOFileException {
        super(rowID, data, dataSize, rowPointer);
        this.index = new Index(data.getUUID(), rowPointer);
        this.writedInFile = false;
//        System.err.println("constructor index : " +index);
    }

    /**
     * {@inheritDoc}
     */
    protected DataRow(DAOFileWriter writer, int dataSize, long rowPointer) throws DAOFileException {
        super(writer, dataSize, rowPointer);
        this.index = new Index(getData().getUUID(), rowPointer);
        this.writedInFile = true;
    }

    /**
     * @return L'index d'un tuple de donnée.
     */
    protected Index getIndex() {
        return index;
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
