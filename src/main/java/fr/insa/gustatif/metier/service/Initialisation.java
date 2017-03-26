package fr.insa.gustatif.metier.service;

import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.util.Saisie;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cette classe initialisation la BDD. Elle procède en la vidant intégralement,
 * puis en la remplissant des données fournies dans le sujet, et en peuplant les
 * employés.
 */
public class Initialisation {

    public static void main(String[] args) {
        try {
            JpaUtil.init();
        } catch (Exception ex) {
            System.err.println("Impossible d'initialiser le contexte de persistance. Le serveur de BDD est sûrement arrêté.");
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        try {
            ServiceMetier serviceMetier = new ServiceMetier();
            
            // Vide la BDD existante
            serviceMetier.viderLaBDD();
            
            // Remplit avec les données fournies dans le sujet
            System.out.println("Remplissez la base de donnée avec les données su TP...");
            Saisie.pause();
            
            // Génère les comptes des employés
            serviceMetier.genererComptesFictifs();
            
        } catch (Exception ex) {
            System.err.println("Une erreur est survenue durant l'initialisation de la BDD.");
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }

        JpaUtil.destroy();
    }
}
