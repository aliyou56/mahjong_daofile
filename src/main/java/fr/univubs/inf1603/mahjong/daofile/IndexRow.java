package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 *
 * @author aliyou
 */
public class IndexRow extends Row<Index> {

    static final int INDEX_SIZE = 25;
    /**
     *
     */
    static final int INDEX_ROW_SIZE = Row.ROW_HEADER_SIZE + INDEX_SIZE;

    /**
     *
     * @param id
     * @param data
     * @param rowPointer
     */
    IndexRow(int id, Index data, long rowPointer) {
        super(id, data, INDEX_SIZE, rowPointer);
    }

    /**
     *
     * @param buffer
     */
    @Override
    protected void writeData(ByteBuffer buffer) {
        buffer.putLong(getData().getUUID().getMostSignificantBits());
        buffer.putLong(getData().getUUID().getLeastSignificantBits());
        buffer.putLong(getData().getPointer());
    }

    /**
     *
     * @param buffer
     * @param recordPointer
     * @return
     */
    static IndexRow readFromBuffer(ByteBuffer buffer, long recordPointer) {
        if (buffer.remaining() >= INDEX_ROW_SIZE - 1) {
            int idRead = buffer.getInt();
            UUID uuidRead = new UUID(buffer.getLong(), buffer.getLong());
            long pointerRead = buffer.getLong();
            Index data = new Index(uuidRead, pointerRead);
            return new IndexRow(idRead, data, recordPointer);
        }
        return null;
    }

    static class Index {

        /**
         * L'identifiant de la donnée pointée
         */
        private final UUID uuid;
        /**
         * 
         */
        private long pointer;

        Index(UUID uuid, long pointer) {
            this.uuid = uuid;
            this.pointer = pointer;
        }

        UUID getUUID() {
            return uuid;
        }

        long getPointer() {
            return pointer;
        }

        void setPointer(long pointer) {
            this.pointer = pointer;
        }

        @Override
        public String toString() {
            return "Index{" + "uuid=" + uuid + ", pointer=" + pointer + '}';
        }
    }
}
