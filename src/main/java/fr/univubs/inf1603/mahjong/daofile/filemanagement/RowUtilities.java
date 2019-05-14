package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import static fr.univubs.inf1603.mahjong.daofile.FileDAOUtilities.checkNotNull;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.engine.persistence.UniqueIdentifiable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe {@code RowUtilities} est une classe utilitaire pour les tuples.
 * Elle regroupe les algorithmes communs aux tuples.
 *
 * @author aliyou
 * @version 1.3
 */
public class RowUtilities {

    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(RowUtilities.class.getName());

    /**
     * Mets à jour les pointeurs de tuple d'une liste triée suivant les
     * pointeurs de tuple {@code sortedListByPointer} en enlevant l'offset
     * {@code offset} aux tuples dont le pointeur est superieur à
     * {@code rowPointer}. Cette méthode est essentiellement utilisée après une
     * suppression pour mettre à jour les pointeurs de tuple des tuples suivants
     * le tuple supprimé.
     *
     * @param sortedListByPointer Liste de tuples triée suivant les pointeurs de
     * tuple. NE DOIT PAS ETRE NULLE.
     * @param rowPointer Pointeur de tuple de reference.
     * @param offset Offset à enlever aux pointeurs de tuple.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si
     * les arguments fournis ne sont pas acceptés.
     */
    public static void updateRowsPointer(List<? extends AbstractRow> sortedListByPointer, long rowPointer, int offset) throws DAOFileException {
        checkNotNull("RowUtilities.updateRowsPointer -> sortedListByPointer", sortedListByPointer);
        int position = getRowPositionFormSortedListByPointer(sortedListByPointer, rowPointer);
        LOGGER.log(Level.FINE, "** RowUtilities.updateRowsPointer : "
                + "\n\t pointer  : {0}"
                + "\n\t offset   : {1}"
                + "\n\t listSize : {2}"
                + "\n\t position : {3}",
                new Object[]{rowPointer, offset, sortedListByPointer.size(), position});
        if (!sortedListByPointer.isEmpty()) {
            LOGGER.log(Level.FINE, print("\t before   : ", sortedListByPointer));
            AbstractRow rowAtPosition = sortedListByPointer.get(position);
//            System.out.println("\n\t type     : " + rowAtPosition.getData().getClass().getSimpleName());
            if (rowAtPosition.getRowPointer() < rowPointer) {
                position += 1;
//                System.out.println("\t position : " + position);
            }
            for (int i = position; i < sortedListByPointer.size(); i++) {
                AbstractRow nextRow = sortedListByPointer.get(i);
                long oldPointer = nextRow.getRowPointer();
                long newPointer = oldPointer - offset;
                if (newPointer >= FileHeaderRow.FILE_HEADER_ROW_SIZE) {
                    nextRow.setRowPointer(newPointer, false);
                    LOGGER.log(Level.FINE, "{0}, olPointer : {1} -> newPointer : {2}, offset={3}",
                            new Object[]{nextRow, oldPointer, newPointer, offset});
                }
            }
            LOGGER.log(Level.FINE, print("\t after    : ", sortedListByPointer));
        }
    }

    /**
     * just for test
     *
     * @param name
     * @param list
     * @return
     */
    static private String print(String name, List<? extends AbstractRow> list) {
        StringBuilder result = new StringBuilder();
        result.append(name);
        list.forEach((ar) -> {
            result.append(ar.getRowPointer()).append(", ");
        });
        result.append("\n");
        return result.toString();
    }

    /**
     * Renvoie un tuple retrouvé à l'aide de son pointeur à partir d'une liste
     * de tuples triée suivant les pointeurs de tuple
     * {@code sortedListByPointer} si le tuple existe dans la liste sinon
     * {@code null}.
     *
     * @param sortedListByPointer Liste de tuples triée suivant les pointeurs de
     * tuple. NE DOIT PAS ETRE NULLE.
     * @param rowPointer Pointeur du tuple à retrouver.
     * @return Le tuple ayant comme pointeur {@code rowPointer} s'il existe dans
     * la liste sinon {@code null}.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si
     * les arguments fournis ne sont pas acceptés.
     */
    public static AbstractRow getRowFromSortedListByPointer(List<? extends AbstractRow> sortedListByPointer, long rowPointer) throws DAOFileException {
        checkNotNull("RowUtilities.getRowFromSortedListByPointer -> sortedListByPointer", sortedListByPointer);
        if (!sortedListByPointer.isEmpty()) {
            int pos = getRowPositionFormSortedListByPointer(sortedListByPointer, rowPointer);
            AbstractRow row = sortedListByPointer.get(pos);
            if (row.getRowPointer() == rowPointer) {
                return row;
            }
        }
        return null;
    }

    /**
     * Ajoute un tuple {@code newRow} dans une liste de tuples
     * {@code sortedListByPointer} à une position déterminée de telle sorte que
     * la liste reste triée suivant les pointeurs de tuple.
     *
     * @param sortedListByPointer Liste de tuples triée suivant les pointeurs de
     * tuple. NE DOIT PAS ETRE NULLE.
     * @param newRow Nouveau tuple à rajouter. NE DOIT PAS ETRE NULL.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si
     * les arguments fournis ne sont pas acceptés.
     */
    public static void addRowToSortedListByPointer(List<AbstractRow> sortedListByPointer, AbstractRow newRow) throws DAOFileException {
        checkNotNull("RowUtilities.addRowToSortedListByPointer -> sortedListByPointer", sortedListByPointer);
        checkNotNull("RowUtilities.addRowToSortedListByPointer -> newRow", newRow);
        if (sortedListByPointer.isEmpty()) {
            sortedListByPointer.add(newRow);
        } else {
            if (!sortedListByPointer.contains(newRow)) { // si le tuple n'existe pas déjà dans la liste
                int pos = getRowPositionFormSortedListByPointer(sortedListByPointer, newRow.getRowPointer());
                if (sortedListByPointer.get(pos).getRowPointer() < newRow.getRowPointer()) {
                    if (pos == sortedListByPointer.size() - 1) { // si c'est le dernier element
                        sortedListByPointer.add(newRow);
                    } else {
                        sortedListByPointer.add(pos + 1, newRow);
                    }
                } else {
                    sortedListByPointer.add(pos, newRow);
                }
            }
        }
    }

