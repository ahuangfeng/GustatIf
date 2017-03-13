package fr.insa.gustatif.metier.service;

import java.io.PrintStream;

/**
 *
 */
public class ServiceTechnique {

    void EnvoyerMail(String destinataire, String sujet, String corps) {
        AfficherSeparateur(System.out);
        
        System.out.println("Expéditeur : gustatif@gustatif.com");
        System.out.println("Pour : " + destinataire);
        System.out.println("Sujet : " + sujet);
        System.out.println("Corps :");
        System.out.println(corps);
        
        AfficherSeparateur(System.out);
    }
    
    void AfficherSeparateur(PrintStream ps) {
        ps.println("-----------------------------------------------------------------------");
    }
    
    public static String GenererString() {
        int longueur = 4 + (int) (Math.random() * 8);
        String r = "";
        for (int i = 0; i < longueur; i++) {
            r += (char) ((char) (Math.random() * 26) + 'A');
        }
        return r;
    }
}
