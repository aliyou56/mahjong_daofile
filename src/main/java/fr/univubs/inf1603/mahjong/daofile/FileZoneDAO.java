package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.fake_engine.GameTile;
import fr.univubs.inf1603.mahjong.dao.fake_engine.TileZone;
import fr.univubs.inf1603.mahjong.dao.fake_engine.Zone;
import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author aliyou
 */
public class FileZoneDAO extends FileDAOMahJong<Zone> {

    private static LinkManager<GameTile> tileToZoneLinkManager;
    private static LinkManager<Zone> zoneToZoneLinkManager;

    /**
     * Constructeur vide
     *
     * @throws DAOException s'il ya une erreur lors de l'instanciation.
     */
    FileZoneDAO() throws DAOException {
        this(Paths.get(System.getProperty("user.home"), "MahJong"));
    }

    /**
     * Constructeur avec le Chemin d'accès du répertoire racine
     *
     * @param rootDir Chemin d'accès du répertoire racine.
     * @throws DAOException s'il ya une erreur lors de l'instanciation.
     */
    FileZoneDAO(Path rootDir) throws DAOException {
        super(rootDir, "zone.data", "zone.index");
        LinkManagerFactory factory = LinkManagerFactory.getInstance(rootDir);
        tileToZoneLinkManager = factory.getTileToZoneLinkManager();
        zoneToZoneLinkManager = factory.getZoneToZoneLinkManager();
    }

    /**
     * Persiste un une tuile dans le fichier de données des tuiles.
     *
     * @param zone Tuile à persister.
     * @throws DAOException s'il y'a une erreur lors de la sauvegarde.
     */
    @Override
    protected void writeToPersistance(Zone zone) throws DAOException {
        try {
            long rowPointer = getNextRowPointer(ZoneRow.ZONE_ROW_SIZE);
            ZoneRow row = new ZoneRow(getNexRowID(), zone, rowPointer);
            super.write(zone.getUUID(), row);
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    /**
     * Charge une tuile depuis le fichier de données à l'aide de son identifiant
     *
     * @param zoneID Identifiant de la tuile à charger.
     * @return La tuile si elle est retrouvée sinon {@code null}
     * @throws DAOException s'il y'a une erreur lors du chargement
     */
    @Override
    protected Zone loadFromPersistance(UUID zoneID) throws DAOException {
        try {
            long pointer = indexManager.getDataRowPointer(zoneID);
            ByteBuffer buff = super.load(zoneID, ZoneRow.ZONE_ROW_SIZE, pointer);
            if (buff != null) {
                ZoneRow dataRow = ZoneRow.ReadFromBuffer(buff, pointer);
                if (dataRow != null) {
                    dataRows.add(dataRow);
                    dataRow.addPropertyChangeListener(dataWriter);
                    return dataRow.getData();
                }
            }
        } catch (IOException  ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
        return null;
    }

    /**
     * Supprimer une tuile du fichier de données.
     *
     * @param zone Tuile à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistance(Zone zone) throws DAOException {
        try {
            if (super.remove(zone.getUUID(), ZoneRow.ZONE_ROW_SIZE)) {
                tileToZoneLinkManager.removeChildren(zone.getUUID(), zone.getTilesCollection());
                System.out.println("zone deleted from persistance");
            }
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    /**
     * Rétourne la liste de toutes les tuiles persistées.
     *
     * @return Liste de toutes les tuiles persistées.
     * @throws DAOException s'il y'a une erreur lors du chargement.
     */
    @Override
    protected List<Zone> laodAll() throws DAOException {
        ArrayList<Zone> zones;
        if (getRowNumber() != map.size()) { // || map.isEmpty()
            for (IndexRow indexRow : indexManager.getRows()) {
                Index index = indexRow.getData();
                if (!map.containsKey(index.getUUID())) {
                    find(index.getUUID());
                }
            }
        }
        zones = new ArrayList<>(map.values());
        return zones;
    }

    /**
     * Cette classe répresente un conteneur pour une tuile. Elle permet de
     * rajouter des métadonnées à une tuile.
     */
    static class ZoneRow extends AbstractRow<Zone> {

        /**
         * Taille d'une tuile en octet.
         */
        private static final int ZONE_SIZE = 16 + (4 + 50) + 4 + 4;
        /**
         * Taille d'un tuple contenant une tuile.
         */
        static final int ZONE_ROW_SIZE = ROW_HEADER_SIZE + ZONE_SIZE;

        ZoneRow(int id, Zone data, long recordPointer) throws DAOException {
            super(id, data, ZONE_SIZE, recordPointer);
        }

        static ZoneRow ReadFromBuffer(ByteBuffer buffer, long recordPointer) throws DAOException {
            if (buffer.remaining() >= 20) {
                int rowID = buffer.getInt();
                UUID zoneID = new UUID(buffer.getLong(), buffer.getLong());
                String name = FileUtilities.readString(buffer);

                int nbTiles = buffer.getInt();
                int nbZones = buffer.getInt();
//                if(nbTileRead != 0) {
                ArrayList<GameTile> tiles = tileToZoneLinkManager.loadChildren(zoneID);
//                }
//                if(nbZoneRead != 0) {
                ArrayList<Zone> zones = zoneToZoneLinkManager.loadChildren(zoneID);
//                }

                TileZone data = new TileZone(zoneID, name, tiles, zones);
                return new ZoneRow(rowID, data, recordPointer);
            }
            return null;
        }

        @Override
        protected void writeData(ByteBuffer buffer) throws DAOException {
            FileUtilities.writeUUID(buffer, getData().getUUID());
            if (getData() instanceof TileZone) {
//                TileZone tileZone = (TileZone) getData();
                FileUtilities.writeString(buffer, getData().getName());
                buffer.putInt(getData().getTilesCollection().size());
//                buffer.putInt(tileZone.getZonesCollection().size());
                if (getData().getTilesCollection().size() > 0) {
                    tileToZoneLinkManager.addChildren(getData().getUUID(), getData().getTilesCollection());
                }
//                if (getData().getZonesCollection().size() > 0) {
//                }
            }
        }

    }
}
