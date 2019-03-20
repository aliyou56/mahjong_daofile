
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
 *
 * @author aliyou
 * @version 1.0.0
 */
public class IndexManager {
    
    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(IndexManager.class.getName());

    /**
     * Fichier d'index
     */
    private final RandomAccessFile indexFile;
    /**
     * Liste des tuples d'index
     */
    private final List<IndexRow> indexRows;

    /**
     * 
     */
    private final FileHeader fileHeader;
    /**
     * 
     */
    private final FileHeaderRow fhr;

    private final FileWriter indexWriter;

    /**
     * Constructeur avec le chemin du fichier d'index
     * @param indexFilePath Chemin du fichier d'index
     * @throws IOException 
     */
    IndexManager(Path indexFilePath) throws IOException {
        this.indexRows = new ArrayList<>();
        this.indexFile = new RandomAccessFile(indexFilePath.toFile(), "rw");
        this.indexWriter = new FileWriter(indexFile.getChannel());
        this.fhr = FileUtilities.loadFileHeader(indexFile.getChannel());
        this.fhr.addPropertyChangeListener(indexWriter);
        this.fileHeader = fhr.getData();
        System.out.println(fileHeader);
        loadAllIndexRow();
    }

    /**
     * Charge l'ensemble des tuples d'index.
     * @return
     * @throws IOException 
     */
    private int loadAllIndexRow() throws IOException {
        int _nbRecords = 0;
        FileChannel fc = indexFile.getChannel();
        if (fc.size() != 0) {
            fc.position(FileHeaderRow.FILE_HEADER_ROW_SIZE);
            ByteBuffer buffer = ByteBuffer.allocate(IndexRow.INDEX_ROW_SIZE);
            long indexRecordPointer = fc.position();
            while (fc.read(buffer) > 0) {
                buffer.flip();
                IndexRow indexRow = IndexRow.readFromBuffer(buffer, indexRecordPointer);
                if (indexRow != null) {
                    this.indexRows.add(indexRow);
                    _nbRecords++;
                    indexRecordPointer += IndexRow.INDEX_ROW_SIZE;
                }
                buffer.clear();
                System.out.println(indexRow);
            }
        }
        return _nbRecords;
    }

    /**
     *
     * @param uuid
     * @param dataPointer
     * @throws IOException
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
        indexWriter.addRecordToDirtyList(newIndexRow);
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
            int nextRecordPosistion = (int) indexRow.getRowPointer() + IndexRow.INDEX_ROW_SIZE;
            System.out.println("nextRecordPosistion : " + nextRecordPosistion);
            System.out.println("before fc.size : " + fc.size());
            int remain = (int) (fc.size() - nextRecordPosistion);
            System.out.println("remain : " + remain);
            if (remain > 0) {
                ByteBuffer remainingBytes = ByteBuffer.allocate(remain);
                fc.position(nextRecordPosistion);
                fc.read(remainingBytes);
                remainingBytes.flip();
                fc.position(indexRow.getRowPointer());
                while (remainingBytes.hasRemaining()) {
                    fc.write(remainingBytes);
                }
            } else {
                remain = 0;
            }
            fc.truncate(indexRow.getRowPointer() + remain);
            System.out.println("after fc.size : " + fc.size());
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
            indexRows.remove(pos);
            indexRow.addPropertyChangeListener(indexWriter);
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
            indexWriter.addRecordToDirtyList(ir);
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
