package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.engine.persistence.UniqueIdentifiable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aliyou, nesrine
 */
public class RowUtilities {
    
    /**
     * Logging
     */
    private static final Logger LOGGER = Logger.getLogger(RowUtilities.class.getName());

    /**
     *
     * @param sortedListByPointer
     * @param pointer
     * @param offset
     */
    static void updateRowsPointer(List<? extends AbstractRow> sortedListByPointer, long pointer, int offset) {
        FileDAOUtilities.checkNotNull("sortedListByPointer", sortedListByPointer);
        int position = getRowPositionFormSortedListByPointer(sortedListByPointer, pointer);

        System.out.println("\n** RowUtilities.updateRowsPointer : "
                + "\n\t pointer  : " + pointer
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
            if (rowAtPosition.getRowPointer() < pointer) {
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
        }
        System.out.println("\n");
    }

    /**
     *
     * @param sortedListByPointer
     * @param pointer
     * @return
     */
    static AbstractRow getRowFromSortedListByPointer(List<? extends AbstractRow> sortedListByPointer, long pointer) {
        FileDAOUtilities.checkNotNull("sortedListByPointer", sortedListByPointer);
        if (!sortedListByPointer.isEmpty()) {
            int pos = getRowPositionFormSortedListByPointer(sortedListByPointer, pointer);
            AbstractRow row = sortedListByPointer.get(pos);
            if (row.getRowPointer() == pointer) {
                return row;
            }
        }
        return null;
    }

    /**
     *
     * @param sortedListByPointer
     * @param newRow
     */
    static void addRowToSortedListByPointer(List<AbstractRow> sortedListByPointer, AbstractRow newRow) {
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
     *
     * @param sortedListByUUID
     * @param newRow
     */
    static void addRowToSortedListByUUID(List<AbstractRow> sortedListByUUID, AbstractRow newRow) {
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
     *
     * @param sortedListByPointer
     * @param pointer
     * @return
     */
    static int getRowPositionFormSortedListByPointer(List<? extends AbstractRow> sortedListByPointer, long pointer) {
        FileDAOUtilities.checkNotNull("sortedListByPointer", sortedListByPointer);
        int a = 0;
        int b = sortedListByPointer.size() - 1;
        int mid = (a + b) / 2;
        while (a < b) {
            AbstractRow row = sortedListByPointer.get(mid);
            if (row.getRowPointer() == pointer) {
                return mid;
            } else if (row.getRowPointer() > pointer) {
                b = mid - 1;
            } else {
                a = mid + 1;
            }
            mid = (a + b) / 2;
        }
        return a;
    }

    /**
     * Rétorune la position d'un tuple T encapsulant un objet d'identifiant
     * <code>dataID</code> avec l'algorithme de recherche dichotomique.
     *
     * @param dataID Identifiant de l'objet encapsulé dans un tuple T.
     * @return Position d'un tuple T dans la liste des tuples.
     */
    static int getRowPositionFromSortedListByUUID(List<? extends AbstractRow> sortedListByUUID, UUID dataID) {
        FileDAOUtilities.checkNotNull("sortedListByUUID", sortedListByUUID);
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

    void print(List<AbstractRow> list) {
        System.out.println("\nprinttttttttttttttt");
        list.forEach((row) -> {
            System.out.println(row);
        });
        System.out.println("printtttttttttttttt\n");
    }
}