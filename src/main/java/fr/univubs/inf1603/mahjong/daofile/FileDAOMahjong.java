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
import fr.univubs.inf1603.mahjong.daofile.filemanagement.Index;
import java.io.IOException;
import java.io.RandomAccessFile;
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
 * @version 1.1.0
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
     * Processus qui écrit dans le fichier de données.
     */
    private DAOFileWriter dataWriter;
    /**
     * Chemin d'accès du fichier de données
     */
    private Path dataFilePath;
    /**
     * Chemin d'accès du fichier d'index.
     */
    private Path indexFilePath;
    /**
     * Fichier de données
     */
    private RandomAccessFile dataFile;
    /**
     * Tuple de l'en-tete du fichier
     */
    private FileHeaderRow fhr;
    /**
     * Chemin d'accès du repertoire racine.
     */
    protected Path rootDirPath;
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
            throw new IllegalArgumentException("rowSize must be greater than zero (0) : " + rowSize);
        }
        try {
            if (!rootDirPath.toFile().exists()) {
                rootDirPath.toFile().mkdirs();
                LOGGER.log(Level.INFO, "rootDir created");
            }
            this.rootDirPath = rootDirPath;
            this.dataFilePath = rootDirPath.resolve(dataFilename);
            this.indexFilePath = rootDirPath.resolve(indexFilename);
            this.rowSize = rowSize;
            this.dataRowsSortedByPointer = new ArrayList<>();
            this.dataFile = new RandomAccessFile(dataFilePath.toFile(), "rw");
            this.dataWriter = new DAOFileWriter(dataFile.getChannel());
            this.indexManager = new IndexManager(indexFilePath, rowSize);
            this.fhr = dataWriter.loadFileHeader();
            System.out.print(fhr.getData());
        } catch (IOException ioe) {
            throw new DAOFileException("Erreur IO : " + ioe.getMessage());
        }
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
    protected abstract AbstractRow<T> getDataRow(int rowID, T data, long pointer) throws DAOFileException;

    /**
     * Rétourne un tuple encapsulant un objet <code>T</code> lu à partir d'un
     * tampon d'octets <code>buffer</code>.
     *
     * @param writer Processus qui éffectue les opérations d'entrée/sortie sur
     * un fichier.
     * @param pointer Pointeur d'un tuple.
     * @return Tuple encapsulant un objet <code>T</code> si les données dans le
     * fichier à la position {@code pointer} correspondent bien à un tuple de
     * {@code T} sion <code>null</code>.
     * @throws DAOFileException s'il y'a une erreur lors de la lecture du tuple.
     */
    protected abstract AbstractRow<T> getDataRow(DAOFileWriter writer, long pointer) throws DAOFileException;

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
    protected final void writeToPersistance(T data) throws DAOException {
        try {
            AbstractRow<T> row = getDataRow(getNexRowID(), data, getNextRowPointer());
            RowUtilities.addRowToSortedListByPointer(dataRowsSortedByPointer, row);
            dataWriter.addRowToMultipleWritingList(row);
            row.addPropertyChangeListener(dataWriter);

//            UUID dataID = ((UniqueIdentifiable) row.getData()).getUUID();
//            Index index = new Index(dataID, row.getRowPointer());
//            indexManager.addIndex(index);
            fhr.getData().incrementRowNumber();
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
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
    final protected T loadFromPersistance(UUID dataID) throws DAOException {
        try {
            IndexRow ir = indexManager.getRow(dataID);
            if (ir != null) {
                AbstractRow<T> dataRow = getDataRow(dataWriter, ir.getData().getPointer());
//            if (dataRow != null) {
                RowUtilities.addRowToSortedListByPointer(dataRowsSortedByPointer, dataRow);
                dataRow.addPropertyChangeListener(dataWriter);
                return dataRow.getData();
//            }
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
    protected final List<T> laodAll() throws DAOException {
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
    protected final boolean removeDataRow(UUID dataID) throws DAOFileException {
        IndexRow indexRow = (IndexRow) indexManager.getRow(dataID);
        return removeDataRow(indexRow);
    }

    private boolean removeDataRow(IndexRow indexRow) throws DAOFileException {
        if (indexRow != null) {
            try {
                Index index = indexRow.getData();
                AbstractRow dataRow = RowUtilities.getRowFromSortedListByPointer(dataRowsSortedByPointer, indexRow.getData().getPointer());
                removeRowFromRowsList(dataRow);
                if (dataWriter.deleteFromFile((int) index.getPointer(), rowSize)) {
                    RowUtilities.updateRowsPointer(dataRowsSortedByPointer, index.getPointer(), rowSize);
                    indexManager.removeIndex(indexRow);
                    fhr.getData().decrementRowNumber();
                    return true;
                }
            } catch (IOException ex) {
                throw new DAOFileException("IO Error : " + ex.getMessage());
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
    final public void deleteFromPersistance(List<T> dataListToDelete) throws DAOFileException { //TODO check
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
                    AbstractRow dataRow = RowUtilities.getRowFromSortedListByPointer(dataRowsSortedByPointer, indexRow.getData().getPointer());
                    removeRowFromRowsList(dataRow);
                    fhr.getData().decrementRowNumber();
                };
                try {
                    indexManager.removeIndex(multipleRemoveList);
                    long startPointer = multipleRemoveList.get(0).getData().getPointer();
                    int offset = multipleRemoveList.size() * rowSize;
                    if (dataWriter.deleteFromFile((int) startPointer, offset)) {
                        RowUtilities.updateRowsPointer(dataRowsSortedByPointer, startPointer, offset);
                        LOGGER.log(Level.INFO, " [OK] {0} {1} successful deleted -> startPointer : {2} -- offset : {3}",
                                new Object[]{multipleRemoveList.size(), dataListToDelete.get(0).getClass().getSimpleName(), startPointer, offset});
                        System.out.println("    { ");
                        multipleRemoveList.forEach(ir -> {
                            System.out.println(" \t -> uuid = " + ir.getData().getUUID());
                        });
                        System.out.println("    } ");
                    }
                } catch (IOException ioe) {
                    throw new DAOFileException("IO Error : " + ioe.getMessage());
                }
            }
        }
    }

    /**
     * Mets le pointeur d'un tuple <code>rowPointer</code> à <code>-1</code>
     * pour éviter que le tuple soit écrit dans le fichier s'il est dans la
     * liste d'attente du processus qui écrit dans le fichier de données
     * <code>dataWriter</code>. Ensuite supprime les écoutes et rétire le tuple
     * de la liste des tuples de données <code>dataRowsSortedByPointer</code>.
     *
     * @param dataRow Tuple à rétirer.
     */
    private void removeRowFromRowsList(AbstractRow dataRow) {
        if (dataRow != null) {
            dataRow.setRowPointer(-1, false);
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
     * @return Identifiant pour le prochain tuple
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
