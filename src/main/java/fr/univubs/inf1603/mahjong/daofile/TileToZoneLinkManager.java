
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.AbstractTile;
import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import static fr.univubs.inf1603.mahjong.daofile.AbstractLinkManager.daoManager;
import fr.univubs.inf1603.mahjong.daofile.LinkRow.Link;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author aliyou
 */
public class TileToZoneLinkManager extends AbstractLinkManager {

    private static TileToZoneLinkManager zoneTileLink;

    private static DAO<AbstractTile> tileDao;

    private TileToZoneLinkManager(Path rowFilePath) throws IOException, DAOException {
        super(rowFilePath);
        tileDao = daoManager.getTileDao();
        System.out.println("TileToZoneLinkManager : " + fileHeader);
    }

    public static TileToZoneLinkManager getInstance(Path rowFilePath) throws IOException, DAOException {
        if (zoneTileLink == null) {
            zoneTileLink = new TileToZoneLinkManager(rowFilePath);
        }
        return zoneTileLink;
    }

    void addTiles(UUID zoneID, ArrayList<AbstractTile> tiles) throws DAOException {
        try {
            for (AbstractTile tile : tiles) {
                Link link = new Link(tile.getUUID(), zoneID);
                super.addLink(link);
                if (tileDao.find(tile.getUUID()) == null) {
                    tileDao.save(tile);
                }

            }
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    void removeTiles(UUID zoneID) throws DAOException {
        if (mapParentChild.containsKey(zoneID)) {
            for (UUID tileID : mapParentChild.get(zoneID)) {
                tileDao.delete(tileID); 
            }
        }
    }

    ArrayList<AbstractTile> loadTilesCollection(UUID zoneID) throws DAOException { 
        if (mapParentChild.containsKey(zoneID)) {
            ArrayList<AbstractTile> tiles = new ArrayList<>();
            for (UUID tileID : mapParentChild.get(zoneID)) {
                AbstractTile tile = tileDao.find(tileID); 
                tiles.add(tile);
            }
            return tiles;
        }
        return null;
    }
}
