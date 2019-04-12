package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOMahjong;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import fr.univubs.inf1603.mahjong.daofile.FileHeaderRow.FileHeader;
import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import fr.univubs.inf1603.mahjong.engine.persistence.UniqueIdentifiable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe générique <code>FileDAOMahjong</code> définit la notion de
 * persistance d'un objet <code>T</code> dans un fichier. Un fichier d'index est associé à
 * chaque fichier de données.
 *
 * L'ensemble des tuples des objets <code>T</code> en mémoire sont stockés dans une liste
 * ordonnée suivant le pointeur de tuple <code>dataRowsSortedByPointer</code>.
 * Cela permet de garder une reference sur les tuples ainsi lorsqu'un objet T
 * change il notifie son tuple et ce dernier notifie le processus qui écrit dans 
 * le fichier de données <code>dataWriter</code> et le tuple est mis dans la liste 
 * d'attente pour etre ecrit dans le fichier de données.
 * 
 *
 * <pre>
 * format d'un fichier de données :
 *        ---------------------------------------------
 *        | ----------------------------------------- |
 *        | | rowID = 0 |          FileHeader       | |  --> FileHeaderRow
 *        | ----------------------------------------- |
 *        | | rowID = 1 |             T             | |  --> TRow
 *        | ----------------------------------------- |
 *        | | rowID = 2 |             T             | |  --> TRow
 *        | ----------------------------------------- |
 *        | | rowID = x |             T             | |  --> TRow
 *        | ----------------------------------------- |
 *        ---------------------------------------------
 * </pre>
 *
 * @author aliyou, nesrine
 * @version 1.0.0
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
    private FileWriter dataWriter;
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
     * stock l'en-tete du fichier
     */
    private FileHeader fileHeader;
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
    protected IndexManager indexManager;
    
    /**
     * Constructeur avec le chemin d'accès du repertoire racine et les noms des
     * fichiers de données et d'index.
     *
     * @param rootDir Chemin d'accès du repertoire racine. NE DOIT PAS ETRE NULL.
     * @param dataFilename Nom du fichier de données. NE DOIT PAS ETRE NULL.
     * @param indexFilename Nom du fichier d'index. NE DOIT PAS ETRE NULL.
     * @param rowSize Taille d'un tuple. DOIT ETRE SUPERIEUR A ZERO (0).
     * @throws DAOException s'il y'a une erreur lors de l'instanciation.
     */
    protected FileDAOMahjong(Path rootDir, String dataFilename, String indexFilename, int rowSize) throws DAOException {
        checkNotNull("rootDir", rootDir);
        checkNotNull("dataFilename", dataFilename);
        checkNotNull("indexFilename", indexFilename);
        if(rowSize < 0) {
            throw new IllegalArgumentException("rowSize must be greater than zero (0) : " +rowSize);
        }
        try {
            if (!rootDir.toFile().exists()) {
                rootDir.toFile().mkdirs();
                LOGGER.log(Level.INFO, "rootDir created");
            }
            this.rootDirPath = rootDir;
            this.dataFilePath = Paths.get(rootDir.toString(), dataFilename);
            this.indexFilePath = Paths.get(rootDir.toString(), indexFilename);
            this.rowSize = rowSize;
            this.dataRowsSortedByPointer = new ArrayList<>();
            this.dataFile = new RandomAccessFile(dataFilePath.toFile(), "rw");
            this.dataWriter = new FileWriter(dataFile.getChannel());
            this.indexManager = new IndexManager(indexFilePath, rowSize);
            this.fhr = dataWriter.loadFileHeader();
            this.fhr.addPropertyChangeListener(dataWriter);
            this.fileHeader = fhr.getData();
            System.out.print(fileHeader);
        } catch (IOException ioe) {
            throw new DAOException("Erreur IO : " + ioe.getMessage());
        }
    }

    /**
     * Rétourne un tuple de données.
     * @param rowID Identifiant du tuple de données.
     * @param data Objet <code>T</code> à encapsuler dans le tuple.
     * @param pointer Pointeur du tuple.
     * @return Tuple de données.
     */
    protected abstract AbstractRow<T> getDataRow(int rowID, T data, long pointer);
 
    /**
     * Rétourne un tuple encapsulant un objet <code>T</code> lu à partir d'un
     * tampon d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets.
     * @param pointer Pointeur du tuple.
     * @return Tuple encapsulant un objet <code>T</code> si les données dans le
     * tampon d'octets sont cohérentes sion <code>null</code>.
     */
    protected abstract AbstractRow<T> getDataRow(ByteBuffer buffer, long pointer);

    /**
     * Ajoute un tuple encapsulant l'objet <code>T</code> à la liste des tuples de données
     * <code>dataRowsSortedByPointer</code>. Ensuite mets le tuple dans la liste
     * d'attente du processus qui écrit dans le fichier de données. Crée un
     * nouvel index associé à l'objet<code>T</code> encapsulé dans le tuple via le
     * gestionnaire d'index. Et incrémente le nombre total de tuple dans le
     * fichier de données.
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
            UUID dataID = ((UniqueIdentifiable) row.getData()).getUUID();
            Index index = new Index(dataID, row.getRowPointer());
            indexManager.addIndex(index);
            fileHeader.incrementRowNumber();
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    /**
     * Rétourne un objet <code>T</code> chargé depuis le fichier de données à
     * l'aide de son identifiant <code>dataID</code> si l'objet est présent dans
     * le fichier sinon <code>null</code>.
     *
     * @param dataID Identifiant de l'objet à charger.
     * @return Objet <code>T</code> s'il existe dans le fichier sinon <code>null</code>. 
     * @throws DAOException s'il y'a une erreur lors de la lecture du fichier de données.
     */
    @Override
    final protected T loadFromPersistance(UUID dataID) throws DAOException {
        try {
            long pointer = indexManager.getDataRowPointer(dataID);
            if (pointer != -1) {
                ByteBuffer buff = dataWriter.read(pointer, rowSize);
                if (buff != null) {
                    AbstractRow<T> dataRow = getDataRow(buff, pointer);
                    if (dataRow != null) {
                        RowUtilities.addRowToSortedListByPointer(dataRowsSortedByPointer, dataRow);
                        dataRow.addPropertyChangeListener(dataWriter);
                        return dataRow.getData();
                    }
                }
            }
            return null;
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
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
        if (getRowNumber() != map.size()) {
            for (IndexRow indexRow : indexManager.getRowsSortedByUUID()) {
                Index index = indexRow.getData();
                if (!map.containsKey(index.getUUID())) {
                    find(index.getUUID());
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
     * @throws IOException s'il y'a une erreur lors de la suppression.
     */
    protected final boolean removeDataRow(UUID dataID) throws IOException {
        IndexRow indexRow = (IndexRow) indexManager.getRow(dataID);
        return removeDataRow(indexRow);
    }
     
    private boolean removeDataRow(IndexRow indexRow) throws IOException {
        if (indexRow != null) {
            Index index = indexRow.getData();
            AbstractRow dataRow = RowUtilities.getRowFromSortedListByPointer(dataRowsSortedByPointer, indexRow.getData().getPointer());
            removeRowFromRowsList(dataRow);
            if (dataWriter.deleteFromFile((int) index.getPointer(), rowSize)) {
                RowUtilities.updateRowsPointer(dataRowsSortedByPointer, index.getPointer(), rowSize);
                indexManager.removeIndex(indexRow);
                fileHeader.decrementRowNumber();
                return true;
            }
        }
        return false;
    }

    /**
     * Supprime un ensemble d'objets <code>T</code> d'un fichier de données.
     * 
     * @param dataToDelete Liste des objets <code>T</code> à supprimer.
     * @throws IOException 
     */
    final void deleteFromPersistance(List<T> dataToDelete) throws IOException { //TODO check
        if (!dataToDelete.isEmpty()) {
            List<? extends AbstractRow> indexRows = new ArrayList<>();
            
            dataToDelete.forEach(data -> {
                IndexRow indexRow = indexManager.getRow(data.getUUID());
                RowUtilities.addRowToSortedListByPointer((List<AbstractRow>) indexRows, indexRow);
            });
            
            List<IndexRow> multipleRemoveList = new ArrayList<>();
            
            IndexRow firstRow = (IndexRow) indexRows.get(0);
            long offset1 = firstRow.getRowPointer() + firstRow.getRowSize();
            for(IndexRow indexRow : (List<IndexRow>) indexRows) {
                if (offset1 - indexRow.getRowPointer() == firstRow.getRowSize()) {
                     multipleRemoveList.add(indexRow);
                } else {
                    removeDataRow(indexRow);
                }
                offset1 += firstRow.getRowSize();
            }
           
            long startPointer = multipleRemoveList.get(0).getRowPointer();
            multipleRemoveList.forEach(indexRow -> {
                AbstractRow dataRow = RowUtilities.getRowFromSortedListByPointer(dataRowsSortedByPointer, indexRow.getData().getPointer());
                if (dataRow != null) {
                    removeRowFromRowsList(dataRow);
                }
                fileHeader.decrementRowNumber();
            });
            indexManager.removeIndex(multipleRemoveList);
            int offset = indexRows.size() * rowSize;
            if (dataWriter.deleteFromFile((int) startPointer, offset)) { //startPointer
                LOGGER.log(Level.INFO, " [OK] {0} {1} successful deleted -> startPointer : {2} -- offset : {3}",
                        new Object[]{indexRows.size(), dataToDelete.get(0).getClass().getSimpleName(), startPointer, offset});
            }
        }
    }

    /**
     * Mets le pointeur d'un tuple <code>rowPointer</code> à <code>-1</code>
     * pour éviter que le tuple soit écrit dans le fichier s'il est dans la
     * liste d'attente du processus qui écrit dans le fichier de données
     * <code>dataWriter</code>. Ensuite supprime les écoutes et rétire le tuple de la
     * liste des tuples de données <code>dataRowsSortedByPointer</code>.
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
        return fileHeader.getRowNumber();
    }

    /**
     * Rétourne un identifiant pour le prochain tuple.
     *
     * @return Identifiant pour le prochain tuple
     */
    private int getNexRowID() {
        return fileHeader.getNextRowID();
    }

    /**
     * Rétourne un pointeur de données pour le prochain tuple.
     *
     * @return Pointeur de données pour le prochain tuple.
     */
    private long getNextRowPointer() {
        return FileHeaderRow.FILE_HEADER_ROW_SIZE + (fileHeader.getRowNumber() * rowSize);
    }
    
    /**
     * Vérifie si un objet est null ou pas. Lève une exception de type
     * <code>IllegalArgumentException</code> si l'ojet est <code>null</code>.
     *
     * @param name Nom de l'objet à tester.
     * @param obj Objet à tester.
     */
    static private void checkNotNull(String name, Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " == null");
        }
    }
}