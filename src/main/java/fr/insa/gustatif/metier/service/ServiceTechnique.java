package fr.insa.gustatif.metier.service;

import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class ServiceTechnique {

    static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    void envoyerMail(String destinataire, String sujet, String corps) {
        afficherSeparateur(System.out);

        System.out.println("Expéditeur : gustatif@gustatif.com");
        System.out.println("Pour : " + destinataire);
        System.out.println("Sujet : " + sujet);
        System.out.println("Corps :");
        System.out.println(corps);

        afficherSeparateur(System.out);
    }

    void afficherSeparateur(PrintStream ps) {
        ps.println("-----------------------------------------------------------------------");
    }

    public static String genererString(boolean capitalized) {
        int longueur = 3 + (int) (Math.random() * 2);
        String r = "";
        for (int i = 0; i < longueur; i++) {
            r += (char) ((char) (Math.random() * 26) + 'a');
        }
        if (capitalized) {
            return Character.toUpperCase(r.charAt(0)) + r.substring(1);
        }
        return r;
    }

    public boolean verifierMail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
}
