package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.daofile.FileDAOManager;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import java.nio.file.Path;

/**
 * Cette classe est une fabrique pour les gestionnaires de liens entre des objets
 * parents et objets enfants.
 * 
 * @author aliyou, nesrine
 * @version 1.1.0
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
    private static PlayerToSimpleGameLinkManager playerToSimpleGameLinkManager = null;

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
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation du
     * gestionnaire de lien.
     */
    synchronized public LinkManager<GameTile> getTileToZoneLinkManager() throws DAOFileException {
        if (tileToZoneLinkManager == null) {
            tileToZoneLinkManager = new LinkManager(rootDir.resolve("tileToZone.link"));
            try {
                tileToZoneLinkManager.setDAO(daoManager.getTileDao());
            } catch (DAOException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
        return tileToZoneLinkManager;
    }
    
    /**
     * Rétourne le gestionnaire de lien Zone - Game.
     *
     * @return Gestionnaire de lien Zone - Game.
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation du
     * gestionnaire de lien.
     */
    synchronized public LinkManager<TileZone> getZoneToGameLinkManager() throws DAOFileException {
        if(zoneToGameLinkManager == null) {
            zoneToGameLinkManager = new LinkManager(rootDir.resolve("zoneToGame.link"));
            try {
                zoneToGameLinkManager.setDAO(daoManager.getZoneDao());
            } catch (DAOException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
        return zoneToGameLinkManager;
    }
    
    /**
     * Rétourne le gestionnaire de lien Player - SimpleGame.
     *
     * @return Gestionnaire de lien Player - SimpleGame.
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation du
     * gestionnaire de lien.
     */
    synchronized public PlayerToSimpleGameLinkManager getPlayerToSimpleGameLinkManager() throws DAOFileException {
        if(playerToSimpleGameLinkManager == null) {
            playerToSimpleGameLinkManager = new PlayerToSimpleGameLinkManager(rootDir.resolve("playerToSimpleGame.link"));
//            playerToSimpleGameLinkManager.getLinkManager().setDAO(daoManager.getPlayerDao());
        }
        return playerToSimpleGameLinkManager;
    }
}