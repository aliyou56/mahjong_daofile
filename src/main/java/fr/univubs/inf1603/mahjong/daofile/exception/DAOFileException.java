
package fr.univubs.inf1603.mahjong.daofile.exception;

/**
 * Cette classe regroupe les exceptions du module DAO fichier
 * 
 * @author aliyou
 * @version 1.1.0
 */
public class DAOFileException extends Exception {
    
    /**
     * Constructeur avec le message d'erreur.
     *
     * @param message Message d'erreur.
     */
    public DAOFileException(String message) {
        super(message);
    }

    /**
     * Constructeur avec la cause de l'erreur.
     *
     * @param throwable Cause de l'erreur.
     */
    public DAOFileException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructeur avec le message et la cause de l'erreur.
     *
     * @param message Message de l'erreur.
     * @param throwable Cause de l'erreur.
     */
    public DAOFileException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
