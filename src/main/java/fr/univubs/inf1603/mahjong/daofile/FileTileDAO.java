package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.rule.AbstractTile;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile.Flower;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile.Season;
import fr.univubs.inf1603.mahjong.engine.rule.SimpleHonor;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor.Dragon;
import fr.univubs.inf1603.mahjong.engine.rule.Wind;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe {@code FileTileDAO} gère la persistance des tuiles
 * {@code GameTile} {@link FileTileDAO.TileRow}.
 *
 * @author aliyou, nesrine
 * @version 1.0.0
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
     * <code>rootDir</code>.
     *
     * @param rootDir Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @throws DAOException s'il ya une erreur lors de l'instanciation.
     */
    FileTileDAO(Path rootDir) throws DAOException {
        super(rootDir, "tile.data", "tile.index", TileRow.TILE_ROW_SIZE);
        System.out.println(" -> FileTileDAO");
    }
    
    /**
     * 
     * @param tileToZoneLinkManager 
     */
    void setTileToZoneLinkManager(LinkManager<GameTile> tileToZoneLinkManager) {
        FileTileDAO.tileToZoneLinkManager = tileToZoneLinkManager;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRow getDataRow(int rowID, GameTile tile, long rowPointer) {
        return new TileRow(rowID, tile, rowPointer);
    }

    @Override
    protected AbstractRow getDataRow(DAOFileWriter writer, long pointer) throws DAOException {
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
        // on vérifie si la tuile n'est pas reliée à une zone 
        if (tileToZoneLinkManager.getRow(gameTile.getUUID()) == null) {
            if (super.removeDataRow(gameTile.getUUID())) {
                LOGGER.log(Level.INFO, "GameTile id={0} deleted from persistance", gameTile.getUUID());
            }
        } else {
            LOGGER.log(Level.INFO, "GameTile id={0} canno't be deleted cause it is linked to a zone", gameTile.getUUID());
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
    static class TileRow extends AbstractRow<GameTile> {

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
        TileRow(int rowID, GameTile data, long rowPointer) {
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
        TileRow(DAOFileWriter writer, long rowPointer) throws DAOException {
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
        protected GameTile readData(ByteBuffer buffer) {
            UUID tileID = new UUID(buffer.getLong(), buffer.getLong());
            AbstractTile abstractTile = null;
            String type = DAOFileWriter.readString(buffer, 3);
            int gameID = buffer.getInt();
            char c = (char) buffer.get();
            switch (TileClassSimpleName.getTileClassSimpleName(type)) {
                case COMMONTILE:
                    abstractTile = new CommonTile(getFamily(c), getNumber(buffer.getInt()));
                    break;
                case SUPERIORHONOR:
                    abstractTile = new SuperiorHonor(getDragon(c));
                    break;
                case SIMPLEHONOR:
                    abstractTile = new SimpleHonor(getWind(c));
                    break;
                case FLOWERTILE:
                    abstractTile = new FlowerTile(getFlower(getWind(c)));
                    break;
                case SEASONTILE:
                    abstractTile = new SeasonTile(getSeason(getWind(c)));
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
        protected void writeData(ByteBuffer buffer) throws IOException {
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

        /**
         * Rétourne le vent <code>Wind</code> correspondant au carctère passé en
         * paramètre <code>c</code>.
         *
         * @param c Caractère.
         * @return Vent <code>Wind</code> si le caractère correspond à un
         * symbole de vent sinon <code>null</code>.
         */
        private static Wind getWind(char c) {
            System.out.println("Wind -> c : " + c);
            for (Wind wind : Wind.values()) {
                if (wind.getSymbol() == c) {
                    return wind;
                }
            }
            return null;
        }

        /**
         * Rétourne le dragon  <code>SuperiorHonor.Dragon</code>
         * {@link DragonTile.Dragon} correspondant au carctère passé en
         * paramètre <code>c</code>.
         *
         * @param c Caractère.
         * @return Dargon <code>SuperiorHonor.Dragon</code> si le caractère
         * correspond à un symbole de dragon sinon <code>null</code>.
         */
        private static SuperiorHonor.Dragon getDragon(char c) {
            for (Dragon dragon : Dragon.values()) {
                if (dragon.getSymbol() == c) {
                    return dragon;
                }
            }
            return null;
        }

        /**
         * Rétourne la saison <code>SeasonTile.Season</code>
         * {@link SeasonTile.Season} correspondant au vent passé en paramètre
         * <code>wind</code>.
         *
         * @param wind Vent.
         * @return Saison  <code>SeasonTile.Season</code> si le vent
         * <code>wind</code> correspond à un vent <code>Wind</code> de saison
         * sinon <code>null</code>.
         */
        private static SeasonTile.Season getSeason(Wind wind) {
            for (Season season : Season.values()) {
                if (season.getWind() == wind) {
                    return season;
                }
            }
            return null;
        }

        /**
         * Rétourne la fleur <code>FlowerTile.Flower</code>
         * {@link FlowerTile.Flower} correspondant au vent passé en paramètre
         * <code>wind</code>.
         *
         * @param wind Vent.
         * @return Fleur <code>FlowerTile.Flower</code> si le vent
         * <code>wind</code> correspond à un vent <code>Wind</code> de fleur
         * <code>FlowerTile.Flower</code> sinon <code>null</code>.
         */
        private static FlowerTile.Flower getFlower(Wind wind) {
            for (Flower flower : Flower.values()) {
                if (flower.getWind().equals(wind)) {
                    return flower;
                }
            }
            return null;
        }

        /**
         * Rétourne la famille <code>CommontTile.Family</code>
         * {@link CommonTile.Family} correspondant au caractère passé en
         * paramètre <code>c</code>.
         *
         * @param c Caractère.
         * @return Famille <code>CommontTile.Family</code> si le caractère
         * correspond à un symbole de famille <code>CommontTile.Family</code>
         * sinon <code>null</code>.
         */
        private static CommonTile.Family getFamily(char c) {
            for (CommonTile.Family family : CommonTile.Family.values()) {
                if (family.getSymbol() == c) {
                    return family;
                }
            }
            return null;
        }

        /**
         * Rétourne le Numéro <code>CommontTile.Number</code>
         * {@link CommonTile.Number} correspondant à la valeur passé en
         * paramètre <code>value</code>.
         *
         * @param value Valeur.
         * @return Numéro <code>CommontTile.Number</code> si la valeur
         * correspond à une valeur de numéro <code>CommontTile.Number</code>
         * sinon <code>null</code>.
         */
        private static CommonTile.Number getNumber(int value) {
            for (CommonTile.Number number : CommonTile.Number.values()) {
                if (number.getValue() == value) {
                    return number;
                }
            }
            return null;
        }
    }
}
