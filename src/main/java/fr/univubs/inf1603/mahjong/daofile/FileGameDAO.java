package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.Wind;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.LinkManager;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DAOFileWriter;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import fr.univubs.inf1603.mahjong.engine.game.Board;
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
 * @version 1.2.5
 */
public class FileGameDAO extends FileDAOMahjong<Game> {

    /**
     *  Contient l'Instance du DAO qui gère les parties de Mahjong.
     */
    private static FileGameDAO instance;
    
    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(FileGameDAO.class.getName());

    /**
     * Gestionnaire de liens entre les zones et les parties de Mahjong.
     */
    private final LinkManager<TileZone> zoneToGameLinkManager;

    /**
     * Constructeur privé avec un Chemin d'accès du répertoire racine
     * <code>rootDirPath</code>.
     *
     * @param rootDirPath Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @throws DAOFileException s'il ya une erreur lors de l'instanciation.
     */
    private FileGameDAO(Path rootDirPath) throws DAOFileException {
        super(rootDirPath, "game.data", "game.index", GameRow.GAME_ROW_SIZE);
        zoneToGameLinkManager = FileZoneDAO.getInstance(rootDirPath).getLinkManager();
    }
    
    /**
     * Renvoie l'instance du DAO qui gère les parties de Mahjong.
     * 
     * @param rootDir Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @return L'instance du DAO qui gère les parties de Mahjong.
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation.
     */
    static FileGameDAO getInstance(Path rootDir) throws DAOFileException {
        if(instance == null) {
            instance = new FileGameDAO(rootDir);
        }
        return instance;
    }

