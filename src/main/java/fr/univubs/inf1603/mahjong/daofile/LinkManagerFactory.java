
package fr.univubs.inf1603.mahjong.daofile;

import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author aliyou
 */
public class LinkManagerFactory {
    
    private static LinkManagerFactory factory;
    
    private TileToZoneLinkManager tileToZoneLinkManager;
    private ZoneToZoneLinkManager zoneToZoneLinkManager;
    
    private final Path rootDir;
    
    private LinkManagerFactory(Path rootDir) {
        this.rootDir = rootDir;
    }
    
    public static LinkManagerFactory getInstance(Path rootDir) {
        if(factory == null) {
            factory = new LinkManagerFactory(rootDir);
        }
        return factory;
    }
    
    public TileToZoneLinkManager getTileToZoneLinkManager() throws IOException {
        if(tileToZoneLinkManager == null) {
            tileToZoneLinkManager = new TileToZoneLinkManager(rootDir);
        }
        return tileToZoneLinkManager;
    }
    
    public ZoneToZoneLinkManager getZoneToZoneLinkManager() throws IOException {
        if(zoneToZoneLinkManager == null) {
            zoneToZoneLinkManager = new ZoneToZoneLinkManager(rootDir);
        }
        return zoneToZoneLinkManager;
    }
}
