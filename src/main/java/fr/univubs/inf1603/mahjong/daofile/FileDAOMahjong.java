package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.filemanagement.FileHeaderRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.IndexRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.IndexManager;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.RowUtilities;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.AbstractRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DAOFileWriter;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOMahjong;
import static fr.univubs.inf1603.mahjong.daofile.FileDAOUtilities.checkNotNull;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.Index;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe générique <code>FileDAOMahjong</code> définit la notion de
 * persistance d'un objet <code>T</code> dans un fichier. Un fichier d'index est
 * associé à chaque fichier de données.
 *
 * L'ensemble des tuples des objets <code>T</code> en mémoire sont stockés dans
 * une liste ordonnée suivant le pointeur de tuple
 * <code>dataRowsSortedByPointer</code>. Cela permet de garder une reference sur
 * les tuples ainsi lorsqu'un objet T change il notifie son tuple et ce dernier
 * notifie le processus qui écrit dans le fichier de données
 * <code>dataWriter</code> et le tuple est mis dans la liste d'attente pour etre
 * ecrit dans le fichier de données.
 *
 *
 * <pre>
 * format d'un fichier de données :
 *        ---------------------------------------------
 *        | ----------------------------------------- |
 *        | | rowID = 0 |          FileHeader       | |  --{@literal >} FileHeaderRow
 *        | ----------------------------------------- |
 *        | | rowID = 1 |             T             | |  --{@literal >} TRow
 *        | ----------------------------------------- |
 *        | | rowID = 2 |             T             | |  --{@literal >} TRow
 *        | ----------------------------------------- |
 *        | | rowID = x |             T             | |  --{@literal >} TRow
 *        | ----------------------------------------- |
 *        ---------------------------------------------
 * </pre>
 *
 * @author aliyou, nesrine
 * @version 1.2.5
 * @param <T> Objet à persister
 */
public abstract class FileDAOMahjong<T extends Persistable> extends DAOMahjong<T> {

    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(FileDAOMahjong.class.getName());

    /**
     * Stock la taille d'un tuple.
     */
    private int rowSize;
    /**
     * Liste des tuples de données en mémoire.
     */
    private List<AbstractRow> dataRowsSortedByPointer;
    /**
     * Tuple de l'en-tete du fichier
     */
    private FileHeaderRow fhr;
    /**
     * Chemin d'accès du repertoire racine.
     */
    final protected Path rootDirPath;
    /**
     * Processus qui écrit dans le fichier de données.
     */
    final protected DAOFileWriter dataWriter;
    /**
     * Gère l'ensemble des index associés aux données
     */
    final protected IndexManager indexManager;

    /**
     * Constructeur avec le chemin d'accès du repertoire racine
     * <code>rootDie</code>, le nom d'un fichier de données
     * <code>dataFilename</code>, le nom d'un fichier d'inde
     * <code>indexFilename</code> et la taille d'un tuple <code>rowSize</code>.
     *
     * @param rootDirPath Chemin d'accès du repertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @param dataFilename Nom du fichier de données. NE DOIT PAS ETRE NULL.
     * @param indexFilename Nom du fichier d'index. NE DOIT PAS ETRE NULL.
     * @param rowSize Taille d'un tuple. DOIT ETRE SUPERIEUR A ZERO (0).
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation.
     */
    protected FileDAOMahjong(Path rootDirPath, String dataFilename, String indexFilename, int rowSize) throws DAOFileException {
        checkNotNull("rootDir", rootDirPath);
        checkNotNull("dataFilename", dataFilename);
        checkNotNull("indexFilename", indexFilename);
        if (rowSize < 0) {
            throw new IllegalArgumentException(" rowSize '"+rowSize+"' must be greater than zero.");
        }
        if (!rootDirPath.toFile().exists()) {
            rootDirPath.toFile().mkdirs();
            LOGGER.log(Level.INFO, "rootDir created");
        }
        this.rootDirPath = rootDirPath;
        this.rowSize = rowSize;
        try {
            this.dataWriter = initDAOWriter(dataFilename);
        } catch (DAOFileWriterException ex) {
            throw new DAOFileException(ex.getMessage(), ex);
        }
        this.indexManager = new IndexManager(rootDirPath.resolve(indexFilename), rowSize);
        this.dataRowsSortedByPointer = new ArrayList<>();
        this.fhr = dataWriter.loadFileHeader();
//        System.out.print(fhr.getData());
    }
    
    protected FileDAOMahjong(Path rootDirPath, String indexFilename, int rowSize, DAOFileWriter writer) throws DAOFileException {
        checkNotNull("rootDir", rootDirPath);
        checkNotNull("indexFilename", indexFilename);
        if (rowSize < 0) {
            throw new IllegalArgumentException(" rowSize '"+rowSize+"' must be greater than zero.");
        }
        if (!rootDirPath.toFile().exists()) {
            rootDirPath.toFile().mkdirs();
            LOGGER.log(Level.INFO, "rootDir created");
        }
        this.rootDirPath = rootDirPath;
        this.rowSize = rowSize;
        
        this.dataWriter = writer; 
            
        this.indexManager = new IndexManager(rootDirPath.resolve(indexFilename), rowSize);
        this.dataRowsSortedByPointer = new ArrayList<>();
        this.fhr = dataWriter.loadFileHeader();
//        System.out.print(fhr.getData());
    }

