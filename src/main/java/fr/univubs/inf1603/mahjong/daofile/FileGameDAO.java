package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.filemanagement.AbstractRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.LinkManager;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DAOFileWriter;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.exception.ByteBufferException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.game.Board;
import fr.univubs.inf1603.mahjong.engine.game.Game;
import fr.univubs.inf1603.mahjong.engine.game.MahjongBoard;
import fr.univubs.inf1603.mahjong.engine.game.Move;
import fr.univubs.inf1603.mahjong.engine.game.MoveException;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import fr.univubs.inf1603.mahjong.engine.game.TileZoneIdentifier;
import fr.univubs.inf1603.mahjong.engine.rule.GameRule;
import fr.univubs.inf1603.mahjong.engine.rule.Wind;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe {@code FileGameDAO} gère la persistance des parties
 * {@code GameTile} {@link FileGameDAO.GameRow}.
 *
 * @author aliyou, nesrine
 * @version 1.1.0
 */
public class FileGameDAO extends FileDAOMahjong<Game> {

    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(FileGameDAO.class.getName());

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
    public FileGameDAO(Path rootDirPath) throws DAOFileException {
        super(rootDirPath, "game.data", "game.index", GameRow.GAME_ROW_SIZE);
        System.out.println(" -> FileGameDAO");
    }

    /**
     * Définit le gestionnaire de liens.
     *
     * @param zoneToGameLinkManager Gestionnaire de liens.
     */
    void setLinkManager(LinkManager<TileZone> zoneToGameLinkManager) {
        if (FileGameDAO.zoneToGameLinkManager == null) {
            FileGameDAO.zoneToGameLinkManager = zoneToGameLinkManager;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow<Game> getDataRow(int rowID, Game data, long pointer) throws DAOFileException {
        return new GameRow(rowID, data, pointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow<Game> getDataRow(DAOFileWriter writer, long pointer) throws DAOFileException {
        return new GameRow(writer, pointer);
    }

    /**
     * Supprime une partie <code>Game</code> du fichier de données.
     *
     * @param game Partie à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistance(Game game) throws DAOException {
        try {
//            zoneToGameLinkManager.removeChildren(game.getBoard(Wind.EAST));
            if (super.removeDataRow(game.getUUID())) {
                LOGGER.log(Level.INFO, "[INFO] {0} id='{1} deleted from persistance", new Object[]{game.getClass().getSimpleName(), game.getUUID()});
            }
        } catch (DAOFileException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    class GameRow extends DataRow<Game> {

        /**
         * Taille d'une game en octet.
         */
        private static final int GAME_SIZE = 16 + 8 + 8 + 16 + 1 /*+x*/ + 4 + 1 + 4 + 16;   // 74
        /**
         * Taille d'un tuple de game.
         */
        static final int GAME_ROW_SIZE = ROW_HEADER_SIZE + GAME_SIZE;                      // 78

        public GameRow(int rowID, Game data, long rowPointer) throws DAOFileException {
            super(rowID, data, GAME_ROW_SIZE, rowPointer);
        }

        /**
         * Constructeur avec un processus qui éffectue des opérations
         * d'entrée/sortie sur un fichier <code>writer</code> et un pointeur de
         * tuple <code>rowPointer</code>..
         *
         * @param writer Processus qui éffectue des opérations d'entrée/sortie
         * sur un fichier.
         * @param rowPointer Pointeur d'un tuple.
         * @throws DAOFileException s'il y'a une erruer lors de la lecture d'une
         * partie.
         */
        GameRow(DAOFileWriter writer, long rowPointer) throws DAOFileException {
            super(writer, GAME_ROW_SIZE, rowPointer);
        }

        /*
           16      8         8              16         1         x                    4              1      4        16
        * uuid, playtime, stealtime, board{uuid, cuurentWind}, rule, lastplayedMove{priority, path(wind, tileID, tileZone)}
        *          long       long
         */
        @Override
        protected Game readData(ByteBuffer buffer) throws DAOFileException {
            try {
                Game game = null;
                GameRule rule = null;
                Board board;
                Move lastPlayedMove;

                UUID gameID = new UUID(buffer.getLong(), buffer.getLong());
                Duration playTime = Duration.ofMillis(buffer.getLong());
                Duration stealTime = Duration.ofMillis(buffer.getLong());

                UUID boardID = new UUID(buffer.getLong(), buffer.getLong());
                Wind currentWind = MahjongUtilities.getWind((char) buffer.get());
                int nbZones = buffer.getInt();
                ArrayList<TileZone> zones = new ArrayList<>();
                if (nbZones != 0) {
                    try {
                        zones = zoneToGameLinkManager.loadChildren(gameID);
                    } catch (DAOException ex) {
                        throw new DAOFileException(ex.getMessage(), ex);
                    }
                    EnumMap<TileZoneIdentifier, TileZone> enumMap = new EnumMap<>(TileZoneIdentifier.class);
                    for (TileZone zone : zones) {
                        enumMap.put(zone.getIdentifier(), zone);
                    }
                    board = new MahjongBoard(currentWind, boardID, enumMap);
                } else {
                    board = new MahjongBoard(currentWind);
                }

                // rule
//                rule = buffer.get();
                // Move
                UUID moveID = new UUID(buffer.getLong(), buffer.getLong());
                int priority = buffer.getInt();
                Wind pathWind = MahjongUtilities.getWind((char) buffer.get());
                int tileIndex = buffer.getInt();
                UUID zoneID = new UUID(buffer.getLong(), buffer.getLong());
                TileZone tileZone;
                try {
                    tileZone = FileDAOManager.getInstance().getZoneDao().find(zoneID);
                } catch (DAOException ex) {
                    throw new DAOFileException(ex.getMessage(), ex);
                }
                HashMap path = new HashMap();
                path.put(tileIndex, tileZone);
                lastPlayedMove = new Move(pathWind, priority, path, moveID);

//                game = new MahjongGame(gameID, rule);
                return game;
            } catch (MoveException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }

        @Override
        protected void writeData(ByteBuffer buffer) throws DAOFileException {
            try {
                DAOFileWriter.writeUUID(buffer, getData().getUUID());
                buffer.putLong(getData().getPlayingTime().toMillis());
                buffer.putLong(getData().getStealingTime().toMillis());

//            buffer.putLong(getData());
//            
//            int nbTiles = getData().getTiles().size();
//            buffer.putInt(nbTiles);
//            DAOFileWriter.writeString(buffer, getData().getIdentifier().toString());
//            if (nbTiles != 0) {
//                tileToZoneLinkManager.addChildren(getData().getUUID(), getData().getTiles());
//            }
                indexManager.addIndex(getIndex());
            } catch (ByteBufferException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
    }
}
