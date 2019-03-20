package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.FileHeaderRow.FileHeader;
import java.nio.ByteBuffer;

/**
 *
 * @author aliyou
 */
public class FileHeaderRow extends Row<FileHeader> {

    private static final int FILE_HEADER_SIZE = 8;

    static final int FILE_HEADER_ROW_SIZE = ROW_HEADER_SIZE + FILE_HEADER_SIZE;

    FileHeaderRow(FileHeader data) {
        super(0, data, FILE_HEADER_SIZE, 0);
    }

    @Override
    protected void writeData(ByteBuffer buffer) {
        buffer.putInt(getData().getRowNumber());
        buffer.putInt(getData().getRowLastId());
    }

    static FileHeaderRow readFromBuffer(ByteBuffer buffer) {
        if (buffer.remaining() >= FILE_HEADER_ROW_SIZE) {
            int idRead = buffer.getInt();
            int nbRecordRead = buffer.getInt();
            int recordLastIdRead = buffer.getInt();
            FileHeader data = new FileHeader(nbRecordRead, recordLastIdRead);
            return new FileHeaderRow(data);
        }
        return null;
    }

    static class FileHeader {

        private int rowNumber;
        private int rowLastId;

        FileHeader(int rowNumber, int rowLastId) {
            this.rowNumber = rowNumber;
            this.rowLastId = rowLastId;
        }

        synchronized int getRowNumber() {
            return rowNumber;
        }

        synchronized int getRowLastId() {
            return rowLastId;
        }

        synchronized void incrementRowNumber() {
            this.rowNumber += 1;
        }

        synchronized void decrementRowNumber() {
            this.rowNumber -= 1;
        }

        synchronized void updateRowLastId() {
            this.rowLastId += 1;
        }

        @Override
        public String toString() {
            return "FileHeader {" + "rowNumber=" + rowNumber + ", rowLastId=" + rowLastId + '}';
        }
    }
}
