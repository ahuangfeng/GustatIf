package fr.insa.gustatif.exceptions;

/**
 *
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
