package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cette classe gère l'ensemble des index d'un objet à persister. Un index est
 * répresenté par l'identifiant {@code UUID} d'un objet et un pointeur qui
 * pointe vers le tuple contenant l'objet. Chaque index est encapsulé dans un
 * tuple. A l'instanciation de la classe l'ensemble des Index est chargé en
 * mémoire dans une liste. Cette liste est toujours ordonnée par ordre croissant
 * suivant les identifiants <code>UUID</code>. Lors d'un ajout à la liste, la
 * position du nouvel index est déterminée et il est inséré à cette posiiton.
 * Cela permet de maintenier la liste toujours trié. Ainsi un Index est
 * retrtouvé à l'aide de l'algorithme de recherche dichotomique (O(log(n))).
 *
 * <pre>
 * format d'un fichier d'index :
 *        ---------------------------------------------
 *        | ----------------------------------------- |
 *        | | idRow = 0 |        FileHeader         | |  --> FileHeaderRow
 *        | ----------------------------------------- |
 *        | | idRow = x | Index=[ dataID | pointer] | |  --> IndexRow
 *        | ----------------------------------------- |
 *        | | idRow = x | Index=[ dataID | pointer] | |  --> IndexRow
 *        | ----------------------------------------- |
 *        | | idRow = x | Index=[ dataID | pointer] | |  --> IndexRow
 *        | ----------------------------------------- |
 *        ---------------------------------------------
 * </pre>
 *
 * @author aliyou
 * @version 1.0.0
 */
public class IndexManager extends AbstractRowManager<IndexRow> {

    private final ArrayList<IndexRow> indexRowsOrderedByDataPointer;

    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(IndexManager.class.getName());

    /**
     * Constructeur avec le chemin d'accès du fichier d'index
     *
     * @param indexFilePath Chemin d'accès du fichier d'index
     * @throws IOException s'il y'a une erreur d'entrée sortie lors de
     * l'instanciation.
     */
    public IndexManager(Path indexFilePath) throws IOException {
        super(indexFilePath, IndexRow.INDEX_ROW_SIZE);
        indexRowsOrderedByDataPointer = new ArrayList<>();
        System.out.println("IndexManager : " + fileHeader);
    }

    private void add(IndexRow newRow) throws IOException {
        if (indexRowsOrderedByDataPointer.isEmpty()) {
            indexRowsOrderedByDataPointer.add(newRow);
        } else {
            int pos = getPosition(newRow.getData().getPointer());
            if(indexRowsOrderedByDataPointer.get(pos).getData().getPointer() < newRow.getData().getPointer()) {
                if (pos == indexRowsOrderedByDataPointer.size() - 1) { // si c'est le dernier element
                    indexRowsOrderedByDataPointer.add(newRow);
                } else {
                    indexRowsOrderedByDataPointer.add(pos+1, newRow);
                }
            } else {
                indexRowsOrderedByDataPointer.add(pos, newRow);
            }
        }
    }

    private int getPosition(long pointer) {
        int a = 0;
        int b = indexRowsOrderedByDataPointer.size() - 1;
        int mid = (a + b) / 2;
        while (a < b) {
            IndexRow row = indexRowsOrderedByDataPointer.get(mid);
            if(row.getData().getPointer() == pointer) {
                return mid;
            } else if(row.getData().getPointer() > pointer) {
                b = mid - 1;
            } else {
                a = mid + 1;
            }
            mid = (a + b) / 2;
        }
        return a;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IndexRow readRowFromBuffer(ByteBuffer buffer, long rowPointer) {
        return IndexRow.readFromBuffer(buffer, rowPointer);
    }

    /**
     * Ajoute un Index à la liste des index
     *
     * @param indexID Identifiant d'un objet indexé
     * @param dataPointer Pointeur de tuplé d'un objet indexé.
     * @throws IOException s'il y'a une erreur d'entrée sortie lors de l'ajout
     * d'index.
     */
    void addIndex(Index index) throws IOException {
        IndexRow newIndexRow = new IndexRow(getNextRowID(), index, super.getNextRowPointer());
        super.addRow(newIndexRow);
        add(newIndexRow); //indexRowsOrderedByDataPointer.
    }

    /**
     *
     * @param dataID
     * @return
     * @throws IOException
     */
    void removeIndex(UUID dataID, int rowSize) throws IOException {
        IndexRow deletedIndexRow = (IndexRow) super.removeRow(dataID);
        LOGGER.log(Level.INFO, "dataID = {0}, rowSize = {1}, rowNumber = {2}",
                new Object[]{dataID, rowSize, getRowNumber()});
        if (deletedIndexRow != null) {
            indexRowsOrderedByDataPointer.remove(deletedIndexRow);
            // update dataRowPointer
            long deletedIndexRowDataPointer = deletedIndexRow.getData().getPointer();
            LOGGER.log(Level.INFO, "deletedIndexRowDataPointer = {0}", deletedIndexRowDataPointer);
            int pos = getPosition(deletedIndexRowDataPointer);
            if (indexRowsOrderedByDataPointer.size() > 0) {
                if (indexRowsOrderedByDataPointer.get(pos).getData().getPointer() < deletedIndexRowDataPointer) {
                    pos++;
                }
                for (int i = pos; i < indexRowsOrderedByDataPointer.size(); i++) {
                    IndexRow indexRow = indexRowsOrderedByDataPointer.get(i);
                    System.out.println("** i = " + i);
                    long old = indexRow.getData().getPointer();
                    long newPointer = indexRow.getData().getPointer() - rowSize;
                    newPointer = newPointer > FileHeaderRow.FILE_HEADER_ROW_SIZE ? newPointer : FileHeaderRow.FILE_HEADER_ROW_SIZE;
                    indexRow.getData().setPointer(newPointer);
                    System.out.println("index update datapointer : " + indexRow + " -> old = " + old + " newPointer = " + newPointer);
                }
            }
            System.out.println("index deleted : " + deletedIndexRow);
        }
        System.out.println("size : " + indexRowsOrderedByDataPointer.size());
    }

    /**
     * Recherche le pointer d'un objet à l'aide de son identifiant par une une
     * recherche dichotomique. Cette méthode cherche dans la liste des index
     * s'il y'a une entrée qui correspond à l'identifiant donné. Si l'index est
     * retrouvé, elle retourne le pointeur de l'objet correspondant à
     * l'identifiant sinon elle retourne -1.
     *
     * @param indexID Identifiant de l'objet à rechercher
     * @return Le pointeur de l'objet correspondant à l'identifiant si l'index
     * est retrouvé sinon -1.
     */
    long getDataRowPointer(UUID indexID) {
        IndexRow indexRow = (IndexRow) super.getRow(indexID);
        if (indexRow != null) {
            return indexRow.getData().getPointer();
        }
        return -1;
    }

}