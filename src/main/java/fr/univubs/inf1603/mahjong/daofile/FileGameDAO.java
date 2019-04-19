
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.engine.game.Game;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * La classe {@code FileGameDAO} gère la persistance des parties
 * {@code GameTile} {@link FileGameDAO.GameRow}.
 *
 *
 * @author aliyou, nesrine
 * @version 1.0.0
 */
public class FileGameDAO extends FileDAOMahjong<Game>{

    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(FileGameDAO.class.getName());
    
    /**
     * Gestionnaire de liens entre les zones et les parties.
     */
    private static LinkManager<TileZone> zoneToGameLinkManager;
    /**
     * Gestionnaire de liens entre les joueurs et les parties.
     */
//    private static LinkManager<Player> playerToGameLinkManager;

    /**
     * Constructeur avec un Chemin d'accès du répertoire racine
     * <code>rootDir</code>.
     *
     * @param rootDir Chemin d'accès du répertoire racine. NE DOIT PAS ETRE
     * NULL.
     * @throws DAOException s'il ya une erreur lors de l'instanciation.
     */
    public FileGameDAO(Path rootDir) throws DAOException {
        super(rootDir, "game.data", "game.index", GameRow.GAME_ROW_SIZE);
        System.out.println(" -> FileGameeDAO");
    }

    /**
     * 
     * @param zoneToGameLinkManager 
     */
    void setZoneToGameLinkManager(LinkManager<TileZone> zoneToGameLinkManager) {
        FileGameDAO.zoneToGameLinkManager = zoneToGameLinkManager;
    }
    
//    void setPlayerToGameLinkManager(LinkManager<Player> playerToGameLinkManager) {
//        FileGameDAO.playerToGameLinkManager = playerToGameLinkManager;
//    }
    
    @Override
    protected AbstractRow<Game> getDataRow(int rowID, Game data, long pointer) {
        return new GameRow(rowID, data, rowID, pointer);
    }

    @Override
    protected AbstractRow<Game> getDataRow(DAOFileWriter writer, long pointer) throws DAOException {
        return new GameRow(writer, pointer);
    }

    @Override
    protected void deleteFromPersistance(Game object) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    class GameRow extends AbstractRow<Game> {

        /**
         * Taille minimale d'une zone en octet.
         */
        private static final int GAME_MIN_SIZE = 16 + 4 + 4 + 1;           // 25
        /**
         * Taille maximale d'une zone en octet.
         */
        private static final int GAME_MAX_SIZE = GAME_MIN_SIZE + 14;       // 39
        /**
         * Taille d'un tuple de zone.
         */
        static final int GAME_ROW_SIZE = ROW_HEADER_SIZE + GAME_MAX_SIZE;  // 43
        
        public GameRow(int rowID, Game data, int dataSize, long rowPointer) {
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
         * @throws DAOException s'il y'a une erruer lors de la lecture d'une zone
         * <code>TileZone</code>.
         */
        GameRow(DAOFileWriter writer, long rowPointer) throws DAOException {
            super(writer, GAME_MAX_SIZE, rowPointer);
        }
        
        @Override
        protected Game readData(ByteBuffer buffer) throws DAOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void writeData(ByteBuffer buffer) throws IOException, DAOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }     
    }
}
