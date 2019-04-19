
package fr.univubs.inf1603.mahjong.daofile;

/**
 *
 * @author aliyou
 */
public class FileDAOUtilities {
    
    /**
     * Vérifie si un objet est null ou pas. Lève une exception de type
     * <code>IllegalArgumentException</code> si l'ojet est <code>null</code>.
     *
     * @param name Nom de l'objet à tester.
     * @param obj Objet à tester.
     */
    public static void checkNotNull(String name, Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " == null");
        }
    }
}
