package fr.univubs.inf1603.mahjong.daofile;

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
 *        | | rowID = 0 |        FileHeader         | |  --> FileHeaderRow
 *        | ----------------------------------------- |
 *        | | rowID = 1 | Index=[ dataID | pointer] | |  --> IndexRow
 *        | ----------------------------------------- |
 *        | | rowID = x | Index=[ dataID | pointer] | |  --> IndexRow
 *        | ----------------------------------------- |
 *        | | rowID = x | Index=[ dataID | pointer] | |  --> IndexRow
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
    IndexManager(Path indexFilePath, int dataRowSize) throws IOException {
        super(indexFilePath, IndexRow.INDEX_ROW_SIZE);
        this.dataRowSize = dataRowSize;
        System.out.println(" -> IndexManager [dataRowSize=" + dataRowSize + "]");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IndexRow readRowFromBuffer(ByteBuffer buffer, long rowPointer) {
        return IndexRow.readFromBuffer(buffer, rowPointer);
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
    Index removeIndex(UUID dataID) throws IOException {
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
    Index removeIndex(IndexRow indexRowToDelete) throws IOException {
        if (indexRowToDelete != null) {
            if (super.removeRow(indexRowToDelete)) {
                updateDataRowsPointer(indexRowToDelete.getRowPointer(), dataRowSize);
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
     * @param indexRowsToDelete Liste des tuples d'index à supprimer.
     * @throws IOException s'il y'a une erreur lors de la suppression.
     */
    void removeIndex(List<IndexRow> indexRowsToDelete) throws IOException {
        if (!indexRowsToDelete.isEmpty()) {
            long startPinter = indexRowsToDelete.get(0).getRowPointer();
            for (IndexRow indexRow : indexRowsToDelete) {
                if (startPinter > indexRow.getRowPointer()) {
                    startPinter = indexRow.getRowPointer();
                }
                super.removeRowFromRowsList(indexRow);
            }
            updateDataRowsPointer(startPinter, indexRowsToDelete.size() * dataRowSize);
            int offset = indexRowsToDelete.size() * rowSize;
            if (rowWriter.deleteFromFile((int) startPinter, offset)) {
                super.updateRowsPointer(startPinter, offset);
            }
        }
    }

    /**
     * Mets à jours les pointeurs de données des index à partir de la position
     * <code>position</code> en enlevant l'offset <code>offset</code>.
     *
     * @param position Position du tuple d'index à partir de laquelle la mise à
     * jour est commencée.
     * @param offset L'offset à enlever aux pointeurs de données.
     */
    private void updateDataRowsPointer(long position, int offset) {
        int pos = RowUtilities.getRowPositionFormSortedListByPointer(getRowsSortedByRowPointer(), position);
        for (int i = pos; i < rowsSortedByPointer.size(); i++) {
            Index nextIndex = (Index) rowsSortedByPointer.get(i).getData();
            long newPointer = nextIndex.getPointer() - offset;
            newPointer = newPointer > FileHeaderRow.FILE_HEADER_ROW_SIZE ? newPointer : FileHeaderRow.FILE_HEADER_ROW_SIZE;
            LOGGER.log(Level.FINE, "rowID={0}, olPointer : {1} -> newPointer : {2}", new Object[]{nextIndex, nextIndex.getPointer(), newPointer});
            nextIndex.setPointer(newPointer);
        }
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
    long getDataRowPointer(UUID dataID) {
        IndexRow indexRow = (IndexRow) super.getRow(dataID);
        if (indexRow != null) {
            return indexRow.getData().getPointer();
        }
        return -1;
    }
}
