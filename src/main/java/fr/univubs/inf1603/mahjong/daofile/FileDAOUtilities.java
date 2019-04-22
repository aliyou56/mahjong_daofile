
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;

/**
 * Classe utilitaire pour le DAO Fichier.
 * 
 * @author aliyou
 * @version 1.1.0
 */
public class FileDAOUtilities {
    
    /**
     * Vérifie si un objet est null ou pas. Lève une exception de type
     * <code>DAOFileException</code> si l'ojet est <code>null</code>.
     *
     * @param name Nom de l'objet à tester.
     * @param obj Objet à tester.
     * @throws DAOFileException si l'objet est null.
     */
    public static void checkNotNull(String name, Object obj) throws DAOFileException {
        if (obj == null) {
            throw new DAOFileException(name + " == null");
        }
    }
    
    /**
     * Vérifie si une chaine de caractère ne dépasse pas une taille maximale.
     * Si c'est le cas la chaine est tronquée à la position {@code lenght}
     * 
     * @param str Chaine de caractères à vérifier.
     * @param lenght Taille maximale à ne pas dépasser par la chaine de caractères.
     * @return La chaine de carctère {@code str} si sa taille ne depasse pas la taille maximale
     * prévue sinon une nouvelle chaine de caractère obtenue en tronquant la chaine {@code str}
     * à la position {@code lenght}. 
     */
    public static String checkStringLenght(String str, int lenght) {
        if (str.length() > lenght) {
            str = str.substring(0, lenght);
        }
        return str;
    }
}
