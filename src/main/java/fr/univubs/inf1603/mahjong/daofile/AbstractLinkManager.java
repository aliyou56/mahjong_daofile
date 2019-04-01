package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOManager;
import fr.univubs.inf1603.mahjong.daofile.LinkRow.Link;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author aliyou
 */
public abstract class AbstractLinkManager extends AbstractRowManager<LinkRow> {

    /**
     *
     */
    protected HashMap<UUID, ArrayList<UUID>> mapParentChild;
    /**
     *
     */
    protected static DAOManager daoManager;

    /**
     *
     * @param rowFilePath
     * @throws IOException
     */
    protected AbstractLinkManager(Path rowFilePath) throws IOException {
        super(rowFilePath, LinkRow.LINK_ROW_SIZE);
        this.mapParentChild = new HashMap<>();
        daoManager = FileDAOManager.getInstance();
        fillMap();
    }

    /**
     *
     */
    private void fillMap() {
        System.out.println("fillMap");
        getRows().forEach((row) -> {
            Link link = row.getData();
            System.out.println(link);
            put(link.getUUID(), link.getParentID());
        });
        printMap();
    }

    private void put(UUID childUUID, UUID parentUUID) {
        if (mapParentChild.containsKey(parentUUID)) {
            mapParentChild.get(parentUUID).add(childUUID);
        } else {
            ArrayList<UUID> list = new ArrayList<>();
            list.add(childUUID);
            mapParentChild.put(parentUUID, list);
        }
    }
    
    private void remove(UUID childUUID, UUID parentUUID) {
        if (mapParentChild.containsKey(parentUUID)) {
            ArrayList<UUID> list = mapParentChild.get(parentUUID);
            list.remove(childUUID);
            if(list.isEmpty()) {
                mapParentChild.remove(parentUUID);
            }
        }
    }

    private void printMap() {
        mapParentChild.entrySet().forEach((entry) -> {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        });
//////        for(Map.Entry<UUID, ArrayList<UUID>> entry : mapParentChild.entrySet()) {
//////            System.out.println(entry.getKey() +" -> "+ entry.getValue());
//////        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LinkRow readRow(ByteBuffer buffer, long rowPointer) {
        return LinkRow.readFromBuffer(buffer, rowPointer);
    }

    void addLink(Link link) throws IOException {
        System.out.println("AbstractLinkManager -> addLink");
        LinkRow newLinkRow = new LinkRow(getNextRowID(), link, getNextRowPointer());
        super.addRow(newLinkRow);
        put(link.getUUID(), link.getParentID());
    }

    void removeLink(UUID linkID) throws IOException {
        System.out.println("AbstractLinkManager -> removeLink");
        LinkRow removedLinkRow = (LinkRow) super.removeRow(linkID);
        if(removedLinkRow != null) {
            Link removedLink = removedLinkRow.getData();
            remove(removedLink.getUUID(), removedLink.getParentID());
            System.out.println("removed from linkfile : " + removedLinkRow);
            printMap();
        } else {
            System.out.println("removedlinkrow == null");
        }
    }

}
