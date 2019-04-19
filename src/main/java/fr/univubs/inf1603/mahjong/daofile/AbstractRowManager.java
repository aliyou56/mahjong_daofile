package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import fr.univubs.inf1603.mahjong.engine.persistence.UniqueIdentifiable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cette classe gère un ensemble de tuples encapulant soit un index
 * {@link IndexRow.Index} ou un lien {@link LinkRow.Link}. A l'instanciation tous les tuples sont
 * chargés en mémoire dans 2 listes triées (la première suivant le pointeur de
 * tuple et la seconde suivant l'identifiant <code>UUID</code> des objets
 * <code>T</code> encapsulés dans les tuples). Lors de l'ajout d'un nouvel
 * element, sa position dans la seconde liste est déterminée et il est inséré à
 * cette posiiton. Cela permet de 4 maintenir en permanence cette liste triée.
 * Ainsi un élement est retrtouvé à l'aide de l'algorithme de recherche
 * dichotomique (O(log(n))). Après la suppression d'un tuple, le pointeur des
 * tuples qui suivent est mis à jour.
 *
 * @author aliyou, nesrine
 * @version 1.0.0
 * @param <T> Tuple
 */
public abstract class AbstractRowManager<T extends AbstractRow> {

    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(AbstractRowManager.class.getName());
    /**
     * Fichier.
     */
    private final RandomAccessFile rowFile;
    /**
     * Tuple encapsulant l'en-tete d'un fichier.
     */
    private final FileHeaderRow fhr;
    /**
     * Taille d'un tuple <code>T</code>.
     */
    protected final int rowSize;
    /**
     * Liste des tuples <code>T</code> triée suivant le pointeur de tuple.
     */
    protected final List<T> rowsSortedByPointer;
    /**
     * Liste des tuples <code>T</code> triée suivant l'identifiant de l'objet <code>T</code> encapsulé.
     */
    private final List<T> rowsSortedByUUID;
    /**
     * Processus qui écrit dans le fichier.
     */
    protected final DAOFileWriter rowWriter;

    /**
     * Constructeur avec le chemin d'accès d'un fichier <code>rowFilePath</code>
     * et la taille d'un tuple <code>T</code>.
     *
     * @param rowFilePath Chemin d'accès d'un fichier.
     * @param rowSize Taille d'un tuple <code>T</code>.
     * @throws DAOException s'il y'a une erreur lors de l'instanciation.
     */
    protected AbstractRowManager(Path rowFilePath, int rowSize) throws DAOException {
        try {
            this.rowsSortedByPointer = new ArrayList<>();
            this.rowsSortedByUUID = new ArrayList<>();
            this.rowFile = new RandomAccessFile(rowFilePath.toFile(), "rw");
            this.rowWriter = new DAOFileWriter(rowFile.getChannel());
            this.fhr = rowWriter.loadFileHeader();
            this.rowSize = rowSize;
            loadAllRow();
            System.out.print(fhr.getData());
        } catch (IOException ex) {
            throw new DAOException("IO error : " + ex.getMessage());
        }
    }

    /**
     * 
     * @param dataList
     * @return 
     */
    protected List<T> getRowList(List<? extends Persistable> dataList) {
//        FileDAOUtilities.checkNotNull("dataListToDelete", dataListToDelete);
        if (!dataList.isEmpty()) {
            List<T> rowList = new ArrayList<>();
            dataList.forEach(data -> {
                AbstractRow row = getRow(data.getUUID());
                RowUtilities.addRowToSortedListByPointer((List<AbstractRow>) rowList, row);
            });
            return rowList;
        }
        return null;
    }
    
    protected List<T> getSingleRemoveList(List<T> rowsSorteByPointer) {
        List<T> singleRemoveList = new ArrayList<>();
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
        return singleRemoveList;
    }
    
