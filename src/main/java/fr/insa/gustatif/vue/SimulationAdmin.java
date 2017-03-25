package fr.insa.gustatif.vue;

import fr.insa.gustatif.exceptions.BadLocationException;
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

/**
 *
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
        accueil();
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
    }

    private void cycliste_validerCommande() {
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
                serviceMetier.validerCommande(enCours);
                System.out.println("La commande a bien été marquée livrée.");
            } else {
                System.out.println("Action annulée.");
            }
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
        int choix = -1;
        while (choix != 8) {
            System.out.println("Interface d'admin de Gustat'IF.");
            afficherIdentite();
            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Connexion / Déconnexion",
                "Visualiser les commandes",
                "Valider une commande en cours de livraison par un drône",
                "Lister les clients",
                "Modifier les données d'un client",
                "Lister les livreurs",
                "Modifier les infos d'un livreur",
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
                case 3: { // Valider une commande en cours de livraison par un drône
                    gestionnaire_validerCommande();
                    break;
                }
                case 4: { // Lister les clients
                    gestionnaire_listerClients();
                    break;
                }
                case 5: { // Modifier les données d'un client
                    gestionnaire_modifierClient();
                    break;
                }
                case 6: { // Lister les livreurs
                    gestionnaire_listerLivreurs();
                    break;
                }
                case 7: { // Modifier les infos d'un livreur
                    gestionnaire_modifierInfosLivreur();
                    break;
                }
            }
        }
    }

    private void gestionnaire_connexion() {
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
    }

    private void gestionnaire_visualiserCommandes() {
        if (null == identiteGestionnaire) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }

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
                System.out.println("Liste des commandes :");
                for (Commande commande : serviceMetier.recupererCommandes()) {
                    System.out.println("  - " + commande);
                }
                break;
            }
            case 2: { // Toutes les commandes
                break;
            }
        }
    }

    private void gestionnaire_validerCommande() {
        if (null == identiteGestionnaire) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }

        List<Commande> lc = serviceMetier.recupererCommandesEnCoursParDrones();
        if (lc.isEmpty()) {
            System.out.println("Il n'y a aucune commande livrée par drône à valider.");
            return;
        }

        System.out.println("Commandes en cours de livraison par des drônes :");
        for (Commande commande : lc) {
            System.out.println("  - " + commande);
        }

        Integer idCommande = Saisie.lireInteger("Quel est l'#ID de la commande à valider : ");
        Commande commande = serviceMetier.recupererCommande(idCommande.longValue());
        if (null == commande) {
            System.out.println("Cet #ID est invalide.");
        } else if (!(commande.getLivreur() instanceof Drone) || null != commande.getDateDeFin()) {
            System.out.println("Cet #ID n'est pas celui d'une commande livrée par drône à valider.");
        } else {
            if ("o".equals(Saisie.lireChaine("Valider la commande ? (o / n) "))) {
                serviceMetier.validerCommande(commande);
                System.out.println("La commande a bien été marquée livrée.");
            } else {
                System.out.println("Action annulée.");
            }
        }
    }

    private void gestionnaire_listerClients() {
        if (null == identiteGestionnaire) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }

        System.out.println("Liste des clients :");
        for (Client client : serviceMetier.recupererClients()) {
            System.out.println("  - " + client);
        }
    }

    private void gestionnaire_modifierClient() {
        if (null == identiteGestionnaire) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }

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
            String email = null;
            while (true) {
                email = Saisie.lireChaine("Adresse mail : ");
                if (null == serviceMetier.recupererClient(email)) {
                    break;
                } else {
                    if (Saisie.choixMenu("Ce mail est déjà utilisé, que voulez-vous faire ?", new String[]{
                        "Entrer un autre mail",
                        "Annuler la modification"
                    }) == 2) { // Annuler l'inscription
                        return;
                    }
                }
            }
            String adresse = Saisie.lireChaine("Adresse de livraison : ");

            serviceMetier.modifierClient(client, nom, prenom, email, adresse);
        } catch (NullPointerException e) {
            System.out.println("Echec de la saisie, la modification n'a pas eu lieu.");
        } catch (BadLocationException ex) {
            System.out.println("L'adresse n'est pas reconnue, la modification n'a pas eu lieu.");
        } catch (DuplicateEmailException ex) {
            System.out.println("Ce mail est déjà utilisé, la modification n'a pas eu lieu.");
        }
    }

    private void gestionnaire_listerLivreurs() {
        if (null == identiteGestionnaire) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }

        System.out.println("Liste des livreurs :");
        for (Livreur livreur : serviceMetier.recupererLivreurs()) {
            System.out.println("  - " + livreur);
        }
    }

    private void gestionnaire_modifierInfosLivreur() {
        if (null == identiteGestionnaire) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }

        System.out.println("Not supported yet.");
    }
}
