package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.engine.persistence.UniqueIdentifiable;
import fr.univubs.inf1603.mahjong.daofile.FileHeaderRow.FileHeader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Cette classe gère un ensemble de tuples encapulant soit un index
 * {@link Index} ou un lien {@link Link}. A l'instanciation tous les tuples sont
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
     * Fichier.
     */
    private final RandomAccessFile rowFile;
    /**
     * L'en-tete du fichier.
     */
    private final FileHeader fileHeader;
    /**
     * Tuple encapsulant l'en-tete du fichier.
     */
    private final FileHeaderRow fhr;
    /**
     * Taille d'un tuple <code>T</code>
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
    protected final FileWriter rowWriter;

    /**
     * Constructeur avec le chemin d'accès du fichier et la taille d'un tuple <code>T</code>.
     *
     * @param rowFilePath Chemin d'accès du fichier.
     * @param rowSize Taille d'un tuple <code>T</code>.
     * @throws IOException s'il y'a une erreur d'entrée sortie lors de
     * l'instanciation.
     */
    protected AbstractRowManager(Path rowFilePath, int rowSize) throws IOException {
        this.rowsSortedByPointer = new ArrayList<>();
        this.rowsSortedByUUID = new ArrayList<>();
        this.rowFile = new RandomAccessFile(rowFilePath.toFile(), "rw");
        this.rowWriter = new FileWriter(rowFile.getChannel());
        this.fhr = rowWriter.loadFileHeader();
        this.fhr.addPropertyChangeListener(rowWriter);
        this.fileHeader = fhr.getData();
        this.rowSize = rowSize;
        loadAllRow();
        System.out.print(fileHeader);
    }

    /**
     * Charge l'ensemble des tuples <code>T</code> en mémoire.
     *
     * @return Nombre de tuples chargés
     * @throws IOException s'il y'a une erreur d'entrée sortie lors du
     * chargement.
     */
    private int loadAllRow() throws IOException {
        int _nbRecords = 0;
        if (rowFile.length() != 0) {
            long rowPointer = FileHeaderRow.FILE_HEADER_ROW_SIZE;
            int lenght = 100 * rowSize;
            ByteBuffer buffer;
            while ((buffer = rowWriter.read(rowPointer, lenght)) != null) {
                while (buffer.hasRemaining()) {
                    T row = readRowFromBuffer(buffer, rowPointer);
                    if (row != null) {
                        RowUtilities.addRowToSortedListByPointer((List<AbstractRow>) rowsSortedByPointer, row);
                        RowUtilities.addRowToSortedListByUUID((List<AbstractRow>) rowsSortedByUUID, row);
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
    }

    /**
     * Lis un tuple <code>T</code> à partir d'un tampon d'octet.
     *
     * @param buffer Tampon d'octet.
     * @param rowPointer Pointeur de tuple.
     * @return Le tuple <code>T</code> lu.
     */
    protected abstract T readRowFromBuffer(ByteBuffer buffer, long rowPointer);

    /**
     * Mets un tuple <code>T</code> dans la liste d'attente du processus qui
     * écrit dans le fichier et l'ajoute aux listes des tuples.
     *
     * @param newRow Tuple <code>T</code> à rajouter.
     * @throws IOException s'il y'a une erreur lors de l'ajout du tuple.
     */
    protected void addRow(T newRow) throws IOException {
        newRow.addPropertyChangeListener(rowWriter);
        rowWriter.addRowToMultipleWritingList(newRow);
        fileHeader.incrementRowNumber();
        RowUtilities.addRowToSortedListByPointer((List<AbstractRow>) rowsSortedByPointer, newRow);
        RowUtilities.addRowToSortedListByUUID((List<AbstractRow>) rowsSortedByUUID, newRow);
    }

    /**
     * Supprime un tuple <code>T</code> du fichier à l'aide de l'identifiant de 
     * l'objet encapsulé.
     *
     * @param dataID Identifiant de l'objet encapsuler dans le tuple <code>T</code>.
     * @return Le tuple <code>T</code> supprimé.
     * @throws IOException s'il y'a une erreur lors de la suppression du tuple.
     */
    protected AbstractRow removeRow(UUID dataID) throws IOException {
        T row = getRow(dataID);
        return removeRow(row) ? row : null;
    }

    /**
     * Supprime un tuple <code>T</code> du fichier.
     * 
     * @param row Tuple <code>T</code> à supprimer.
     * @return <code>true</code> si le tuplé est supprimé sinon <code>false</code>
     * @throws IOException s'il y'a une erreur lors de la suppression.
     */
    protected boolean removeRow(T row) throws IOException {
        if (row != null) {
            int pointer = (int) row.getRowPointer();
            ByteBuffer buf = rowWriter.read(pointer, rowSize);
            if (buf != null) {
                T realRow = readRowFromBuffer(buf, pointer);
//                System.out.println("todelete : " + row.getData());
//                System.out.println("real : " + realRow.getData());
                if ( ((UniqueIdentifiable)realRow.getData()).getUUID().compareTo(((UniqueIdentifiable)row.getData()).getUUID()) == 0) {
                    removeRowFromRowsList(row);
                    updateRowsPointer(pointer, rowSize);
                    return rowWriter.deleteFromFile(pointer, rowSize);
                } //else {
//                    System.err.println("** not equal");
               // }
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
     * Et enfin décremente le nombre de tuple total dans le fichier.
     * 
     * @param row Tuple à retirer.
     */
    protected void removeRowFromRowsList(T row) {
        row.setRowPointer(-1, false);
        row.getData().removePropertyChangeListener(row);
        row.removePropertyChangeListener(rowWriter);
        rowsSortedByPointer.remove(row);
        rowsSortedByUUID.remove(row);
        fileHeader.decrementRowNumber();
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
     * Rétourne le nombre total de tuple dans un fichier.
     *
     * @return Nombre total de tuple dans un fichier.
     */
    int getRowNumber() {
        return fileHeader.getRowNumber();
    }

    /**
     * Rétourne le prochain identifiant de tuple.
     *
     * @return Prochain identifiant de tuple.
     */
    int getNextRowID() {
        return fileHeader.getNextRowID();
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
     * Rétourne la liste des tuples <code>T</code>.
     *
     * @return Liste des tuples <code>T</code>.
     */
    protected List<T> getRowsSortedByRowPointer() {
        return rowsSortedByPointer;
    }
    
    protected List<T> getRowsSortedByUUID() {
        return rowsSortedByUUID;
    }
}