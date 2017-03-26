package fr.insa.gustatif.exceptions;

/**
 * Cette exception signifie que les informations de l'utilisateur sont
 * invalides. Lire le message pour obtenir les détails.
 */
public class IllegalUserInfoException extends Exception {

    public IllegalUserInfoException() {
        super("Les informations utilisateur ne sont pas valides.");
    }

    public IllegalUserInfoException(String details) {
        super("Les informations utilisateur ne sont pas valides.\nRaison : " + details);
    }
}