    /**
     * Charge l'ensemble des tuples <code>T</code> en mémoire.
     *
     * @return Nombre de tuples chargés
     * @throws IOException s'il y'a une erreur d'entrée sortie lors du
     * chargement.
     */
    private int loadAllRow() throws DAOException {
        try {
            int _nbRecords = 0;
            if (rowFile.length() != 0) {
                long rowPointer = FileHeaderRow.FILE_HEADER_ROW_SIZE;
                int lenght = 100 * rowSize;
                ByteBuffer buffer;
                while ((buffer = rowWriter.read(rowPointer, lenght)) != null) {
                    while (buffer.hasRemaining()) {
                        T row = createRow(buffer, rowPointer);
                        if (row != null) {
                            addRowToList(row);
                            _nbRecords++;
                            rowPointer += this.rowSize;
                        }
//                    System.out.println(row);
                    }
                }
//            for(int i=0; i<getRowNumber(); i++) {
//                System.out.println("id : " +rowsSortedByPointer.get(i).geRowID() +", " + rowsSortedByUUID.get(i).geRowID()
//                + " | uuid : "+((UniqueIdentifiable)rowsSortedByPointer.get(i).getData()).getUUID()
//                + ",  " + ((UniqueIdentifiable)rowsSortedByUUID.get(i).getData()).getUUID());
//            }
            }
            return _nbRecords;
        } catch (IOException ex) {
            throw new DAOException("IO error : " + ex.getMessage());
        }
    }

    private void addRowToList(AbstractRow row) {
        RowUtilities.addRowToSortedListByPointer((List<AbstractRow>) rowsSortedByPointer, row);
        RowUtilities.addRowToSortedListByUUID((List<AbstractRow>) rowsSortedByUUID, row);
        row.addPropertyChangeListener(rowWriter);
    }
    
    /**
     * Lis un tuple <code>T</code> à partir d'un tampon d'octet.
     *
     * @param buffer Tampon d'octet.
     * @param rowPointer Pointeur de tuple.
     * @return Le tuple <code>T</code> lu.
     * @throws DAOException S'il y'a une erreur lors de la création d'un tuple <code>T</code>.
     */
    protected abstract T createRow(ByteBuffer buffer, long rowPointer) throws DAOException;

    /**
     * Mets un tuple <code>T</code> dans la liste d'attente du processus qui
     * écrit dans le fichier et l'ajoute aux listes des tuples.
     *
     * @param newRow Tuple <code>T</code> à rajouter.
     * @throws IOException s'il y'a une erreur lors de l'ajout du tuple.
     */
    protected void addRow(T newRow) throws IOException {
        addRowToList(newRow);
        rowWriter.addRowToMultipleWritingList(newRow);
        fhr.getData().incrementRowNumber();
    }

    /**
     * Supprime un tuple <code>T</code> d'un fichier à l'aide de l'identifiant de 
     * l'objet encapsulé.
     *
     * @param dataID Identifiant de l'objet encapsulé dans un tuple <code>T</code>.
     * @return Le tuple <code>T</code> supprimé.
     * @throws DAOException s'il y'a une erreur lors de la suppression du tuple.
     */
    protected AbstractRow removeRow(UUID dataID) throws  DAOException {
        T row = getRow(dataID);
        return removeRow(row) ? row : null;
    }

