
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.AbstractTile;
import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
//import fr.univubs.inf1603.mahjong.engine.AbstractTile;
import fr.univubs.inf1603.mahjong.engine.Zone;
import fr.univubs.inf1603.mahjong.sapi.Player;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * La classe {@code FileDAOManager} est une implémentation du {@code DAOManager}
 * qui gère l'ensemble des DAO fichier. Elle utilise le patron de conception 
 * singleton. Son instance est obtenue via la méthode statique {@code getInstance}
 * sans paramètre ou avec le chemin du répertoire racine {@code Path} comme paramètre.
 * 
 * @author aliyou
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
//    private FileZoneDAO zoneDao;
    private FileTileDAO tileDao;
//    private FilePlayerDAO playereDao;
    
    /**
     * Constructeur privé avec le chemin d'accès du repertoire racine.
     * @param rootDir Chemin d'accès du repertoire racine.
     */
    private FileDAOManager(Path rootDir) {
        this.rootDir = rootDir;
    }
    
    /**
     * Rétourne l'instance du gestionnaire des DAO.
     * @return l'instance du {@code DAOManager}
     */
    public static DAOManager getInstance() {
        return getInstance(Paths.get(System.getProperty("user.home"), "MahJong"));
    }
    
    /**
     * Rétourne l'instance du gestionnaire des DAO.
     * @param rootDir Chemin d'accès du repertoire racine.
     * @return l'instance du {@code DAOManager}
     */
    public static FileDAOManager getInstance(Path rootDir) {
        if(daoManager == null) {
            daoManager = new FileDAOManager(rootDir);
        }
        return daoManager;
    }
    
    /**
     * Rétourne l'instance du DAO fichier qui gère les parties {@code Game}.
     * @return  l'instance du DAO fichier qui gère les parties {@code Game}
     * @throws DAOException s'il y'a une erreur lors de l'instanciation de {@code GameDAO}
     */
    @Override
    public DAO getGameDao() throws DAOException {
        return null;
    }

    /**
     * Rétourne l'instance du DAO qui gère les zones {@code Zone}.
     * @return  l'instance du DAO qui gère les zonees {@code Zone}
     * @throws DAOException s'il y'a une erreur lors de l'instanciation de {@code ZoneDAO}
     */
    @Override
    public DAO<Zone> getZoneDao() throws DAOException {
        return null;
    }
    
    /**
     * Rétourne l'instance du DAO fichier qui gère les tuiles {@code AbstractTile}.
     * @return  l'instance du DAO fichier qui gère les tuiles {@code AbstractTile}
     * @throws DAOException s'il y'a une erreur lors de l'instanciation de {@code TileDAO}
     */
    @Override
    public DAO<AbstractTile> getTileDao() throws DAOException {
        if(tileDao == null) {
            tileDao = (rootDir != null) ? new FileTileDAO(rootDir) : new FileTileDAO();
        }
        return tileDao;
    }

    /**
     * Rétourne l'instance du DAO fichier qui gère les joueurs {@code Player}.
     * @return  l'instance du DAO fichier qui gère les  joueurs {@code Player}
     * @throws DAOException s'il y'a une erreur lors de l'instanciation de {@code PlayerDAO}
     */
    @Override
    public DAO<Player> getPlayerDao() throws DAOException {
        return null;
    }
    
}
