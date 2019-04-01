
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Cette classe gère l'ensemble des index d'un objet à persister. Un index est 
 * répresenté par l'identifiant {@code UUID} d'un objet et un pointeur qui pointe
 * vers le tuple contenant l'objet. Chaque index est encapsulé dans un tuple.
 * A l'instanciation de la classe l'ensemble des Index est chargé en mémoire dans
 * une liste. Cette liste est toujours ordonnée par ordre croissant suivant les
 * identifiants <code>UUID</code>. Lors d'un ajout à la liste, la position du nouvel 
 * index est déterminée et il est inséré à cette posiiton. Cela permet de 
 * maintenier la liste toujours trié. Ainsi un Index est retrtouvé à l'aide de 
 * l'algorithme de recherche dichotomique (O(log(n))).
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

    /**
     * Logging
     */
//    private final static Logger LOGGER = Logger.getLogger(IndexManager.class.getName());

    /**
     * Constructeur avec le chemin d'accès du fichier d'index
     * @param indexFilePath Chemin d'accès du fichier d'index
     * @throws IOException s'il y'a une erreur d'entrée sortie lors de l'instanciation.
     */
    public IndexManager(Path indexFilePath) throws IOException {
        super(indexFilePath, IndexRow.INDEX_ROW_SIZE);
        System.out.println("IndexManager : " + fileHeader); 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IndexRow readRow(ByteBuffer buffer, long rowPointer) {
        return IndexRow.readFromBuffer(buffer, rowPointer);
    }
    
    /**
     * Ajoute un Index à la liste des index
     * @param indexID Identifiant d'un objet indexé
     * @param dataPointer Pointeur de tuplé d'un objet indexé. 
     * @throws IOException s'il y'a une erreur d'entrée sortie lors de l'ajout d'index.
     */
    void addIndex(Index index) throws IOException {
        IndexRow newIndexRow = new IndexRow(getNextRowID(), index, super.getNextRowPointer());
        super.addRow(newIndexRow);
    }
    
    /**
     *
     * @param dataID
     * @return
     * @throws IOException 
     */
    void removeIndex(UUID dataID, int rowSize) throws IOException { 
        IndexRow indexRow = (IndexRow) super.removeRow(dataID);
        if (indexRow != null) {
            // update dataRowPointer
            int pos = getRowPosition(dataID);
            System.out.println("rowSize = " + rowSize);
            System.out.println("pos = " + pos);
            System.out.println("getRowNumber() = " + getRowNumber());
            System.out.println("index deletd pointer = " + indexRow.getData().getPointer());
            if (getRowNumber() != 0) {
                System.out.println("pos pointer = " + rows.get(pos).getData().getPointer());
                System.out.println("rows.get(pos).getData().getPointer() ? indexRow.getData().getPointer() : " 
                        + rows.get(pos).getData().getPointer()+ " ? " +indexRow.getData().getPointer());
                if (rows.get(pos).getData().getPointer() < indexRow.getData().getPointer()) {
                    pos++;
                }
                System.out.println("pos = " + pos);
            }
            for (int i = pos; i < getRowNumber(); i++) {
                Index index = (Index) rows.get(i).getData();
                index.setPointer(index.getPointer() - rowSize);
            }
            System.out.println("index deleted : " + indexRow);
        }
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
