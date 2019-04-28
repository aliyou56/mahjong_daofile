package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.SapiGameDAO;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DAOFileWriter;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.FileHeaderRow;
import fr.univubs.inf1603.mahjong.engine.game.Game;
import fr.univubs.inf1603.mahjong.engine.game.MahjongGame;
import fr.univubs.inf1603.mahjong.sapi.Difficulty;
import fr.univubs.inf1603.mahjong.sapi.impl.SapiGame;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliyou
 */
public class FileSapiGameDAO extends FileDAOMahjong<SapiGame> implements SapiGameDAO {

    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(FileSapiGameDAO.class.getName());

    /**
     * DAO qui gère les parties.
     */
    private static DAO<Game> gameDAO = null;

    private HashMap<String, UUID> mapNameUUID; 
    
    /**
     * Constructeur avec un Chemin d'accès du répertoire racine
     * <code>rootDirPath</code>.
     *
     * @param rootDirPath Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @param gameDAO DAO qui gère les parties.
     * @throws DAOFileException s'il ya une erreur lors de l'instanciation.
     */
    public FileSapiGameDAO(Path rootDirPath, DAO<Game> gameDAO) throws DAOFileException {
        super(rootDirPath, "sapiGame.data", "sapiGame.index", SapiGameRow.SAPI_GAME_ROW_SIZE);
//        System.out.println(" -> FileSapiGameDAO");
        FileSapiGameDAO.gameDAO = gameDAO;
        this.mapNameUUID = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataRow<SapiGame> getDataRow(int rowID, SapiGame data, long pointer) throws DAOFileException {
        return new SapiGameRow(rowID, data, pointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataRow<SapiGame> getDataRow(DAOFileWriter writer, long pointer) throws DAOFileException {
        return new SapiGameRow(writer, pointer);
    }

    /**
     * Supprime une vision simplifiée d'une partie de Mahjong
     * <code>SimpleGame</code> du fichier de données.
     *
     * @param sapiGame Simple Game à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistance(SapiGame sapiGame) throws DAOException {
        try {
            gameDAO.delete(sapiGame.getUUID());
            if (super.removeDataRow(sapiGame.getUUID())) {
                if (mapNameUUID.containsKey(sapiGame.getName())) {
                    mapNameUUID.remove(sapiGame.getName());
                }
                LOGGER.log(Level.INFO, "{0} id={1} deleted from persistance",
                        new Object[]{sapiGame.getClass().getSimpleName(), sapiGame.getUUID()});
            }
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> loadPersistedNames() throws DAOException {
        List<String> names = new ArrayList<>();
        long rowPointer = FileHeaderRow.FILE_HEADER_ROW_SIZE;
        int lenght = 16 + 54;
        try {
            for (int i = 0; i < getRowNumber(); i++) {
                ByteBuffer buffer = dataWriter.read(rowPointer, lenght); 
                if (buffer != null) {
                    UUID gameID = new UUID(buffer.getLong(), buffer.getLong());
                    String gameName = DAOFileWriter.readString(buffer);
                    names.add(gameName);
                    mapNameUUID.put(gameName, gameID);
                }
                rowPointer += SapiGameRow.SAPI_GAME_ROW_SIZE;
            }
        } catch (DAOFileWriterException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
            throw new DAOException(ex.getMessage(), ex);
        }
        return names;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SapiGame find(String gameName) throws DAOException {
        FileDAOUtilities.checkNotNull("gameName", gameName);
        if (mapNameUUID.containsKey(gameName)) {
            UUID gameID = mapNameUUID.get(gameName);
            return super.find(gameID);
        }
        return null;
    }

    /**
     * La classe <code>SimpleGameRow</code> répresente un tuple de simple game
     * {@link SimpleGame}. C'est un conteneur pour un simple game.
     * <br>
     * La taille maximale pour un nom de partie ainsi qu'un nom de règle est de
     * 50 octets. Si la taille d'un nom dépasse la taille maximale, le nom est
     * tronqué à la position correspondant à la taille maximale.
     *
     * <pre>
     *
     *      Format d'une vison simplifiée d'une partie de Mahjong #@code SimpleGame} dans un tuple :
     *
     *          UUID=16  |  String=54 | string=11  |  -> (Byte) 81
     *           gameID  |  gameName  | difficulty |
     *   ex :       -    |      -     |   MEDIUM   |
     * </pre>
     */
    class SapiGameRow extends DataRow<SapiGame> {

        /**
         * Taille maximale d'un nom en octet.
         */
        static final int NAME_SIZE = 50;
        /**
         * Taille d'une vison simplifiée d'une partie de Mahjong
         * {@code simpleGame}..
         */
        private static final int SAPI_GAME_SIZE = 16 + (4 + NAME_SIZE) + (4 + 11);  // 81
        /**
         * Taille d'un tuple de la vison simplifiée d'une partie de Mahjong
         * {@code simpleGame}.
         */
        static final int SAPI_GAME_ROW_SIZE = ROW_HEADER_SIZE + SAPI_GAME_SIZE;  // 85

        /**
         * Constructeur avec un identifiant de tuple <code>rowID</code>, une
         * simple game <code>data</code> et un pointeur de tuple
         * <code>rowPointer</code>.
         *
         * @param rowID Identifiant d'un tuple.
         * @param data La vision simplifiée d'une partie de Mahjong
         * {@code SimpleGame} à encapsuler dans un tuple.
         * @param rowPointer Pointeur d'un tuple.
         */
        SapiGameRow(int rowID, SapiGame data, long rowPointer) throws DAOFileException {
            super(rowID, data, SAPI_GAME_ROW_SIZE, rowPointer);
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
        SapiGameRow(DAOFileWriter writer, long rowPointer) throws DAOFileException {
            super(writer, SAPI_GAME_ROW_SIZE, rowPointer);
        }

        /**
         * Renvoie un objet {code @SapiGame}
         * lu à partir d'un tampon d'octets '@code buffer}.
         *
         * @param buffer Tampon d'octets.
         */
        @Override
        protected SapiGame readData(ByteBuffer buffer) throws DAOFileException { 
            try {
                UUID gameID = new UUID(buffer.getLong(), buffer.getLong());
                String gameName = DAOFileWriter.readString(buffer);
                Difficulty difficulty = Difficulty.valueOf(DAOFileWriter.readString(buffer));

                MahjongGame loadedGame = (MahjongGame) gameDAO.find(gameID);

                return new SapiGame(gameName, difficulty, loadedGame);
            } catch (DAOFileWriterException | DAOException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }

        /**
         * Ecrit un objet {@code SapiGame} dans un
         * tampon d'octet <code>buffer</code>.
         *
         * @param buffer Tampon d'octet.
         */
        @Override
        protected int writeData(ByteBuffer buffer) throws DAOFileException {
            try {
                int startPosition = buffer.position();
                UUID sapiGameID = getData().getUUID();
                String gameName = FileDAOUtilities.checkStringLenght(getData().getName(), NAME_SIZE);
                Difficulty surrenderDifficulty = getData().getSurrenderDifficulty();
                
                DAOFileWriter.writeUUID(buffer, sapiGameID);
                DAOFileWriter.writeString(buffer, gameName);
                DAOFileWriter.writeString(buffer, surrenderDifficulty.toString());
                
                try {
                    gameDAO.save(getData().getGame());
                } catch (DAOException ex) {
                    Logger.getLogger(FileSapiGameDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                indexManager.addIndex(getIndex());
                return buffer.position() - startPosition;
            } catch (DAOFileWriterException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
    }
}
