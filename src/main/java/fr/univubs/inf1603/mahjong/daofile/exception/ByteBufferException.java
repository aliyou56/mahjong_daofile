
package fr.univubs.inf1603.mahjong.daofile.exception;

/**
 *
 * @author aliyou
 * @version 1.1.0
 */
public class ByteBufferException extends Exception {
    
    /**
     * Constructeur avec le message d'erreur.
     *
     * @param message Message d'erreur.
     */
    public ByteBufferException(String message) {
        super(message);
    }

    /**
     * Constructeur avec la cause de l'erreur.
     *
     * @param throwable Cause de l'erreur.
     */
    public ByteBufferException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructeur avec le message et la cause de l'erreur.
     *
     * @param message Message de l'erreur.
     * @param throwable Cause de l'erreur.
     */
    public ByteBufferException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
