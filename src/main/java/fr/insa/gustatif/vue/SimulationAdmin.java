package fr.insa.gustatif.vue;

import com.google.maps.errors.NotFoundException;
import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.Cycliste;
import fr.insa.gustatif.metier.modele.Drone;
import fr.insa.gustatif.metier.modele.Gestionnaire;
import fr.insa.gustatif.metier.modele.Livreur;
import fr.insa.gustatif.metier.service.ServiceMetier;
import fr.insa.gustatif.util.Saisie;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;

/**
 * Simulation textuelle de l'IHM d'administration.
 */
public class SimulationAdmin {

    Cycliste identiteCycliste;
    Gestionnaire identiteGestionnaire;
    ServiceMetier serviceMetier;

    public SimulationAdmin() {
        this.identiteCycliste = null;
        this.identiteGestionnaire = null;
        this.serviceMetier = new ServiceMetier();
    }

    public void run() {
        try {
            accueil();
        } catch (Exception ex) {
            System.out.println("Une erreur non gérée est survenue.");
            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void accueil() {
        int choix = -1;
        while (choix != 3) {
            System.out.println("Interface d'admin de Gustat'IF.");
            afficherIdentite();
            choix = Saisie.choixMenu("Qui êtes-vous ?", new String[]{
                "Livreur",
                "Gestionnaire",
                "Quitter"
            });
            switch (choix) {
                case 1: { // Livreur
                    cycliste_accueil();
                    break;
                }
                case 2: { // Gestionnaire
                    gestionnaire_accueil();
                    break;
                }
                case 3: { // Quitter
                    break;
                }
            }
        }
    }

    private void afficherIdentite() {
        if (null != identiteCycliste && null != identiteGestionnaire) {
            System.out.println("Erreur : identifié deux fois.");
            System.exit(0);
        } else if (null != identiteCycliste) {
            System.out.println("Connecté en tant que : " + identiteCycliste);
        } else if (null != identiteGestionnaire) {
            System.out.println("Connecté en tant que : " + identiteGestionnaire);
        } else {
            System.out.println("Non connecté.");
        }
    }

    private void cycliste_accueil() {
        identiteGestionnaire = null;

        int choix = -1;
        while (choix != 4) {
            System.out.println("Interface d'admin de Gustat'IF.");
            afficherIdentite();
            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Connexion / Déconnexion",
                "Voir mes commandes en cours",
                "Valider ma commande en cours",
                "Quitter"
            });
            switch (choix) {
                case 1: { // Connexion / Déconnexion
                    if (null == identiteCycliste) {
                        cycliste_connexion();
                    } else {
                        identiteCycliste = null;
                        System.out.println("Vous êtes déconnecté.");
                    }
                    break;
                }
                case 2: { // Voir mes commandes en cours
                    if (null != identiteCycliste) {
                        voirCommandesCycliste(identiteCycliste);
                    } else {
                        System.out.println("Vous n'êtes pas connecté.");
                    }
                    break;
                }
                case 3: { // Valider ma commande en cours
                    cycliste_validerCommande();
                    break;
                }
            }
        }
    }

