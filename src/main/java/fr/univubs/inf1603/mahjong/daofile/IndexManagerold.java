
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.FileHeaderRow.FileHeader;
import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Cette classe gère l'ensemble des index d'un objet à persister. Un index est 
 * répresenté par l'identifiant {@code UUID} d'un objet et un pointeur qui pointe
 * vers le tuple contenant l'objet. Chaque index est encapsulé dans un tuple.
 * A l'instanciation de la classe l'ensemble des Index est chargé en mémoire dans
 * une liste. Cette liste est toujours ordonnée par ordre croissant suivant les
 * identifiants {@code UUID}. Lors d'un ajout à la liste, la position du nouvel 
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
 *        | | idRow = x |          Index            | |  --> IndexRow
 *        | ----------------------------------------- |
 *        | | idRow = x |          Index            | |  --> IndexRow
 *        | ----------------------------------------- |
 *        | | idRow = x |          Index            | |  --> IndexRow
 *        | ----------------------------------------- |
 *        ---------------------------------------------
 * </pre>
 * 
 * @author aliyou
 * @version 1.0.0
 */
public class IndexManagerold {
    
    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(IndexManagerold.class.getName());

    /**
     * Fichier d'index
     */
    private final RandomAccessFile indexFile;
    /**
     * Liste des tuples d'index
     */
    private final List<IndexRow> indexRows;

    /**
     * L'en-tete du fichier d'index.
     */
    private final FileHeader fileHeader;
    /**
     * tuple encapsulant l'en-tete du fichier d'index.
     */
    private final FileHeaderRow fhr;
    /**
     * Processus qui écrit dans le fichier d'index
     */
    private final FileWriter indexWriter;

    /**
     * Constructeur avec le chemin d'accès du fichier d'index
     * @param indexFilePath Chemin d'accès du fichier d'index
     * @throws IOException s'il y'a une erreur d'entrée sortie lors de l'instanciation.
     */
    IndexManagerold(Path indexFilePath) throws IOException {
        this.indexRows = new ArrayList<>();
        this.indexFile = new RandomAccessFile(indexFilePath.toFile(), "rw");
        this.indexWriter = new FileWriter(indexFile.getChannel());
        this.fhr = FileUtilities.loadFileHeader(indexFile.getChannel());
        this.fhr.addPropertyChangeListener(indexWriter);
        this.fileHeader = fhr.getData();
        System.out.println("Index Manager : " + fileHeader);
        loadAllIndexRow();
    }

    /**
     * Charge l'ensemble des tuples d'index.
     * @return Nombre d'index chargé
     * @throws IOException s'il y'a une erreur d'entrée sortie lors du chargement.
     */
    private int loadAllIndexRow() throws IOException {
        int _nbRecords = 0;
        FileChannel fc = indexFile.getChannel();
        if (fc.size() != 0) {
            fc.position(FileHeaderRow.FILE_HEADER_ROW_SIZE);
            ByteBuffer buffer = ByteBuffer.allocate( IndexRow.INDEX_ROW_SIZE); //150 *
            long indexRecordPointer = fc.position();
            while (fc.read(buffer) > 0) {
                buffer.flip();
//                while (buffer.limit() >= IndexRow.INDEX_ROW_SIZE) {
                    IndexRow indexRow = IndexRow.readFromBuffer(buffer, indexRecordPointer);
                    if (indexRow != null) {
                        this.indexRows.add(indexRow);
                        _nbRecords++;
                        indexRecordPointer += IndexRow.INDEX_ROW_SIZE;
                    }
//                }
                buffer.clear();
//                System.out.println(indexRow);
            }
        }
        return _nbRecords;
    }

    /**
     * Ajoute un Index à la liste des index
     * @param uuid Identifiant d'un objet indexé
     * @param dataPointer Pointeur de tuplé d'un objet indexé. 
     * @throws IOException s'il y'a une erreur d'entrée sortie lors de l'ajout d'index.
     */
    void addDataIndex(UUID uuid, long dataPointer) throws IOException {
        Index index = new Index(uuid, dataPointer);
        IndexRow newIndexRow = new IndexRow(getNextIndexRowId(), index, getNextIndexRowPointer());
        if (getRowNumber() == 0) {
            indexRows.add(newIndexRow);
        } else {
            int pos = getRecordPosition(uuid);
//            System.out.println("pos : " + pos + " | getRowNumber() : " + getRowNumber());
            int compare = indexRows.get(pos).getData().getUUID().compareTo(uuid);
            if (compare == -1) { // uuid plus grand
                if (pos == getRowNumber() - 1) { // si c'est le dernier element
                    indexRows.add(newIndexRow);
                } else {
                    insertAtPosition(pos + 1, newIndexRow);
                }
            } else { // uuid plus petit
                insertAtPosition(pos, newIndexRow);
            }
        }
        indexWriter.addRowToDirtyList(newIndexRow);
        newIndexRow.addPropertyChangeListener(indexWriter);
        fileHeader.incrementRowNumber();
    }

