
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.fake_engine.Zone;
import fr.univubs.inf1603.mahjong.daofile.LinkRow.Link;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author aliyou
 */
public class ZoneToZoneLinkManager extends AbstractLinkManager {
    
    /**
     * 
     * @param rootDir
     * @throws IOException 
     */
    ZoneToZoneLinkManager(Path rootDir) throws IOException {
        super(Paths.get(rootDir.toString(), "zoneToZone.link"));
        System.out.println("ZoneToZoneLinkManager : " + fileHeader);
    }
    
    /**
     * 
     * @param parentZoneID
     * @param zones
     * @throws DAOException 
     */
    void addZones(UUID parentZoneID, ArrayList<Zone> zones) throws DAOException {
        try {
            for (Zone childZone : zones) {
                Link link = new Link(childZone.getUUID(), parentZoneID);
                super.addLink(link);
                if (daoManager.getZoneDao().find(childZone.getUUID()) == null) {
                    daoManager.getZoneDao().save(childZone);
                }

            }
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    /**
     * 
     * @param parentZoneID
     * @throws DAOException 
     */
    void removeZones(UUID parentZoneID) throws DAOException {
        if (mapParentChild.containsKey(parentZoneID)) {
            for (UUID childZoneID : mapParentChild.get(parentZoneID)) {
                daoManager.getZoneDao().delete(childZoneID); 
            }
        }
    }

    /**
     * 
     * @param parentZoneID
     * @return
     * @throws DAOException 
     */
    ArrayList<Zone> loadZonesCollection(UUID parentZoneID) throws DAOException { 
        if (mapParentChild.containsKey(parentZoneID)) {
            ArrayList<Zone> zones = new ArrayList<>();
            for (UUID childZoneID : mapParentChild.get(parentZoneID)) {
                Zone zone = daoManager.getZoneDao().find(childZoneID); 
                zones.add(zone);
            }
            return zones;
        }
        return null;
    }
    
}
