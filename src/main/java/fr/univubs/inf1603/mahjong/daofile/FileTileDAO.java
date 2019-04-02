package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.fake_engine.GameTile;
import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
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
 * La classe <code>FileTileDAO</code> gère la persistance d'une tuile.
 *
 * @author aliyou, nesrine
 * @version 1.0.0
 */
public class FileTileDAO extends FileDAOMahJong<GameTile> {

    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(FileDAOMahJong.class.getName());
    
    private static LinkManager<GameTile> tileToZoneLinkManager;

    /**
     * Constructeur vide
     *
     * @throws DAOException s'il ya une erreur lors de l'instanciation.
     */
    FileTileDAO() throws DAOException {
        this(Paths.get(System.getProperty("user.home"), "MahJong"));
    }

    /**
     * Constructeur avec le Chemin d'accès du répertoire racine
     *
     * @param rootDir Chemin d'accès du répertoire racine.
     * @throws DAOException s'il ya une erreur lors de l'instanciation.
     */
    FileTileDAO(Path rootDir) throws DAOException {
        super(rootDir, "tile.data", "tile.index");
        tileToZoneLinkManager = LinkManagerFactory.getInstance(rootDirPath).getTileToZoneLinkManager();
    }

    /**
     * Persiste un une tuile dans le fichier de données des tuiles.
     *
     * @param tile Tuile à persister.
     * @throws DAOException s'il y'a une erreur lors de la sauvegarde.
     */
    @Override
    protected void writeToPersistance(GameTile tile) throws DAOException {
        try {
            long rowPointer = getNextRowPointer(TileRow.TILE_ROW_SIZE);
            TileRow row = new TileRow(getNexRowID(), tile, rowPointer);
            super.write(tile.getUUID(), row);
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    /**
     * Charge une tuile depuis le fichier de données à l'aide de son identifiant
     *
     * @param tileID Identifiant de la tuile à charger.
     * @return La tuile si elle est retrouvée sinon <code>null</code>
     * @throws DAOException s'il y'a une erreur lors du chargement
     */
    @Override
    protected GameTile loadFromPersistance(UUID tileID) throws DAOException {
        try {
            long pointer = indexManager.getDataRowPointer(tileID);
            ByteBuffer buff = super.load(tileID, TileRow.TILE_ROW_SIZE, pointer);
            if (buff != null) {
                TileRow dataRow = TileRow.ReadFromBuffer(buff, pointer);
                if (dataRow != null) {
                    dataRows.add(dataRow);
                    dataRow.addPropertyChangeListener(dataWriter);
                    return dataRow.getData();
                }
            }
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
        return null;
    }

    /**
     * Supprimer une tuile du fichier de données.
     *
     * @param tile Tuile à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistance(GameTile tile) throws DAOException {
        try {
            // on vérifie si la tuile n'est pas reliée à une zone
            if (tileToZoneLinkManager.getRow(tile.getUUID()) != null) {
                LOGGER.log(Level.INFO, " ++++++ Tile id={0} canno't be deleted cause it is linked to a zone", tile.getUUID());
            }
                if(!tileToZoneLinkManager.isChildLinkedToAParent(tile.getUUID())) {
                LOGGER.log(Level.INFO, "Tile id={0} no more link", tile.getUUID());
                if (super.remove(tile.getUUID(), TileRow.TILE_ROW_SIZE)) {
                    LOGGER.log(Level.INFO, "Tile id={0} deleted from persistance", tile.getUUID());
                }
            } else {
                LOGGER.log(Level.INFO, "Tile id={0} canno't be deleted cause it is linked to a zone", tile.getUUID());
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
    protected List<GameTile> laodAll() throws DAOException {
        ArrayList<GameTile> tiles;
        if (getRowNumber() != map.size()) { // || map.isEmpty()
            for (IndexRow indexRow : indexManager.getRows()) {
                Index index = indexRow.getData();
                if (!map.containsKey(index.getUUID())) {
                    find(index.getUUID());
                }
            }
        }
        tiles = new ArrayList<>(map.values());
        return tiles;
    }

    /**
     * Cette classe répresente un conteneur pour une tuile. Elle permet de
     * rajouter des métadonnées à une tuile.
     */
    static class TileRow extends AbstractRow<GameTile> {

        /**
         * Taille d'une tuile en octet.
         */
        private static final int TILE_SIZE = 50;
        /**
         * Taille d'un tuple contenant une tuile.
         */
        static final int TILE_ROW_SIZE = ROW_HEADER_SIZE + TILE_SIZE;

        /**
         * Constructeur avec l'identifiant du tuple, la tuile et le pointeur de
         * tuple
         *
         * @param rowID Identifiant du tuple
         * @param data Tuile encapsulé dans le tuple
         * @param recordPointer Pointeur du tuple
         */
        TileRow(int rowID, GameTile data, long recordPointer) {
            super(rowID, data, TILE_SIZE, recordPointer);
        }

        /**
         * Lis un tuple contenant une tuile à partir d'un tampon d'octets.
         * Rétourne le tuple lue si les données dans le tampon sont cohérentes
         * sinon <code>null</code>..
         *
         * @param buffer Tampon d'octets
         * @param rowPointer Pointeur de tuple
         * @return Tuple de tuile si les données lues sont cohérentes sinon
         * <code>null</code>.
         */
        static TileRow ReadFromBuffer(ByteBuffer buffer, long rowPointer) {
            if (buffer.remaining() >= 30) {
                int rowID = buffer.getInt();
                UUID tileID = new UUID(buffer.getLong(), buffer.getLong());
                String categoryRead = FileUtilities.readString(buffer);
                String famillyRead = FileUtilities.readString(buffer);
                GameTile data = new GameTile(tileID, categoryRead, famillyRead);
                return new TileRow(rowID, data, rowPointer);
            }
            return null;
        }

        /**
         * Ecrit une tuile dans un tampon d'octet.
         *
         * @param buffer Tampon d'octet
         */
        @Override
        protected void writeData(ByteBuffer buffer) {
            FileUtilities.writeUUID(buffer, getData().getUUID());
            FileUtilities.writeString(buffer, getData().getCategory());
            FileUtilities.writeString(buffer, getData().getFamily());
        }
    }

}
