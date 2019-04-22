package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.daofile.FileDAOManager;
import fr.univubs.inf1603.mahjong.daofile.FilePlayerDAO;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.exceptions.DestroyedGameException;
import fr.univubs.inf1603.mahjong.sapi.Player;
import fr.univubs.inf1603.mahjong.sapi.SimpleGame;
import fr.univubs.inf1603.mahjong.sapi.SimpleRule;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * Cette classe fait le lien entre un joueur et la simple game.
 *
 * @author aliyou
 * @version 1.1.0
 */
public class PlayerToSimpleGameLinkManager {

    /**
     * Gestionnaire de liens
     */
    private static LinkManager<Player> linkManager = null;

    /**
     * Constructeur avec le chemin du fichier de lien.
     *
     * @param linkFilePath Chemin du fichier de lien.
     * @throws DAOFileException s'il y'a une lors de l'instanciation.
     */
    public PlayerToSimpleGameLinkManager(Path linkFilePath) throws DAOFileException {
        if (PlayerToSimpleGameLinkManager.linkManager == null) {
            PlayerToSimpleGameLinkManager.linkManager = new LinkManager<>(linkFilePath);
            try {
                PlayerToSimpleGameLinkManager.linkManager.setDAO(FileDAOManager.getInstance().getPlayerDao());
            } catch (DAOException ex) {
                throw new DAOFileException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Crée des liens entre une simple game et ses joueurs.
     *
     * @param parent La simple game
     * @throws DAOFileException s'il y'a une erreur lors de la création des
     * liens.
     */
    public void addChildren(SimpleGame parent) throws DAOFileException {
        try {
            linkManager.addChildren(parent.getUUID(), parent.getPlayers());
        } catch (DestroyedGameException ex) {
            throw new DAOFileException(ex.getMessage(), ex);
        }
    }

    /**
     * Supprime les liens entre une simple game et ses joueurs et lance la
     * suppression des joueurs.
     *
     * @param parent La simple game
     * @throws DAOFileException s'il y'a une erreur lors de la suppréssion des
     * liens.
     */
    public void removeChildren(SimpleGame parent) throws DAOFileException {
        try {
            linkManager.removeChildren(parent.getPlayers());
        } catch (DestroyedGameException ex) {
            throw new DAOFileException(ex.getMessage(), ex);
        }
    }

    /**
     * Rémonte les joueurs d'une simple game.
     *
     * @param simpleGameID Indentifiant d'une simple game.
     * @param rule Règle de la simple game
     * @return Liste des joueurs de la simple game.
     * @throws DAOException s'il y'a une errue lors du chargement.
     */
    public List<Player> loadChildren(UUID simpleGameID, SimpleRule rule) throws DAOException {
        FilePlayerDAO playerDAO = (FilePlayerDAO) linkManager.getDAO();
        playerDAO.setRule(rule);
        return linkManager.loadChildren(simpleGameID);
    }

    /**
     * Renvoie un tuple de lien ayant l'identifant {@code playerID}.
     *
     * @param playerID Identifiant d'un joueur qui représente également
     * l'identifiant d'un lien.
     * @return Tuple de lien dont l'identifiant correspond à {@code playerID}.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException s'i
     * y'a une erreur lors de l'obtention de la position du tuple.
     */
    public LinkRow getRow(UUID playerID) throws DAOFileException {
        return linkManager.getRow(playerID);
    }
}
