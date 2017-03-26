package fr.insa.gustatif.util;

import fr.insa.gustatif.metier.service.ServiceMetier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;

/**
 *
 * @author DASI Team
 * @author B3233 : choixMenu() et lireEmailAvecVerification()
 */
public class Saisie {

    public static String lireChaine(String invite) {
        String chaineLue = null;
        System.out.print(invite);
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            chaineLue = br.readLine();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        return chaineLue;

    }

    public static Integer lireInteger(String invite) {
        Integer valeurLue = null;
        while (valeurLue == null) {
            try {
                valeurLue = Integer.parseInt(lireChaine(invite));
            } catch (NumberFormatException ex) {
                System.out.println("/!\\ Erreur de saisie - Nombre entier attendu /!\\");
            }
        }
        return valeurLue;
    }

    public static Integer lireInteger(String invite, List<Integer> valeursPossibles) {
        Integer valeurLue = null;
        while (valeurLue == null) {
            try {
                valeurLue = Integer.parseInt(lireChaine(invite));
            } catch (NumberFormatException ex) {
                System.out.println("/!\\ Erreur de saisie - Nombre entier attendu /!\\");
            }
            if (!(valeursPossibles.contains(valeurLue))) {
                System.out.println("/!\\ Erreur de saisie - Valeur non-autorisée /!\\");
                valeurLue = null;
            }
        }
        return valeurLue;
    }

    public static String lireEmailAvecVerification(ServiceMetier serviceMetier) {
        try {
            while (true) {
                String email = Saisie.lireChaine("Adresse mail : ");
                if (null == serviceMetier.recupererClient(email)) {
                    return email;
                } else {
                    if (Saisie.choixMenu("Ce mail est déjà utilisé, que voulez-vous faire ?", new String[]{
                        "Entrer un autre mail",
                        "Annuler la modification"
                    }) == 2) { // Annuler l'inscription
                        return null;
                    }
                }
            }
        } catch (PersistenceException ex) {
            System.out.println("Erreur de persistence lors de la vérification de l'utilisation d'une adresse mail client.");
            Logger.getLogger(Saisie.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Integer choixMenu(String invite, String[] choix) {
        String generatedInvite = invite;
        ArrayList<Integer> vals = new ArrayList<>();
        for (int i = 0; i < choix.length; ++i) {
            generatedInvite += "\n";
            generatedInvite += "  " + (i + 1) + ". " + choix[i];
            vals.add(i + 1);
        }
        generatedInvite += "\n";
        return lireInteger(generatedInvite, vals);
    }

    public static void pause() {
        lireChaine("--PAUSE--");
    }
}
