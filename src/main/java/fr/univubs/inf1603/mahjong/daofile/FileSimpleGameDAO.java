
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.filemanagement.PlayerToSimpleGameLinkManager;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.AbstractRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DAOFileWriter;
import fr.univubs.inf1603.mahjong.ai.Difficulty;
import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.sapi.SimpleGame;
import java.nio.file.Path;
import java.util.List;
import fr.univubs.inf1603.mahjong.dao.SimpleGameDAO;
import fr.univubs.inf1603.mahjong.daofile.exception.ByteBufferException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.game.Game;
import fr.univubs.inf1603.mahjong.engine.game.MahjongGame;
import fr.univubs.inf1603.mahjong.exceptions.DestroyedGameException;
import fr.univubs.inf1603.mahjong.sapi.Player;
import fr.univubs.inf1603.mahjong.sapi.SapiManager;
import fr.univubs.inf1603.mahjong.sapi.SimpleRule;
import fr.univubs.inf1603.mahjong.sapi.impl.SapiManagerImpl;
import fr.univubs.inf1603.mahjong.sapi.impl.SimpleGameImpl;
import fr.univubs.inf1603.mahjong.sapi.impl.SimpleRuleImpl;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe {@code FileSimpleGameDAO} gère la persistance des simple games
 * {@code SimpleGame} {@link FileSimpleGameDAO.SimpleGameRow}.
 *
 * @author aliyou, nesrine
 * @version 1.1.0
 */
public class FileSimpleGameDAO extends FileDAOMahjong<SimpleGame> implements SimpleGameDAO {

    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(FileSimpleGameDAO.class.getName());
    
    private static DAO<Game> gameDAO = null;
    
    /**
     * Gestionnaire de liens entre les joueurs et les parties.
     */
    private static PlayerToSimpleGameLinkManager playerToSimpleGameLinkManager;
    
    private Game game = null;
    private static SapiManager sapiManager = null;
    
    
    /**
     * Constructeur avec un Chemin d'accès du répertoire racine
     * <code>rootDirPath</code>.
     *
     * @param rootDirPath Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @param gameDAO DAO qui gère les parties.
     * @throws DAOFileException s'il ya une erreur lors de l'instanciation.
     */
    public FileSimpleGameDAO(Path rootDirPath, DAO<Game> gameDAO) throws DAOFileException {
        super(rootDirPath, "simpleGame.data", "simpleGame.index", SimpleGameRow.SIMPLE_GAME_ROW_SIZE);
        System.out.println(" -> FileSimpleGameDAO");
        FileSimpleGameDAO.gameDAO = gameDAO;
    }

