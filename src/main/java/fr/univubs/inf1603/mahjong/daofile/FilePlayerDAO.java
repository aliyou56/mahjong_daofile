package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.filemanagement.PlayerToSimpleGameLinkManager;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.AbstractRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DAOFileWriter;
import fr.univubs.inf1603.mahjong.ai.Bot;
import fr.univubs.inf1603.mahjong.ai.BotDifficulties.Difficulty;
import fr.univubs.inf1603.mahjong.ai.BotFactory;
import fr.univubs.inf1603.mahjong.ai.exceptions.InvalidDifficultyException;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.exception.ByteBufferException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.rule.Wind;
import fr.univubs.inf1603.mahjong.sapi.HumanPlayer;
import fr.univubs.inf1603.mahjong.sapi.Player;
import fr.univubs.inf1603.mahjong.sapi.SimpleRule;
import fr.univubs.inf1603.mahjong.sapi.impl.HumanPlayerImpl;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe {@code FilePlayerDAO} gère la persistance des joueurs
 * {@code Player} {@link FilePlayerDAO.PlayerRow}.
 *
 * @author aliyou, nesrine
 * @version 1.1.0
 */
public class FilePlayerDAO extends FileDAOMahjong<Player> {

    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(FilePlayerDAO.class.getName());

    /**
     * Gestionnaire de liens entre les tuiles et les zones.
     */
    private static PlayerToSimpleGameLinkManager playerToSimpleGameLinkManager;

    private SimpleRule rule;

    private BotFactory botFactory;

    /**
     * Constructeur avec un Chemin d'accès du répertoire racine
     * <code>rootDirPath</code>.
     *
     * @param rootDirPath Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @throws DAOFileException s'il ya une erreur lors de l'instanciation.
     */
    public FilePlayerDAO(Path rootDirPath) throws DAOFileException {
        super(rootDirPath, "player.data", "player.index", PlayerRow.PLAYER_ROW_SIZE);
        System.out.println(" -> FilePlayerDAO");
//        this.botFactory = new BotFactory(); 
    }

    public void setRule(SimpleRule rule) {
        this.rule = rule;
    }

