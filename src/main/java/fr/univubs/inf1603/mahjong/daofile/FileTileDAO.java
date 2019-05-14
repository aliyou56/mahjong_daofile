package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.Wind;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.LinkManager;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DAOFileWriter;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import static fr.univubs.inf1603.mahjong.daofile.MahjongUtilities.getWind;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.GameTileInterface;
import fr.univubs.inf1603.mahjong.engine.rule.AbstractTile;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile;
import fr.univubs.inf1603.mahjong.engine.rule.SimpleHonor;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor;
import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe {@code FileTileDAO} gère la persistance des tuiles {@code GameTile}
 * {@link FileTileDAO.TileRow}.
 *
 * @author aliyou
 * @version 1.3
 */
public class FileTileDAO extends FileDAOMahjong<GameTileInterface> {
    
    /**
     * Nom de 
     */
    public static final String TILE_WRITED_PROPERTY = "jobDone";
    
    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(FileDAOMahjong.class.getName());

    /**
     *  Contient l'Instance du DAO qui gère les tuiles.
     */
    private static FileTileDAO instance;
    
    /**
     * Gestionnaire de liens entre les tuiles et les zones.
     */
    private final LinkManager<GameTileInterface> tileToZoneLinkManager;

    /**
     * Constructeur privé avec un Chemin d'accès du répertoire racine
     * <code>rootDirPath</code>.
     *
     * @param rootDirPath Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @throws DAOFileException s'il ya une erreur lors de l'instanciation.
     */
    private FileTileDAO(Path rootDirPath) throws DAOFileException {
        super(rootDirPath, "tile", TileRow.TILE_ROW_SIZE);
        this.tileToZoneLinkManager = new TileToZoneLinkManager(this);
    }
    
    /**
     * Renvoie l'instance du DAO qui gère les tuiles.
     * 
     * @param rootDir Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @return L'instance du DAO qui gère les tuiles.
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation.
     */
    static FileTileDAO getInstance(Path rootDir) throws DAOFileException {
        if(instance == null) {
            instance = new FileTileDAO(rootDir);
        }
        return instance;
    }
    
