
package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;

/**
 * Cette classe répresente un tuple de données. Un tuple de données est un 
 * tuple qui a un index.
 * 
 * @author aliyou
 * @version 1.1.0
 * @param <T> Obet à persister.
 */
public abstract class DataRow<T extends Persistable> extends AbstractRow<T> {
    
    /**
     * Index d'un tuple.
     */
    private final Index index;
    
    /**
     * {@inheritDoc}
     */
    protected DataRow(int rowID, T data, int dataSize, long rowPointer) throws DAOFileException {
        super(rowID, data, dataSize, rowPointer);
        this.index = new Index(data.getUUID(), rowPointer);
//        System.err.println("constructor index : " +index);
    }
    
    /**
     * {@inheritDoc}
     */
    protected DataRow(DAOFileWriter writer, int dataSize, long rowPointer) throws DAOFileException {
        super(writer, dataSize, rowPointer);
        this.index = new Index(getData().getUUID(), rowPointer);
    }

    /**
     * @return L'index d'un tuple de donnée.
     */
    protected Index getIndex() {
        return index;
    }
    
}