    /**
     * Renvoie un tuple de partie de Mahjong {@code FileGameRow} d'identifiant
     * {@code rowID} et de pointeur de tuple {@code rowPointer}.
     *
     * @param rowID Identifiant d'un tuple.
     * @param data Partie de Mahjong {@code Game} à encapsuler dans le tuple.
     * @param rowPointer Pointeur de tuple.
     * @return Nouveau tuple de partie de Mahjong {@code FileGameRow}.
     * @throws DAOFileException s'il y'a une erreur lors de la création du
     * tuple.
     */
    @Override
    protected DataRow<Game> getDataRow(int rowID, Game data, long rowPointer) throws DAOFileException {
        return new GameRow(rowID, data, rowPointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataRow<Game> getDataRow(long rowPointer) throws DAOFileException {
        return new GameRow(dataWriter, rowPointer);
    }

    /**
     * Supprime une partie de Mahjong {@code Game} du fichier de données.
     * L'esemble des zones {@code TileZone} du plateau de jeu {@code Board} sont
     * d'abord supprimées.
     *
     * @param game Partie de Mahjong à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistence(Game game) throws DAOException {
        try {
            MahjongBoard board = (MahjongBoard) ((MahjongGame) game).getBoard();
            if (board != null) {
                zoneToGameLinkManager.removeChildren(getTileZones(board.getZones()));
            }
            if (super.removeDataRow(game.getUUID())) {
                LOGGER.log(Level.INFO, " {0} id={1} deleted from persistance",
                        new Object[]{game.getClass().getSimpleName(), game.getUUID()});
            }
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
    }

    /**
     * Supprime une liste de parties de Mahjong {@code Game} du fichier de
     * données.
     *
     * @param games Liste de parties de Mahjong
     * @throws DAOFileException s'il y'a une erreur lors de la suppression.
     */
    @Override
    public void delete(List<Game> games) throws DAOFileException { //TODO check
        try {
            for (Game game : games) {
                deleteFromPersistence(game);
            }
        } catch (DAOException ex) {
            throw new DAOFileException(ex.getMessage(), ex);
        }
    }

    private List<TileZone> getTileZones(EnumMap<TileZoneIdentifier, TileZone> mapsZone) {
        List<TileZone> zones = new ArrayList<>();
        if (mapsZone != null) {
            mapsZone.entrySet().forEach((entry) -> {
                TileZoneIdentifier tzi = entry.getKey();
                TileZone zone = entry.getValue();
                zones.add(zone);
            });
        }
        return zones;
    }

    /**
     * La classe <code>GameRow</code> répresente un tuple contenant une partie
     * de Mahjong {@link Game}.
     *
     * <pre>
     *
     *      Format d'une partie de Mahjong {@code Game} dans un tuple :
     *
     *       UUID=16 | String=54 |    long=8    |    long=8    |  int[4]=16   |  byte[4]=4   |  -> (Byte)
     *       gameID  | ruleName  | stealingTime | playingTime  | playerPoints | playerWinds  |
     * ex :     -    |    -      |       -      |       -      |       -      |       -      |
     *
     *       + {@literal ->} Board
     *             | UUID=16 |    byte=1   | -> (Byte)
     *             | boardID | currentWind |
     *       ex :  |    -    |   e/s/w/n   |
     *
     *       + {@literal ->} Move
     *             | UUID=16 | byte=1  |  int=4   |    int=4   | 14 *(int=4 + byte=1) | -> (Byte)
     *             | moveID  |  wind   | priority | pathLenght |      pathLenght      |
     *       ex :  |   -     | e/s/w/n |    -     |     -      |           -          |
     *
     *
     * </pre>
     *
     */
    class GameRow extends DataRow<Game> {

        /**
         * Taille d'un Board en octet. boardID | currentWind
         */
        private static final int BOARD_SIZE = 16 + 1;                                              // 17
        /**
         * Taille d'une Move en octet. moveID=16 | wind=1 | priority=4 |
         * TileZoneIdentifier=4+12 | nbTiles=4 | 14 * (tilrIndex=4 +
         * isPublicalyVisible=1)
         */
        private static final int MOVE_SIZE = 16 + 1 + 4 + (4 + 12) + (4 + 14 * (4 + 1));              // 111
        /**
         * Taille d'une partie de Mahjong en octet.
         */
        private static final int GAME_SIZE = 16 + 54 + 8 + 8 + 4 * 4 + 4 * 1 + BOARD_SIZE + MOVE_SIZE;   // 234
        /**
         * Taille d'un tuple de game.
         */
        static final int GAME_ROW_SIZE = ROW_HEADER_SIZE + GAME_SIZE;                                // 238

        /**
         * Constructeur avec un identifiant de tuple {@code rowID}, une partie
         * de Mahjong {@code data} et un pointeur de tuple {@code rowPointer}
         *
         * @param rowID Identifiant de tuple. DOIT ETRE POSITIF.
         * @param data Partie de Mahjong {@code Game}. NE DOIT PAS ETRE NULL.
         * @param rowPointer Pointeur d'un tuple. Pointe sur le début du tuple
         * dans un fichier. DOIT ETRE POSITIF.
         * @throws DAOFileException s'il y'a une erreur lors de l'instanciation.
         */
        GameRow(int rowID, Game data, long rowPointer) throws DAOFileException {
            super(rowID, data, GAME_SIZE, rowPointer);
            Board board = ((MahjongGame) this.getData()).getBoard();
            FileDAOUtilities.checkNotNull("board", board);
            FileDAOUtilities.checkNotNull("lastPlayedMove", data.getLastPlayedMove());
//            if(board != null) {
//            board.addPropertyChangeListener(this);
//            }
        }

        /**
         * Constructeur avec un processus qui éffectue des opérations
         * d'entrée/sortie sur un fichier <code>writer</code> et un pointeur de
         * tuple <code>rowPointer</code>..
         *
         * @param writer Processus qui éffectue des opérations d'entrée/sortie
         * sur un fichier.
         * @param rowPointer Pointeur d'un tuple.
         * @throws DAOFileException s'il y'a une erreur lors de la lecture d'une
         * partie de Mahjong {@code Game}.
         */
        GameRow(DAOFileWriter writer, long rowPointer) throws DAOFileException {
            super(writer, GAME_SIZE, rowPointer);
//            Board board = ((MahjongGame) this.getData()).getBoard();
//            if(board != null) {
//            board.addPropertyChangeListener(this);
//            }
        }

        /**
         * Change l'état d'un tuple de zone lorsque une nouvelle tuile est
         * rajoutée à la zone.
         *
         * @param evt Evenement
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if (propertyName.equals(Game.LAST_PLAYED_MOVE_PROPERTY)) {
                setDirty(true);
            }
        }

        /**
         * Renvoie une partie de Mahjong {@code Game} lue depuis un tampon
         * d'octets.
         *
         * @param buffer Tampon d'octets.
         * @return Partie de Mahjong lue.
         * @throws DAOFileException s'il y'a une erreur lors de la lecture.
         */
        @Override
        protected Game readData(ByteBuffer buffer) throws DAOFileException {
            if (buffer.remaining() < GAME_SIZE) {
                String message = "Game can't be read from the buffer '" + buffer + "'"
                        + "\n\t cause -> Game size '" + GAME_SIZE + "' is greater than remaining bytes '" + buffer.remaining() + "'.";
                throw new DAOFileException(message);
            }
            int stratPosition = buffer.position();
            try {
                UUID gameID = new UUID(buffer.getLong(), buffer.getLong());
                String ruleName = DAOFileWriter.readString(buffer);
                GameRule rule;
                try {
                    rule = new GameRuleFactory().create(ruleName);
                } catch (RulesException ex) {
                    throw new DAOFileException("Couldn't create the rule. \n\t cause -> " + ex.getMessage());
                }
                Duration stealTime = Duration.ofMillis(buffer.getLong());
                Duration playTime = Duration.ofMillis(buffer.getLong());
                int[] playerPoints = new int[4];
                for (int i = 0; i < playerPoints.length; i++) {
                    playerPoints[i] = buffer.getInt();
                }
                Wind[] playerWinds = new Wind[4];
                for (int i = 0; i < playerWinds.length; i++) {
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

                try {
                    Game game = new MahjongGame(rule, board, lastPlayedMove, stealTime, playTime, playerPoints, gameID, playerWinds);
                    return game;
                } catch (GameException ex) {
                    throw new DAOFileException("Couldn't create the game. \n\t cause -> " + ex.getMessage());
                }
            } catch (DAOFileWriterException | DAOException ex) {
                buffer.position(stratPosition);
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }

        /**
         * Ecrit une partie de Mahjong {@code Game} dans un tampon d'octets.
         *
         * @param buffer Tampon d'octets
         * @return Le nombre d'octets écrit d'octets écrits dans le tampon
         * d'octets.
         * @throws DAOFileException s'il y'a une erreur lors de l'écriture.
         */
        @Override
        protected int writeData(ByteBuffer buffer) throws DAOFileException {
            if (buffer.remaining() < GAME_SIZE) {
                String message = "Remianing bytes '" + buffer.remaining() + "' is less than GAME_SIZE '"
                        + GAME_SIZE + "'";
                throw new DAOFileException(message);
            }
            int startPosition = buffer.position();
            try {
                MahjongGame game = (MahjongGame) getData();
                UUID gameID = game.getUUID();
                int[] playerPoints = game.getAllPlayerPoints();

                DAOFileWriter.writeUUID(buffer, game.getUUID());
                DAOFileWriter.writeString(buffer, game.getRule().getName());
                buffer.putLong(game.getPlayingTime().toMillis());
                buffer.putLong(game.getPlayingTime().toMillis());
                for (int i = 0; i < playerPoints.length; i++) {
                    buffer.putInt(playerPoints[i]);
                }
                for (Wind playerWind : game.getPlayerWinds()) {
                    buffer.put((byte) playerWind.getSymbol());
                }

                MahjongBoard board = (MahjongBoard) game.getBoard();
                DAOFileWriter.writeUUID(buffer, board.getUUID());
                buffer.put((byte) board.getCurrentWind().getSymbol());

                int nb = writeMove(buffer, game.getLastPlayedMove());

                List<TileZone> zones = getTileZones(board.getZones());
                if (!isWritedInFile()) { // première écriture dans le fichier de données
                    zoneToGameLinkManager.addLink(gameID, zones);
                    setWritedInFile(true);
                    indexManager.addIndex(getIndex());
                }
                return buffer.position() - startPosition;
            } catch (DAOFileWriterException | GameException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }

        /**
         * Ecrit une Move dans un tampon d'octets.
         *
         * @param buffer Tampon d'octets.
         * @param move Move à écrire dans le tampon d'octets.
         * @return Le nombre d'octes écrits dans le tampon d'octets.
         * @throws DAOFileException s'il y'a une erreur lors de l'écriture.
         */
        private int writeMove(ByteBuffer buffer, Move move) throws DAOFileException {
            if (buffer.remaining() < MOVE_SIZE) {
//                return -1;
                String message = "Remianing bytes '" + buffer.remaining() + "' is less than MOVE_SIZE '"
                        + MOVE_SIZE + "'";
                throw new DAOFileException(message);
            }
            int startPosition = buffer.position();
            try {
                DAOFileWriter.writeUUID(buffer, move.getUUID());
                buffer.put((byte) move.getWind().getSymbol());
                buffer.putInt(move.getPriority());
                HashMap<Integer, TileZoneIdentifier> path = move.getPath();
                Map.Entry<Integer, TileZoneIdentifier> element = path.entrySet().stream().findAny().get();
                DAOFileWriter.writeString(buffer, element.getValue().toString());
                buffer.putInt(path.size());
                path.keySet().forEach(key -> {
                    buffer.putInt(key);
                    buffer.put((move.getPubliclyVisible().get(key)) ? (byte) 1 : (byte) 0);
                });
                return buffer.position() - startPosition;
            } catch (DAOFileWriterException ex) {
                buffer.position(startPosition);
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }

        /**
         * Renvoie une Move lue depuis un tampon d'octets.
         *
         * @param buffer Tampon d'octets
         * @return Move réprésentant le {@code lastPlayedMove}.
         * @throws DAOFileException s'il y'a une erreur lors de lecture.
         */
        private Move readMove(ByteBuffer buffer) throws DAOFileException {
            if (buffer.remaining() < MOVE_SIZE) {
//                return null;
                String message = "Move can't be read from the buffer '" + buffer + "'"
                        + "\n\t cause -> Move size '" + MOVE_SIZE + "' is greater than remaining bytes '" + buffer.remaining() + "'.";
                throw new DAOFileException(message);
            }
            int startPosition = buffer.position();
            try {
                UUID moveID = new UUID(buffer.getLong(), buffer.getLong());
                Wind wind = MahjongUtilities.getWind((char) buffer.get());
                int priority = buffer.getInt();
                TileZoneIdentifier tzi = TileZoneIdentifier.valueOf(DAOFileWriter.readString(buffer));
                int pathLenght = buffer.getInt();
                HashMap<Integer, TileZoneIdentifier> path = new HashMap<>();
                HashMap<Integer, Boolean> publicalyVisible = new HashMap<>();
                for (int i = 0; i < pathLenght; i++) {
                    int tileIndex = buffer.getInt();
                    path.put(tileIndex, tzi);
                    publicalyVisible.put(tileIndex, (buffer.get() == 1));
                }
                return new Move(wind, priority, path, publicalyVisible, moveID);
            } catch (DAOFileWriterException ex) {
                buffer.position(startPosition);
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
    }
}
