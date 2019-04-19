
package fr.univubs.inf1603.mahjong.daofile.engine;

import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import fr.univubs.inf1603.mahjong.engine.game.TileZoneIdentifier;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author aliyou
 */
public class MahjongTileZone implements TileZone{

    private final UUID uuid;
    private ArrayList<GameTile> tiles;
    private TileZoneIdentifier identifier;

    public MahjongTileZone(UUID uuid, ArrayList<GameTile> tiles, TileZoneIdentifier identifier) {
        this.uuid = uuid;
        this.tiles = tiles;
        this.identifier = identifier;
    }
    
    public MahjongTileZone(ArrayList<GameTile> tiles, TileZoneIdentifier identifier) {
        this(UUID.randomUUID(), tiles, identifier);
    }
    
    @Override
    public ArrayList<GameTile> getTiles() {
        return this.tiles;
    }

    @Override
    public TileZoneIdentifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    @Override
    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.pcs;
    }
}
