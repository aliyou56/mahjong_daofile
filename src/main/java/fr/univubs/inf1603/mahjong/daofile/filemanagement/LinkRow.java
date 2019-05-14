package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.engine.persistence.Persistable;
import fr.univubs.inf1603.mahjong.daofile.filemanagement.LinkRow.Link;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import java.beans.PropertyChangeSupport;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

/**
 * Cette classe répresente un tuple qui encapsule un lien.
 *
 * @author aliyou
 * @version 1.3
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
     * Constructeur avec un identifiant de tuple <code>rowID</code>, un lien
     * encapsulé <code>data</code> dans un tuple et un pointeur de tuple
     * <code>rowPointer</code>.
     *
     * @param rowID Identifiant d'un tuple
     * @param data Lien <code>LinkRow.Link</code> encapsulé dans un tuple.
     * @param rowPointer Pointeur d'un tuple.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * s'il y'a une erreur lors de l'instanciation.
     */
    LinkRow(int rowID, Link data, long rowPointer) throws DAOFileException {
        super(rowID, data, LINK_SIZE, rowPointer);
    }

    /**
     * Constructeur avec un processus qui éffectue des opérations
     * d'entrée/sortie sur un fichier <code>writer</code> et un pointeur de
     * tuple <code>rowPointer</code>.
     *
     * @param writer Processus qui éffectue des opérations d'entrée/sortie sur
     * un fichier
     * @param rowPointer Pointeur d'un tuple.
     * @throws DAOFileException s'il y'a une erruer lors de la lecture d'un
     * lien.
     */
    LinkRow(DAOFileWriter writer, long rowPointer) throws DAOFileException {
        super(writer, LINK_SIZE, rowPointer);
    }

    /**
     * Constructeur avec un tampon d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets à partir duquel un lien
     * <code>LinkRow.Link</code> est lu.
     * @param rowPointer Pointeur d'un tuple.
     * @throws DAOFileException s'il y'a une erruer lors de la lecture d'un lien
     * <code>LinkRow.Link</code>.
     */
    LinkRow(ByteBuffer buffer, long rowPointer) throws DAOFileException {
        super(buffer, LINK_SIZE, rowPointer);
    }

    /**
     * Lis un lien <code>LinkRow.Link</code> à partir d'un tampon d'octets
     * <code>buffer</code>.
     *
     * @param buffer Tampon d'octets à partir duquel le lien
     * <code>LinkRow.Link</code> est lu.
     * @return Le lien lu.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * s'il y'a une erreur lors de la lecture
     */
    @Override
    protected Link readData(ByteBuffer buffer) throws DAOFileException {
        if (buffer.remaining() < LINK_SIZE) {
            String message = "Remianing bytes '" + buffer.remaining() + "' is less than LINK_SIZE '"
                    + LINK_SIZE + "'";
            throw new DAOFileException(message);
        }
        UUID childID = new UUID(buffer.getLong(), buffer.getLong());
        UUID parentID = new UUID(buffer.getLong(), buffer.getLong());
        Link data = new Link(childID, parentID);
        return data;
    }

    /**
     * Ecrit un lien dans un tampon d'octet.
     *
     * @param buffer Tampon d'octet
     * @return Le nombre d'octes écrits.
     * @throws DAOFileException s'il y'a une erreur lors de l'écriture.
     */
    @Override
    protected int writeData(ByteBuffer buffer) throws DAOFileException {
        if (buffer.remaining() < LINK_SIZE) {
            String message = "Remianing bytes '" + buffer.remaining() + "' is less than LINK_SIZE '"
                    + LINK_SIZE + "'";
            throw new DAOFileException(message);
        }
        try {
            int startPosition = buffer.position();
            DAOFileWriter.writeUUID(buffer, getData().getUUID());
            DAOFileWriter.writeUUID(buffer, getData().getParentID());
            return buffer.position() - startPosition;
        } catch (DAOFileWriterException ex) {
            throw new DAOFileException(ex.getMessage(), ex);
        }
    }

    /**
     * Cette classe répresente un lien entre un objet parent et un objet enfant.
     */
    static public class Link implements Persistable {

        /**
         * Support d'écoute
         */
        private final PropertyChangeSupport pcs;

        /**
         * Identifiant de l'objet enfant
         */
        private final UUID childID;
        /**
         * Identifiant de l'objet parent
         */
        private UUID parentID;

        /**
         * Constructeur avec l'identifiant d'un objet enfant et l'identifiant
         * d'un objet parent.
         *
         * @param childID Identifiant d'un objet enfant
         * @param parentID Identifiant d'un objet parent
         */
        public Link(UUID childID, UUID parentID) {
            this.childID = childID;
            this.parentID = parentID;
            this.pcs = new PropertyChangeSupport(this);
        }

        /**
         * Renvoie L'identifiant d'un objet enfant qui répresente l'identifiant
         * d'un lien.
         *
         * @return Identifiant d'un lien
         */
        @Override
        public UUID getUUID() {
            return childID;
        }

        /**
         * Renvoie l'identifiant d'un objet parent
         *
         * @return Identifiant d'un objet parent
         */
        public UUID getParentID() {
            return parentID;
        }

        /**
         * Modifie l'identifiant d'un objet parent.
         *
         * @param parentID Nouvel identifiant d'un objet parent.
         */
        public void setParentID(UUID parentID) {
            if (this.parentID != parentID) {
                UUID oldValue = this.parentID;
                this.parentID = parentID;
                this.pcs.firePropertyChange("parentID", oldValue, this.parentID);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PropertyChangeSupport getPropertyChangeSupport() {
            return this.pcs;
        }

        /**
         * Renvoie une description textuelle d'un lien
         *
         * @return Description textuelle d'un lien.
         */
        @Override
        public String toString() {
            return "Link{" + "childID=" + childID + ", parentID=" + parentID + '}';
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 43 * hash + Objects.hashCode(this.childID);
            hash = 43 * hash + Objects.hashCode(this.parentID);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Link other = (Link) obj;
            if (!Objects.equals(this.childID, other.childID)) {
                return false;
            }
            return Objects.equals(this.parentID, other.parentID);
        }

    }
}