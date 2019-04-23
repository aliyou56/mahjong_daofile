package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.filemanagement.AbstractRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.LinkManager;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DAOFileWriter;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.engine.MahjongTileZone;
import fr.univubs.inf1603.mahjong.daofile.exception.ByteBufferException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import fr.univubs.inf1603.mahjong.engine.game.TileZoneIdentifier;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe {@code FileTileDAO} gère la persistance des zones {@code TileZone}
 * {@link FileZoneDAO.ZoneRow}.
 *
 * @author aliyou, nesrine
 * @version 1.1.0
 */
public class FileZoneDAO extends FileDAOMahjong<TileZone> {

    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(FileZoneDAO.class.getName());
    /**
     * Gestionnaire de liens entre les tuiles et les zones.
     */
    private static LinkManager<GameTile> tileToZoneLinkManager;
    /**
     * Gestionnaire de liens entre les zones et les parties.
     */
    private static LinkManager<TileZone> zoneToGameLinkManager;

    /**
     * Constructeur avec un Chemin d'accès du répertoire racine
     * <code>rootDirPath</code>.
     *
     * @param rootDirPath Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @throws DAOFileException s'il ya une erreur lors de l'instanciation.
     */
    FileZoneDAO(Path rootDirPath) throws DAOFileException {
        super(rootDirPath, "zone.data", "zone.index", ZoneRow.ZONE_ROW_SIZE);
        System.out.println(" -> FileZoneDAO");
    }

    /**
     * Définit le gestionnaire de liens.
     *
     * @param zoneToGameLinkManager Gestionnaire de liens.
     */
    void setZoneLinkManager(LinkManager<TileZone> zoneToGameLinkManager) {
        if (FileZoneDAO.zoneToGameLinkManager == null) {
            FileZoneDAO.zoneToGameLinkManager = zoneToGameLinkManager;
        }
    }

    /**
     * Définit le gestionnaire de liens.
     *
     * @param tileToZoneLinkManager Gestionnaire de liens.
     */
    void setTileLinkManager(LinkManager<GameTile> tileToZoneLinkManager) {
        if (FileZoneDAO.tileToZoneLinkManager == null) {
            FileZoneDAO.tileToZoneLinkManager = tileToZoneLinkManager;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow getDataRow(int rowID, TileZone tileZone, long rowPointer) throws DAOFileException {
        return new ZoneRow(rowID, tileZone, rowPointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow getDataRow(DAOFileWriter writer, long pointer) throws DAOFileException {
        return new ZoneRow(writer, pointer);
    }

    /**
     * Supprime une zone de tuiles <code>TileZone</code> du fichier de données.
     *
     * @param tileZone Zone à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistance(TileZone tileZone) throws DAOException {
        try {
            tileToZoneLinkManager.removeChildren(tileZone.getTiles());
            if (super.removeDataRow(tileZone.getUUID())) {
                LOGGER.log(Level.INFO, "[INFO] {0} id='{1} deleted from persistance", new Object[]{tileZone.getClass().getSimpleName(), tileZone.getUUID()});
            }
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
    }

    /**
     * La classe <code>ZoneRow</code> répresente un tuple de zone
     * {@link TileZone}. C'est un conteneur pour une zone.
     *
     * <pre>
     *
     *      Format d'une zone dans un tuple dans le fichier de données :
     *
     *          UUID=16    |     int=4       |      String=20       |   -> (Byte)
     *         tileZoneID  |   nbTiles       |      identifier      |
     * </pre>
     *
     */
    class ZoneRow extends DataRow<TileZone> {

        /**
         * Taille minimale d'une zone en octet.
         */
        private static final int ZONE_MIN_SIZE = 16 + 4 + (4 + 1);           // 25
        /**
         * Taille maximale d'une zone en octet.
         */
        private static final int ZONE_MAX_SIZE = ZONE_MIN_SIZE + 14;       // 39
        /**
         * Taille d'un tuple de zone.
         */
        static final int ZONE_ROW_SIZE = ROW_HEADER_SIZE + ZONE_MAX_SIZE;  // 43

        /**
         * Constructeur avec l'identifiant d'un tuple <code>rowID</code>, une
         * zone <code>data</code> et un pointeur de tuple
         * <code>rowPointer</code>.
         *
         * @param rowID Identifiant du tuple.
         * @param data Zone encapsulé dans le tuple.
         * @param rowPointer Pointeur de tuple.
         */
        ZoneRow(int rowID, TileZone data, long rowPointer) throws DAOFileException {
            super(rowID, data, ZONE_MAX_SIZE, rowPointer);
        }

        /**
         * Constructeur avec un processus qui éffectue des opérations
         * d'entrée/sortie sur un fichier <code>writer</code> et un pointeur de
         * tuple <code>rowPointer</code>..
         *
         * @param writer Processus qui éffectue des opérations d'entrée/sortie
         * sur un fichier.
         * @param rowPointer Pointeur d'un tuple.
         * @throws DAOException s'il y'a une erruer lors de la lecture d'une
         * zone <code>TileZone</code>.
         */
        ZoneRow(DAOFileWriter writer, long rowPointer) throws DAOFileException {
            super(writer, ZONE_MAX_SIZE, rowPointer);
        }

        /**
         * Lis une zone <code>TileZone</code> à partir d'un tampon d'octets
         * <code>buffer</code>.
         *
         * @param buffer Tampon d'octets à partir duquel une zone
         * <code>TileZone</code> est lue.
         */
        @Override
        protected TileZone readData(ByteBuffer buffer) throws DAOFileException {
            UUID zoneID = new UUID(buffer.getLong(), buffer.getLong());
            int nbTiles = buffer.getInt();
            String ident = DAOFileWriter.readString(buffer);
            TileZoneIdentifier identifier = TileZoneIdentifier.valueOf(ident);
            ArrayList<GameTile> tiles = new ArrayList<>();
            if (nbTiles != 0) {
                try {
                    tiles = tileToZoneLinkManager.loadChildren(zoneID);
                } catch (DAOException ex) {
                    throw new DAOFileException(ex.getMessage(), ex);
                }
            }
//                TileZone data = new MahjongTileZone(tiles, zoneID, identifier);
            MahjongTileZone data = new MahjongTileZone(zoneID, tiles, identifier);
            return data;
        }

        /**
         * Ecrit une zone dans un tampon d'octet <code>buffer</code>.
         *
         * @param buffer Tampon d'octet.
         */
        @Override
        protected void writeData(ByteBuffer buffer) throws DAOFileException {
            try {
                DAOFileWriter.writeUUID(buffer, getData().getUUID());
                int nbTiles = getData().getTiles().size();
                buffer.putInt(nbTiles);
                DAOFileWriter.writeString(buffer, getData().getIdentifier().toString());
                if (nbTiles != 0) {
                    tileToZoneLinkManager.addChildren(getData().getUUID(), getData().getTiles());
                }
                indexManager.addIndex(getIndex());
            } catch (ByteBufferException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
    }
}
