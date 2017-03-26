package fr.insa.gustatif.exceptions;

/**
 * Cette exception signifie que la commande est mal formée. Lire le message pour
 * obtenir plus de détails.
 */
public class CommandeMalFormeeException extends Exception {

    public CommandeMalFormeeException(String message) {
        super(message);
    }

}
