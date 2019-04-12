package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import fr.univubs.inf1603.mahjong.engine.game.MahjongTileZone;
import fr.univubs.inf1603.mahjong.engine.game.TileZoneIdentifier;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliyou
 */
public class FileZoneDAO extends FileDAOMahjong<TileZone> {

    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(FileZoneDAO.class.getName());
    /**
     * 
     */
    private static LinkManager<GameTile> tileToZoneLinkManager;

    /**
     * Constructeur vide
     *
     * @throws DAOException s'il ya une erreur lors de l'instanciation.
     */
    FileZoneDAO() throws DAOException {
        this(Paths.get(System.getProperty("user.home"), "MahJong"));
    }

    /**
     * Constructeur avec le Chemin d'accès du répertoire racine <code>rootDir</code>.
     *
     * @param rootDir Chemin d'accès du répertoire racine.
     * @throws DAOException s'il ya une erreur lors de l'instanciation.
     */
    FileZoneDAO(Path rootDir) throws DAOException {
        super(rootDir, "zone.data", "zone.index", ZoneRow.ZONE_ROW_SIZE);
        System.out.println(" -> FileZoneDAO");
        LinkManagerFactory factory = LinkManagerFactory.getInstance(rootDir);
        tileToZoneLinkManager = factory.getTileToZoneLinkManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow getDataRow(int rowID, TileZone zone, long rowPointer) {
        return new ZoneRow(rowID, zone, rowPointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow getDataRow(ByteBuffer buffer, long pointer) {
        return ZoneRow.ReadFromBuffer(buffer, pointer);
    }

    /**
     * Supprimer une zone du fichier de données.
     *
     * @param zone Zone à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistance(TileZone zone) throws DAOException {
        try {
            if (super.removeDataRow(zone.getUUID())) {
                tileToZoneLinkManager.removeChildren(zone.getTiles());
                System.out.println("zone deleted from persistance");
            }
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    /**
     * Cette classe répresente un tuple pour une zone. Elle permet de
     * rajouter des métadonnées à une zone.
     */
    static class ZoneRow extends AbstractRow<TileZone> {

        /**
         * Taille d'une zone en octet.
         */
        private static final int ZONE_SIZE = 16 + (4 + 50) + 4 + 4;
        /**
         * Taille d'un tuple de zone.
         */
        static final int ZONE_ROW_SIZE = ROW_HEADER_SIZE + ZONE_SIZE;

        /**
         * 
         * @param id
         * @param data
         * @param recordPointer 
         */
        ZoneRow(int id, TileZone data, long recordPointer) {
            super(id, data, ZONE_SIZE, recordPointer);
        }

        /**
         * 
         * @param buffer
         * @param rowPointer
         * @return 
         */
        static ZoneRow ReadFromBuffer(ByteBuffer buffer, long rowPointer) {
            if (buffer.remaining() >= 20) {
                try {
                    int rowID = buffer.getInt();
                    UUID zoneID = new UUID(buffer.getLong(), buffer.getLong());
//                    String name = FileWriter.readString(buffer);
                    TileZoneIdentifier identifier = null;

                    int nbTiles = buffer.getInt();
//                if(nbTileRead != 0) {
                    ArrayList<GameTile> tiles = tileToZoneLinkManager.loadChildren(zoneID);
//                }
                    TileZone data = new MahjongTileZone(tiles, zoneID, identifier);
                    return new ZoneRow(rowID, data, rowPointer);
                } catch (DAOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
            return null;
        }

        /**
         * 
         * @param buffer
         * @throws DAOException 
         */
        @Override
        protected void writeData(ByteBuffer buffer) throws IOException, DAOException {
            FileWriter.writeUUID(buffer, getData().getUUID());
            if (getData() instanceof TileZone) {
//                TileZone tileZone = (TileZone) getData();
//                FileWriter.writeString(buffer, getData().getName());
//                FileWriter.writeString(buffer, getData().getIdentifier());
                buffer.putInt(getData().getTiles().size());
                if (getData().getTiles().size() > 0) {
                    tileToZoneLinkManager.addChildren(getData().getUUID(), getData().getTiles());
                }
            }
        }

    }
}
