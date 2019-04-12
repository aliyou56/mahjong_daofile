package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.engine.persistence.UniqueIdentifiable;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author aliyou, nesrine
 */
public class RowUtilities {

    /**
     * 
     * @param sortedListByPointer
     * @param pointer
     * @param offset 
     */
    static void updateRowsPointer(List<? extends AbstractRow> sortedListByPointer, long pointer, int offset) { 
        int pos = getRowPositionFormSortedListByPointer(sortedListByPointer, pointer);
        for (int i = pos; i < sortedListByPointer.size(); i++) {
            AbstractRow nextRow = sortedListByPointer.get(i);
            long newPointer = nextRow.getRowPointer() - offset;
            newPointer = newPointer > FileHeaderRow.FILE_HEADER_ROW_SIZE ? newPointer : FileHeaderRow.FILE_HEADER_ROW_SIZE;
//            System.out.println(nextRow + " -> newPointer : " + newPointer);
            nextRow.setRowPointer(newPointer, false);
        }
    }

    /**
     * 
     * @param sortedListByPointer
     * @param pointer
     * @return 
     */
    static AbstractRow getRowFromSortedListByPointer(List<? extends AbstractRow> sortedListByPointer, long pointer) {
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