    @Override
    synchronized public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(DAOFileWriter.DONE_PROPERTY)) {
            System.out.println("\n* FileTileDAO  -> job done notification received from the writer");
//            LOGGER.log(Level.INFO, " * writer -> job done");
            notify();
            super.getPropertyChangeSupport().firePropertyChange(TILE_WRITED_PROPERTY, false, true);
        }
    }
    
    /**
     * @return Le gestionnaire de liens entre les tuiles et les zones.
     */
    LinkManager<GameTileInterface> getLinkManager() {
        return tileToZoneLinkManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataRow getDataRow(int rowID, GameTileInterface tile, long rowPointer) {
        return new TileRow(rowID, tile, rowPointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataRow getDataRow(long rowPointer) throws DAOFileException {
        return new TileRow(dataWriter, rowPointer);
    }

    /**
     * Supprime une tuile <code>GameTileInterface</code> du fichier de données si la
     * tuile n'est reliée à aucune zone <code>TileZone</code>.
     *
     * @param gameTile Tuile à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistence(GameTileInterface gameTile) throws DAOException {
        try {
            // on vérifie si la tuile n'est pas reliée à une zone
            if (tileToZoneLinkManager.getRow(gameTile.getUUID()) == null) {
                if (super.removeDataRow(gameTile.getUUID())) {
                    LOGGER.log(Level.INFO, " {0} id={1} deleted from persistance",
                            new Object[]{gameTile.getClass().getSimpleName(), gameTile.getUUID()});
                }
            } else {
                LOGGER.log(Level.INFO, "GameTile id={0} can't be deleted \n\t cause -> it is linked to a zone", gameTile.getUUID());
            }
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
    }

    /**
     * Cette classe represente le gestionnaire de liens entre les tuiles et les zones.
     */
    class TileToZoneLinkManager extends LinkManager<GameTileInterface> {

        /**
         * Constructeur privé avec le DAO qui gère les tuiles.
         * @param dao DAO qui gère les tuiles.
         * @throws DAOFileException s'il y'a une erreur lors de l'instanciation.
         */
        TileToZoneLinkManager(FileDAOMahjong<GameTileInterface> dao) throws DAOFileException {
            super(rootDirPath.resolve("tileToZone.link"), dao);
//            System.out.println("constructor : " + this);
        }
    }

    /**
     * La classe <code>TileRow</code> répresente un tuple de tuile
     * {@link GameTileInterface}. C'est un conteneur pour une tuile.
     *
     * <pre>
     *
     *      Format d'une tuile dans  un tuple :
     *
     *       UUID=16 |        String=3      | int=4  |      byte=1      |   byte=1   |    byte=1     |       int=4         | -> (Byte) 30
     *       tileID  | tileClassShortenName | gameID | publicalyVisible | orientation|  wind/dragon  | number (commonTile) |
     * ex :     -    | cop/suh/sih/flt/sep  |   -    |        true      |  e/s/w/n   | e/s/w/n r/g/w |       [1-9]         |
     * </pre>
     *
     */
    static class TileRow extends DataRow<GameTileInterface> {
        
        /**
         * Taille d'une tuile en octet.
         */
        private static final int TILE_SIZE = 16 + 3 + 4 + 1 + 1 + 1 + 4;    // 30
        /**
         * Taille d'un tuple de tuile.
         */
        static final int TILE_ROW_SIZE = ROW_HEADER_SIZE + TILE_SIZE;       // 34

        /**
         * Constructeur avec un identifiant de tuple <code>rowID</code>, une
         * tuile <code>data</code> et un pointeur de tuple
         * <code>rowPointer</code>.
         *
         * @param rowID Identifiant d'un tuple.
         * @param data Tuile à encapsuler dans un tuple.
         * @param rowPointer Pointeur d'un tuple.
         */
        TileRow(int rowID, GameTileInterface data, long rowPointer) {
            super(rowID, data, TILE_SIZE, rowPointer);
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
         * tuile <code>GameTile</code>.
         */
        TileRow(DAOFileWriter writer, long rowPointer) throws DAOFileException {
            super(writer, TILE_SIZE, rowPointer);
        }

        /**
         * Lis une tuile <code>GameTile</code> à partir d'un tampon d'octets
         * <code>buffer</code>.
         *
         * @param buffer Tampon d'octets à partir duquel une tuile
         * <code>GameTile</code> est lue.
         */
        @Override
        protected GameTileInterface readData(ByteBuffer buffer) throws DAOFileException {
            if (buffer.remaining() < TILE_SIZE) {
                String message = "Remianing bytes '" + buffer.remaining() + "' is less than TILE_SIZE '"
                        + TILE_SIZE + "'";
                throw new DAOFileException(message);
            }
            int startPosition = buffer.position();
            try {
                UUID tileID = new UUID(buffer.getLong(), buffer.getLong());
                String type = DAOFileWriter.readString(buffer, 3);
                int gameID = buffer.getInt();
                boolean isPublicalyVisible = (buffer.get() == 1);
                Wind orientation = getWind((char) buffer.get());
                AbstractTile abstractTile = null;
                char c = (char) buffer.get();
                switch (TileClassSimpleName.getTileClassSimpleName(type)) {
                    case COMMONTILE:
                        abstractTile = new CommonTile(MahjongUtilities.getFamily(c), MahjongUtilities.getNumber(buffer.getInt()));
                        break;
                    case SUPERIORHONOR:
                        abstractTile = new SuperiorHonor(MahjongUtilities.getDragon(c));
                        break;
                    case SIMPLEHONOR:
                        abstractTile = new SimpleHonor(MahjongUtilities.getWind(c));
                        break;
                    case FLOWERTILE:
                        abstractTile = new FlowerTile(MahjongUtilities.getFlower(MahjongUtilities.getWind(c)));
                        break;
                    case SEASONTILE:
                        abstractTile = new SeasonTile(MahjongUtilities.getSeason(MahjongUtilities.getWind(c)));
                        break;
                }
                GameTileInterface data = new GameTile(gameID, abstractTile, tileID, isPublicalyVisible, orientation);
                return data;
            } catch (DAOFileWriterException ex) {
                buffer.position(startPosition);
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }

        /**
         * Ecrit une tuile dans un tampon d'octet <code>buffer</code>.
         *
         * @param buffer Tampon d'octet dans lequel la tuile est écrite.
         */
        @Override
        protected int writeData(ByteBuffer buffer) throws DAOFileException {
            int stratPosition = buffer.position();
            if (buffer.remaining() < TILE_SIZE) {
                String message = "Remianing bytes '" + buffer.remaining() + "' is less than TILE_SIZE '"
                        + TILE_SIZE + "'";
                throw new DAOFileException(message);
            }
            GameTile tile = (GameTile) getData();
            UUID tileID = tile.getUUID();
            AbstractTile abstractTile = getData().getTile();
            TileClassSimpleName tileClassSimpleName = TileClassSimpleName.valueOf(abstractTile.getClass().getSimpleName().toUpperCase());
            int gameID = getData().getGameID();
            boolean isPubliclyVisible = tile.isPubliclyVisible();
            Wind orientation = tile.getOrientation();

//            LOGGER.log(Level.INFO, " GameTileInterface "
//                              + "\n tileID              : {0} "
//                              + "\n tileClassSimpleName : {1}" 
//                              + "\n gameID              : {2}"
//                              + "\n isPubliclyVisible   : {3}"
//                              + "\n orientation         : {4}", 
//                    new Object[]{tileID, tileClassSimpleName, gameID, isPubliclyVisible, orientation});
            try {
                DAOFileWriter.writeUUID(buffer, tileID);
                buffer.put(tileClassSimpleName.getShortenName().getBytes());
                buffer.putInt(gameID);
                buffer.put(isPubliclyVisible ? (byte) 1 : (byte) 0);
                buffer.put((byte) orientation.getSymbol());
                switch (tileClassSimpleName) {
                    case COMMONTILE:
                        CommonTile commonTile = (CommonTile) abstractTile;
                        buffer.put((byte) commonTile.getFamily().getSymbol());             // 1 Byte
                        int value = commonTile.getNumber().getValue();
                        buffer.putInt(value);                  // 4 Bytes
                        break;
                    case SUPERIORHONOR:
                        SuperiorHonor superiorHonor = (SuperiorHonor) abstractTile;
                        buffer.put((byte) superiorHonor.getDragon().getSymbol());          // 1 Byte
                        break;
                    case SIMPLEHONOR:
                        SimpleHonor simpleHonor = (SimpleHonor) abstractTile;
                        buffer.put((byte) simpleHonor.getWind().getSymbol());              // 1 Byte
                        break;
                    case FLOWERTILE:
                        FlowerTile flowerTile = (FlowerTile) abstractTile;
                        char symbol = flowerTile.getFlower().getWind().getSymbol();
                        buffer.put((byte) symbol);   // 1 Byte
                        break;
                    case SEASONTILE:
                        SeasonTile seasonTile = (SeasonTile) abstractTile;
                        char symbol1 = seasonTile.getSeason().getWind().getSymbol();
                        buffer.put((byte) symbol1);   // 1 Byte
                        break;
                }
                return buffer.position() - stratPosition;
            } catch (DAOFileWriterException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Enumération correspondant aux types de tuiles.
     */
    private static enum TileClassSimpleName {
        COMMONTILE("cot"),
        SUPERIORHONOR("suh"),
        SIMPLEHONOR("sih"),
        FLOWERTILE("flt"),
        SEASONTILE("set");

        /**
         * Nom abrégé de la classe.
         */
        private final String shortenName;

        /**
         * Constructeur avec le nom abrégé de la classe.
         *
         * @param shortenName Nom simple de la classe
         */
        private TileClassSimpleName(String shortenName) {
            this.shortenName = shortenName;
        }

        /**
         * Retourne le nom abrégé de la classe.
         *
         * @return Nom abrégé de la classe.
         */
        public String getShortenName() {
            return shortenName;
        }

        /**
         *
         * @param shortenName
         * @return
         */
        public static TileClassSimpleName getTileClassSimpleName(String shortenName) {
            for (TileClassSimpleName tcsn : values()) {
                if (tcsn.getShortenName().equals(shortenName)) {
                    return tcsn;
                }
            }
            return null;
        }
    }
}
