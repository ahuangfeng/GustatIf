package fr.insa.gustatif.exceptions;

/**
 * Cette exception est renvoyé si un service n'arrive pas à effectuer une action
 * pour des raisons de concurrence, même après plusieurs essais.
 */
public class ServeurOccupeException extends Exception {
}
