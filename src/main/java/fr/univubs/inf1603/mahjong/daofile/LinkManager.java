package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import fr.univubs.inf1603.mahjong.daofile.LinkRow.Link;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Cette classe gère l'ensemble des liens {@link LinkRow.Link} entre un objet fils
 * <code>T</code> et un objet parent. Un Lien est répresenté par les
 * identifiants <code>UUID</code> des 2 objets qui sont liés. Chaque lien est
 * encapsulé dans un tuple de lien <code>LinkRow</code>.
 *
 * <pre>
 * format d'un fichier d'index :
 *        ---------------------------------------------
 *        | ----------------------------------------- |
 *        | | rowID = 0 |        FileHeader         | |  --> FileHeaderRow
 *        | ----------------------------------------- |
 *        | | rowID = 1 | Link=[childID | parentID] | |  --> LinkRow
 *        | ----------------------------------------- |
 *        | | rowID = 2 | Link=[childID | parentID] | |  --> LinkRow
 *        | ----------------------------------------- |
 *        | | rowID = x | Link=[childID | parentID] | |  --> LinkRow
 *        | ----------------------------------------- |
 *        ---------------------------------------------
 * </pre>
 *
 * @see AbstractRowManager
 * @author aliyou, nesrine
 * @version 1.0.0
 * @param <T> Objet enfant du lien.
 */
public class LinkManager<T extends Persistable> extends AbstractRowManager<LinkRow> {

    /**
     * Tableau associatif associant l'identifiant d'un objet parent à un
     * ensemble d'identifiants d'objets enfants.
     */
    private final HashMap<UUID, ArrayList<UUID>> mapParentChild;
    /**
     * DAO
     */
    private FileDAOMahjong<T> dao = null;

    /**
     * Constructeur avec le chemin d'accès du fichier de lien.
     *
     * @param linkFilePath Chemin d'accès du fichier de lien.
     * @throws IOException s'il y'a une erreur lors de l'instanciation.
     */
    LinkManager(Path linkFilePath) throws IOException {
        super(linkFilePath, LinkRow.LINK_ROW_SIZE);
        this.mapParentChild = new HashMap<>();
        getRowsSortedByRowPointer().forEach((row) -> {
            Link link = (Link) row.getData();
            putInMap(link);
        });
        System.out.println(" -> LinkManager");
    }

    /**
     * Définit le DAO à utiliser.
     *
     * @param dao DAO gerant des objets <code>T</code>.
     */
    void setDAO(DAO<T> dao) {
        this.dao = (FileDAOMahjong<T>) dao;
    }

    /**
     * Insere un lien dans le tableau associatif parent - enfant.
     *
     * @param link Lien à inserer.
     */
    private void putInMap(Link link) {
        if (mapParentChild.containsKey(link.getParentID())) {
            mapParentChild.get(link.getParentID()).add(link.getUUID());
        } else {
            ArrayList<UUID> list = new ArrayList<>();
            list.add(link.getUUID());
            mapParentChild.put(link.getParentID(), list);
        }
    }

    /**
     * Retire un lien du tableau associatif parent - enfant.
     *
     * @param link lien à retirer.
     */
    private void removeFromMap(Link link) {
        if (mapParentChild.containsKey(link.getParentID())) {
            ArrayList<UUID> list = mapParentChild.get(link.getParentID());
            list.remove(link.getUUID());
            if (list.isEmpty()) {
                mapParentChild.remove(link.getParentID());
            }
        }
    }

//    boolean isChildLinkedToAParent(UUID childID) {
//        return mapParentChild.entrySet().stream().anyMatch((entry) -> (entry.getValue().contains(childID)));
//    }

//    private void printMap() {
//        mapParentChild.entrySet().forEach((entry) -> {
//            System.out.println(entry.getKey() + " -> " + entry.getValue());
//        });
////////        for(Map.Entry<UUID, ArrayList<UUID>> entry : mapParentChild.entrySet()) {
////////            System.out.println(entry.getKey() +" -> "+ entry.getValue());
////////        }
//    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected LinkRow readRowFromBuffer(ByteBuffer buffer, long rowPointer) {
        return LinkRow.readFromBuffer(buffer, rowPointer);
    }

    /**
     * Lie un ensemble d'objets enfant à un objet parent.
     *
     * @param parentID Identifiant de l'objet parent.
     * @param children Liste des objets enfants.
     * @throws DAOException s'il y'a une erreur lors de la liaison.
     */
    void addChildren(UUID parentID, List<T> children) throws DAOException {
        System.out.println("parentID : " + parentID + ", nbChilds to add : " + children.size());
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
     * Supprime tous les liens existant entre un ensemble d'objets enfants et un 
     * objet parent.
     * 
     * @param children Liste des objets enfants.
     * @throws IOException s'il y'a une erreur lors de la suppression.
     */
    void removeChildren(List<T> children) throws IOException {
        if (!children.isEmpty()) {
            LinkRow firstLinkRow = getRow(children.get(0).getUUID());
            long pointer = firstLinkRow.getRowPointer();
            for (T child : children) {
                LinkRow linkRow = getRow(child.getUUID());
                if (pointer > linkRow.getRowPointer()) {
                    pointer = linkRow.getRowPointer();
                    firstLinkRow = linkRow;
                }
                super.removeRowFromRowsList(linkRow);
                removeFromMap(linkRow.getData());
            }

            int offset = children.size() * LinkRow.LINK_ROW_SIZE;
            if (rowWriter.deleteFromFile((int) pointer, offset)) {
                super.updateRowsPointer(firstLinkRow.getRowPointer(), offset);
                dao.deleteFromPersistance(children);
            }
        }
    }

    /**
     * Charge tous les objets enfants <code>T</code> liés à un objet parent en
     * mémoire.
     *
     * @param parentID Identifiant de l'objet parent du lien.
     * @return Liste des objets enfants <code>T</code>.
     * @throws DAOException s'il y'a une erreur lors du chargement.
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