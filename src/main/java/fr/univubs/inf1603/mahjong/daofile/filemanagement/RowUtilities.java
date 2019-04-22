package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import fr.univubs.inf1603.mahjong.daofile.FileDAOUtilities;
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
 * @author aliyou, nesrine
 * @version  1.1.0
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
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si les parametres fournis ne sont pas acceptés.
     */
    public static void updateRowsPointer(List<? extends AbstractRow> sortedListByPointer, long rowPointer, int offset) throws DAOFileException {
        FileDAOUtilities.checkNotNull("sortedListByPointer", sortedListByPointer);
        int position = getRowPositionFormSortedListByPointer(sortedListByPointer, rowPointer);

        System.out.println("** RowUtilities.updateRowsPointer : "
                + "\n\t pointer  : " + rowPointer 
                + "\n\t offset   : " + offset
                + "\n\t listSize : " + sortedListByPointer.size()
                + "\n\t position : " + position);
        if (!sortedListByPointer.isEmpty()) {
            System.out.print("\t before   : ");
            sortedListByPointer.forEach((ar) -> {
                System.out.print(ar.getRowPointer() + ", ");
            });

            AbstractRow rowAtPosition = sortedListByPointer.get(position);
            System.out.println("\n\t type     : " + rowAtPosition.getData().getClass().getSimpleName());
            if (rowAtPosition.getRowPointer() < rowPointer) {
                position += 1;
                System.out.println("\t position : " + position);
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

            System.out.print("\t after    : ");
            sortedListByPointer.forEach((ar) -> {
                System.out.print(ar.getRowPointer() + ", ");
            });
            System.out.println("");
        }
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
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si les parametres fournis ne sont pas acceptés.
     */
    public static AbstractRow getRowFromSortedListByPointer(List<? extends AbstractRow> sortedListByPointer, long rowPointer) throws DAOFileException {
        FileDAOUtilities.checkNotNull("sortedListByPointer", sortedListByPointer);
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
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si les parametres fournis ne sont pas acceptés.
     */
    public static void addRowToSortedListByPointer(List<AbstractRow> sortedListByPointer, AbstractRow newRow) throws DAOFileException {
        FileDAOUtilities.checkNotNull("sortedListByPointer", sortedListByPointer);
        FileDAOUtilities.checkNotNull("newRow", newRow);
        if (sortedListByPointer.isEmpty()) {
            sortedListByPointer.add(newRow);
        } else {
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

    /**
     * Ajoute un tuple {@code newRow} dans une liste de tuples
     * {@code sortedListByUUID} à une position déterminée de telle sorte que la
     * liste reste triée suivant l'identifiant {@code UUID} des objets
     * encapsulés dans les tuples.
     *
     * @param sortedListByUUID Liste de tuples triée suivant l'identifiant des
     * objets encapsulés dans les tuples. NE DOIT PAS ETRE NULLE.
     * @param newRow Nouveau tuple à rajouter. NE DOIT PAS ETRE NULL.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si les parametres fournis ne sont pas acceptés.
     */
    public static void addRowToSortedListByUUID(List<AbstractRow> sortedListByUUID, AbstractRow newRow) throws DAOFileException {
        FileDAOUtilities.checkNotNull("sortedListByUUID", sortedListByUUID);
        FileDAOUtilities.checkNotNull("newRow", newRow);
        if (sortedListByUUID.isEmpty()) {
            sortedListByUUID.add(newRow);
        } else {
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
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si les parametres fournis ne sont pas acceptés.
     */
    public static int getRowPositionFormSortedListByPointer(List<? extends AbstractRow> sortedListByPointer, long rowPointer) throws DAOFileException {
        FileDAOUtilities.checkNotNull("sortedListByPointer", sortedListByPointer);
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
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException si les parametres fournis ne sont pas acceptés.
     */
    public static int getRowPositionFromSortedListByUUID(List<? extends AbstractRow> sortedListByUUID, UUID dataID) throws DAOFileException {
        FileDAOUtilities.checkNotNull("sortedListByUUID", sortedListByUUID);
        FileDAOUtilities.checkNotNull("dataID", dataID);
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

//    void print(List<AbstractRow> list) {
//        System.out.println("\nprinttttttttttttttt");
//        list.forEach((row) -> {
//            System.out.println(row);
//        });
//        System.out.println("printtttttttttttttt\n");
//    }
}
