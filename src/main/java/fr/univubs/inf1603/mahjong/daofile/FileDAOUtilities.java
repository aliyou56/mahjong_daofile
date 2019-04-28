
package fr.univubs.inf1603.mahjong.daofile;

/**
 * Classe utilitaire pour le DAO Fichier.
 * 
 * @author aliyou
 * @version 1.1.0
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
    
    /**
     * Vérifie si une chaine de caractère ne dépasse pas une taille maximale.
     * Si c'est le cas la chaine est tronquée à la position {@code lenght}
     * 
     * @param name Chaine de caractères à vérifier.
     * @param lenght Taille maximale à ne pas dépasser par la chaine de caractères.
     * @return La chaine de carctère {@code str} si sa taille ne depasse pas la taille maximale
     * prévue sinon une nouvelle chaine de caractère obtenue en tronquant la chaine {@code str}
     * à la position {@code lenght}. 
     */
    public static String checkStringLenght(String name, int lenght) {
        checkNotNull("name", name);
        if (name.length() > lenght) {
            name = name.substring(0, lenght);
        }
        return name;
    }
}
