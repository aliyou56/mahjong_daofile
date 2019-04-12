package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author aliyou, nesrine
 * @version 1.0.0
 */
public class LinkManagerFactory {

    /**
     * Factory
     */
    private static LinkManagerFactory factory;
    /**
     * Gestionnaire des DAOs.
     */
    private static DAOManager daoManager;

    /**
     * Gestionnaire de lien entre une zone et ses tuiles.
     */
    private LinkManager<GameTile> tileToZoneLinkManager;

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
        daoManager = FileDAOManager.getInstance();
    }

    /**
     * Rétourne l'instance de la factory.
     *
     * @param rootDir Chemin d'accès du repertoire racine.
     * @return L'instance de la factory.
     */
    public static LinkManagerFactory getInstance(Path rootDir) {
        if (factory == null) {
            factory = new LinkManagerFactory(rootDir);
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
    public LinkManager<GameTile> getTileToZoneLinkManager() throws DAOException {
        try {
            if (tileToZoneLinkManager == null) {
                tileToZoneLinkManager = new LinkManager(Paths.get(rootDir.toString(), "tileToZone.link"));
                tileToZoneLinkManager.setDAO(daoManager.getTileDao());
            }
        } catch (IOException ioe) {
            throw new DAOException("Erreur IO : " + ioe.getMessage());
        }
        return tileToZoneLinkManager;
    }

}