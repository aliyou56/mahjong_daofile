
package fr.univubs.inf1603.mahjong.daofile.myengine;

import fr.univubs.inf1603.mahjong.dao.Zone;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author aliyou
 */
public class Board {
    
    UUID uuid;
    List<Zone> zones;
    
    Board(List<Zone> zones) {
        this(UUID.randomUUID(), zones);
    }  
    
    Board(UUID uuid, List<Zone> zones) {
        this.uuid = uuid;
        this.zones = zones;
    }
}
