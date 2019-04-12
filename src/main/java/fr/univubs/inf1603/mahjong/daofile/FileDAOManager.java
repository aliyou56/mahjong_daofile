package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
//import fr.univubs.inf1603.mahjong.dao.fake_engine.Player;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * La classe <code>FileDAOManager</code> est une implémentation du
 * <code>DAOManager</code> qui gère l'ensemble des DAO fichier. Elle utilise le
 * patron de conception singleton. Son instance est obtenue via la méthode
 * statique <code>getInstance()</code> sans paramètre ou avec le chemin du
 * répertoire racine <code>Path</code> comme paramètre.
 *
 * @author aliyou, nesrine
 * @version 1.0.0
 */
public class FileDAOManager implements DAOManager {

    /**
     * Gestionnaire de DAO
     */
    private static FileDAOManager daoManager;
    /**
     * Chemin du repertoire racine
     */
    private final Path rootDir;

//    private FileGameDAO gameDao;
    private FileZoneDAO zoneDao;
    private FileTileDAO tileDao;
//    private FilePlayerDAO playereDao;

    /**
     * Constructeur privé avec le chemin d'accès du repertoire racine.
     *
     * @param rootDir Chemin d'accès du repertoire racine.
     */
    private FileDAOManager(Path rootDir) {
        this.rootDir = rootDir;
    }

    /**
     * Rétourne l'instance du gestionnaire des DAO.
     *
     * @return L'instance du <code>DAOManager</code>
     */
    public static DAOManager getInstance() {
        return getInstance(Paths.get(System.getProperty("user.home"), "MahJong"));
    }

    /**
     * Rétourne l'instance du gestionnaire des DAO.
     *
     * @param rootDir Chemin d'accès du repertoire racine.
     * @return L'instance du <code>DAOManager</code>.
     */
    public static FileDAOManager getInstance(Path rootDir) {
        if (daoManager == null) {
            daoManager = new FileDAOManager(rootDir);
        }
        return daoManager;
    }

    /**
     * Rétourne l'instance du DAO fichier qui gère les parties
     * <code>Game</code>.
     *
     * @return l'instance du DAO fichier qui gère les parties <code>Game</code>.
     * @throws DAOException s'il y'a une erreur lors de l'instanciation de
     * <code>FileGameDAO</code>.
     */
    @Override
    public DAO getGameDao() throws DAOException {
//        if(gameDao == null) {
//            gameDao = new FileGameDAO(rootDir);
//        }
//        return gameDao;
        return null;
    }

    /**
     * Rétourne l'instance du DAO qui gère les zones <code>TileZone</code>.
     *
     * @return l'instance du DAO qui gère les zonees <code>TileZone</code>.
     * @throws DAOException s'il y'a une erreur lors de l'instanciation de
     * <code>FileZoneDAO</code>
     */
    @Override
    public DAO<TileZone> getZoneDao() throws DAOException {
        if (zoneDao == null) {
            zoneDao = new FileZoneDAO(rootDir);
        }
        return zoneDao;
    }

    /**
     * Rétourne l'instance du DAO fichier qui gère les tuiles
     * <code>GameTileOld</code>.
     *
     * @return l'instance du DAO fichier qui gère les tuiles
     * <code>GameTileOld</code>
     * @throws DAOException s'il y'a une erreur lors de l'instanciation de
     * <code>FileTileDAO</code>
     */
    @Override
    public DAO<GameTile> getTileDao() throws DAOException {
        if (tileDao == null) {
            tileDao = new FileTileDAO(rootDir);
        }
        return tileDao;
    }
    
    /**
     * Rétourne l'instance du DAO fichier qui gère les joueurs
     * <code>Player</code>.
     *
     * @return l'instance du DAO fichier qui gère les joueurs
     * <code>Player</code>.
     * @throws DAOException s'il y'a une erreur lors de l'instanciation de
     * {<code>FilePlayerDAO</code>.
     */
//    @Override
//    public DAO<Player> getPlayerDao() throws DAOException {
//        return null;
//    }
}