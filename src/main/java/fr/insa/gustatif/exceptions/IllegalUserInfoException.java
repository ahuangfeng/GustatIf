/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insa.gustatif.exceptions;

/**
 *
 */
public class IllegalUserInfoException extends Exception {

    public IllegalUserInfoException() {
        super("Les informations utilisateur ne sont pas valides.");
    }
}
