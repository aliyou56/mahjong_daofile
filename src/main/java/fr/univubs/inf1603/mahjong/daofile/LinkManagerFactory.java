
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.dao.fake_engine.GameTile;
import fr.univubs.inf1603.mahjong.dao.fake_engine.Zone;
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
     * 
     */
    private static LinkManagerFactory factory;
    /**
     * 
     */
    private static DAOManager daoManager;
    
    /**
     * 
     */
    private LinkManager<GameTile> tileToZoneLinkManager;
    /**
     * 
     */
    private LinkManager<Zone> zoneToZoneLinkManager;
    
    /**
     * 
     */
    private final Path rootDir;
    
    /**
     * 
     * @param rootDir 
     */
    private LinkManagerFactory(Path rootDir) {
        this.rootDir = rootDir;
        daoManager = FileDAOManager.getInstance();
    }
    
    /**
     * 
     * @param rootDir
     * @return 
     */
    public static LinkManagerFactory getInstance(Path rootDir) {
        if(factory == null) {
            factory = new LinkManagerFactory(rootDir);
        }
        return factory;
    }
    
    /**
     * 
     * @return
     * @throws DAOException 
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
    
    /**
     * 
     * @return
     * @throws DAOException 
     */
    public LinkManager<Zone> getZoneToZoneLinkManager() throws DAOException {
        try {
            if (zoneToZoneLinkManager == null) {
                zoneToZoneLinkManager = new LinkManager(Paths.get(rootDir.toString(), "zoneToZone.link"));
                zoneToZoneLinkManager.setDAO(daoManager.getZoneDao());
            }
        } catch (IOException ioe) {
            throw new DAOException("Erreur IO : " + ioe.getMessage());
        }
        return zoneToZoneLinkManager;
    }
}
