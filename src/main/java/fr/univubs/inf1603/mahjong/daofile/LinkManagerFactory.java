package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import java.nio.file.Path;

/**
 *
 * @author aliyou, nesrine
 * @version 1.0.0
 */
public class LinkManagerFactory {

    /**
     * Factory
     */
    private static LinkManagerFactory factory = null;
    /**
     * Gestionnaire des DAOs.
     */
    private static DAOManager daoManager;

    /**
     * Gestionnaire de lien entre une zone et ses tuiles.
     */
    private static LinkManager<GameTile> tileToZoneLinkManager = null;
    /**
     * Gestionnaire de liens entre les zones et les parties.
     */
    private static LinkManager<TileZone> zoneToGameLinkManager = null;
    /**
     * Gestionnaire de liens entre les joueurs et les parties.
     */
//    private static LinkManager<Player> playerToGameLinkManager = null;

    /**
     * Chemin d'accès du repertoire racine.
     */
    private final Path rootDir;

    /**
     * Constructeur privé avec le chemin d'accès du repertoire racine.
     *
     * @param rootDir Chemin d'accès du repertoire racine.
     */
    private LinkManagerFactory(Path rootDir) {
        this.rootDir = rootDir;
    }

    /**
     * Rétourne l'instance de la factory.
     *
     * @param rootDir Chemin d'accès du repertoire racine.
     * @return L'instance de la factory.
     */
    synchronized public static LinkManagerFactory getInstance(Path rootDir) {
        if (factory == null) {
            factory = new LinkManagerFactory(rootDir);
            daoManager = FileDAOManager.getInstance(rootDir);
        }
        return factory;
    }

    /**
     * Rétourne le gestionnaire de lien Tuile - Zone.
     *
     * @return Gestionnaire de lien Tuile - Zone.
     * @throws DAOException s'il y'a une erreur lors de l'instanciation du
     * gestionnaire de lien.
     */
    synchronized public LinkManager<GameTile> getTileToZoneLinkManager() throws DAOException {
        if (tileToZoneLinkManager == null) {
            tileToZoneLinkManager = new LinkManager(rootDir.resolve("tileToZone.link"));
            tileToZoneLinkManager.setDAO(daoManager.getTileDao());
        }
        return tileToZoneLinkManager;
    }
    
    /**
     * Rétourne le gestionnaire de lien Zone - Game.
     *
     * @return Gestionnaire de lien Zone - Game.
     * @throws DAOException s'il y'a une erreur lors de l'instanciation du
     * gestionnaire de lien.
     */
    synchronized public LinkManager<TileZone> getZoneToGameLinkManager() throws DAOException {
        if(zoneToGameLinkManager == null) {
            zoneToGameLinkManager = new LinkManager(rootDir.resolve("zoneToGame.link"));
            zoneToGameLinkManager.setDAO(daoManager.getZoneDao());
        }
        return zoneToGameLinkManager;
    }
    
    /**
     * Rétourne le gestionnaire de lien Player - Game.
     *
     * @return Gestionnaire de lien Player - Game.
     * @throws DAOException s'il y'a une erreur lors de l'instanciation du
     * gestionnaire de lien.
     */
//    synchronized public LinkManager<TileZone> getPlayerToGameLinkManager() throws DAOException {
//        if(playerToGameLinkManager == null) {
//            playerToGameLinkManager = new LinkManager(rootDir.resolve("playerToGame.link"));
//            playerToGameLinkManager.setDAO(daoManager.getPlayerDao());
//        }
//        return playerToGameLinkManager;
//    }
}