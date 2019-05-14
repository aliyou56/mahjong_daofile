package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.FileDAOUtilities;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import fr.univubs.inf1603.mahjong.engine.persistence.UniqueIdentifiable;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cette classe gère un ensemble de tuples encapulant soit un index
 * {@link Index} ou un lien {@link LinkRow.Link}. A l'instanciation tous les
 * tuples sont chargés en mémoire dans 2 listes triées (la première suivant le
 * pointeur de tuple et la seconde suivant l'identifiant <code>UUID</code> des
 * objets <code>T</code> encapsulés dans les tuples). Lors de l'ajout d'un
 * nouvel element, sa position dans la seconde liste est déterminée et il est
 * inséré à cette posiiton. Cela permet de 4 maintenir en permanence cette liste
 * triée. Ainsi un élement est retrtouvé à l'aide de l'algorithme de recherche
 * dichotomique (O(log(n))). Après la suppression d'un tuple, le pointeur des
 * tuples qui suivent est mis à jour.
 *
 * @author aliyou
 * @version 1.3
 * @param <T> Tuple
 */
public abstract class AbstractRowManager<T extends AbstractRow> {

    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(AbstractRowManager.class.getName());

    /**
     * Taille d'un tuple <code>T</code>.
     */
    final protected int rowSize;
    /**
     * Tuple encapsulant l'en-tete d'un fichier.
     */
    final private FileHeaderRow fhr;
    /**
     * Liste des tuples <code>T</code> triée suivant le pointeur de tuple.
     */
    final protected List<T> rowsSortedByPointer;
    /**
     * Liste des tuples <code>T</code> triée suivant l'identifiant de l'objet
     * <code>T</code> encapsulé.
     */
    final private List<T> rowsSortedByUUID;
    /**
     * Processus qui écrit dans le fichier.
     */
    final protected DAOFileWriter rowWriter;

    /**
     * Constructeur avec le chemin d'accès d'un fichier <code>rowFilePath</code>
     * et la taille d'un tuple <code>T</code>.
     *
     * @param rowFilePath Chemin d'accès d'un fichier.
     * @param rowSize Taille d'un tuple <code>T</code>.
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation.
     */
    protected AbstractRowManager(Path rowFilePath, int rowSize) throws DAOFileException {
        FileDAOUtilities.checkNotNull("AbstractRowManager -> rowFilePath", rowFilePath);
        if (rowSize <= 0) {
            throw new IllegalArgumentException("AbstractRowManager -> rowSize '" + rowSize + "' must be greater than 0.");
        }
        this.rowSize = rowSize;
        this.rowsSortedByPointer = new ArrayList<>();
        this.rowsSortedByUUID = new ArrayList<>();
        try {
            this.rowWriter = new DAOFileWriter(rowFilePath);
        } catch (DAOFileWriterException ex) {
            throw new DAOFileException(ex.getMessage(), ex);
        }
        this.fhr = rowWriter.loadFileHeader();
        int nbRows = loadAllRow();
        if (nbRows != this.fhr.getData().getRowNumber()) {
            this.fhr.getData().setRowNumber(nbRows);
            LOGGER.log(Level.INFO, "rowNumber updated -> newRowNumber = {0}\n", nbRows);
        }
    }

    /**
     * Renvoie la liste de tuples encapsulant les objets de la liste
     * {@code dataList}
     *
     * @param dataList Liste des objets Persistables.
     * @return Liste des tuples contenant les objets de la liste
     * {@code dataList}.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * s'il y'a une erreur lors de l'ajout d'un tuple à la liste.
     */
    public List<T> getRowList(List<? extends Persistable> dataList) throws DAOFileException {
        FileDAOUtilities.checkNotNull("AbstractRowManager.getRowList -> dataListToDelete", dataList);
        List<T> rowList = new ArrayList<>();
        if (!dataList.isEmpty()) {
            for (Persistable data : dataList) {
                AbstractRow row = getRow(data.getUUID());
                if (row != null) {
                    RowUtilities.addRowToSortedListByPointer((List<AbstractRow>) rowList, row);
                }
            }
        }
        return rowList;
    }

