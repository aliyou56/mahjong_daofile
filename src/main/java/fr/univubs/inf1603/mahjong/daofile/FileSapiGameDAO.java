package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.SapiGameDAO;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.AbstractRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DAOFileWriter;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.FileHeaderRow;
import fr.univubs.inf1603.mahjong.engine.game.MahjongGame;
import fr.univubs.inf1603.mahjong.sapi.Difficulty;
import fr.univubs.inf1603.mahjong.sapi.impl.SapiGame;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe {@code FileSapiGameDAO} gère la persistance des objets
 * {@code SapiGame} {@link FileSapiGameDAO.SapiGameRow}.
 *
 * @author aliyou, nesrine
 * @version 1.3.0
 */
public class FileSapiGameDAO extends FileDAOMahjong<SapiGame> implements SapiGameDAO, Serializable {

    /**
     *  Contient l'Instance du DAO qui gère les objets {@code SapiGame}.
     */
    private static FileSapiGameDAO instance;
    
    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(FileSapiGameDAO.class.getName());

    /**
     * DAO qui gère les parties de Mahjong {@code Game}.
     */
    private final FileGameDAO gameDAO;

    /**
     * Tableau associatif gardant le lien entre les noms de parties et les
     * identifiants.
     */
    private HashMap<String, UUID> mapGameNameUUID;

    /**
     * Constructeur privé avec un Chemin d'accès du répertoire racine
     * <code>rootDirPath</code>.
     *
     * @param rootDirPath Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @param gameDAO DAO qui gère les parties.
     * @throws DAOFileException s'il ya une erreur lors de l'instanciation.
     */
    private FileSapiGameDAO(Path rootDirPath) throws DAOFileException {
        super(rootDirPath, "sapiGame.data", "sapiGame.index", SapiGameRow.SAPI_GAME_ROW_SIZE);
        gameDAO = FileGameDAO.getInstance(rootDirPath);
        this.mapGameNameUUID = new HashMap<>();
    }
    
    /**
     * Renvoie l'instance du DAO qui gère les objets {@code SapiGame}.
     * 
     * @param rootDir Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @return L'instance du DAO qui gère les objets {@code SapiGame}.
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation.
     */
    static FileSapiGameDAO getInstance(Path rootDir) throws DAOFileException {
        if(instance == null) {
            instance = new FileSapiGameDAO(rootDir);
        }
        return instance;
    }

