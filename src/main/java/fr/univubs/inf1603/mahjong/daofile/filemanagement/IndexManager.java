package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cette classe gère l'ensemble des index {@link Index} liés à un objet à
 * persister. Un index est répresenté par l'identifiant <code>UUID</code> d'un
 * objet et un pointeur qui pointe vers le tuple contenant l'objet. Chaque index
 * est encapsulé dans un tuple d'index <code>IndexRow</code>.
 *
 * <pre>
 * format d'un fichier d'index :
 *        ---------------------------------------------
 *        | ----------------------------------------- |
 *        | | rowID = 0 |        FileHeader         | |  --{@literal >} FileHeaderRow
 *        | ----------------------------------------- |
 *        | | rowID = 1 | Index=[ dataID | pointer] | |  --{@literal >} IndexRow
 *        | ----------------------------------------- |
 *        | | rowID = x | Index=[ dataID | pointer] | |  --{@literal >} IndexRow
 *        | ----------------------------------------- |
 *        | | rowID = x | Index=[ dataID | pointer] | |  --{@literal >} IndexRow
 *        | ----------------------------------------- |
 *        ---------------------------------------------
 * </pre>
 *
 * @see AbstractRowManager
 * @author aliyou, nesrine
 * @version 1.2.5
 */
public class IndexManager extends AbstractRowManager<IndexRow> {

    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(IndexManager.class.getName());

    /**
     * Taille d'un tuple de données.
     */
    private final int dataRowSize;

