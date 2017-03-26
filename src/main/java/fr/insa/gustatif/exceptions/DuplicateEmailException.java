package fr.insa.gustatif.exceptions;

/**
 * Cette exception signifie que le mail est déjà utilisé par une autre entité
 * dans la base de données.
 */
public class DuplicateEmailException extends Exception {

    String requestedEmail;

    public DuplicateEmailException(String requestedEmail) {
        super("L'email demandé est déjà utilisé.");
        this.requestedEmail = requestedEmail;
    }

    public String getRequestedEmail() {
        return requestedEmail;
    }
}
