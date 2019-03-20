
package fr.univubs.inf1603.mahjong.daofile.myengine;

//import fr.univubs.inf1603.mahjong.engine.AbstractTile;
import fr.univubs.inf1603.mahjong.dao.AbstractTile;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

/**
 *
 * @author aliyou
 */
public class Tile implements AbstractTile {
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private final UUID uuid;
    private String category;
    private String family;
    
    public Tile(UUID uuid, String category, String family) {
        this.uuid = uuid;
        this.category = category;
        this.family = family;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void setCategory(String category) {
        String oldValue = this.category;
        this.category = category;
        this.pcs.firePropertyChange("categorie", oldValue, category);
    }

    @Override
    public String getFamily() {
        return family;
    }

    @Override
    public void setFamily(String family) {
        String oldValue = this.family;
        this.family = family;
        this.pcs.firePropertyChange("family", oldValue, family);
    }
 
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return "Tile{" + "uuid=" + uuid + ", category=" + category + ", family=" + family + '}';
    }
    
}
