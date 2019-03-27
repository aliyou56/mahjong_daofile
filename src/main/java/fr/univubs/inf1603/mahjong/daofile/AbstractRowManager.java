package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.FileHeaderRow.FileHeader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Cette classe gère un ensemble de tuple encapulant soit un index ou un lien. A
 * la création l'ensemble des tuples sont chargés en mémoire dans une liste.
 * Cette liste est toujours ordonnée par ordre croissant suivant les
 * identifiants <code>UUID</code> des objets encapsulés dans les tuples. Lors
 * d'un ajout à la liste, la position du nouvel élement est déterminée et il est
 * inséré à cette posiiton. Cela permet de maintenier la liste toujours trié.
 * Ainsi un élement est retrtouvé à l'aide de l'algorithme de recherche
 * dichotomique (O(log(n))).
 *
 * @author aliyou
 * @version 1.0.0
 * @param <T>
 */
public abstract class AbstractRowManager<T extends AbstractRow> {

    /**
     * Fichier.
     */
    protected final RandomAccessFile rowfile;
    /**
     * Liste des tuples T.
     */
    protected final List<T> rows;

    /**
     * L'en-tete du fichier.
     */
    protected final FileHeader fileHeader;
    /**
     * Tuple encapsulant l'en-tete du fichier.
     */
    protected final FileHeaderRow fhr;
    /**
     * Processus qui écrit dans le fichier.
     */
    protected final FileWriter rowWriter;
    /**
     * Taille d'un tuple T
     */
    private final int rowSize;

    /**
     * Constructeur avec le chemin d'accès du fichier et la taille d'un tuple T.
     *
     * @param rowFilePath Chemin d'accès du fichier.
     * @param rowSize Taille d'un tuple T.
     * @throws IOException s'il y'a une erreur d'entrée sortie lors de
     * l'instanciation.
     */
    protected AbstractRowManager(Path rowFilePath, int rowSize) throws IOException {
        this.rows = new ArrayList<>();
        this.rowfile = new RandomAccessFile(rowFilePath.toFile(), "rw");
        this.rowWriter = new FileWriter(rowfile.getChannel());
        this.fhr = FileUtilities.loadFileHeader(rowfile.getChannel());
        this.fhr.addPropertyChangeListener(rowWriter);
        this.fileHeader = fhr.getData();
        this.rowSize = rowSize;
        loadAllRow();
    }

    /**
     * Charge l'ensemble des tuples T en mémoire.
     *
     * @return Nombre de tuples chargés
     * @throws IOException s'il y'a une erreur d'entrée sortie lors du chargement.
     */
    private int loadAllRow() throws IOException {
        int _nbRecords = 0;
        FileChannel fc = rowfile.getChannel();
        if (fc.size() != 0) {
            fc.position(FileHeaderRow.FILE_HEADER_ROW_SIZE);
            ByteBuffer buffer = ByteBuffer.allocate(rowSize);
            long rowPointer = fc.position();
            while (fc.read(buffer) > 0) {
                buffer.flip();
                T row = readRow(buffer, rowPointer);
                if (row != null) {
                    this.rows.add(row);
                    _nbRecords++;
                    rowPointer += this.rowSize;
                }
                buffer.clear();
//                System.out.println(row);
            }
        }
        return _nbRecords;
    }

    /**
     * Lis un tuple T à partir d'un tampon d'octet.
     * 
     * @param buffer Tampon d'octet.
     * @param rowPointer Pointeur de tuple.
     * @return Le tuple T lu.
     */
    protected abstract T readRow(ByteBuffer buffer, long rowPointer);

    /**
     * Ajoute un tuple T à la liste des tuples.
     *
     * @param newRow Tuple T à rajouter.
     * @throws IOException s'il y'a une erreur  lors de l'ajout du tuple.
     */
    void addRow(T newRow) throws IOException {
        if (getRowNumber() == 0) {
            rows.add(newRow);
        } else {
            UUID newRowID = getObjectUUID(newRow.getData());
            int pos = getRowPosition(newRowID);
            Object rowObject = rows.get(pos).getData();
            int compare = getObjectUUID(rowObject).compareTo(newRowID);
            if (compare == -1) { // uuid plus grand
                if (pos == getRowNumber() - 1) { // si c'est le dernier element
                    rows.add(newRow);
                } else {
                    insertAtPosition(pos + 1, newRow);
                }
            } else { // uuid plus petit
                insertAtPosition(pos, newRow);
            }
        }
        rowWriter.addRowToDirtyList(newRow);
        newRow.addPropertyChangeListener(rowWriter);
        fileHeader.incrementRowNumber();
    }