    private void cycliste_connexion() {
        try {
            if (null == identiteCycliste) {
                System.out.println("Connexion cycliste :");
                String email = Saisie.lireChaine("Email : ");
                identiteCycliste = serviceMetier.recupererCycliste(email);
                if (null == identiteCycliste) {
                    System.out.println("La connexion a échoué.");
                } else {
                    System.out.println("Vous êtes connecté !");
                }
            } else {
                System.out.println("Vous êtes déjà connecté.");
            }
        } catch (PersistenceException ex) {
            System.out.println("Erreur de persistence lors de la connexion du cycliste.");
            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cycliste_validerCommande() {
        try {
            if (null == identiteCycliste) {
                System.out.println("Vous devez être connecté.");
            } else {
                Commande enCours = identiteCycliste.getCommandeEnCours();
                if (null == enCours) {
                    System.out.println("Vous n'avez pas de commande en cours.");
                    return;
                }

                System.out.println("Commande en cours : ");
                System.out.println(enCours);

                if ("o".equals(Saisie.lireChaine("Valider la commande ? (o / n) "))) {
                    serviceMetier.terminerCommande(enCours);
                    System.out.println("La commande a bien été marquée livrée.");
                } else {
                    System.out.println("Action annulée.");
                }
            }
        } catch (PersistenceException ex) {
            System.out.println("Erreur de persistence lors de la validation de la commande.");
            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void voirCommandesCycliste(Livreur livreur) {
        if (null != livreur) {
            if (livreur.getCommandesLivrees().isEmpty()) {
                System.out.println("Aucune commande livrée.");
            } else {
                System.out.println("Livrées :");
                for (Commande commandesLivree : livreur.getCommandesLivrees()) {
                    System.out.println("  - " + commandesLivree);
                }
            }

            if (null != livreur.getCommandeEnCours()) {
                System.out.println("En cours de livraison :");
                System.out.println(livreur.getCommandeEnCours());
            } else {
                System.out.println("Aucune commande en cours de livraison.");
            }
        }
    }

    private void gestionnaire_accueil() {
        identiteCycliste = null;

        int choix = -1;
        while (choix != 7) {
            System.out.println("Interface d'admin de Gustat'IF.");
            afficherIdentite();
            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Connexion / Déconnexion",
                "Visualiser les commandes",
                "Lister les livreurs",
                "Valider une commande en cours de livraison par un drône",
                "Lister les clients",
                "Modifier les données d'un client",
                "Quitter"
            });
            switch (choix) {
                case 1: { // Connexion / Déconnexion
                    if (null == identiteGestionnaire) {
                        gestionnaire_connexion();
                    } else {
                        identiteGestionnaire = null;
                        System.out.println("Vous êtes déconnecté.");
                    }
                    break;
                }
                case 2: { // Visualiser toutes les commandes
                    gestionnaire_visualiserCommandes();
                    break;
                }
                case 3: { // Lister les livreurs
                    gestionnaire_listerLivreurs();
                    break;
                }
                case 4: { // Valider une commande en cours de livraison par un drône
                    gestionnaire_validerCommandeDrone();
                    break;
                }
                case 5: { // Lister les clients
                    gestionnaire_listerClients();
                    break;
                }
                case 6: { // Modifier les données d'un client
                    gestionnaire_modifierClient();
                    break;
                }
            }
        }
    }

    private void gestionnaire_connexion() {
        try {
            if (null == identiteGestionnaire) {
                System.out.println("Connexion gestionnaire :");
                String email = Saisie.lireChaine("Email : ");
                identiteGestionnaire = serviceMetier.recupererGestionnaire(email);
                if (null == identiteGestionnaire) {
                    System.out.println("La connexion a échoué.");
                } else {
                    System.out.println("Vous êtes connecté !");
                }
            } else {
                System.out.println("Vous êtes déjà connecté.");
            }
        } catch (PersistenceException ex) {
            System.out.println("Erreur de persistence lors de la connexion du gestionnaire.");
            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void gestionnaire_visualiserCommandes() {
        if (null == identiteGestionnaire) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }
        try {
            afficherIdentite();
            int choix = Saisie.choixMenu("Quelles commandes lister ?", new String[]{
                "D'un livreur particulier",
                "Toutes les commandes",
                "Retour"
            });
            switch (choix) {
                case 1: { // D'un livreur particulier
                    Livreur livreur = serviceMetier.recupererLivreur(Saisie.lireInteger("#ID du livreur : ").longValue());
                    if (null == livreur) {
                        System.out.println("#ID invalide.");
                    } else {
                        voirCommandesCycliste(livreur);
                    }
                    break;
                }
                case 2: { // Toutes les commandes
                    System.out.println("Liste des commandes :");
                    for (Commande commande : serviceMetier.recupererCommandes()) {
                        System.out.println("  - " + commande);
                    }
                    break;
                }
            }
        } catch (PersistenceException ex) {
            System.out.println("Erreur de persistence lors de la visualisation des commandes.");
            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void gestionnaire_listerLivreurs() {
        if (null == identiteGestionnaire) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }
        try {
            int choix = Saisie.choixMenu("Qui voulez-vous lister ?", new String[]{
                "Uniquement les drônes",
                "Uniquement les cyclistes",
                "Tous les livreurs",
                "Retour"
            });
            switch (choix) {
                case 1: { // Uniquement les drônes
                    System.out.println("Liste des drônes :");
                    for (Livreur livreur : serviceMetier.recupererLivreurs()) {
                        if (livreur instanceof Drone) {
                            System.out.println("  - " + livreur);
                        }
                    }
                    break;
                }
                case 2: { // Uniquement les cyclistes
                    System.out.println("Liste des cyclistes :");
                    for (Livreur livreur : serviceMetier.recupererLivreurs()) {
                        if (livreur instanceof Cycliste) {
                            System.out.println("  - " + livreur);
                        }
                    }
                    break;
                }
                case 3: { // Tous les livreurs
                    System.out.println("Liste des livreurs :");
                    for (Livreur livreur : serviceMetier.recupererLivreurs()) {
                        System.out.println("  - " + livreur);
                    }
                    break;
                }
            }
        } catch (PersistenceException ex) {
            System.out.println("Erreur de persistence lors de la récupération des livreurs.");
            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void gestionnaire_validerCommandeDrone() {
        if (null == identiteGestionnaire) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }
        try {
            List<Commande> lc = serviceMetier.recupererCommandesEnCoursParDrones();
            if (lc.isEmpty()) {
                System.out.println("Il n'y a aucune commande livrée par drône à valider.");
                return;
            }

            System.out.println("Commandes en cours de livraison par des drônes :");
            for (Commande commande : lc) {
                System.out.println("  - " + commande);
            }

            // TODO: ID de la commande ou du drône ? (ou matricule du drône)
            Integer idCommande = Saisie.lireInteger("Quel est l'#ID de la commande à valider : ");
            Commande commande = serviceMetier.recupererCommande(idCommande.longValue());
            if (null == commande) {
                System.out.println("Cet #ID est invalide.");
            } else if ((null != commande.getLivreur() && !(commande.getLivreur() instanceof Drone)) || null != commande.getDateDeFin()) {
                System.out.println("Cet #ID n'est pas celui d'une commande livrée par drône à valider.");
            } else {
                if ("o".equals(Saisie.lireChaine("Valider la commande ? (o / n) "))) {
                    serviceMetier.terminerCommande(commande);
                    System.out.println("La commande a bien été marquée livrée.");
                } else {
                    System.out.println("Action annulée.");
                }
            }
        } catch (PersistenceException ex) {
            System.out.println("Erreur de persistence lors de la validation de la commande.");
            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void gestionnaire_listerClients() {
        if (null == identiteGestionnaire) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }

        try {
            System.out.println("Liste des clients :");
            for (Client client : serviceMetier.recupererClients()) {
                System.out.println("  - " + client);
            }
        } catch (PersistenceException ex) {
            System.out.println("Erreur de persistence lors de la récupération des clients.");
            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void gestionnaire_modifierClient() {
        if (null == identiteGestionnaire) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }

        try {
            Integer idClient = Saisie.lireInteger("Quel est l'#ID du client à modifier : ");
            Client client = serviceMetier.recupererClient(idClient.longValue());
            if (null == client) {
                System.out.println("Cet #ID est invalide.");
            }

            System.out.println("Client à modifier : ");
            System.out.println(client);

            try {
                System.out.println("Laisser vide pour ne pas modifier.");
                String nom = Saisie.lireChaine("Nom : ");
                String prenom = Saisie.lireChaine("Prénom : ");
                String email = Saisie.lireEmailAvecVerification(serviceMetier);
                if (null == email) {
                    return;
                }
                String adresse = Saisie.lireChaine("Adresse de livraison : ");

                serviceMetier.modifierClient(client, nom, prenom, email, adresse);
            } catch (NullPointerException e) {
                System.out.println("Echec de la saisie, la modification n'a pas eu lieu.");
            } catch (NotFoundException ex) {
                System.out.println("L'adresse n'est pas reconnue, la modification n'a pas eu lieu.");
            } catch (DuplicateEmailException ex) {
                System.out.println("Ce mail est déjà utilisé, la modification n'a pas eu lieu.");
            }
        } catch (PersistenceException ex) {
            System.out.println("Erreur de persistence lors de la modification du client.");
            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
