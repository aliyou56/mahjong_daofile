package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.dao.DAO;
import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.FileDAOMahjong;
import static fr.univubs.inf1603.mahjong.daofile.FileDAOUtilities.checkNotNull;
import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.LinkRow.Link;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cette classe gère l'ensemble des liens {@link LinkRow.Link} entre un objet
 * enfant <code>T</code> et un objet parent. Un Lien est répresenté par les
 * identifiants <code>UUID</code> des 2 objets qui sont liés. Chaque lien est
 * encapsulé dans un tuple de lien <code>LinkRow</code>.
 *
 * <pre>
 *
 *  format d'un fichier de lien :
 *
 *        ---------------------------------------------
 *        | ----------------------------------------- |
 *        | | rowID = 0 |        FileHeader         | |  --{@literal >} FileHeaderRow
 *        | ----------------------------------------- |
 *        | | rowID = 1 | Link=[childID | parentID] | |  --{@literal >} LinkRow
 *        | ----------------------------------------- |
 *        | | rowID = 2 | Link=[childID | parentID] | |  --{@literal >} LinkRow
 *        | ----------------------------------------- |
 *        | | rowID = x | Link=[childID | parentID] | |  --{@literal >} LinkRow
 *        | ----------------------------------------- |
 *        ---------------------------------------------
 * </pre>
 *
 * @see AbstractRowManager
 * @author aliyou, nesrine
 * @version 1.2.4
 * @param <T> Objet <code>T</code> enfant du lien.
 */
public class LinkManager<T extends Persistable> extends AbstractRowManager<LinkRow> {

    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(LinkManager.class.getName());
    
    /**
     * Tableau associatif associant l'identifiant d'un objet parent à un
     * ensemble d'identifiants d'objets enfants.
     */
    private final HashMap<UUID, ArrayList<UUID>> mapParentChild;
    /**
     * DAO gérant les objets enfants du lien.
     */
    private FileDAOMahjong<T> dao = null;

    /**
     * Constructeur avec le chemin d'accès du fichier de lien.
     *
     * @param linkFilePath Chemin d'accès du fichier de lien.
     * @throws DAOFileException s'il y'a une erreur lors de l'instanciation.
     */
    public LinkManager(Path linkFilePath) throws DAOFileException {
        super(linkFilePath, LinkRow.LINK_ROW_SIZE);
//        System.out.println(" -> LinkManager");
        this.mapParentChild = new HashMap<>();
        getRowsSortedByRowPointer().forEach((row) -> {
            Link link = (Link) row.getData();
            putInMap(link);
        });
//        printMap();
    }

    /**
     * @return FileDAOMahjong
     */
    public FileDAOMahjong<T> getDAO() {
        return dao;
    }