    /**
     * Renvoie une liste de tuples qui ne peuvent pas etre écrits d'un seul
     * coup. La différence entre les pointeur de tuple de ces tuples et les
     * tuples voisins sont différentes de la taille d'un tuple de meme type.
     *
     * @param rowsSorteByPointer Liste de tuples.
     * @return Liste de tuples qui peuvent pas etre écrit d'un seul coup.
     */
    public List<T> getSingleRemoveList(List<T> rowsSorteByPointer) {
        List<T> singleRemoveList = new ArrayList<>();
        if (!rowsSorteByPointer.isEmpty()) {
            AbstractRow firstRow = rowsSorteByPointer.get(0);
            long offset = firstRow.getRowPointer() + firstRow.getRowSize();
            for (T row : rowsSorteByPointer) {
                if (offset - row.getRowPointer() != firstRow.getRowSize()) {
                    singleRemoveList.add(row);
                }
                offset += firstRow.getRowSize();
            }
            singleRemoveList.forEach(r -> {
                rowsSorteByPointer.remove(r);
            });
        }
        return singleRemoveList;
    }

    /**
     * Charge l'ensemble des tuples <code>T</code> en mémoire.
     *
     * @return Nombre de tuples chargés
     * @throws DAOFileException s'il y'a une erreur lors du chargement.
     */
    private int loadAllRow() throws DAOFileException {
        try {
            int _nbRecords = 0;
            if (rowWriter.getFileLenght() != 0) {
                long rowPointer = FileHeaderRow.FILE_HEADER_ROW_SIZE;
                int lenght = 100 * rowSize;
                ByteBuffer buffer;
                try {
                    while ((buffer = rowWriter.read(rowPointer, lenght)) != null) {
                        while (buffer.hasRemaining()) {
                            T row = createRow(buffer, rowPointer);
                            if (row != null) {
                                addRowToList(row);
                                _nbRecords++;
                                rowPointer += this.rowSize;
                                LOGGER.log(Level.FINE, "[OK] row loaded : {0}", row);
                            }
                        }
                    }
                } catch (DAOFileWriterException ex) {
                    LOGGER.log(Level.SEVERE, ex.getMessage());
                    throw new DAOFileException(ex.getMessage(), ex);
                }
            }
            LOGGER.log(Level.FINE, "{0} tuples charg\u00e9s.", _nbRecords);
            return _nbRecords;
        } catch (DAOFileException ex) {
            throw new DAOFileException(ex.getMessage());
        }
    }

    /**
     * Lis un tuple <code>T</code> à partir d'un tampon d'octet.
     *
     * @param buffer Tampon d'octet.
     * @param rowPointer Pointeur de tuple.
     * @return Le tuple <code>T</code> lu.
     * @throws DAOFileException S'il y'a une erreur lors de la création d'un
     * tuple <code>T</code>.
     */
    protected abstract T createRow(ByteBuffer buffer, long rowPointer) throws DAOFileException;

    /**
     * Renvoie un tuple <code>T</code> lu à partir d'un fichier.
     *
     * @param rowPointer Pointeur de tuple.
     * @return Le tuple <code>T</code> lu.
     * @throws DAOFileException S'il y'a une erreur lors de la création d'un
     * tuple <code>T</code>.
     */
    protected abstract T createRow(long rowPointer) throws DAOFileException;

