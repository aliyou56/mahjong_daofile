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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cette classe gère l'ensemble des liens {@link LinkRow.Link} entre un objet enfant
 * <code>T</code> et un objet parent. Un Lien est répresenté par les
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
 * @version 1.0.0
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
     * DAO
     */
    private FileDAOMahjong<T> dao = null;

    /**
     * Constructeur avec le chemin d'accès du fichier de lien.
     *
     * @param linkFilePath Chemin d'accès du fichier de lien.
     * @throws IOException s'il y'a une erreur lors de l'instanciation.
     */
    LinkManager(Path linkFilePath) throws DAOException {
        super(linkFilePath, LinkRow.LINK_ROW_SIZE);
        System.out.println(" -> LinkManager");
        this.mapParentChild = new HashMap<>();
        getRowsSortedByRowPointer().forEach((row) -> {
            Link link = (Link) row.getData();
            putInMap(link);
        });
//        printMap();
    }

    /**
     * Définit le DAO à utiliser.
     *
     * @param dao DAO gerant des objets <code>T</code>.
     */
    void setDAO(DAO<T> dao) {
        this.dao = (FileDAOMahjong<T>) dao;
//        System.out.println(" set tileDAO : "+this.dao);
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
    protected LinkRow createRow(ByteBuffer buffer, long rowPointer) throws DAOException {
        return new LinkRow(buffer, rowPointer);
    }

    /**
     * Lie un ensemble d'objets <code>T</code> enfant à un objet parent.
     *
     * @param parentID Identifiant de l'objet parent.
     * @param children Liste des objets <code>T</code> enfants.
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
     * Supprime tous les liens existant entre un ensemble d'objets enfants <code>T</code>  et un 
     * objet parent.
     * 
     * @param children Liste des objets enfants.
     * @throws IOException s'il y'a une erreur lors de la suppression.
     */
    void removeChildren(List<T> children) throws IOException, DAOException {
        if (!children.isEmpty()) {
            List<LinkRow> multipleRemoveList = super.getRowList(children);
            List<LinkRow> singleRemoveList = super.getSingleRemoveList(multipleRemoveList);
            if (!singleRemoveList.isEmpty()) {
                for (LinkRow linkRow : singleRemoveList) {
                    super.removeRow(linkRow);
                    removeFromMap(linkRow.getData());
                }
            }
            if(!multipleRemoveList.isEmpty()) {
                long startPointer = multipleRemoveList.get(0).getRowPointer();
                multipleRemoveList.forEach(linkRow -> {
                    super.removeRowFromRowsList(linkRow);
                    removeFromMap(linkRow.getData());
                });
                int offset = multipleRemoveList.size() * rowSize;
                if (rowWriter.deleteFromFile((int) startPointer, offset)) {
                    super.updateRowsPointer(startPointer, offset);
                    dao.deleteFromPersistance(children);
                    LOGGER.log(Level.INFO, " [OK] {0} Link successful deleted -> startPointer : {1} -- offset : {2}",
                            new Object[]{multipleRemoveList.size(), startPointer, offset});
                    System.out.println("    { ");
                    multipleRemoveList.forEach(linkRow -> {
                        System.out.println(" \t -> childID = " + linkRow.getData().getUUID() + " : parentID = " + linkRow.getData().getParentID());
                    });
                    System.out.println("    } ");
                }
            }
        }
//        printMap();
    }

    /**
     * Charge en mémoire tous les objets enfants <code>T</code> liés à un objet parent.
     *
     * @param parentID Identifiant de l'objet parent du lien.
     * @return Liste des objets enfants <code>T</code>.
     * @throws DAOException s'il y'a une erreur lors du chargement.
     */
    ArrayList<T> loadChildren(UUID parentID) throws DAOException {
        ArrayList<T> children = new ArrayList<>();
//        System.err.println("\n loadChildren -> parentID="+parentID + ", nbChildren={ ");
        if (mapParentChild.containsKey(parentID)) {
            for (UUID childID : mapParentChild.get(parentID)) {
                T child = dao.find(childID);
//                System.err.println(" \tchilID="+childID + ", loadedID="+child.getUUID());
                children.add(child);
            }
        }
//        System.err.println("   }\n");
        return children;
    }
}