package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.filemanagement.AbstractRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.LinkManager;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DataRow;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.DAOFileWriter;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.exception.ByteBufferException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.rule.AbstractTile;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile;
import fr.univubs.inf1603.mahjong.engine.rule.SimpleHonor;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe {@code FileTileDAO} gère la persistance des tuiles {@code GameTile}
 * {@link FileTileDAO.TileRow}.
 *
 * @author aliyou, nesrine
 * @version 1.1.0
 */
public class FileTileDAO extends FileDAOMahjong<GameTile> {

    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(FileDAOMahjong.class.getName());

    /**
     * Gestionnaire de liens entre les tuiles et les zones.
     */
    private static LinkManager<GameTile> tileToZoneLinkManager;

    /**
     * Constructeur avec un Chemin d'accès du répertoire racine
     * <code>rootDirPath</code>.
     *
     * @param rootDirPath Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @throws DAOFileException s'il ya une erreur lors de l'instanciation.
     */
    FileTileDAO(Path rootDirPath) throws DAOFileException {
        super(rootDirPath, "tile.data", "tile.index", TileRow.TILE_ROW_SIZE);
        System.out.println(" -> FileTileDAO");
    }

    /**
     * Définit le gestionnaire de liens.
     *
     * @param tileToZoneLinkManager Gestionnaire de liens.
     */
    void setLinkManager(LinkManager<GameTile> tileToZoneLinkManager) {
        if (FileTileDAO.tileToZoneLinkManager == null) {
            FileTileDAO.tileToZoneLinkManager = tileToZoneLinkManager;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow getDataRow(int rowID, GameTile tile, long rowPointer)throws DAOFileException {
        return new TileRow(rowID, tile, rowPointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow getDataRow(DAOFileWriter writer, long pointer) throws DAOFileException {
        return new TileRow(writer, pointer);
    }

    /**
     * Supprime une tuile <code>gameTile</code> du fichier de données si la
     * tuile n'est reliée à aucune zone <code>TileZone</code>.
     *
     * @param gameTile Tuile à supprimer.
     * @throws DAOException s'il y'a une erreur lors de la suppression.
     */
    @Override
    protected void deleteFromPersistance(GameTile gameTile) throws DAOException {
        try {
            // on vérifie si la tuile n'est pas reliée à une zone
            if (tileToZoneLinkManager.getRow(gameTile.getUUID()) == null) {
                if (super.removeDataRow(gameTile.getUUID())) {
                    LOGGER.log(Level.INFO, "[INFO] {0} id={1} deleted from persistance", new Object[]{gameTile.getClass().getSimpleName(), gameTile.getUUID()});
                }
            } else {
                LOGGER.log(Level.INFO, "GameTile id={0} canno't be deleted cause it is linked to a zone", gameTile.getUUID());
            }
        } catch (DAOFileException ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
    }

    /**
     * La classe <code>TileRow</code> répresente un tuple de tuile
     * {@link GameTile}. C'est un conteneur pour une tuile.
     *
     * <pre>
     *
     *      Format d'une tuile dans  un tuple de tuile dans le fichier de données :
     *
     *          UUID=16    |        String=3      |   int=4   |    byte=1     |        int=4        | -> (Byte)
     *          tileID     | tileClassShortenName |   gameID  |  wind/dragon  | number (commonTile) |
     *   ex :       -      | cop/suh/sih/flt/sep  |     -     | e/s/w/n r/g/w |         [1-9]       |
     * </pre>
     *
     */
    class TileRow extends DataRow<GameTile> {

        /**
         * Taille minimale d'une tuile en octet.
         * <pre>
         *          UUID=16    |        String=3      |   int=4   |    byte=1   -> (Byte)
         *          tileID     | tileClassShortenName |   gameID  |  wind/dragon
         * </pre>
         */
        private static final int TILE_MIN_SIZE = 16 + 3 + 4 + 1;           // 24
        /**
         * Taille maximale d'une tuile en octet.
         * <pre>
         *     + ->    |         int=4         | -> (Byte)
         *     + ->    |  number (commonTile)  |
         * </pre>
         */
        private static final int TILE_MAX_SIZE = TILE_MIN_SIZE + 4;        // 28
        /**
         * Taille d'un tuple de tuile.
         */
        static final int TILE_ROW_SIZE = ROW_HEADER_SIZE + TILE_MAX_SIZE;  // 32

        /**
         * Constructeur avec un identifiant de tuple <code>rowID</code>, une
         * tuile <code>data</code> et un pointeur de tuple
         * <code>rowPointer</code>.
         *
         * @param rowID Identifiant d'un tuple.
         * @param data Tuile à encapsuler dans un tuple.
         * @param rowPointer Pointeur d'un tuple.
         */
        TileRow(int rowID, GameTile data, long rowPointer) throws DAOFileException {
            super(rowID, data, TILE_MAX_SIZE, rowPointer);
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
         * tuile <code>GameTile</code>.
         */
        TileRow(DAOFileWriter writer, long rowPointer) throws DAOFileException {
            super(writer, TILE_MAX_SIZE, rowPointer);
        }

        /**
         * Lis une tuile <code>GameTile</code> à partir d'un tampon d'octets
         * <code>buffer</code>.
         *
         * @param buffer Tampon d'octets à partir duquel une tuile
         * <code>GameTile</code> est lue.
         */
        @Override
        protected GameTile readData(ByteBuffer buffer) throws DAOFileException {
            UUID tileID = new UUID(buffer.getLong(), buffer.getLong());
            AbstractTile abstractTile = null;
            String type = DAOFileWriter.readString(buffer, 3);
            int gameID = buffer.getInt();
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
            GameTile data = new GameTile(gameID, abstractTile, tileID);
            return data;
        }

        /**
         * Ecrit une tuile dans un tampon d'octet <code>buffer</code>.
         *
         * @param buffer Tampon d'octet.
         */
        @Override
        protected void writeData(ByteBuffer buffer) throws DAOFileException {
            try {
                DAOFileWriter.writeUUID(buffer, getData().getUUID());
                AbstractTile abstractTile = getData().getTile();
                TileClassSimpleName tileClassSimpleName = TileClassSimpleName.valueOf(abstractTile.getClass().getSimpleName().toUpperCase());
                buffer.put(tileClassSimpleName.getShortenName().getBytes());
                buffer.putInt(getData().getGameID());
                switch (tileClassSimpleName) {
                    case COMMONTILE:
                        CommonTile commonTile = (CommonTile) abstractTile;
                        buffer.put((byte) commonTile.getFamily().getSymbol());             // 1 Byte
                        buffer.putInt(commonTile.getNumber().getValue());                  // 4 Bytes
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
//                    System.out.println("flower wind symbol : " + symbol);
                        buffer.put((byte) symbol);   // 1 Byte
                        break;
                    case SEASONTILE:
                        SeasonTile seasonTile = (SeasonTile) abstractTile;
                        char symbol1 = seasonTile.getSeason().getWind().getSymbol();
//                    System.out.println("season wind symbol : " + symbol1);
                        buffer.put((byte) symbol1);   // 1 Byte
                        break;
                }
                indexManager.addIndex(getIndex());
            } catch (ByteBufferException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
    }

    /**
     *
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