    /**
     * Charge les noms de tous les parties de Mahjong persistés. Les noms sont
     * gardés dans un tableau associatif avec les identifiants comme valeur.
     */
    private void fillMap() {
        long rowPointer = FileHeaderRow.FILE_HEADER_ROW_SIZE + AbstractRow.ROW_HEADER_SIZE;
        int lenght = 16 + 54;
        LOGGER.log(Level.INFO, "rowNumber : {0} | mapNameUUID size : {1} | lenght : {2} ",
                new Object[]{getRowNumber(), mapGameNameUUID.size(), lenght});
        if (getRowNumber() > mapGameNameUUID.size()) {
            for (int i = 0; i < getRowNumber(); i++) {
                try {
                    if (dataWriter.getFileLenght() > rowPointer) {
                        ByteBuffer buffer = dataWriter.read(rowPointer, lenght);
                        if (buffer != null) {
                            UUID gameID = new UUID(buffer.getLong(), buffer.getLong());
                            String gameName = DAOFileWriter.readString(buffer);
                            mapGameNameUUID.put(gameName, gameID);
                            LOGGER.log(Level.INFO, "name : {0} -> id : {1}", new Object[]{gameName, gameID});
                        }
                    }
                    rowPointer += SapiGameRow.SAPI_GAME_ROW_SIZE;
                } catch (DAOFileWriterException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage());
                }
            }

        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected DataRow<SapiGame> getDataRow(int rowID, SapiGame data, long pointer) throws DAOFileException {
        String gameName = data.getName();
        if (!mapGameNameUUID.containsKey(gameName)) {
            mapGameNameUUID.put(gameName, data.getUUID());
        }
        return new SapiGameRow(rowID, data, pointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataRow<SapiGame> getDataRow(long pointer) throws DAOFileException {
        return new SapiGameRow(dataWriter, pointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> loadPersistedNames() throws DAOException {
        fillMap();
        List<String> names = new ArrayList<>();
        mapGameNameUUID.keySet().forEach((name) -> {
            names.add(name);
        });
        return names;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UUID> loadPersistedUUIDs() throws DAOException {
        fillMap();
        return new ArrayList<>(mapGameNameUUID.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SapiGame find(String gameName) throws DAOException {
        FileDAOUtilities.checkNotNull("gameName", gameName);
        fillMap();
        if (mapGameNameUUID.containsKey(gameName)) {
            UUID gameID = mapGameNameUUID.get(gameName);
            SapiGame result = super.find(gameID);
            return result;
        }
        return null;
    }

    /**
     * Supprime un objet {@code SapiGame} du fichier de données. La partie de
     * Mahjong associé à l'objet {@code  SapiGame} est d'abord supprimé.
     *
     * @param sapiGame Objet {@code SapiGame} à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistence(SapiGame sapiGame) throws DAOException {
        try {
            if (mapGameNameUUID.containsKey(sapiGame.getName())) {
                mapGameNameUUID.remove(sapiGame.getName());
            }
            gameDAO.delete(sapiGame.getUUID());
            if (super.removeDataRow(sapiGame.getUUID())) {
                LOGGER.log(Level.INFO, "{0} id={1} deleted from persistance",
                        new Object[]{sapiGame.getClass().getSimpleName(), sapiGame.getUUID()});
            }
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
    }

    /**
     * La classe <code>SapiGameRow</code> répresente un tuple d'un objet
     * {@code SapiGame}. {@link SapiGame}.
     * <br>
     * La taille maximale pour un nom de partie ainsi est de 50 octets. Si la
     * taille d'un nom dépasse la taille maximale, le nom est tronqué à la
     * position correspondant à la taille maximale.
     *
     * <pre>
     *
     *      Format d'un objet {@code SapiGame} dans un tuple :
     *
     *          UUID=16  |    int=4   |  String=50 |        int=4      | string=11  |  -> (Byte) 84
     *           gameID  | nameLenght |  gameName  | difficultyLenght  | difficulty |
     *   ex :       -    |     -      |      -     |          -        |   MEDIUM   |
     * </pre>
     */
    class SapiGameRow extends DataRow<SapiGame> {

        /**
         * Taille maximale d'un nom en octet.
         */
        static final int NAME_SIZE = 50;
        /**
         * Taille d'un objet {@code SapiGame}.
         */
        private static final int SAPI_GAME_SIZE = 16 + (4 + NAME_SIZE) + (4 + 11);  // 85
        /**
         * Taille d'un tuple de {@code SapiGame}.
         */
        static final int SAPI_GAME_ROW_SIZE = ROW_HEADER_SIZE + SAPI_GAME_SIZE;     // 89

        /**
         * Constructeur avec un identifiant de tuple <code>rowID</code>, une
         * simple game <code>data</code> et un pointeur de tuple
         * <code>rowPointer</code>.
         *
         * @param rowID Identifiant d'un tuple.
         * @param data Objet {@code SapiGame} à encapsuler dans un tuple.
         * @param rowPointer Pointeur d'un tuple.
         */
        SapiGameRow(int rowID, SapiGame data, long rowPointer) throws DAOFileException {
            super(rowID, data, SAPI_GAME_SIZE, rowPointer);
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
            super(writer, SAPI_GAME_SIZE, rowPointer);
        }

        /**
         * Renvoie un objet {@code SapiGame} lu à partir d'un tampon d'octets
         * {@code buffer}.
         *
         * @param buffer Tampon d'octets.
         */
        @Override
        protected SapiGame readData(ByteBuffer buffer) throws DAOFileException {
            if (buffer.remaining() < SAPI_GAME_SIZE - 1) {
//                return null;
                String message = "SapiGame can't be read from the buffer '" + buffer + "'"
                        + "\n\t cause -> SapiGame size '" + SAPI_GAME_SIZE + "' is greater than remaining bytes '" + buffer.remaining() + "'.";
                throw new DAOFileException(message);
            }
            try {
                UUID gameID = new UUID(buffer.getLong(), buffer.getLong());
                String gameName = DAOFileWriter.readString(buffer);
                String difficultyString = DAOFileWriter.readString(buffer);
                Difficulty difficulty = Difficulty.valueOf(difficultyString);

                MahjongGame loadedGame = (MahjongGame) gameDAO.find(gameID);

                return new SapiGame(gameName, difficulty, loadedGame);
            } catch (DAOFileWriterException | DAOException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }

        /**
         * Ecrit un objet {@code SapiGame} dans un tampon d'octet
         * <code>buffer</code>.
         *
         * @param buffer Tampon d'octet.
         */
        @Override
        protected int writeData(ByteBuffer buffer) throws DAOFileException {
            if (buffer.remaining() < SAPI_GAME_SIZE) {
//                return -1;
                String message = "Remianing bytes '" + buffer.remaining() + "' is less than SAPI_GAME_SIZE '"
                        + SAPI_GAME_SIZE + "'";
                throw new DAOFileException(message);
            }
            int startPosition = buffer.position();
            try {
                UUID sapiGameID = getData().getUUID();
                String gameName = FileDAOUtilities.checkStringLenght(getData().getName(), NAME_SIZE);
                Difficulty surrenderDifficulty = getData().getSurrenderDifficulty();

                DAOFileWriter.writeUUID(buffer, sapiGameID);
                DAOFileWriter.writeString(buffer, gameName);
                DAOFileWriter.writeString(buffer, surrenderDifficulty.toString().toUpperCase());

                if (gameDAO.find(getData().getUUID()) == null) {
                    gameDAO.save(getData().getGame());
                    indexManager.addIndex(getIndex());
                }

                return buffer.position() - startPosition;
            } catch (DAOFileWriterException | DAOException ex) {
                buffer.position(startPosition);
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
    }
}
