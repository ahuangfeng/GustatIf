package fr.insa.gustatif.vue;

import fr.insa.gustatif.exceptions.BadLocationException;
import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Produit;
import fr.insa.gustatif.metier.modele.Restaurant;
import fr.insa.gustatif.metier.service.ServiceMetier;
import fr.insa.gustatif.util.Saisie;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class SimulationPublic {

    Client identite;
    ServiceMetier serviceMetier;

    public SimulationPublic() {
        this.identite = null;
        this.serviceMetier = new ServiceMetier();
    }

    public void run() {
        accueil();
    }

    private void accueil() {
        int choix = -1;
        while (choix != 4) {
            System.out.println("Accueil de Gustat'IF.");
            afficherIdentite();
            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Gestion compte / Connexion / Inscription",
                "Restaurants",
                "Contact",
                "Quitter"
            });
            switch (choix) {
                case 1: { // Gestion compte / Connexion / Inscription OU Déconnexion
                    gestionCompte();
                    break;
                }
                case 2: { // Restaurants
                    restaurants();
                    break;
                }
                case 3: { // Contact
                    break;
                }
            }
        }
    }

    private void restaurants() {
        int choix = -1;
        while (choix != 2) {
            afficherIdentite();
            System.out.println("Liste des restaurants :");
            List<Restaurant> restaurants;
            try {
                restaurants = serviceMetier.recupererRestaurantsTriesAlpha();
                for (Restaurant restaurant : restaurants) {
                    System.out.println("  - " + restaurant);
                }
            } catch (Exception ex) {
                System.out.println("Erreur lors de la récupération de la liste des restaurants.");
                Logger.getLogger(SimulationPublic.class.getName()).log(Level.SEVERE, null, ex);
            }

            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Produits et détails d'un restaurant",
                "Retour"
            });
            switch (choix) {
                case 1: { // Produits et détails d'un restaurant
                    Integer idRestaurant = Saisie.lireInteger("Rentrez l'#ID du restaurant : ");
                    Restaurant resto = serviceMetier.recupererRestaurant(idRestaurant.longValue());
                    if (resto == null) {
                        System.out.println("L'#ID est invalide.");
                        break;
                    }
                    details_restaurant(resto);
                    break;
                }
            }
        }
    }

    private void details_restaurant(Restaurant restaurant) {
        int choix = -1;
        while (choix != 2) {
            afficherIdentite();
            System.out.println("Détails du restaurant \"" + restaurant.getDenomination() + "\" :");
            System.out.println("  -> " + restaurant);
            System.out.println("Liste des produits :");
            List<Produit> produits = serviceMetier.recupererProduitsFromRestaurant(restaurant.getId());
            for (Produit produit : produits) {
                System.out.println("  - " + produit);
            }

            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Ajouter un produit au panier",
                "Retour"
            });
            switch (choix) {
                case 1: { // Ajouter un produit au panier
                    Integer idProduit = Saisie.lireInteger("Rentrez l'#ID du produit : ");
                    Produit produit = serviceMetier.recupererProduit(idProduit.longValue());
                    if (produit == null) {
                        System.out.println("L'#ID est invalide.");
                        break;
                    }
                    ajouterAuPanier(produit);
                    break;
                }
            }
        }
    }

    private void inscription() {
        if (identite != null) {
            System.out.println("Vous êtes déjà connecté.");
            return;
        }
        String nom = Saisie.lireChaine("Nom : ");
        String prenom = Saisie.lireChaine("Prénom : ");
        String email = null;
        while (true) {
            email = Saisie.lireChaine("Adresse mail : ");
            if (serviceMetier.recupererClient(email) == null) {
                break;
            } else {
                if (Saisie.choixMenu("Ce mail est déjà utilisé, que voulez-vous faire ?", new String[]{
                    "Entrer un autre mail",
                    "Annuler l'inscription"
                }) == 2) { // Annuler l'inscription
                    return;
                }
            }
        }
        String adresse = Saisie.lireChaine("Adresse de livraison : ");

        boolean cree;
        try {
            cree = serviceMetier.creerClient(new Client(nom, prenom, email, adresse));
        } catch (DuplicateEmailException ex) {
            cree = false;
            System.out.println("Ce mail est déjà utilisé !");
        } catch (IllegalUserInfoException ex) {
            cree = false;
            System.out.println(ex.getMessage());
        } catch (BadLocationException ex) {
            cree = false;
            System.out.println("Votre adresse n'est pas reconnue !");
        }

        if (cree) {
            System.out.println("Votre compte a été créé, et un mail de confirmation vous a été envoyé.");
            System.out.println("Vous pouvez maintenant vous connecter.");
            System.out.println("(connexion non automatique pour permettre de tester la connexion, sans avoir à se déconnecter)");
        } else {
            System.out.println("Votre compte n'a pas pu être créé.");
        }
    }

    private void connexion() {
        System.out.println("Connexion :");
        String email = Saisie.lireChaine("Email : ");
        identite = serviceMetier.recupererClient(email);
        if (null == identite) {
            System.out.println("La connexion a échoué.");
        } else {
            System.out.println("Vous êtes connecté !");
        }
    }

    private void gestionCompte() {
        int choix = -1;
        while (true) {
            if (null != identite) {
                afficherIdentite();
                System.out.println("Gestion du compte :");

                choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                    "Modifier mes informations",
                    "Déconnexion",
                    "Retour"
                });
                switch (choix) {
                    case 1: { // Modifier mes informations
                        System.out.println("Ne rentrez rien pour ne pas modifier un champ.");
                        String nom = Saisie.lireChaine("Nom : ");
                        String prenom = Saisie.lireChaine("Prénom : ");
                        String email = null;
                        while (true) {
                            email = Saisie.lireChaine("Adresse mail : ");
                            if (serviceMetier.recupererClient(email) == null) {
                                break;
                            } else {
                                if (Saisie.choixMenu("Ce mail est déjà utilisé, que voulez-vous faire ?", new String[]{
                                    "Entrer un autre mail",
                                    "Annuler l'inscription"
                                }) == 2) { // Annuler l'inscription
                                    return;
                                }
                            }
                        }
                        String adresse = Saisie.lireChaine("Adresse de livraison : ");

                        try {
                            serviceMetier.modifierClient(identite, nom, prenom, email, adresse);
                        } catch (DuplicateEmailException ex) {
                            System.out.println("Votre compte n'a pas pu être mis à jour, car cet email est déjà utilisé.");
                            afficherIdentite();
                            break;
                        }
                        System.out.println("Votre compte a bien été mis à jour.");
                        afficherIdentite();
                        break;
                    }
                    case 2: { // Déconnexion
                        identite = null;
                        System.out.println("Vous avez été déconnecté.");
                        break;
                    }
                    case 3: { // Retour
                        return;
                    }
                }
            } else {
                afficherIdentite();
                choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                    "Inscription",
                    "Connexion",
                    "Retour"
                });
                switch (choix) {
                    case 1: { // Inscription
                        inscription();
                        break;
                    }
                    case 2: { // Connexion
                        connexion();
                        break;
                    }
                    case 3: { // Retour
                        return;
                    }
                }
            }
        }
    }

    private void afficherIdentite() {
        if (identite == null) {
            System.out.println("Non connecté.");
        } else {
            System.out.println(
                    "Connecté en tant que : "
                    + identite.getPrenom() + " " + identite.getNom() + "<" + identite.getMail() + ">"
            );
        }
    }

    private void ajouterAuPanier(Produit produit) {
        // TODO: Ajout au panier
        System.out.println("Cette opération n'est pas encore gérée.");
    }
}
