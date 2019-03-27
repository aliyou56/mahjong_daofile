package fr.univubs.inf1603.mahjong.daofile.myengine;

import fr.univubs.inf1603.mahjong.dao.AbstractTile;
import fr.univubs.inf1603.mahjong.dao.Zone;
import fr.univubs.inf1603.mahjong.engine.TileZoneException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This class is a representation of a zone where the tiles will be contains
 *
 * @author COGOLUEGNES Charles
 */
public class TileZone implements Zone {

    private final PropertyChangeSupport pcs;
    private final UUID id;
    private final String name;
    private ArrayList<AbstractTile> tiles;
//    private ArrayList<Zone> zones;

    /**
     * The constructor of TileZone, the id is generate in the constructor
     *
     * @param name The name of the zone
     * @param tiles The collection of tiles contains in this zone (cannot be
     * null)
     * @param zones The collection of zones contains in this zone (can be null)
     * @throws TileZoneException if the collection of tiles is null
     */
    public TileZone(String name, ArrayList<AbstractTile> tiles) throws TileZoneException { //, ArrayList<Zone> zones
        this(UUID.randomUUID(), name, tiles); //zones
    }

    public TileZone(UUID uuid, String name, ArrayList<AbstractTile> tiles) throws TileZoneException { //, ArrayList<Zone> zones
        this.id = uuid;
        this.name = name;

        if (tiles == null) {
            throw new TileZoneException("The collection of tiles cannot be null.");
        }
        this.tiles = tiles;

//        this.zones = zones;
        this.pcs = new PropertyChangeSupport(this);
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the collection that contains the tiles
     *
     * @return tiles
     */
    @Override
    public ArrayList<AbstractTile> getTilesCollection() {
        return this.tiles;
    }

    /**
     * Returns the collection that contains the zones
     *
     * @return zones
     */
//    @Override
//    public ArrayList<Zone> getZonesCollection() {
//        return this.zones;
//    }

    /**
     * Not suppose to be used in TileZone
     *
     * @return 0
     */
//    @Override
//    public int getMoney() {
//        return 0;
//    }

    /**
     * Returns the id of the TileZone
     *
     * @return id
     */
    @Override
    public UUID getUUID() {
        return this.id;
    }

    /**
     * Allows to add a tile in the collection
     *
     * @param tile The tile we want to add
     * @return if the tile has been correctly added
     */
    public boolean addTile(AbstractTile tile) {
        return this.tiles.add(tile);
    }

    /**
     * Allows to remove a tile from the collection
     *
     * @param tile The tile we want to remove
     * @return if the tile has been correctly removed
     */
    public boolean removeTile(AbstractTile tile) {
        return this.tiles.remove(tile);
    }

    /**
     * Allows to add a zone in the collection
     *
     * @param zone The zone we want to add
     * @return if the zone has been correctly added
     */
//    public boolean addZone(Zone zone) {
//        return this.zones.add(zone);
//    }

    /**
     * Allows to remove a zone in the collection
     *
     * @param listener
     * @param zone The zone we want to remove
     * @return if the zone has been correctly removed
     */
//    public boolean removeZone(Zone zone) {
//        return this.zones.remove(zone);
//    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "TileZone{" + "id=" + id + ", name=" + name + ", tiles=" + tiles + '}'; // ", zones=" + zones +
    }
    
}