    /**
     *
     * @param uuid
     * @return
     * @throws IOException
     */
    long removeDataIndex(UUID uuid) throws IOException {
        long dataPointer = -1;
        int pos = getRecordPosition(uuid);
        IndexRow indexRow = getIndexRow(uuid);
        if (indexRow != null) {
            System.out.println(indexRow);
            dataPointer = indexRow.getData().getPointer();
//            indexRecord.setRecordPointer(-1);
            // isWrited
            FileChannel fc = indexFile.getChannel();
            int nextIndexRowPointerPosition = (int) indexRow.getRowPointer() + IndexRow.INDEX_ROW_SIZE;
            System.out.println("nextRecordPosistion : " + nextIndexRowPointerPosition);
            System.out.println("before fc.size : " + fc.size());
            int nbRemainBytes = (int) (fc.size() - nextIndexRowPointerPosition);
            nbRemainBytes = nbRemainBytes < 0 ? 0 : nbRemainBytes;
            ByteBuffer remainingBytes = ByteBuffer.allocate(nbRemainBytes);
            fc.position(nextIndexRowPointerPosition);
            fc.read(remainingBytes);
            remainingBytes.flip();
            fc.position(indexRow.getRowPointer());
            while (remainingBytes.hasRemaining()) {
                fc.write(remainingBytes);
            }
            fc.truncate(indexRow.getRowPointer() + nbRemainBytes);
//            System.out.println("after fc.size : " + fc.size());
            // update datarecordPointer
            int offset = getDataRowPointerOffset();
            System.out.println("offset = " + offset);
            if (offset != -1) {
                int nextPos = pos + 1;
                for (int i = nextPos; i < getRowNumber(); i++) {
                    Index index = indexRows.get(pos).getData();
                    index.setPointer(index.getPointer() - offset);
                }
            }
            indexRow.removePropertyChangeListener(indexWriter);
            indexRows.remove(indexRow);
            fileHeader.decrementRowNumber();
        }
        return dataPointer;
    }

    /**
     *
     * @return
     */
    private int getDataRowPointerOffset() {
        if (getRowNumber() > 1) {
            int firstPointer = (int) indexRows.get(0).getData().getPointer();
            int secondPointer = (int) indexRows.get(1).getData().getPointer();
            int offset = secondPointer - (firstPointer + FileHeaderRow.FILE_HEADER_ROW_SIZE);
            return offset;
        }
        return -1;
    }

    /**
     * Recherche le pointer d'un objet à l'aide de son identifiant par une une
     * recherche dichotomique. Cette méthode cherche dans la liste des index
     * s'il y'a une entrée qui correspond à l'identifiant donné. Si l'index est
     * retrouvé, elle retourne le pointeur de l'objet correspondant à
     * l'identifiant sinon elle retourne -1.
     *
     * @param uuid Identifiant de l'objet à rechercher
     * @return Le pointeur de l'objet correspondant à l'identifiant si l'index
     * est retrouvé sinon -1.
     */
    long getDataRowPointer(UUID uuid) {
        IndexRow indexRow = getIndexRow(uuid);
        if (indexRow != null) {
            return indexRow.getData().getPointer();
        }
        return -1;
    }

    /**
     * 
     * @param pos
     * @param element 
     */
    private void insertAtPosition(int pos, IndexRow element) {
        element.setRowPointer(indexRows.get(pos).getRowPointer());
        for (int i = pos; i < getRowNumber(); i++) {
            IndexRow ir = indexRows.get(i);
            long oldPointer = ir.getRowPointer();
            ir.setRowPointer(oldPointer + IndexRow.INDEX_ROW_SIZE);
            indexWriter.addRowToDirtyList(ir);
        }
        indexRows.add(pos, element);
    }

    /**
     * 
     * @param uuid
     * @return 
     */
    private IndexRow getIndexRow(UUID uuid) {
        int pos = getRecordPosition(uuid);
        if (getRowNumber() > 0) {
            IndexRow indexRow = indexRows.get(pos);
            if (indexRow.getData().getUUID().compareTo(uuid) == 0) {
                return indexRow;
            }
        }
        return null;
    }

    /**
     * 
     * @param uuid
     * @return 
     */
    private int getRecordPosition(UUID uuid) {
        int a = 0;
        int b = getRowNumber() - 1;
        int mid = (a + b) / 2;
        while (a < b) {
            IndexRow indexRow = indexRows.get(mid);
            switch (indexRow.getData().getUUID().compareTo(uuid)) {
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
     * 
     * @return 
     */
    int getRowNumber() {
        return indexRows.size();
    }

    /**
     * 
     * @return 
     */
    int getNextIndexRowId() {
        fileHeader.updateRowLastId();
        return fileHeader.getRowLastId();
    }

    /**
     * 
     * @return 
     */
    long getNextIndexRowPointer() {
        return FileHeaderRow.FILE_HEADER_ROW_SIZE + (getRowNumber() * IndexRow.INDEX_ROW_SIZE);
    }

    /**
     * 
     * @return 
     */
    List<IndexRow> getIndexRows() {
        return indexRows;
    }
}
