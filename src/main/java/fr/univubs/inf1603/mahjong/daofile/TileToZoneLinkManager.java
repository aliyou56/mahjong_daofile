package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.fake_engine.GameTile;
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
public class TileToZoneLinkManager extends AbstractLinkManager {

    /**
     *
     * @param rootDir
     * @throws IOException
     */
    TileToZoneLinkManager(Path rootDir) throws IOException {
        super(Paths.get(rootDir.toString(), "tileToZone.link"));
        System.out.println("TileToZoneLinkManager : " + fileHeader);
    }

    /**
     *
     * @param zoneID
     * @param tiles
     * @throws DAOException
     */
    void addTiles(UUID zoneID, ArrayList<GameTile> tiles) throws DAOException {
        System.out.println("TileToZoneLinkManager -> addTiles : zoneID : " + zoneID);
        try {
            for (GameTile tile : tiles) {
                Link link = new Link(tile.getUUID(), zoneID);
                super.addLink(link);
                if (daoManager.getTileDao().find(tile.getUUID()) == null) {
                    daoManager.getTileDao().save(tile);
                }

            }
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    /**
     *
     * @param zoneID
     * @throws DAOException
     */
    void removeTiles(UUID zoneID, ArrayList<GameTile> tiles) throws DAOException {
        System.out.println("TileToZoneLinkManager -> removeTiles : zoneID : " + zoneID);
        try {
            for (GameTile tile : tiles) {
                super.removeLink(tile.getUUID());
                daoManager.getTileDao().delete(tile.getUUID());
            }
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    /**
     *
     * @param zoneID
     * @return
     * @throws DAOException
     */
    ArrayList<GameTile> loadTilesCollection(UUID zoneID) throws DAOException {
        System.out.println("TileToZoneLinkManager -> loadTiles : zoneID : " + zoneID);
        if (mapParentChild.containsKey(zoneID)) {
            ArrayList<GameTile> tiles = new ArrayList<>();
            for (UUID tileID : mapParentChild.get(zoneID)) {
                GameTile tile = daoManager.getTileDao().find(tileID);
                tiles.add(tile);
            }
            return tiles;
        }
        return null;
    }
}
