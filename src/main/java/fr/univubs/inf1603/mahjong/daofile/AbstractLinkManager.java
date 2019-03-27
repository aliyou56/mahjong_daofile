
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
    
    protected static DAOManager daoManager;
    
    protected AbstractLinkManager(Path rowFilePath) throws IOException {
        super(rowFilePath, LinkRow.LINK_ROW_SIZE);
        this.mapParentChild = new HashMap<>();
        daoManager = FileDAOManager.getInstance();
        fillMap();
    }
    
    private void fillMap() {
        System.out.println("fillMap");
        getRows().forEach((row) -> {
            Link link = row.getData();
            System.out.println(link);
            put(link.getUUID(), link.getParentID());
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected LinkRow readRow(ByteBuffer buffer, long rowPointer) {
        return LinkRow.readFromBuffer(buffer, rowPointer);
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

    void addLink(Link link) throws IOException {
        LinkRow newLinkRow = new LinkRow(getNextRowID(), link, getNextRowPointer());
        super.addRow(newLinkRow);
        put(link.getUUID(), link.getParentID());
    }
    
    void removeLink(UUID dataID) throws IOException {
        LinkRow linkRow = (LinkRow) super.removeRow(dataID);
//        Link data = new Link(childUUID, parentUUID);
//        LinkRow newLinkRow = new LinkRow(getNextRowID(), data, getNextRowPointer());
//        super.addRow(newLinkRow);
//        put(childUUID, parentUUID);
    }
    
}