    /**
     * Définit le gestionnaire de liens.
     * 
     * @param playerToGameLinkManager 
     */
    void setLinkManager(PlayerToSimpleGameLinkManager playerToGameLinkManager) {
        if(FileSimpleGameDAO.playerToSimpleGameLinkManager == null) {
            FileSimpleGameDAO.playerToSimpleGameLinkManager = playerToGameLinkManager;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow<SimpleGame> getDataRow(int rowID, SimpleGame data, long pointer) throws DAOFileException {
        return new SimpleGameRow(rowID, data, rowID, pointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow<SimpleGame> getDataRow(DAOFileWriter writer, long pointer) throws DAOFileException {
        return new SimpleGameRow(writer, pointer);
    }

    /**
     * Supprime une simple game <code>SimpleGame</code> du fichier de données.
     *
     * @param simpleGame Simple Game à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistance(SimpleGame simpleGame) throws DAOException {
        try {
            playerToSimpleGameLinkManager.removeChildren(simpleGame);
            if (super.removeDataRow(simpleGame.getUUID())) {
                LOGGER.log(Level.INFO, "[INFO] {0} id='{1} deleted from persistance", new Object[]{simpleGame.getClass().getSimpleName(), simpleGame.getUUID()});
            }
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
    }
    
    private void setGame(Game game) {
        this.game = game;
    }

    @Override
    public List<String> loadPersistedNames() {
        List<String> names = new ArrayList<>();
        return names;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void save(SimpleGame simpleGame, Game game) throws DAOException {
        setGame(game);
        super.save(simpleGame);
        gameDAO.save(game);
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void setSapiManager(SapiManager sp) {
        FileSimpleGameDAO.sapiManager = sp;
    }
    
    /**
     * La classe <code>SimpleGameRow</code> répresente un tuple de simple game
     * {@link SimpleGame}. C'est un conteneur pour un simple game.
     *
     * <pre>
     *
     *      Format d'un simple game dans un tuple dans le fichier de données :
     *
     *          UUID=16    |  String=54 | string=54 |    int=4   |   int=4  | string=11  | UUID=16      | -> (Byte)
     *        simpleGameID |  gameName  | ruleName  |  stealTime | playTime | difficulty |MahjongGameID |
     *   ex :       -      |      -     |     -     |      -     |    -     |  MEDIUM    |              |
     * </pre>  
     */
    class SimpleGameRow extends DataRow<SimpleGame> {

        /**
         * Taille d'un simple game.
         */
        private static final int SIMPLE_GAME_SIZE = 16 + 54 + 54 + 4 + 4 + 11 + 16;
        /**
         * Taille d'un tuple de de simple game.
         */
        static final int SIMPLE_GAME_ROW_SIZE = ROW_HEADER_SIZE + SIMPLE_GAME_SIZE;  // 32
        
        /**
         * Constructeur avec un identifiant de tuple <code>rowID</code>, une
         * simple game <code>data</code> et un pointeur de tuple
         * <code>rowPointer</code>.
         *
         * @param rowID Identifiant d'un tuple.
         * @param data Simple Game à encapsuler dans un tuple.
         * @param rowPointer Pointeur d'un tuple.
         */
        public SimpleGameRow(int rowID, SimpleGame data, int dataSize, long rowPointer) throws DAOFileException {
            super(rowID, data, dataSize, rowPointer);
        }

        /**
         * Constructeur avec un processus qui éffectue des opérations
         * d'entrée/sortie sur un fichier <code>writer</code> et un pointeur de
         * tuple <code>rowPointer</code>..
         *
         * @param writer Processus qui éffectue des opérations d'entrée/sortie
         * sur un fichier.
         * @param rowPointer Pointeur d'un tuple.
         * @throws DAOFileException s'il y'a une erruer lors de la lecture.
         */
        public SimpleGameRow(DAOFileWriter writer, long rowPointer) throws DAOFileException {
            super(writer, SIMPLE_GAME_ROW_SIZE, rowPointer);
        }

        /**
         * Lis une simple game <code>SimpleGame</code> à partir d'un tampon d'octets
         * <code>buffer</code>.
         *
         * @param buffer Tampon d'octets .
         */
        @Override
        protected SimpleGame readData(ByteBuffer buffer) throws DAOFileException {
            SimpleGame simpleGame = null;
            try {
                UUID simpleGameID = new UUID(buffer.getLong(), buffer.getLong());
                String gameName = DAOFileWriter.readString(buffer);
                String ruleName = DAOFileWriter.readString(buffer);
                SimpleRule rule = new SimpleRuleImpl(ruleName, "");
                int stealTime = buffer.getInt();
                int playTime = buffer.getInt();
                Difficulty difficulty = Difficulty.valueOf(DAOFileWriter.readString(buffer));
                
                Player[] playersArray = (Player[]) playerToSimpleGameLinkManager.loadChildren(simpleGameID, rule).toArray();
                
                UUID gameID = new UUID(buffer.getLong(), buffer.getLong());
                MahjongGame loadedGame = (MahjongGame) gameDAO.find(gameID);
                
                simpleGame = new SimpleGameImpl(playersArray, rule, simpleGameID, gameName, (SapiManagerImpl) sapiManager, stealTime, playTime, difficulty, loadedGame);
            } catch (DAOException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
            return simpleGame;
        }

        /**
         * Ecrit une simple game dans un tampon d'octet <code>buffer</code>.
         *
         * @param buffer Tampon d'octet.
         */
        @Override
        protected void writeData(ByteBuffer buffer) throws DAOFileException {
            if (game != null) {
                try {
                    DAOFileWriter.writeUUID(buffer, getData().getUUID());
                    DAOFileWriter.writeString(buffer, getData().getName());
                    DAOFileWriter.writeString(buffer, getData().getRule().getName());
                    buffer.putInt(getData().getStealTime());
                    buffer.putInt(getData().getPlayTime());
                    DAOFileWriter.writeString(buffer, getData().getSurrenderDifficulty().toString());
                    DAOFileWriter.writeUUID(buffer, game.getUUID());

                    playerToSimpleGameLinkManager.addChildren(getData());

                    indexManager.addIndex(getIndex());
                } catch (ByteBufferException | DestroyedGameException ex) {
                    throw new DAOFileException(ex.getMessage(), ex);
                }
                game = null;
            } else {
                System.out.println("game est null");
            }
        }
    }
}
