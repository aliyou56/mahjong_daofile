package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.LinkRow.Link;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.ByteBuffer;
import java.util.UUID;

 /**
 * Cette classe répresente un conteneur qui encapsule un lien.
 *
 * @author aliyou
 * @version 1.0.0
 */
public class LinkRow extends AbstractRow<Link> {

    /**
     * Taille d'un lien (2 UUID)
     */
    static final int LINK_SIZE = 32;
    /**
     * Taille d'un tuple contenant un lien
     */
    static final int LINK_ROW_SIZE = AbstractRow.ROW_HEADER_SIZE + LINK_SIZE;

    /**
     * Constructeur avec l'identifiant du tuple, le lien et le pointeur de
     * tuple
     *
     * @param rowID Identifiant du tuple
     * @param data Lien encapsulé dans le tuple
     * @param recordPointer Pointeur du tuple
     */
    LinkRow(int rowID, Link data, long rowPointer) {
        super(rowID, data, LINK_SIZE, rowPointer);
        data.addPropertyChangeListener(this);
    }

    /**
     * Lis un tuple contenant un lien à partir d'un tampon d'octets. Rétourne
     * le tuple lu si les données dans le tampon sont cohérentes sinon
     * <code>null</code>.
     *
     * @param buffer Tampon d'octets
     * @param rowPointer Pointeur de tuple
     * @return Tuple contenant un Lien si les données lues sont cohérentes sinon
     * <code>null</code>.
     */
    static LinkRow readFromBuffer(ByteBuffer buffer, long rowPointer) {
        if (buffer.remaining() >= LINK_ROW_SIZE - 1) {
            int rowID = buffer.getInt();
            UUID linkID = new UUID(buffer.getLong(), buffer.getLong());
            UUID parentID = new UUID(buffer.getLong(), buffer.getLong());
            Link data = new Link(linkID, parentID);
            return new LinkRow(rowID, data, rowPointer);
        }
        return null;
    }

    /**
     * Ecrit un lien dans un tampon d'octet.
     *
     * @param buffer Tampon d'octet
     */
    @Override
    protected void writeData(ByteBuffer buffer) {
        FileUtilities.writeUUID(buffer, getData().getUUID());
        FileUtilities.writeUUID(buffer, getData().getParentID());
    }

    /**
     * Cette classe répresente un lien entre un objet parent et un objet enfant.
     * 
     */
    static class Link {

        /**
         * Support d'écoute
         */
        private final PropertyChangeSupport pcs;

        /**
         * Identifiant de l'objet enfant
         */
        private final UUID id;
        /**
         * Identifiant de l'objet parent
         */
        private UUID parentID;

        /**
         * Constructeur avec l'identifiant de l'objet enfant et l'identifiant de 
         * l'objet parent.
         * 
         * @param id Identifiant de l'objet enfant
         * @param parentID Identifiant de l'objet parent
         */
        Link(UUID id, UUID parentID) {
            this.id = id;
            this.parentID = parentID;
            this.pcs = new PropertyChangeSupport(this);
        }

        /**
         * Rétourne L'identifiant de l'objet enfant qui répresent l'identifiant 
         * du lien.
         * 
         * @return Identifiant d'un lien
         */
        public UUID getUUID() {
            return id;
        }

        /**
         * Rétourne l'identifiant de l'objet parent
         * 
         * @return Identifiant de l'objet parent
         */
        public UUID getParentID() {
            return parentID;
        }

        /**
         * Modifie l'identifiant de l'objet parent.
         * 
         * @param parentID Nouvel identifiant de l'objet parent.
         */
        public void setParentID(UUID parentID) {
            UUID oldValue = this.parentID;
            this.parentID = parentID;
            this.pcs.firePropertyChange("increment", oldValue, this.parentID);
        }

        /**
         * Ajoute un écouteur
         * 
         * @param listener Ecouteur à rajouter.
         */
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            this.pcs.addPropertyChangeListener(listener);
        }

        /**
         * Supprime un écouteur 
         * 
         * @param listener Ecouteur à supprimer
         */
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            this.pcs.removePropertyChangeListener(listener);
        }

        /**
         * Rétourne une description textuelle d'un lien
         * 
         * @return Description textuelle d'un lien.
         */
        @Override
        public String toString() {
            return "Link{" + "id=" + id + ", parentID=" + parentID + '}';
        }
    }
}
