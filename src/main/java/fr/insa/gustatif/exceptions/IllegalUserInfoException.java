package fr.insa.gustatif.exceptions;

/**
 *
 */
public class IllegalUserInfoException extends Exception {

    public IllegalUserInfoException() {
        super("Les informations utilisateur ne sont pas valides.");
    }

    public IllegalUserInfoException(String details) {
        super("Les informations utilisateur ne sont pas valides.\nRaison : " + details);
    }
}