    /**
     * Ajoute un tuple {@code newRow} dans une liste de tuples
     * {@code sortedListByUUID} à une position déterminée de telle sorte que la
     * liste reste triée suivant l'identifiant {@code UUID} des objets
     * encapsulés dans les tuples.
     *
     * @param sortedListByUUID Liste de tuples triée suivant l'identifiant des
     * objets encapsulés dans les tuples. NE DOIT PAS ETRE NULLE.
     * @param newRow Nouveau tuple à rajouter. NE DOIT PAS ETRE NULL.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si
     * les arguments fournis ne sont pas acceptés.
     */
    public static void addRowToSortedListByUUID(List<AbstractRow> sortedListByUUID, AbstractRow newRow) throws DAOFileException {
        checkNotNull("RowUtilities.addRowToSortedListByUUID -> sortedListByUUID", sortedListByUUID);
        checkNotNull("RowUtilities.addRowToSortedListByUUID -> newRow", newRow);
        if (sortedListByUUID.isEmpty()) {
            sortedListByUUID.add(newRow);
        } else {
            if (!sortedListByUUID.contains(newRow)) { // si le tuple n'existe pas déjà dans la liste
                UniqueIdentifiable newData = (UniqueIdentifiable) newRow.getData();
                UUID newDataID = newData.getUUID();
                int pos = getRowPositionFromSortedListByUUID(sortedListByUUID, newDataID);
                UniqueIdentifiable dataAtPos = (UniqueIdentifiable) sortedListByUUID.get(pos).getData();
                int compare = dataAtPos.getUUID().compareTo(newDataID);
                if (compare == -1) { // uuid plus grand
                    if (pos == sortedListByUUID.size() - 1) { // si c'est le dernier element
                        sortedListByUUID.add(newRow);
                    } else {
                        sortedListByUUID.add(pos + 1, newRow);
                    }
                } else { // uuid plus petit
                    sortedListByUUID.add(pos, newRow);
                }
            }
        }
    }

    /**
     * Renvoie la position d'un tuple ayant un pointeur de tuple
     * {@code rowPointer}, dans une liste {@code sortedListByPointer}, avec
     * l'algorithme de recherche dichotomique.
     * <br>
     * La liste de tuples {@code sortedListByPointer} est supposée etre triée
     * suivant les pointeurs de tuple.
     *
     * @param sortedListByPointer Liste de tuples triée suivant les pointeurs de
     * tuple. NE DOIT PAS ETRE NULLE.
     * @param rowPointer Pointeur du tuple dont la position est recherchée.
     * @return Position d'un tuple dans une liste de tuples.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si
     * les arguments fournis ne sont pas acceptés.
     */
    public static int getRowPositionFormSortedListByPointer(List<? extends AbstractRow> sortedListByPointer, long rowPointer) throws DAOFileException {
        checkNotNull("RowUtilities.getRowPositionFormSortedListByPointer -> sortedListByPointer", sortedListByPointer);
        int a = 0;
        int b = sortedListByPointer.size() - 1;
        int mid = (a + b) / 2;
        while (a < b) {
            AbstractRow row = sortedListByPointer.get(mid);
            if (row.getRowPointer() == rowPointer) {
                return mid;
            } else if (row.getRowPointer() > rowPointer) {
                b = mid - 1;
            } else {
                a = mid + 1;
            }
            mid = (a + b) / 2;
        }
        return a;
    }

    /**
     * Renvoie la position d'un tuple encapsulant un objet d'identifiant
     * {@code dataID}, dans une liste {@code sortedListByUUID}, avec
     * l'algorithme de recherche dichotomique.
     * <br>
     * La liste de tuples {@code sortedListByUUID} est supposée etre triée
     * suivant l'identifiant des objets encapsulés dans les tuples.
     *
     * @param sortedListByUUID Liste de tuples triée suivant l'identifiant des
     * objets encapsulés dans les tuples. NE DOIT PAS ETRE NULLE.
     * @param dataID Identifiant de l'objet encapsulé dans un tuple. NE DOIT PAS
     * ETRE NULL.
     * @return Position d'un tuple dans une liste de tuples.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si
     * les arguments fournis ne sont pas acceptés.
     */
    public static int getRowPositionFromSortedListByUUID(List<? extends AbstractRow> sortedListByUUID, UUID dataID) throws DAOFileException {
        checkNotNull("RowUtilities.getRowPositionFromSortedListByUUID -> sortedListByUUID", sortedListByUUID);
        checkNotNull("RowUtilities.getRowPositionFromSortedListByUUID -> dataID", dataID);
        int a = 0;
        int b = sortedListByUUID.size() - 1;
        int mid = (a + b) / 2;
        while (a < b) {
            UniqueIdentifiable data = (UniqueIdentifiable) sortedListByUUID.get(mid).getData();
            switch (data.getUUID().compareTo(dataID)) {
                case 0:
                    return mid;
                case 1:
                    b = mid - 1;
                    break;
                default:
                    a = mid + 1;
                    break;
            }
            mid = (a + b) / 2;
        }
        return a;
    }
}