    protected DAOFileWriter initDAOWriter(String dataFilename) throws DAOFileWriterException {
        return new DAOFileWriter(rootDirPath.resolve(dataFilename));
    }
    
    /**
     * Rétourne un tuple de données encapsulant l'objet <code>T</code>.
     *
     * @param rowID Identifiant du tuple de données.
     * @param data Objet <code>T</code> à encapsuler dans le tuple.
     * @param pointer Pointeur du tuple.
     * @return Tuple de données.
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation du
     * tuple.
     */
    protected abstract DataRow<T> getDataRow(int rowID, T data, long pointer) throws DAOFileException;

    /**
     * Rétourne un tuple encapsulant un objet <code>T</code> lu à partir d'un
     * tampon d'octets <code>buffer</code>.
     *
     * @param rowPointer Pointeur d'un tuple.
     * @return Tuple encapsulant un objet <code>T</code> si les données dans le
     * fichier à la position {@code pointer} correspondent bien à un tuple de
     * {@code T} sion <code>null</code>.
     * @throws DAOFileException s'il y'a une erreur lors de la lecture du tuple.
     */
    protected abstract DataRow<T> getDataRow(long rowPointer) throws DAOFileException;

    /**
     * Ajoute un tuple encapsulant l'objet <code>T</code> à la liste des tuples
     * de données <code>dataRowsSortedByPointer</code>. Ensuite mets le tuple
     * dans la liste d'attente du processus qui écrit dans le fichier de
     * données. Crée un nouvel index associé à l'objet<code>T</code> encapsulé
     * dans le tuple via le gestionnaire d'index. Et incrémente le nombre total
     * de tuple dans le fichier de données.
     *
     * @param data Objet <code>T</code> à persister.
     * @throws DAOException s'il y'a une erreur lors de la sauvegarde.
     */
    @Override
    protected synchronized final void writeToPersistence(T data) throws DAOException {
        LOGGER.log(Level.FINE, "start : {0} -> {1}", new Object[]{ data.getClass().getSimpleName(), data.getUUID()});
        try {
            DataRow<T> row = getDataRow(getNexRowID(), data, getNextRowPointer());
            RowUtilities.addRowToSortedListByPointer(dataRowsSortedByPointer, row);
            dataWriter.addRowToMultipleWritingList(row);
            row.addPropertyChangeListener(dataWriter);
            fhr.getData().incrementRowNumber();
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
        LOGGER.log(Level.FINE, "end : {0} -> {1}", new Object[]{ data.getClass().getSimpleName(), data.getUUID()});
    }

    /**
     * Rétourne un objet <code>T</code> chargé depuis le fichier de données à
     * l'aide de son identifiant <code>dataID</code> si l'objet est présent dans
     * le fichier sinon <code>null</code>.
     *
     * @param dataID Identifiant de l'objet à charger.
     * @return Objet <code>T</code> s'il existe dans le fichier sinon
     * <code>null</code>.
     * @throws DAOException s'il y'a une erreur lors de la lecture du fichier de
     * données.
     */
    @Override
    final synchronized protected T loadFromPersistence(UUID dataID) throws DAOException {
        try {
            IndexRow indexRow = indexManager.getRow(dataID);
            if (indexRow != null) {
                DataRow<T> dataRow = getDataRow(indexRow.getData().getDataPointer());
                if (dataRow != null) {
                    // on ajoute le tuple chargé à la liste des tuples.
                    RowUtilities.addRowToSortedListByPointer(dataRowsSortedByPointer, dataRow);
                    dataRow.addPropertyChangeListener(dataWriter);
                    return dataRow.getData();
                }
            }
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Rétourne la liste de tous les objets <code>T</code> persistés.
     *
     * @return Liste de tous les objets <code>T</code> persistés.
     * @throws DAOException s'il y'a une erreur lors du chargement.
     */
    @Override
    protected synchronized final List<T> laodAll() throws DAOException {
        ArrayList<T> dataList;
        if (getRowNumber() > map.size()) {
            for (IndexRow indexRow : indexManager.getRowsSortedByUUID()) {
                UUID dataID = indexRow.getData().getUUID();
                if (!map.containsKey(dataID)) {
                    find(dataID);
                }
            }
        }
        dataList = new ArrayList<>(map.values());
        return dataList;
    }

    /**
     * Supprime un tuple du fichier de données en tronquant le fichier à la
     * position du tuple plus des éventuels tuples restants.
     *
     * @param dataID Identifiant de l'objet encapsulé dans le tuple à retirer.
     * @return <code>true</code> si le tuple a bien été supprimé sinon
     * <code>false</code>.
     * @throws DAOFileException s'il y'a une erreur lors de la suppression.
     */
    protected synchronized final boolean removeDataRow(UUID dataID) throws DAOFileException {
        IndexRow indexRow = (IndexRow) indexManager.getRow(dataID);
        return removeDataRow(indexRow);
    }

    private synchronized boolean removeDataRow(IndexRow indexRow) throws DAOFileException {
        if (indexRow != null) {
            Index index = indexRow.getData();
            DataRow dataRow = (DataRow) RowUtilities.getRowFromSortedListByPointer(dataRowsSortedByPointer, indexRow.getData().getDataPointer());
            removeRowFromList(dataRow);
            try {
                if (dataWriter.deleteFromFile((int) index.getDataPointer(), rowSize)) {
                    RowUtilities.updateRowsPointer(dataRowsSortedByPointer, index.getDataPointer(), rowSize);
                    indexManager.removeIndex(indexRow);
                    fhr.getData().decrementRowNumber();
                    return true;
                }
            } catch (DAOFileWriterException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
        return false;
    }

    /**
     * Supprime un ensemble d'objets <code>T</code> d'un fichier de données.
     *
     * @param dataListToDelete Liste des objets <code>T</code> à supprimer.
     * @throws DAOFileException s'il y'a une erreur lors de la suppression.
     */
    public synchronized void delete(List<T> dataListToDelete) throws DAOFileException { //TODO check
        List<IndexRow> multipleRemoveList = indexManager.getRowList(dataListToDelete);
        if (multipleRemoveList != null) {
            List<IndexRow> singleRemoveList = indexManager.getSingleRemoveList(multipleRemoveList);
            if (!singleRemoveList.isEmpty()) {
                for (IndexRow row : singleRemoveList) {
                    removeDataRow(row);
                }
            }
            if (!multipleRemoveList.isEmpty()) {
                for (IndexRow indexRow : multipleRemoveList) {
                    DataRow dataRow = (DataRow) RowUtilities.getRowFromSortedListByPointer(dataRowsSortedByPointer, indexRow.getData().getDataPointer());
                    removeRowFromList(dataRow);
                    fhr.getData().decrementRowNumber();
                }
                indexManager.removeIndex(multipleRemoveList);
                long startPointer = multipleRemoveList.get(0).getData().getDataPointer();
                int offset = multipleRemoveList.size() * rowSize;
                try {
                    if (dataWriter.deleteFromFile((int) startPointer, offset)) {
                        RowUtilities.updateRowsPointer(dataRowsSortedByPointer, startPointer, offset);
                        LOGGER.log(Level.INFO, " [OK] {0} {1} successful deleted -> startPointer : {2} -- offset : {3}",
                                new Object[]{multipleRemoveList.size(), dataListToDelete.get(0).getClass().getSimpleName(), startPointer, offset});
                        StringBuilder sb = new StringBuilder();
                        sb.append("\t{ ");
                        multipleRemoveList.forEach(ir -> {
                            sb.append(" \t -> uuid = ").append(ir.getData().getUUID()).append("\n");
                        });
                        sb.append("\t} ");
                        LOGGER.log(Level.INFO, sb.toString());
                    }
                } catch (DAOFileWriterException ex) {
                    throw new DAOFileException(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Passe l'attibut <code>dirty</code> du tuple à {@code  false}
     * pour éviter que le tuple soit écrit dans le fichier s'il est dans la
     * liste d'attente du processus qui écrit dans le fichier de données
     * <code>dataWriter</code>. Ensuite supprime les écoutes et rétire le tuple
     * de la liste des tuples de données <code>dataRowsSortedByPointer</code>.
     *
     * @param dataRow Tuple à rétirer.
     */
    private void removeRowFromList(DataRow dataRow) {
        if (dataRow != null) {
            dataRow.setDirty(false);
            Persistable data = (Persistable) dataRow.getData();
            map.remove(data.getUUID());
            dataRow.getData().removePropertyChangeListener(dataRow);
            dataRow.removePropertyChangeListener(dataWriter);
            dataRowsSortedByPointer.remove(dataRow);
        }
    }

    /**
     * Rétourne le nombre total de tuple dans le fichier de données.
     *
     * @return Nombre total de tuple dans le fichier de données
     */
    protected int getRowNumber() {
        return fhr.getData().getRowNumber();
    }

    /**
     * Rétourne un identifiant pour le prochain tuple.
     *
     * @return Identifiant pour le prochain tuple.
     */
    private int getNexRowID() {
        return fhr.getData().getNextRowID();
    }

    /**
     * Rétourne un pointeur de données pour le prochain tuple.
     *
     * @return Pointeur de données pour le prochain tuple.
     */
    private long getNextRowPointer() {
        return FileHeaderRow.FILE_HEADER_ROW_SIZE + (getRowNumber() * rowSize);
    }
}