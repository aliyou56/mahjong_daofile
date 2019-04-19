package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
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
    private static FileDAOManager daoManager = null;
    /**
     * Chemin du repertoire racine
     */
    private final Path rootDir;

    private static FileGameDAO gameDao = null;
    private static FileZoneDAO zoneDao = null;
    private static FileTileDAO tileDao = null;
//    private static FilePlayerDAO playereDao;

    private static LinkManagerFactory linkManagerFactory = null;
    
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
    synchronized public static DAOManager getInstance() {
        return getInstance(Paths.get(System.getProperty("user.home"), "MahJong"));
    }

    /**
     * Rétourne l'instance du gestionnaire des DAO.
     *
     * @param rootDir Chemin d'accès du repertoire racine.
     * @return L'instance du <code>DAOManager</code>.
     */
    synchronized public static FileDAOManager getInstance(Path rootDir) {
        if (daoManager == null) {
            daoManager = new FileDAOManager(rootDir);
            linkManagerFactory = LinkManagerFactory.getInstance(rootDir);
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
    synchronized public DAO getGameDao() throws DAOException {
        if(gameDao == null) {
            gameDao = new FileGameDAO(rootDir);
            gameDao.setZoneToGameLinkManager(linkManagerFactory.getZoneToGameLinkManager());
//            gameDao.setPlayerToGameLinkManager(linkManagerFactory.getPlayerToGameLinkManager());
        }
        return gameDao;
    }

    /**
     * Rétourne l'instance du DAO qui gère les zones <code>TileZone</code>.
     *
     * @return l'instance du DAO qui gère les zonees <code>TileZone</code>.
     * @throws DAOException s'il y'a une erreur lors de l'instanciation de
     * <code>FileZoneDAO</code>
     */
    @Override
    synchronized public DAO<TileZone> getZoneDao() throws DAOException {
        if (zoneDao == null) {
            zoneDao = new FileZoneDAO(rootDir);
            zoneDao.setTileToZoneLinkManager(linkManagerFactory.getTileToZoneLinkManager());
        }
        return zoneDao;
    }

    /**
     * Rétourne l'instance du DAO fichier qui gère les tuiles
     * <code>GameTile</code>.
     *
     * @return l'instance du DAO fichier qui gère les tuiles
     * <code>GameTile</code>
     * @throws DAOException s'il y'a une erreur lors de l'instanciation de
     * <code>FileTileDAO</code>
     */
    @Override
    synchronized public DAO<GameTile> getTileDao() throws DAOException {
        if(tileDao == null) {
            tileDao = new FileTileDAO(rootDir);
            tileDao.setTileToZoneLinkManager(linkManagerFactory.getTileToZoneLinkManager());
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