package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.filemanagement.LinkManager;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DAOFileWriter;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import fr.univubs.inf1603.mahjong.engine.game.Game;
import fr.univubs.inf1603.mahjong.engine.game.GameException;
import fr.univubs.inf1603.mahjong.engine.game.MahjongBoard;
import fr.univubs.inf1603.mahjong.engine.game.MahjongGame;
import fr.univubs.inf1603.mahjong.engine.game.Move;
import fr.univubs.inf1603.mahjong.engine.game.MoveException;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import fr.univubs.inf1603.mahjong.engine.game.TileZoneIdentifier;
import fr.univubs.inf1603.mahjong.engine.rule.GameRule;
import fr.univubs.inf1603.mahjong.engine.rule.GameRuleFactory;
import fr.univubs.inf1603.mahjong.engine.rule.RulesException;
import fr.univubs.inf1603.mahjong.engine.rule.Wind;
import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe {@code FileGameDAO} gère la persistance des parties de Mahjong
 * {@code Game} {@link FileGameDAO.GameRow}.
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
     * Gestionnaire de liens entre les zones et les parties de Mahjong.
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
//        System.out.println(" -> FileGameDAO");
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
    protected DataRow<Game> getDataRow(int rowID, Game data, long pointer) throws DAOFileException {
        return new GameRow(rowID, data, pointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataRow<Game> getDataRow(DAOFileWriter writer, long pointer) throws DAOFileException {
        return new GameRow(writer, pointer);
    }

    /**
     * Supprime une partie de Mahjong <code>Game</code> du fichier de données.
     *
     * @param game Partie à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistance(Game game) throws DAOException { //TODO board
        try {
            MahjongBoard board = (MahjongBoard) ((MahjongGame) game).getBoard();
            zoneToGameLinkManager.removeChildren(getTileZones(board.getZones()));
            if (super.removeDataRow(game.getUUID())) {
                LOGGER.log(Level.INFO, "[INFO] {0} id={1} deleted from persistance", 
                        new Object[]{game.getClass().getSimpleName(), game.getUUID()});
            }
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
    }
    
    @Override
    public void deleteFromPersistance(List<Game> games) throws DAOFileException { //TODO check
        try {
            for (Game game : games) {
                deleteFromPersistance(game);
            } 
        } catch (DAOException ex) {
            throw new DAOFileException(ex.getMessage(), ex);
        }
    }

    private List<TileZone> getTileZones(EnumMap<TileZoneIdentifier, TileZone> mapsZone) {
        List<TileZone> zones = new ArrayList<>();
        mapsZone.entrySet().forEach((entry) -> {
            TileZoneIdentifier tzi = entry.getKey();
            TileZone zone = entry.getValue();
            zones.add(zone);
        });
//        System.out.println("getTilesZones size : " + zones.size());
        return zones;
    }

    /**
     * La classe <code>GameRow</code> répresente un tuple contenant une partie de Mahjong
     * {@link GameTile}.
     *
     * <pre>
     *
     *      Format d'une partie de Mahjong {@code GameTile} dans  un tuple :
     *
     *       UUID=16 | String=54 |    long=8    |    long=8    |  int[4]=16   |  byte[4]=4   | UUID=16 |    byte=1   | UUID=16 | byte=1  |  int=4   |    int=4   | 14 *(int=4 + byte=1) | -> (Byte)
     *       gameID  | ruleName  | stealingTime | playingTime  | playerPoints | playerWinds  | boardID | currentWind | moveID  |  wind   | priority | pathLenght |      pathLenght      |
     * ex :     -    |    -      |       -      |       -      |       -      |       -      |    -    |   e/s/w/n   |   -     | e/s/w/n |    -     |     -      |           -          |
     * </pre>
     *
     */
    class GameRow extends DataRow<Game> {

        /**
         * Taille d'un Board en octet
         */
        private static final int BOARD_SIZE = 16 + 1;                                                // 17
        /**
         * Taille d'une move en octet
         */
        private static final int MOVE_SIZE = 16 + 1 + 4 + (4+12) + (4 + 14*(4 + 1));                 // 97
        /**
         * Taille d'une partie de Mahjong en octet.
         */
        private static final int GAME_SIZE = 16 + 54 + 8 + 8 + 4*4 + 4*1 + BOARD_SIZE + MOVE_SIZE;   // 114
        /**
         * Taille d'un tuple de game.
         */
        static final int GAME_ROW_SIZE = ROW_HEADER_SIZE + GAME_SIZE;                                // 118

        GameRow(int rowID, Game data, long rowPointer) throws DAOFileException {
            super(rowID, data, GAME_SIZE, rowPointer);
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
            super(writer, GAME_SIZE, rowPointer);
        }

        /**
         * Change l'état d'un tuple de zone lorsque une nouvelle tuile est
         * rajoutée à la zone.
         *
         * @param evt Evenement
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(Game.LAST_PLAYED_MOVE_PROPERTY)) {
                setChanged(true);
            }
        }
        
        @Override
        protected Game readData(ByteBuffer buffer) throws DAOFileException {
            try {
                UUID gameID = new UUID(buffer.getLong(), buffer.getLong());
                String ruleName = DAOFileWriter.readString(buffer);
                GameRule rule = new GameRuleFactory().create(ruleName);
                Duration stealTime = Duration.ofMillis(buffer.getLong());
                Duration playTime = Duration.ofMillis(buffer.getLong());
                int[] playerPoints = new int[4];
                Wind[] playerWinds = new Wind[4];
                for(int i=0; i<playerPoints.length; i++) {
                    playerPoints[i] = buffer.getInt();
                }
                for(int i=0; i<playerWinds.length; i++) {
                    playerWinds[i] = MahjongUtilities.getWind((char) buffer.get());
                }

                UUID boardID = new UUID(buffer.getLong(), buffer.getLong());
                Wind currentWind = MahjongUtilities.getWind((char) buffer.get());
                EnumMap<TileZoneIdentifier, TileZone> enumMapZones = new EnumMap<>(TileZoneIdentifier.class);
                zoneToGameLinkManager.loadChildren(gameID).forEach((zone) -> {
                    enumMapZones.put(zone.getIdentifier(), zone);
                });
                MahjongBoard board = new MahjongBoard(currentWind, boardID, enumMapZones);
                
                Move lastPlayedMove = readMove(buffer);
                Game game = new MahjongGame(rule, board, lastPlayedMove, stealTime, playTime, playerPoints, gameID, playerWinds);
                return game;
            } catch (DAOFileWriterException | GameException | DAOException | RulesException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
            return null;
        }

        @Override
        protected int writeData(ByteBuffer buffer) throws DAOFileException {
            int startPosition = buffer.position();
            int ret = -1;
            try {
                MahjongGame game = (MahjongGame) getData();
                UUID gameID = game.getUUID();
                GameRule rule = game.getRule();
                Duration stealingTime = game.getPlayingTime();
                Duration playingTime = game.getPlayingTime();
                int[] playerPoints = game.getAllPlayerPoints();
                Wind[] playerWinds = game.getPlayerWinds();
                
                DAOFileWriter.writeUUID(buffer, gameID);
                DAOFileWriter.writeString(buffer, rule.getName());
                buffer.putLong(stealingTime.toMillis());
                buffer.putLong(playingTime.toMillis());
                for(int i=0; i<playerPoints.length; i++) {
                    buffer.putInt(playerPoints[i]);
                }
                for (Wind playerWind : playerWinds) {
                    buffer.put((byte) playerWind.getSymbol());
                }

                MahjongBoard board = (MahjongBoard) game.getBoard();
                DAOFileWriter.writeUUID(buffer, board.getUUID());
                buffer.put((byte) board.getCurrentWind().getSymbol());

                writeMove(buffer, game.getLastPlayedMove());

                List<TileZone> zones = getTileZones(board.getZones());
                if (!isWritedInFile()) { // première écriture dans le fichier de données
                    zoneToGameLinkManager.addChildren(gameID, zones);
                    setWritedInFile(true);
                    indexManager.addIndex(getIndex());
                }
                ret = buffer.position() - startPosition;
            } catch (DAOFileWriterException | GameException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
            return ret;
        }
        
        private boolean writeMove(ByteBuffer buffer, Move move) throws DAOFileException { //TODO tzi just one time
            if (buffer.capacity() - buffer.position() >= MOVE_SIZE - 1) {
                try {
                    DAOFileWriter.writeUUID(buffer, move.getUUID());
                    buffer.put((byte) move.getWind().getSymbol());
                    buffer.putInt(move.getPriority());
                    HashMap<Integer, TileZoneIdentifier> path = move.getPath();
//                map.get(map.keySet().toArray()[0]);
                    Map.Entry<Integer, TileZoneIdentifier> element = path.entrySet().stream().findAny().get();
                    DAOFileWriter.writeString(buffer, element.getValue().toString());
                    buffer.putInt(path.size());
                    System.out.println("mapPath");
                    path.keySet().forEach(key -> {
                        buffer.putInt(key);
                        buffer.put((move.getPubliclyVisible().get(key)) ? (byte) 1 : (byte) 0);
                    });
                    return true;
                } catch (DAOFileWriterException ex) {
                    throw new DAOFileException(ex.getMessage(), ex);
                }
            }
            return false;
        }

        private Move readMove(ByteBuffer buffer) throws DAOFileException {
            try {
            UUID moveID = new UUID(buffer.getLong(), buffer.getLong());
            Wind wind = MahjongUtilities.getWind((char) buffer.get());
            int priority = buffer.getInt();
            TileZoneIdentifier tzi = TileZoneIdentifier.valueOf(DAOFileWriter.readString(buffer));
            int pathLenght = buffer.getInt();
            HashMap<Integer, TileZoneIdentifier> path = new HashMap<>();
            HashMap<Integer, Boolean> publicalyVisible = new HashMap<>();
            for(int i=0; i<pathLenght; i++) {
                int tileIndex = buffer.getInt();
                path.put(tileIndex, tzi);
                publicalyVisible.put(tileIndex,  (buffer.get()==1));
            }
                return new Move(wind, priority, path, publicalyVisible, moveID);
            } catch (MoveException ex) {
                LOGGER.log(Level.WARNING, "Error when creating the lastPlayedMove : \n\t{0}", ex.getMessage());
                return null;
            } catch(DAOFileWriterException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
    }
}
