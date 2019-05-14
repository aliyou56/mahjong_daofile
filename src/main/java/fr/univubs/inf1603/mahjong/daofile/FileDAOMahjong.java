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
import fr.univubs.inf1603.mahjong.engine.persistence.MahjongObservable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
 * @author aliyou
 * @version 1.3
 * @param <T> Objet à persister
 */
public abstract class FileDAOMahjong<T extends Persistable> extends DAOMahjong<T> implements PropertyChangeListener, MahjongObservable {

    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(FileDAOMahjong.class.getName());

    /**
     * Support d'écoute
     */
    private final PropertyChangeSupport pcs;
    
    /**
     * Taille d'un tuple.
     */
    private int rowSize;
    /**
     * Liste des tuples de données en mémoire.
     */
    final private List<AbstractRow> dataRowsSortedByPointer;
    /**
     * Tuple de l'en-tete du fichier
     */
    final private FileHeaderRow fhr;
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
     * <code>rootDir</code>, le nom d'un fichier de données
     * <code>dataFilename</code> et la taille d'un tuple <code>rowSize</code>.
     *
     * @param rootDirPath Chemin d'accès du repertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @param dataFilename Nom du fichier de données. NE DOIT PAS ETRE NULL.
     * @param rowSize Taille d'un tuple. DOIT ETRE SUPERIEUR A ZERO (0).
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation.
     */
    protected FileDAOMahjong(Path rootDirPath, String dataFilename,  int rowSize) throws DAOFileException {
        checkNotNull("rootDir", rootDirPath);
        checkNotNull("dataFilename", dataFilename);
        if (rowSize < 0) {
            throw new IllegalArgumentException("FileDAOMahjong -> rowSize '"+rowSize+"' must be greater than zero.");
        }
        if (!rootDirPath.toFile().exists()) {
            rootDirPath.toFile().mkdirs();
            String msg = "New rootDir created '"+rootDirPath+"' \n\t cause -> rootDir not found";
            LOGGER.log(Level.INFO, msg);
        }
        this.rootDirPath = rootDirPath;
        this.rowSize = rowSize;
        this.indexManager = new IndexManager(rootDirPath.resolve(dataFilename + ".index"), rowSize);
        this.dataRowsSortedByPointer = new ArrayList<>();
        try {
            this.dataWriter = new DAOFileWriter(rootDirPath.resolve(dataFilename + ".data"));
            this.dataWriter.addPropertyChangeListener(this); 
        } catch (DAOFileWriterException ex) {
            throw new DAOFileException(ex.getMessage(), ex);
        }
        this.fhr = this.dataWriter.loadFileHeader();
        // Si le nombre total de tuples dans le fichier de données est différent 
        // de celui du fichier d'index on le mets à jour.
        if(this.fhr.getData().getRowNumber() != this.indexManager.getRowNumber()) {
            this.fhr.getData().setRowNumber(this.indexManager.getRowNumber());
            LOGGER.log(Level.INFO, "rowNumber updated -> newRowNumber = {0}\n", this.indexManager.getRowNumber());
        }
        this.pcs = new PropertyChangeSupport(this);
    }
    
    /**
     * Renvoie un tuple de données encapsulant l'objet <code>T</code>.
     *
     * @param rowID Identifiant du tuple de données.
     * @param data Objet <code>T</code> à encapsuler dans le tuple.
     * @param pointer Pointeur du tuple.
     * @return Tuple de données.
     */
    protected abstract DataRow<T> getDataRow(int rowID, T data, long pointer);

    /**
     * Renvoie un tuple encapsulant un objet <code>T</code> lu à partir d'un
     * tampon d'octets <code>buffer</code>.
     *
     * @param rowPointer Pointeur d'un tuple.
     * @return Tuple encapsulant un objet <code>T</code> si les données dans le
     * fichier à la position {@code rowPointer} correspondent bien à un tuple de
     * {@code T} sion <code>null</code>.
     * @throws DAOFileException s'il y'a une erreur lors de la lecture du tuple.
     */
    protected abstract DataRow<T> getDataRow(long rowPointer) throws DAOFileException;