    /**
     * Définit le gestionnaire de liens.
     *
     * @param playerToSimpleGameLinkManager Gestionnaire de liens.
     */
    void setLinkManager(PlayerToSimpleGameLinkManager playerToSimpleGameLinkManager) {
        if (FilePlayerDAO.playerToSimpleGameLinkManager == null) {
            FilePlayerDAO.playerToSimpleGameLinkManager = playerToSimpleGameLinkManager;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow<Player> getDataRow(int rowID, Player data, long pointer) throws DAOFileException {
        return new PlayerRow(rowID, data, pointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow<Player> getDataRow(DAOFileWriter writer, long pointer) throws DAOFileException {
        return new PlayerRow(writer, pointer);
    }

    /**
     * Supprime un joueur <code>Player</code> du fichier de données si le joueur
     * n'est relié à aucune simple game <code>SimpleGame</code>.
     *
     * @param player Joueur à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistance(Player player) throws DAOException {
        try {
            if (playerToSimpleGameLinkManager.getRow(player.getUUID()) == null) {
                if (super.removeDataRow(player.getUUID())) {
                    LOGGER.log(Level.INFO, "[INFO] {0} id='{1} deleted from persistance", new Object[]{player.getClass().getSimpleName(), player.getUUID()});
                }
            } else {
                LOGGER.log(Level.INFO, "Player id={0} canno't be deleted cause it is linked to a simple game", player.getUUID());
            }
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
    }

    /**
     * La classe <code>PlayerRow</code> répresente un tuple de joueur
     * {@link Player}. C'est un conteneur pour un joueur.
     *
     * <pre>
     *
     *      Format d'un joueur dans un tuple dans le fichier de données :
     *
     *          UUID=16    |   byte=1  |   String=54   |   byte=1   |   String=11  | -> (Byte)
     *           playerID  |   type    |     name      |    wind    |  difficulty  |
     *   ex :       -      |   H / B   |       -       |   e/s/w/n  |    MEDIUM    |
     * </pre>
     *
     */
    class PlayerRow extends DataRow<Player> {

        static final int NAME_SIZE = 50;

        private static final int PLAYER_GAME_SIZE = 16 + 1 + (4 + NAME_SIZE) + 1 + 11;
        /**
         * Taille d'un tuple de joueur.
         */
        static final int PLAYER_ROW_SIZE = ROW_HEADER_SIZE + PLAYER_GAME_SIZE;

        /**
         * Constructeur avec l'identifiant d'un tuple <code>rowID</code>, un
         * joueur <code>data</code> et un pointeur de tuple
         * <code>rowPointer</code>.
         *
         * @param rowID Identifiant du tuple.
         * @param data Joueur encapsulé dans le tuple.
         * @param rowPointer Pointeur de tuple.
         */
        public PlayerRow(int rowID, Player data, long rowPointer) throws DAOFileException {
            super(rowID, data, PLAYER_ROW_SIZE, rowPointer);
        }

        /**
         * Constructeur avec un processus qui éffectue des opérations
         * d'entrée/sortie sur un fichier <code>writer</code> et un pointeur de
         * tuple <code>rowPointer</code>..
         *
         * @param writer Processus qui éffectue des opérations d'entrée/sortie
         * sur un fichier.
         * @param rowPointer Pointeur d'un tuple.
         * @throws DAOFileException s'il y'a une erruer lors de la lecture d'un
         * joueur <code>Player</code>.
         */
        public PlayerRow(DAOFileWriter writer, long rowPointer) throws DAOFileException {
            super(writer, PLAYER_ROW_SIZE, rowPointer);
        }

        /**
         * Lis un joueur <code>Player</code> à partir d'un tampon d'octets
         * <code>buffer</code>.
         *
         * @param buffer Tampon d'octets.
         */
        @Override
        protected Player readData(ByteBuffer buffer) throws DAOFileException {
            Player player = null;
//            try {
            UUID playerID = new UUID(buffer.getLong(), buffer.getLong());
            char type = (char) buffer.get();
            String name = DAOFileWriter.readString(buffer);
            Wind wind = MahjongUtilities.getWind((char) buffer.get());
            if (type == 'H') {
                player = new HumanPlayerImpl(name, playerID);
                player.setWind(wind);
            } else {
//                    Difficulty dificulty = Difficulty.valueOf(DAOFileWriter.readString(buffer));
//                    player = botFactory.createBot(name, dificulty, rule, playerID);
            }
//                player.setWind(wind);
//                rule = null;
//            } catch (InvalidDifficultyException ex) {
//                throw new DAOException(ex.getMessage());
//            }
            return player;
        }

        /**
         * Ecrit un joueur dans un tampon d'octet <code>buffer</code>.
         *
         * @param buffer Tampon d'octet.
         */
        @Override
        protected void writeData(ByteBuffer buffer) throws DAOFileException {
            try {
                System.err.println("writeData");
                UUID playerID = getData().getUUID();
                System.err.println("playerID : " + playerID);
                DAOFileWriter.writeUUID(buffer, playerID);
                char type = (getData() instanceof HumanPlayer) ? 'H' : 'B';
                System.err.println("type : " + type);
                buffer.put((byte) type);
                String name = FileDAOUtilities.checkStringLenght(getData().getName(), NAME_SIZE);
                System.err.println("name : " + name);
                DAOFileWriter.writeString(buffer, name);
                char symbol = getData().getWind().getSymbol();
                System.err.println("wind : " + symbol);
                buffer.put((byte) symbol);
                if (type == 'B') {
                    Bot bot = (Bot) getData();
                    DAOFileWriter.writeString(buffer, bot.getDifficulty().toString());
                }
                indexManager.addIndex(getIndex());
                System.err.println("writeData end");
            } catch (ByteBufferException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
    }
}