    /**
     * Mets un tuple <code>T</code> dans la liste d'attente du processus qui
     * écrit dans le fichier et l'ajoute aux listes des tuples.
     *
     * @param newRow Tuple <code>T</code> à rajouter.
     * @return {@code true} si le tuple a été ajouté sinon {@code false}.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * s'il y'a une erreur lors de l'ajout d'un tuple.
     */
    protected boolean addRow(T newRow) throws DAOFileException {
        if (addRowToList(newRow)) {
            rowWriter.addRowToMultipleWritingList(newRow);
            fhr.getData().incrementRowNumber();
            LOGGER.log(Level.FINE, "[OK] newRow added : {0}", newRow);
            return true;
        }
        LOGGER.log(Level.FINE, "[NOK] newRow did not added : {0}", newRow);
        return false;
    }

    /**
     * Supprime un tuple <code>T</code> d'un fichier à l'aide de l'identifiant
     * de l'objet encapsulé.
     *
     * @param dataID Identifiant de l'objet encapsulé dans un tuple
     * <code>T</code>.
     * @return Le tuple <code>T</code> supprimé.
     * @throws DAOFileException s'il y'a une erreur lors de la suppression du
     * tuple.
     */
    protected T removeRow(UUID dataID) throws DAOFileException {
        T row = getRow(dataID);
        return removeRow(row) ? row : null;
    }

    /**
     * Supprime un tuple <code>T</code> d'un fichier.
     *
     * @param row Tuple <code>T</code> à supprimer.
     * @return <code>true</code> si le tuple est supprimé sinon
     * <code>false</code>
     * @throws DAOFileException s'il y'a une erreur lors de la suppression.
     */
    protected boolean removeRow(T row) throws DAOFileException {
        if (row != null) {
            removeRowFromList(row);
            if (rowWriter.getFileLenght() > row.getRowPointer()) {
                T realRow = createRow(row.getRowPointer());
                LOGGER.log(Level.FINE, "row to delete : {0}", row);
                LOGGER.log(Level.FINE, "real row at the position in the file : {0}", realRow.getData());
                if (((UniqueIdentifiable) realRow.getData()).getUUID().compareTo(((UniqueIdentifiable) row.getData()).getUUID()) == 0) {
                    int rowPointer = (int) row.getRowPointer();
                    updateRowsPointer(rowPointer, rowSize);
                    LOGGER.log(Level.FINE, "[OK] row removed : {0}", row);
                    try {
                        return rowWriter.deleteFromFile(rowPointer, rowSize);
                    } catch (DAOFileWriterException ex) {
                        throw new DAOFileException(ex.getMessage(), ex);
                    }
                }
            } else { // le tuple n'a pas encore été écrit dans le fichier
                return true;
            }
            LOGGER.log(Level.FINE, "[NOK] row not removed : {0}", row);
        }
        return false;
    }

    /**
     * Ajoute un tuple aux listes de tuples {@code rowsSortedByPointer} et
     * {@code rowsSortedByUUID} s'il n'existe pas déjà dans les listes.
     *
     * @param row Tuple à ajouter.
     * @return {@code true} si le tuple a été ajouté sinon {@code false}.
     */
    private boolean addRowToList(AbstractRow row) throws DAOFileException {
        UUID dataID = ((UniqueIdentifiable) row.getData()).getUUID();
        if (getRow(dataID) == null) {
            RowUtilities.addRowToSortedListByPointer((List<AbstractRow>) rowsSortedByPointer, row);
            RowUtilities.addRowToSortedListByUUID((List<AbstractRow>) rowsSortedByUUID, row);
            row.addPropertyChangeListener(rowWriter);
            LOGGER.log(Level.FINE, "[OK] row added to lists : {0}", row);
            return true;
        }
        LOGGER.log(Level.FINE, "[NOK] row not added to lists : {0}", row);
        return false;
    }

    /**
     * Mets à jour les pointeurs de tuple des tuples à partir de la position
     * <code>position</code> dans la liste ordonnée suivant le pointeur de tuple
     * <code>rowsSortedByPointer</code> en enlevant l'offset <code>offset</code>
     * donné en paramètre.
     *
     * @param posisiton Position à partir de laquelle la mise à jour est
     * commencée.
     * @param offset L'offset à enlever des pointeurs de tuple.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * s'il y'a une erreur lors de la mis à jour des pointeurs de tuple
     */
    protected void updateRowsPointer(long posisiton, int offset) throws DAOFileException {
        RowUtilities.updateRowsPointer((List<AbstractRow>) rowsSortedByPointer, posisiton, offset);
    }

