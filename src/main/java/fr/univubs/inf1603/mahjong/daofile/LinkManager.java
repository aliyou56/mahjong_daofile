
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.Persistable;
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
 * @param <T>
 */
public class LinkManager<T extends Persistable> extends AbstractRowManager<LinkRow>{
    
    /**
     *
     */
    private HashMap<UUID, ArrayList<UUID>> mapParentChild;
    /**
     *
     */
    private DAO<T> dao = null;

    /**
     *
     * @param linkFilePath
     * @throws IOException
     */
    LinkManager(Path linkFilePath) throws IOException { //, DAO<T> dao
        super(linkFilePath, LinkRow.LINK_ROW_SIZE);
        this.mapParentChild = new HashMap<>();
//        this.dao = dao;
        fillMap();
    }
    
    void setDAO(DAO<T> dao) {
        this.dao = dao;
    }

    /**
     *
     */
    private void fillMap() {
        System.out.println("fillMap");
        getRows().forEach((row) -> {
            LinkRow.Link link = row.getData();
            System.out.println(link);
            putInMap(link);
        });
        printMap();
    }
/**
 * 
 * @param link 
 */
    private void putInMap(LinkRow.Link link) {
        if (mapParentChild.containsKey(link.getParentID())) {
            mapParentChild.get(link.getParentID()).add(link.getUUID());
        } else {
            ArrayList<UUID> list = new ArrayList<>();
            list.add(link.getUUID());
            mapParentChild.put(link.getParentID(), list);
        }
    }
    
    /**
     * 
     * @param link 
     */
    private void removeFromMap(LinkRow.Link link) {
        if (mapParentChild.containsKey(link.getParentID())) {
            ArrayList<UUID> list = mapParentChild.get(link.getParentID());
            list.remove(link.getUUID());
            if(list.isEmpty()) {
                mapParentChild.remove(link.getParentID());
            }
        }
    }
 
    boolean isChildLinkedToAParent(UUID childID) {
        return mapParentChild.entrySet().stream().anyMatch((entry) -> (entry.getValue().contains(childID)));
    }
    
    /**
     * 
     */
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
    protected LinkRow readRowFromBuffer(ByteBuffer buffer, long rowPointer) {
        return LinkRow.readFromBuffer(buffer, rowPointer);
    }

    /**
     * 
     * @param parentID
     * @param children
     * @throws DAOException 
     */
    void addChildren(UUID parentID, ArrayList<T> children) throws DAOException {
        try {
            for (T child : children) {
                Link link = new Link(child.getUUID(), parentID);
                LinkRow newLinkRow = new LinkRow(getNextRowID(), link, getNextRowPointer());
                super.addRow(newLinkRow);
                putInMap(link);
                if (dao.find(child.getUUID()) == null) {
                    dao.save(child);
                }
            }
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }

    /**
     *
     * @param parentID
     * @throws DAOException
     */
    void removeChildren(UUID parentID, ArrayList<T> children) throws DAOException {
        try {
            for (T child : children) {
                LinkRow removedLinkRow = (LinkRow) super.removeRow(child.getUUID());
                if (removedLinkRow != null) {
                    LinkRow.Link removedLink = removedLinkRow.getData();
                    removeFromMap(removedLink);
                    System.out.println("link removed : " + removedLinkRow);
//                      printMap();
                } else {
                    System.out.println("removedlinkrow == null");
                }
                dao.delete(child.getUUID());
            }
        } catch (IOException ex) {
            throw new DAOException("Erreur IO : \n" + ex.getMessage());
        }
    }
    
    /**
     *
     * @param parentID
     * @return
     * @throws DAOException
     */
    ArrayList<T> loadChildren(UUID parentID) throws DAOException {
        if (mapParentChild.containsKey(parentID)) {
            ArrayList<T> children = new ArrayList<>();
            for (UUID childID : mapParentChild.get(parentID)) {
                T child = dao.find(childID);
                children.add(child);
            }
            return children;
        }
        return null;
    }
}
