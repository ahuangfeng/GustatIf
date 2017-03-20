/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
