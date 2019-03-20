
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.AbstractTile;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import fr.univubs.inf1603.mahjong.daofile.myengine.Tile;
//import fr.univubs.inf1603.mahjong.engine.AbstractTile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author aliyou
 */
public class FileTileDAO extends FileDAOMahJong<AbstractTile> {

    /**
     * Logging
     */
    //private final static Logger LOGGER = Logger.getLogger(FileDAOMahJong.class.getName());

    /**
     * Constructeur vide
     * @throws DAOException 
     */
    FileTileDAO() throws DAOException {
        super("tile.data", "tile.index");
    }

    /**
     * Constructeur avec le Chemin du répertoire racine
     * @param rootDir Chemin du répertoire racine.
     * @throws DAOException 
     */
    FileTileDAO(Path rootDir) throws DAOException {
        super(rootDir, "tile.data", "tile.index");
    }

    /**
     *
     * @param tile
     * @throws DAOException
     */
    @Override
    protected void writeToPersistance(AbstractTile tile) throws DAOException {
        try {
            long recordPointer = getNextRowPointer(TileRow.TILE_ROW_SIZE);
            TileRow row = new TileRow(getNexRowId(), tile, recordPointer);
            dataRows.add(row);
            
            dataWriter.addRecordToDirtyList(row);
            row.addPropertyChangeListener(dataWriter);
            
            indexManager.addDataIndex(tile.getUUID(), recordPointer);
            
            fileHeader.incrementRowNumber();
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    @Override
    protected AbstractTile loadFromPersistance(UUID uuid) throws DAOException {
        long pointer = indexManager.getDataRowPointer(uuid);
        if (pointer != -1) {
            FileChannel fileChannel = dataFile.getChannel();
            try {
                fileChannel.position(pointer);
                ByteBuffer buffer = ByteBuffer.allocate(TileRow.TILE_ROW_SIZE);
                if (fileChannel.read(buffer) > 0) {
                    buffer.flip();
                    TileRow dataRow = TileRow.ReadFromBuffer(buffer, pointer);
                    if(dataRow != null) {
                        dataRows.add(dataRow);
//                        dataRow.addObserver(dataWriter);
                        dataRow.addPropertyChangeListener(dataWriter);
                        return dataRow.getData();
                    }
                }
            } catch (IOException ex) {
                throw new DAOException("Erreur IO : \n" + ex.getMessage());
            }
        }
        return null;
    }

    @Override
    protected void deleteFromPersistance(AbstractTile tile) throws DAOException {
        try {
            long dataPointer = indexManager.removeDataIndex(tile.getUUID());
//            System.out.println("dataPointer : "+ dataPointer);
            if (dataPointer != -1) {
//                TileRow tileRecord = (TileRow) mapDataRecords.get(tile.getUUID());
//             isWrited
//            tileRecord.setRecordPointer(-1);
                // pas dans le map ; pas encore chargé
                FileChannel fc = dataFile.getChannel();
                int nextPointerPosistion = (int) dataPointer + TileRow.TILE_ROW_SIZE;
//                System.out.println("nextRecordPosistion : " + nextPointerPosistion);
//                System.out.println("before fc.size : " + fc.size());
                int remain = (int) (fc.size() - nextPointerPosistion);
//                System.out.println("remain : " + remain);
                if (remain > 0) {
                    ByteBuffer remainingBytes = ByteBuffer.allocate(remain);
                    fc.position(nextPointerPosistion);
                    fc.read(remainingBytes);
                    remainingBytes.flip();
//                    System.out.println("remainbBytes : " + remainingBytes);
                    while (remainingBytes.hasRemaining()) {
                        fc.write(remainingBytes);
                    }
                } else {
                    remain = 0;
                }
                fc.truncate(dataPointer + remain);
                fc.position(dataPointer);
//                System.out.println("after fc.size : " + fc.size());

                fileHeader.decrementRowNumber();
                System.out.println("tile deleted to persistance");
            }
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    /**
     * Rétourne la liste de toutes les tuiles persistés.
     * @return Liste de toutes les tuiles persistés.
     * @throws DAOException s'il y'a une erreur lors du chargement.
     */
    @Override
    protected List<AbstractTile> laodAll() throws DAOException {
        ArrayList<AbstractTile> tiles;
        if (fileHeader.getRowNumber() != map.size()) {
            for (IndexRow indexRow : indexManager.getIndexRows()) {
                Index index = indexRow.getData();
                if ( ! map.containsKey(index.getUUID())) {
                    find(index.getUUID());
                }
            }
        }
        tiles = new ArrayList<>(map.values());
        return tiles;
    }

    static class TileRow extends Row<AbstractTile> {

        private static final int TILE_SIZE = 50;
        static final int TILE_ROW_SIZE = ROW_HEADER_SIZE + TILE_SIZE;

        TileRow(int id, AbstractTile data, long recordPointer) {
            super(id, data, TILE_SIZE, recordPointer);
        }

        static TileRow ReadFromBuffer(ByteBuffer buffer, long recordPointer) {
            if (buffer.remaining() >= 30) {
                int idRead = buffer.getInt();
                UUID uuidRead = new UUID(buffer.getLong(), buffer.getLong());
                String categoryRead = FileUtilities.readString(buffer);
                String famillyRead = FileUtilities.readString(buffer);
                Tile data = new Tile(uuidRead, categoryRead, famillyRead);
                return new TileRow(idRead, data, recordPointer);
            }
            return null;
        }

        @Override
        protected void writeData(ByteBuffer buffer) {
            buffer.putLong(getData().getUUID().getMostSignificantBits());
            buffer.putLong(getData().getUUID().getLeastSignificantBits());
            FileUtilities.writeString(buffer, getData().getCategory());
            FileUtilities.writeString(buffer, getData().getFamily());
        }
    }

}