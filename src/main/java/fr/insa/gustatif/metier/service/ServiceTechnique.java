package fr.insa.gustatif.metier.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cette classe contient tous les services techniques de Gustat'IF.
 */
public class ServiceTechnique {

    static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,8}$", Pattern.CASE_INSENSITIVE);

    /**
     * Envoi un mail fictif.
     *
     * @param destinataire Le destinataire du mail.
     * @param sujet Le sujet du mail.
     * @param corps Le corps du mail.
     */
    void envoyerMail(String destinataire, String sujet, String corps) {
        System.out.println("-----------------------------------------------------------------------");

        System.out.println("Expéditeur : gustatif@gustatif.com");
        System.out.println("Pour : " + destinataire);
        System.out.println("Sujet : " + sujet);
        System.out.println("Corps :");
        System.out.println(corps);

        System.out.println("-----------------------------------------------------------------------");
    }

    /**
     * Génère une chaîne de caractères aléatoire. Sa longueur est déterminée
     * aléatoirement dans [3..5].
     *
     * @param capitalized true pour mettre la première lettre en majuscule.
     * @return La chaîne aléatoire.
     */
    public static String genererString(boolean capitalized) {
        int longueur = 3 + (int) (Math.random() * 3);
        String r = "";
        for (int i = 0; i < longueur; i++) {
            r += (char) ((char) (Math.random() * 26) + 'a');
        }
        if (capitalized) {
            return Character.toUpperCase(r.charAt(0)) + r.substring(1);
        }
        return r;
    }

    /**
     * Vérifie si l'adresse mail fournie est valide.
     * @param email L'email à vérifier
     * @return true si elle est valide, sinon false
     */
    public boolean verifierMail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
}