    /**
     * Supprime un tuple T du fichier.
     * 
     * @param dataID Identifiant de l'objet encapsuler dans le tuple T.
     * @return Le tuple T supprimé.
     * @throws IOException  s'il y'a une erreur  lors de la suppression du tuple.
     */
    AbstractRow removeRow(UUID dataID) throws IOException {
        T row = getRow(dataID);
        if (row != null) {
            System.out.println("todelete : " + row);
//            dataPointer = row.getData().getPointer();
////            indexRecord.setRecordPointer(-1);
//            // isWrited
            FileChannel fc = rowfile.getChannel();
            int nextRowPointerPosition = (int) row.getRowPointer() + this.rowSize;
//            System.out.println("nextRowPointerPosition : " + nextRowPointerPosition);
//            System.out.println("before fc.size : " + fc.size());
            int nbRemainBytes = (int) (fc.size() - nextRowPointerPosition);
            nbRemainBytes = nbRemainBytes < 0 ? 0 : nbRemainBytes;
            ByteBuffer remainingBytes = ByteBuffer.allocate(nbRemainBytes);
            fc.position(nextRowPointerPosition);
            fc.read(remainingBytes);
            remainingBytes.flip();
            fc.position(row.getRowPointer());
            while (remainingBytes.hasRemaining()) {
                fc.write(remainingBytes);
            }
            fc.truncate(row.getRowPointer() + nbRemainBytes);
////            System.out.println("after fc.size : " + fc.size());
            row.removePropertyChangeListener(rowWriter);
            rows.remove(row);
            fileHeader.decrementRowNumber();
        }
        return row;
    }

//    protected void addData(T data) {
//        
//    }
//    
//    protected void removeData(UUID dataID) {
//        
//    }
    
    /**
     * Insère un tuple T à la position pos et mets à jour le pointeur de tuple des
     * tuples suivants la position pos.
     * 
     * @param pos Position
     * @param newRow Tuple T à inserer.
     */
    private void insertAtPosition(int pos, T newRow) {
        newRow.setRowPointer(rows.get(pos).getRowPointer());
        for (int i = pos; i < getRowNumber(); i++) {
            AbstractRow row = rows.get(i);
            long oldPointer = row.getRowPointer();
            row.setRowPointer(oldPointer + IndexRow.INDEX_ROW_SIZE);
            rowWriter.addRowToDirtyList(row);
        }
        rows.add(pos, newRow);
    }

    /**
     * Rétourne un tuple T s'il existe dans la liste des tuples sinon <code>null</code>.
     * 
     * @param dataID Identifiant de l'objet encapsuler dans le tuple.
     * @return Tuple T s'il existe dans la liste des tuples sinon <code>null</code>.
     */
    T getRow(UUID dataID) {
        int pos = getRowPosition(dataID);
        if (getRowNumber() > 0) {
            T row = rows.get(pos);
            if (getObjectUUID(row.getData()).compareTo(dataID) == 0) {
                return row;
            }
        }
        return null;
    }

    /**
     * Rétorune la position d'un tuple T encapsulant l'objet d'identifiant <code>dataID</code>
     * avec l'algorithme de recherche dichotomique.
     * 
     * @param dataID Identifiant de l'objet encapsuler dans le tuple T.
     * @return Position d'un tuple T dans la liste des tuples.
     */
    protected int getRowPosition(UUID dataID) {
        int a = 0;
        int b = getRowNumber() - 1;
        int mid = (a + b) / 2;
        while (a < b) {
            AbstractRow row = rows.get(mid);
            switch (getObjectUUID(row.getData()).compareTo(dataID)) {
                case 0:
                    return mid;
                case 1:
                    b = mid - 1;
                    break;
                default:
                    a = mid + 1;
                    break;
            }
            mid = (a + b) / 2;
        }
        return a;
    }

    /**
     * Rétourne l'identifiant <code>UUID</code> d'un objet avec la reflexivité.
     * Elle recherche la méthode <code>getUUID()</code> dans l'objet en question.
     * Si elle existe, l'identifiant est rétourné sinon une erreur.
     * 
     * @param object Objet
     * @return Identifiant d'un objet
     */
    private static UUID getObjectUUID(Object object) {
        try {
            Class<?> cl = Class.forName(object.getClass().getName());
            Method method = cl.getDeclaredMethod("getUUID");
            return (UUID) method.invoke(object);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace(System.out);
        }
        return null;
    }

    /**
     * Rétourne le nombre total de tuple dans le fichier.
     * 
     * @return Nombre total de tuple dans le fichier.
     */
    int getRowNumber() {
        return rows.size();
    }

    /**
     * Rétourne le prochain identifiant de tuple.
     * 
     * @return Prochain identifiant de tuple.
     */
    int getNextRowID() {
        fileHeader.updateRowLastId();
        return fileHeader.getRowLastId();
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
     * Rétourne la liste des tuples T.
     * 
     * @return Liste des tuples T.
     */
    protected List<T> getRows() {
        return rows;
    }
}