    /**
     * Définit le DAO à utiliser.
     *
     * @param dao DAO gerant des objets <code>T</code>.
     */
    public void setDAO(DAO<T> dao) {
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

    private void printMap() {
        System.out.println("");
        mapParentChild.entrySet().forEach((entry) -> {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        });
        System.out.println("");
//////        for(Map.Entry<UUID, ArrayList<UUID>> entry : mapParentChild.entrySet()) {
//////            System.out.println(entry.getKey() +" -> "+ entry.getValue());
//////        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected LinkRow createRow(ByteBuffer buffer, long rowPointer) throws DAOFileException {
        return new LinkRow(buffer, rowPointer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LinkRow createRow(long rowPointer) throws DAOFileException {
        return new LinkRow(rowWriter, rowPointer);
    }

    /**
     * Ajoute un nouveau lien.
     * 
     * @param parentID Identifiant de l'objet parent.
     * @param child Identifiant de l'objet enfant.
     * @throws DAOFileException s'il y'a une erreur lors de l'ajout.
     */
    public void addLink(UUID parentID, T child) throws DAOFileException {
        checkNotNull("parentID", parentID);
        checkNotNull("child", child);
        LOGGER.log(Level.FINE, "addChild -> parentID : {0} childID : {1}", new Object[]{parentID, child.getUUID()});
        Link link = new Link(child.getUUID(), parentID);
        LinkRow newLinkRow = new LinkRow(getNextRowID(), link, getNextRowPointer());
        super.addRow(newLinkRow);
        putInMap(link);
        try {
            if (dao.find(child.getUUID()) == null) {
                dao.save(child);
            }
        } catch (DAOException ex) {
            throw new DAOFileException(ex.getMessage(), ex);
        }
    }

    /**
     * Lie un ensemble d'objets <code>T</code> enfant à un objet parent.
     *
     * @param parentID Identifiant de l'objet parent.
     * @param children Liste des objets <code>T</code> enfants.
     * @throws DAOFileException s'il y'a une erreur lors de la liaison.
     */
    public void addLink(UUID parentID, List<T> children) throws DAOFileException {
        checkNotNull("parentID", parentID);
        checkNotNull("children", children);
        LOGGER.log(Level.INFO, "parentID : {0}, nbChilds to add : {1}", new Object[]{parentID, children.size()});
        for (T child : children) {
            addLink(parentID, child);
        }
    }

    /**
     * Mets à jour les liens existant entre un objet parent et ses objets enfants.
     * 
     * @param parentID Identifiant de l'objet parent.
     * @param children Liste des objets <code>T</code> enfants.
     * @throws DAOFileException s'il y'a une erreur lors de la mis à jour.
     */
    public void updateLink(UUID parentID, List<T> children) throws DAOFileException {
        checkNotNull("parentID", parentID);
        checkNotNull("children", children);
        ArrayList<UUID> existedChildrenIDs = mapParentChild.get(parentID);
        if (existedChildrenIDs.size() > children.size()) { // delete 
            ArrayList<UUID> childrenIDs = new ArrayList<>();
            children.forEach(child -> {
                childrenIDs.add(child.getUUID());
            });
            ArrayList<UUID> toRemovechildrenIDs = new ArrayList<>();
            existedChildrenIDs.forEach(childID -> {
                if (!childrenIDs.contains(childID)) {
                    toRemovechildrenIDs.add(childID);
                }
            });

            LOGGER.log(Level.INFO, " updateChildrenLink : parentID : {0}, nbChilds to remove : {1}", new Object[]{parentID, toRemovechildrenIDs.size()});
            try {
                removeChildrenByID(toRemovechildrenIDs);
            } catch (DAOException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        } else {
            List<T> toUpdateList = new ArrayList<>();
            children.forEach(child -> {
                if (!existedChildrenIDs.contains(child.getUUID())) {
                    toUpdateList.add(child);
                }
            });
            LOGGER.log(Level.INFO, " updateChildrenLink : parentID : {0}, nbNewChilds to add : {1}", new Object[]{parentID, toUpdateList.size()});
            LinkManager.this.addLink(parentID, toUpdateList);
        }
    }

    private void removeChildrenByID(List<UUID> childIDs) throws DAOException, DAOFileException {
        List<T> toRemove = new ArrayList<>();
        for(UUID childID : childIDs) {
            T child = dao.find(childID);
            if(child != null) {
                toRemove.add(child);
            }
        }
        removeChildren(toRemove);
    }

    /**
     * Supprime tous les liens existant entre un ensemble d'objets enfants
     * <code>T</code> et un objet parent.
     *
     * @param children Liste des objets enfants.
     * @throws DAOFileException s'il y'a une erreur lors de la suppression des
     * liens.
     */
    public void removeChildren(List<T> children) throws DAOFileException {
        checkNotNull("children", children);
        if (!children.isEmpty()) {
            List<LinkRow> multipleRemoveList = super.getRowList(children);
            List<LinkRow> singleRemoveList = super.getSingleRemoveList(multipleRemoveList);
            if (!singleRemoveList.isEmpty()) {
                for (LinkRow linkRow : singleRemoveList) {
                    super.removeRow(linkRow);
                    removeFromMap(linkRow.getData());
                }
            }
            if (!multipleRemoveList.isEmpty()) {
                long startPointer = multipleRemoveList.get(0).getRowPointer();
                multipleRemoveList.forEach(linkRow -> {
                    super.removeRowFromList(linkRow);
                    removeFromMap(linkRow.getData());
                });
                int offset = multipleRemoveList.size() * rowSize;
                try {
                    if (rowWriter.deleteFromFile((int) startPointer, offset)) {
                        super.updateRowsPointer(startPointer, offset);
                        dao.delete(children);
                        LOGGER.log(Level.INFO, " [OK] {0} Link successful deleted -> startPointer : {1} -- offset : {2}",
                                new Object[]{multipleRemoveList.size(), startPointer, offset});
                        StringBuilder sb = new StringBuilder();
                        sb.append("\t{ ");
                        multipleRemoveList.forEach(linkRow -> {
                            sb.append("\n\t childID = ").append(linkRow.getData().getUUID()).append(" -> parentID = ").append(linkRow.getData().getParentID());
                        });
                        sb.append("\t} ");
                        LOGGER.log(Level.FINE, sb.toString());
                    }
                } catch (DAOFileWriterException ex) {
                    throw new DAOFileException(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Charge en mémoire tous les objets enfants <code>T</code> liés à un objet
     * parent.
     *
     * @param parentID Identifiant de l'objet parent du lien.
     * @return Liste des objets enfants <code>T</code>.
     * @throws DAOException s'il y'a une erreur lors du chargement.
     */
    public ArrayList<T> loadChildren(UUID parentID) throws DAOException {
        ArrayList<T> children = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("\n parentID=").append(parentID).append(", children = { ");
        if (mapParentChild.containsKey(parentID)) {
            for (UUID childID : mapParentChild.get(parentID)) {
                T child = dao.find(childID);
                sb.append("\n\tchilID=").append(childID).append(", loadedID=").append(child.getUUID());
                children.add(child);
            }
        }
        sb.append("\n   }\n");
        LOGGER.log(Level.FINE, sb.toString());
        return children;
    }
}