    /**
     * Constructeur avec le chemin d'accès du fichier d'index.
     *
     * @param indexFilePath Chemin d'accès du fichier d'index
     * @param dataRowSize Taille d'un tuple de données.
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation.
     */
    public IndexManager(Path indexFilePath, int dataRowSize) throws DAOFileException {
        super(indexFilePath, IndexRow.INDEX_ROW_SIZE);
//        System.out.println(" -> IndexManager [dataRowSize=" + dataRowSize + "]");
        this.dataRowSize = dataRowSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IndexRow createRow(ByteBuffer buffer, long rowPointer) throws DAOFileException {
        return new IndexRow(buffer, rowPointer);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected IndexRow createRow(long rowPointer) throws DAOFileException {
        return new IndexRow(rowWriter, rowPointer);
    }

    /**
     * Ajoute un Index à la liste des index.
     *
     * @param index Index à rajouter.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException s'il y'a une erreur lors de la création du tuple d'index.
     */
    public void addIndex(Index index) throws DAOFileException {
        IndexRow newIndexRow = new IndexRow(getNextRowID(), index, super.getNextRowPointer());
        if(!super.addRow(newIndexRow)) {
            LOGGER.log(Level.SEVERE, "index not added : {0}", index);
        }
    }

    /**
     * Supprime un tuple tuple d'index encapsulant l'index d'identifiant
     * <code>dataID</code> d'un fichier d'index. Et mets à jour les pointeurs
     * des tuples d'index et les pointeurs de données qui suivent le tuple
     * d'index à retirer.
     *
     * @param dataID Identifiant de l'index du tuple d'index à supprimer.
     * @return L'index supprimé.
     * @throws DAOFileException s'il y'a une erreur lors de la suppression.
     */
    public Index removeIndex(UUID dataID) throws DAOFileException {
        IndexRow indexRowToDelete = super.getRow(dataID);
        LOGGER.log(Level.FINE, "[OK] index deleted -> dataID = {0}, rowSize = {1}, rowNumber = {2}",
                new Object[]{indexRowToDelete.getData().getUUID(), rowSize, getRowNumber()});
        return removeIndex(indexRowToDelete);
    }

    /**
     * Supprime un tuple tuple d'index <code>indexRowToDelete</code> d'un
     * fichier d'index. Et mets à jour les pointeurs des tuples d'index et les
     * pointeurs de données qui suivent le tuple d'index à retirer.
     *
     * @param indexRowToDelete Tuple d'index à supprimer.
     * @return L'index supprimé
     * @throws DAOFileException s'il y'a une erreur lors de la suppression.
     */
    public Index removeIndex(IndexRow indexRowToDelete) throws DAOFileException {
        if (indexRowToDelete != null) {
            long pointer = indexRowToDelete.getRowPointer();
            long dataPointer = indexRowToDelete.getData().getDataPointer();
            if (super.removeRow(indexRowToDelete)) { // pointer = -1 after delete
                updateDataRowsPointer(pointer, dataPointer, dataRowSize);
                LOGGER.log(Level.FINE, "[OK] index deleted -> dataID = {0}, rowSize = {1}, rowNumber = {2}",
                        new Object[]{indexRowToDelete.getData().getUUID(), rowSize, getRowNumber()});
                return indexRowToDelete.getData();
            }
        }
        return null;
    }

    /**
     * Supprime un ensemble de tuple d'index <code>indexRowsToDelete</code> d'un
     * fichier d'index. Et mets à jour les pointeurs des tuples d'index et les
     * pointeurs de données qui suivent le dernier tuple d'index de la liste.
     *
     * @param indexRowsSortedByPointerToDelete Liste des tuples d'index à supprimer.
     * @throws DAOFileException s'il y'a une erreur lors de la suppression.
     */
    public void removeIndex(List<IndexRow> indexRowsSortedByPointerToDelete) throws DAOFileException {
        if (!indexRowsSortedByPointerToDelete.isEmpty()) {
            IndexRow firstIndexRow = indexRowsSortedByPointerToDelete.get(0);
            long startPointer = firstIndexRow.getRowPointer();
            long dataPointer = firstIndexRow.getData().getDataPointer();
            indexRowsSortedByPointerToDelete.forEach((indexRow) -> {
                super.removeRowFromList(indexRow);
            });
            updateDataRowsPointer(startPointer, dataPointer, indexRowsSortedByPointerToDelete.size() * this.dataRowSize);
            int offset = indexRowsSortedByPointerToDelete.size() * super.rowSize;
            try {
                if (rowWriter.deleteFromFile((int) startPointer, offset)) {
                    super.updateRowsPointer(startPointer, offset);
                }
            } catch (DAOFileWriterException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Mets à jours les pointeurs de données des index à partir de la position
     * <code>position</code> en enlevant l'offset <code>offset</code>.
     *
     * @param pointer Position du tuple d'index à partir de laquelle la mise à
     * jour est commencée.
     * @param offset L'offset à enlever aux pointeurs de données.
     * @throws DAOFileException s'il y'a une erreur lors de la mis à jour des pointeurs des tuples de données.
     */
    private void updateDataRowsPointer(long pointer, long dataPointer, int offset) throws DAOFileException {
        int position = RowUtilities.getRowPositionFormSortedListByPointer(getRowsSortedByRowPointer(), pointer); 
        
        LOGGER.log(Level.FINE, "++ IndexManager.updateDataRowsPointer : " 
                + "\n\t pointer  : {0}"
                + "\n\t offset   : {1}" 
                + "\n\t listSize : {2}" 
                + "\n\t position : {3}", 
                new Object[]{pointer, offset, getRowsSortedByRowPointer().size(), position});
        if (!getRowsSortedByRowPointer().isEmpty()) {
            LOGGER.log(Level.FINE, print("\t before   : ", getRowsSortedByRowPointer()));
            
            IndexRow indexRowAtPosition = getRowsSortedByRowPointer().get(position);
            if (indexRowAtPosition.getData().getDataPointer() < dataPointer) {
                position += 1;
//                System.out.println("\n\t position : " + position);
            }
            for (int i = position; i < getRowsSortedByRowPointer().size(); i++) {
                Index nextIndex = (Index) getRowsSortedByRowPointer().get(i).getData();
                long oldPointer = nextIndex.getDataPointer();
                long newPointer = oldPointer - offset;
                if (newPointer >= FileHeaderRow.FILE_HEADER_ROW_SIZE) {
                    nextIndex.setDataPointer(newPointer);
                    LOGGER.log(Level.FINE, "{0}, olPointer : {1} -> newPointer : {2}, offset={3}",
                            new Object[]{nextIndex, oldPointer, newPointer, offset});
                }
            }
            LOGGER.log(Level.FINE, print("\t after    : ", getRowsSortedByRowPointer()));
        }
    }
    
    /**
     * just for log
     * @param name
     * @param list
     * @return 
     */
    private String print(String name, List<IndexRow> list) {
        StringBuilder result = new StringBuilder();
        result.append(name);
        list.forEach((ar) -> {
                result.append(ar.getData().getDataPointer()).append(", ");
        });
        result.append("\n");
        return result.toString();
    }
}
