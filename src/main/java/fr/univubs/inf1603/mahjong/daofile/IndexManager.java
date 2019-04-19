package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import java.io.IOException;
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
 * @version 1.0.0
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
     * @throws IOException s'il y'a une erreur d'entrée sortie lors de
     * l'instanciation.
     */
    IndexManager(Path indexFilePath, int dataRowSize) throws DAOException {
        super(indexFilePath, IndexRow.INDEX_ROW_SIZE);
        System.out.println(" -> IndexManager [dataRowSize=" + dataRowSize + "]");
        this.dataRowSize = dataRowSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IndexRow createRow(ByteBuffer buffer, long rowPointer) throws DAOException {
        return new IndexRow(buffer, rowPointer);
    }

    /**
     * Ajoute un Index à la liste des index.
     *
     * @see AbstractRowManager.addRow
     *
     * @param index Index à rajouter.
     * @throws IOException s'il y'a une erreur d'entrée sortie lors de l'ajout.
     */
    void addIndex(Index index) throws IOException {
        IndexRow newIndexRow = new IndexRow(getNextRowID(), index, super.getNextRowPointer());
        super.addRow(newIndexRow);
    }

    /**
     * Supprime un tuple tuple d'index encapsulant l'index d'identifiant
     * <code>dataID</code> d'un fichier d'index. Et mets à jour les pointeurs
     * des tuples d'index et les pointeurs de données qui suivent le tuple
     * d'index à retirer.
     *
     * @param dataID Identifiant de l'index du tuple d'index à supprimer.
     * @throws IOException s'il y'a une erreur lors de la suppression.
     */
    Index removeIndex(UUID dataID) throws IOException, DAOException {
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
     * @throws IOException s'il y'a une erreur lors de la suppression.
     */
    Index removeIndex(IndexRow indexRowToDelete) throws IOException, DAOException {
        if (indexRowToDelete != null) {
            long pointer = indexRowToDelete.getRowPointer();
            long dataPointer = indexRowToDelete.getData().getPointer();
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
     * @throws IOException s'il y'a une erreur lors de la suppression.
     */
    void removeIndex(List<IndexRow> indexRowsSortedByPointerToDelete) throws IOException { 
        if (!indexRowsSortedByPointerToDelete.isEmpty()) {
            IndexRow firstIndexRow = indexRowsSortedByPointerToDelete.get(0);
            long startPointer = firstIndexRow.getRowPointer();
            long dataPointer = firstIndexRow.getData().getPointer();
            indexRowsSortedByPointerToDelete.forEach((indexRow) -> {
                super.removeRowFromRowsList(indexRow);
            });
            updateDataRowsPointer(startPointer, dataPointer, indexRowsSortedByPointerToDelete.size() * this.dataRowSize);
            int offset = indexRowsSortedByPointerToDelete.size() * super.rowSize;
            if (rowWriter.deleteFromFile((int) startPointer, offset)) {
                super.updateRowsPointer(startPointer, offset);
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
     */
    private void updateDataRowsPointer(long pointer, long dataPointer, int offset) {
        int position = RowUtilities.getRowPositionFormSortedListByPointer(getRowsSortedByRowPointer(), pointer); 
        
        System.out.println("\n++ IndexManager.updateDataRowsPointer : "
                + "\n\t pointer  : " + pointer
                + "\n\t offset   : " + offset
                + "\n\t listSize : " + getRowsSortedByRowPointer().size()
                + "\n\t position : " + position);
        
        if (!getRowsSortedByRowPointer().isEmpty()) {
            System.out.print("\t before   : ");
            getRowsSortedByRowPointer().forEach((ar) -> {
                System.out.print(ar.getData().getPointer() + ", ");
            });
            
            IndexRow indexRowAtPosition = getRowsSortedByRowPointer().get(position);
            if (indexRowAtPosition.getData().getPointer() < dataPointer) {
                position += 1;
                System.out.println("\n\t position : " + position);
            }else {
                System.out.println("");
            }
            for (int i = position; i < getRowsSortedByRowPointer().size(); i++) {
                Index nextIndex = (Index) getRowsSortedByRowPointer().get(i).getData();
                long oldPointer = nextIndex.getPointer();
                long newPointer = oldPointer - offset;
                if (newPointer >= FileHeaderRow.FILE_HEADER_ROW_SIZE) {
                    nextIndex.setPointer(newPointer);
                    LOGGER.log(Level.FINE, "{0}, olPointer : {1} -> newPointer : {2}, offset={3}",
                            new Object[]{nextIndex, oldPointer, newPointer, offset});
                }
            }
            
            System.out.print("\t after    : ");
            getRowsSortedByRowPointer().forEach((ar) -> {
                System.out.print(ar.getData().getPointer() + ", ");
            });
        }
        System.out.println("\n");
    }

    /**
     * Recherche le pointer d'un objet à l'aide de son identifiant
     * <code>UUID</code> par une recherche dichotomique. Cette méthode cherche
     * dans la liste des index s'il y'a une entrée qui correspond à
     * l'identifiant donné. Si l'index est retrouvé, elle retourne le pointeur
     * de l'objet correspondant à l'identifiant sinon elle retourne
     * <code>-1</code>.
     *
     * @param dataID Identifiant de l'objet à rechercher.
     * @return Le pointeur de l'objet correspondant à l'identifiant si l'index
     * est retrouvé sinon <code>-1</code>.
     */
//    long getDataRowPointer(UUID dataID) {
//        IndexRow indexRow = (IndexRow) super.getRow(dataID);
//        if (indexRow != null) {
//            return indexRow.getData().getPointer();
//        }
//        return -1;
//    }
}