    /**
     * Passe l'attibut <code>dirty</code> du tuple à {@code  false} pour éviter
     * qu'il soit écrit dans le fichier s'il est dans la liste d'attente du
     * processus qui écrit dans le fichier. Supprime les écoutes et retire le
     * tuple des listes de tuples. Et enfin décremente le nombre total de tuples
     * dans le fichier.
     *
     * @param row Tuple à retirer.
     */
    protected void removeRowFromList(T row) {
        row.setDirty(false);
        row.getData().removePropertyChangeListener(row);
        row.removePropertyChangeListener(rowWriter);
        rowsSortedByPointer.remove(row);
        rowsSortedByUUID.remove(row);
        fhr.getData().decrementRowNumber();
        LOGGER.log(Level.FINE, "[OK] row removed from the list -> new rowNumber={0} \n\t", new Object[]{getRowNumber(), row});
    }

    /**
     * Renvoie un tuple <code>T</code> s'il existe dans la liste des tuples
     * {@code rowsSortedByUUID} ordonnée suivant l'identifiant {@code UUID} de
     * l'objet encapsulé sinon <code>null</code>. La recherche du tuple est
     * basée sur l'algorithme de recherche dichotomique.
     *
     * @param dataID Identifiant de l'objet encapsulé dans le tuple.
     * @return Tuple <code>T</code> s'il existe dans la liste des tuples sinon
     * <code>null</code>.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * s'il y'a une erreur lors de récupération de la position du tuple de
     * l'objet dont l'identifiant est {@code dataID}.
     */
    public T getRow(UUID dataID) throws DAOFileException {
        int position = RowUtilities.getRowPositionFromSortedListByUUID((List<AbstractRow>) rowsSortedByUUID, dataID);
        if (!rowsSortedByUUID.isEmpty()) {
            T row = rowsSortedByUUID.get(position);
            UniqueIdentifiable data = (UniqueIdentifiable) row.getData();
            if (data.getUUID().compareTo(dataID) == 0) {
                LOGGER.log(Level.FINE, "[OK] row founded in the list : {0}", row);
                return row;
            }
        }
        LOGGER.log(Level.FINE, "[NOK] row not founded in the list : id={0}", dataID);
        return null;
    }

    /**
     * Renvoie le nombre total de tuples dans un fichier.
     *
     * @return Nombre total de tuples dans un fichier.
     */
    public int getRowNumber() {
        return this.fhr.getData().getRowNumber();
    }

    /**
     * Renvoie le prochain identifiant de tuple.
     *
     * @return Prochain identifiant de tuple.
     */
    int getNextRowID() {
        return this.fhr.getData().getNextRowID();
    }

    /**
     * Renvoie le prochain pointeur de tuple.
     *
     * @return Prochain pointeur de tuple.
     */
    protected long getNextRowPointer() {
        return FileHeaderRow.FILE_HEADER_ROW_SIZE + (getRowNumber() * this.rowSize);
    }

    /**
     * Renvoie la liste des tuples <code>T</code> suivant le pointeur de tuple.
     *
     * @return Liste des tuples <code>T</code> suivant le pointeur de tuple.
     */
    protected List<T> getRowsSortedByRowPointer() {
        return rowsSortedByPointer;
    }

    /**
     * Renvoie la liste des tuples <code>T</code> triée suivant l'identifiant
     * de l'objet <code>T</code> encapsulé.
     *
     * @return Liste des tuples <code>T</code> triée suivant l'identifiant de
     * l'objet <code>T</code> encapsulé.
     */
    public List<T> getRowsSortedByUUID() {
        return rowsSortedByUUID;
    }
}