    /**
     * Supprime un tuple <code>T</code> d'un fichier.
     * 
     * @param row Tuple <code>T</code> à supprimer.
     * @return <code>true</code> si le tuple est supprimé sinon <code>false</code>
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    protected boolean removeRow(T row) throws DAOException {
        if (row != null) {
            try {
                int pointer = (int) row.getRowPointer();
                ByteBuffer buf = rowWriter.read(pointer, rowSize);
                if (buf != null) {
                    T realRow = createRow(buf, pointer);
//                System.out.println("todelete : " + row.getData());
//                System.out.println("real : " + realRow.getData());
                    if (((UniqueIdentifiable) realRow.getData()).getUUID().compareTo(((UniqueIdentifiable) row.getData()).getUUID()) == 0) {
                        removeRowFromRowsList(row);
                        updateRowsPointer(pointer, rowSize);
                        return rowWriter.deleteFromFile(pointer, rowSize);
                    }
                }
            } catch (IOException ex) {
                throw new DAOException("IO Error : " + ex.getMessage());
            }
        }
        return false;
    }

    /**
     * Mets à jour les pointeurs de tuple des tuples à partir de la position <code>position</code>
     * dans la liste ordonnée suivant le pointeur de tuple <code>rowsSortedByPointer</code> en enlevant
     * l'offset <code>offset</code> donné en paramètre.
     * 
     * @param posisiton Position à partir de laquelle la mise à jour est commencée. 
     * @param offset L'offset à enlever des pointeurs de tuple. 
     */
    protected void updateRowsPointer(long posisiton, int offset) {
        RowUtilities.updateRowsPointer((List<AbstractRow>) rowsSortedByPointer, posisiton, offset);
    }
    
    /**
     * Mets le pointeur d'un tuple <code>T</code> à <code>-1</code> pour éviter 
     * qu'il soit écrit dans le fichier s'il est dans la liste d'attente du processus
     * qui écrit dans le fichier.
     * Supprime les écoutes et retire le tuple des listes de tuples.
     * Et enfin décremente le nombre total de tuples dans le fichier.
     * 
     * @param row Tuple à retirer.
     */
    protected void removeRowFromRowsList(T row) {
        row.setRowPointer(-1, false);
        row.getData().removePropertyChangeListener(row);
        row.removePropertyChangeListener(rowWriter);
        rowsSortedByPointer.remove(row);
        rowsSortedByUUID.remove(row);
//        fileHeader.decrementRowNumber();
        fhr.getData().decrementRowNumber();
    }

    /**
     * Rétourne un tuple <code>T</code> s'il existe dans la liste des tuples sinon
     * <code>null</code>.
     *
     * @param dataID Identifiant de l'objet encapsulé dans le tuple.
     * @return Tuple <code>T</code> s'il existe dans la liste des tuples sinon
     * <code>null</code>.
     */
    T getRow(UUID dataID) {
        int position = RowUtilities.getRowPositionFromSortedListByUUID((List<AbstractRow>) rowsSortedByUUID, dataID);
        if (rowsSortedByUUID.size() > 0) { 
            T row = rowsSortedByUUID.get(position);
            UniqueIdentifiable data = (UniqueIdentifiable) row.getData();
            if (data.getUUID().compareTo(dataID) == 0) {
                return row;
            }
        }
        return null;
    }

    /**
     * Rétourne le nombre total de tuples dans un fichier.
     *
     * @return Nombre total de tuples dans un fichier.
     */
    int getRowNumber() {
        return this.fhr.getData().getRowNumber();
    }

    /**
     * Rétourne le prochain identifiant de tuple.
     *
     * @return Prochain identifiant de tuple.
     */
    int getNextRowID() {
        return this.fhr.getData().getNextRowID();
    }

    /**
     * Rétourne le prochain pointeur de tuple.
     *
     * @return Prochain pointeur de tuple.
     */
    protected long getNextRowPointer() {
        return FileHeaderRow.FILE_HEADER_ROW_SIZE + (getRowNumber() * this.rowSize);
    }

    /**
     * Rétourne la liste des tuples <code>T</code> suivant le pointeur 
     * de tuple.
     *
     * @return Liste des tuples <code>T</code> suivant le pointeur de tuple.
     */
    protected List<T> getRowsSortedByRowPointer() {
        return rowsSortedByPointer;
    }
    
    /**
     * Rétourne la liste des tuples <code>T</code> triée suivant l'identifiant 
     * de l'objet <code>T</code> encapsulé.
     *
     * @return Liste des tuples <code>T</code> triée suivant l'identifiant 
     * de l'objet <code>T</code> encapsulé.
     */
    protected List<T> getRowsSortedByUUID() {
        return rowsSortedByUUID;
    }
}