    /**
     * Ajoute un tuple encapsulant un objet <code>T</code> à la liste des tuples
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
    final protected void writeToPersistence(T data) throws DAOException {
        LOGGER.log(Level.FINE, "start : {0} -> {1}", new Object[]{ data.getClass().getSimpleName(), data.getUUID()});
        try {
            DataRow<T> dataRow = getDataRow(getNexRowID(), data, getNextRowPointer());
            Index index = new Index(dataRow.getData().getUUID(), dataRow.getRowPointer());
            indexManager.addIndex(index);
            dataWriter.addRowToMultipleWritingList(dataRow);
            dataRow.addPropertyChangeListener(dataWriter);
            RowUtilities.addRowToSortedListByPointer(dataRowsSortedByPointer, dataRow);
            fhr.getData().incrementRowNumber();
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
        LOGGER.log(Level.FINE, "end : {0} -> {1}", new Object[]{ data.getClass().getSimpleName(), data.getUUID()});
    }

    /**
     * Renvoie un objet <code>T</code> chargé depuis le fichier de données à
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
    final protected T loadFromPersistence(UUID dataID) throws DAOException {
        try {
            // on recupère d'abors le tuple d'index correspondant à l'objet encapsuler à l'aide de son identifiant
            IndexRow indexRow = this.indexManager.getRow(dataID);
            if (indexRow != null) { // Si le tuple d'index existe 
                DataRow<T> dataRow = getDataRow(indexRow.getData().getDataPointer());
                if (dataRow != null) {
                    // on ajoute le tuple chargé à la liste des tuples.
                    RowUtilities.addRowToSortedListByPointer(this.dataRowsSortedByPointer, dataRow);
//                    dataRow.addPropertyChangeListener(this.dataWriter); // deplacé dans le constructeur du tuple
                    return dataRow.getData();
                }
            }
        } catch (DAOFileException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
//            throw new DAOException(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Renvoie la liste de tous les objets <code>T</code> persistés.
     *
     * @return Liste de tous les objets <code>T</code> persistés.
     * @throws DAOException s'il y'a une erreur lors du chargement.
     */
    @Override
    final synchronized protected  List<T> laodAll() throws DAOException {
        List<T> dataList;
        if (getRowNumber() > super.map.size()) {
            for (IndexRow indexRow : this.indexManager.getRowsSortedByUUID()) {
                UUID dataID = indexRow.getData().getUUID();
                if (!super.map.containsKey(dataID)) {
                    super.find(dataID);
                }
            }
        }
        dataList = new ArrayList<>(super.map.values());
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
        IndexRow indexRow = indexManager.getRow(dataID);
        return removeDataRow(indexRow);
    }

    private synchronized boolean removeDataRow(IndexRow indexRow) throws DAOFileException {
        if (indexRow != null) {
            Index index = indexRow.getData();
            DataRow dataRow = (DataRow) RowUtilities.getRowFromSortedListByPointer(this.dataRowsSortedByPointer, index.getDataPointer());
            removeRowFromList(dataRow);
            this.indexManager.removeIndex(indexRow);
            try {
                if (this.dataWriter.deleteFromFile((int) index.getDataPointer(), this.rowSize)) {
                    RowUtilities.updateRowsPointer(this.dataRowsSortedByPointer, index.getDataPointer(), this.rowSize);
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
    synchronized public void delete(List<T> dataListToDelete) throws DAOFileException { //TODO check
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
                        sb.append("\t{ \n");
                        multipleRemoveList.forEach(ir -> {
                            sb.append(" \t -> uuid = ").append(ir.getData().getUUID()).append("\n");
                        });
                        sb.append("\t} ");
                        LOGGER.log(Level.FINE, sb.toString());
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
            this.fhr.getData().decrementRowNumber();
            Persistable data = (Persistable) dataRow.getData();
            data.removePropertyChangeListener(dataRow);
            dataRow.removePropertyChangeListener(this.dataWriter);
            this.dataRowsSortedByPointer.remove(dataRow);
            super.map.remove(data.getUUID());
        }
    }

    /**
     * Renvoie le nombre total de tuple dans le fichier de données.
     *
     * @return Nombre total de tuple dans le fichier de données
     */
    protected int getRowNumber() {
        return fhr.getData().getRowNumber();
    }

    /**
     * Renvoie un identifiant pour le prochain tuple.
     *
     * @return Identifiant pour le prochain tuple.
     */
    private int getNexRowID() {
        return fhr.getData().getNextRowID();
    }

    /**
     * Renvoie un pointeur de données pour le prochain tuple.
     *
     * @return Pointeur de données pour le prochain tuple.
     */
    private long getNextRowPointer() {
        return FileHeaderRow.FILE_HEADER_ROW_SIZE + (getRowNumber() * rowSize);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.pcs;
    }   
}