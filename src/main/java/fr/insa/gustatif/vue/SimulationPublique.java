package fr.insa.gustatif.vue;

import com.google.maps.errors.NotFoundException;
import com.google.maps.errors.OverDailyLimitException;
import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.CommandeMalFormeeException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
import fr.insa.gustatif.exceptions.AucunLivreurDisponibleException;
import fr.insa.gustatif.exceptions.ServeurOccupeException;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.Produit;
import fr.insa.gustatif.metier.modele.ProduitCommande;
import fr.insa.gustatif.metier.modele.Restaurant;
import fr.insa.gustatif.metier.service.ServiceMetier;
import fr.insa.gustatif.util.Saisie;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;

/**
 * Simulation textuelle de l'IHM publique.
 */
public class SimulationPublique {

    Client identite;
    ServiceMetier serviceMetier;

    List<ProduitCommande> panier;
    Long panierIdRestaurant;

    public SimulationPublique() {
        this.identite = null;
        this.serviceMetier = new ServiceMetier();
        this.panier = new ArrayList<>();
        this.panierIdRestaurant = -1L;
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
        while (choix != 5) {
            System.out.println("Accueil de Gustat'IF.");
            afficherIdentite();
            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Gestion compte / Connexion / Inscription",
                "Voir toutes mes commandes",
                "Vider mon panier",
                "Restaurants",
                "Quitter"
            });
            switch (choix) {
                case 1: { // Gestion compte / Connexion / Inscription OU Déconnexion
                    gestionCompte();
                    break;
                }
                case 2: {
                    if (null != identite) {
                        voirToutesMesCommandes();
                    } else {
                        System.out.println("Vous n'êtes pas connecté.");
                    }
                    break;
                }
                case 3: { // Vider mon panier
                    viderPanier();
                    System.out.println("Le panier a bien été vidé.");
                    break;
                }
                case 4: { // Restaurants
                    try {
                        restaurants();
                    } catch (BackToHomeException e) {
                    }
                    break;
                }
            }
        }
    }

    private void restaurants() throws BackToHomeException {
        int choix = -1;
        while (choix != 2) {
            try {
                afficherIdentite();
                System.out.println("Liste des restaurants :");
                List<Restaurant> restaurants = serviceMetier.recupererRestaurants();
                restaurants.sort((Restaurant r1, Restaurant r2) -> r1.getDenomination().compareToIgnoreCase(r2.getDenomination()));
                for (Restaurant restaurant : restaurants) {
                    System.out.println("  - " + restaurant);
                }
            } catch (PersistenceException ex) {
                System.out.println("Erreur de persistence lors de la récupération de la liste des restaurants.");
                Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }

            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Produits et détails d'un restaurant",
                "Retour"
            });
            switch (choix) {
                case 1: { // Produits et détails d'un restaurant
                    try {
                        Integer idRestaurant = Saisie.lireInteger("Rentrez l'#ID du restaurant : ");
                        Restaurant resto = serviceMetier.recupererRestaurant(idRestaurant.longValue());
                        if (resto == null) {
                            System.out.println("L'#ID est invalide.");
                            break;
                        }
                        details_restaurant(resto);
                    } catch (PersistenceException ex) {
                        System.out.println("Erreur de persistence lors de la récupération du restaurant.");
                        Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
            }
        }
    }

    private void details_restaurant(Restaurant restaurant) throws BackToHomeException {
        if (null == restaurant) {
            System.out.println("Le restaurant est invalide.");
            return;
        }

        int choix = -1;
        while (choix != 3) {
            afficherIdentite();
            System.out.println("Détails du restaurant \"" + restaurant.getDenomination() + "\" :");
            System.out.println("  -> " + restaurant);
            System.out.println("Liste des produits :");
            List<Produit> produits = restaurant.getProduits();
            for (Produit produit : produits) {
                System.out.println("  - " + produit);
            }

            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Ajouter un produit au panier",
                "Passer à la commande",
                "Retour"
            });
            switch (choix) {
                case 1: { // Ajouter un produit au panier
                    try {
                        Integer idProduit = Saisie.lireInteger("Rentrez l'#ID du produit : ");
                        Integer quantite = Saisie.lireInteger("Rentrez la quantité : ");
                        if (0 == quantite) {
                            System.out.println("Produit non ajouté.");
                            break;
                        }
                        Produit produit = serviceMetier.recupererProduit(idProduit.longValue());
                        if (null == produit) {
                            System.out.println("L'#ID est invalide.");
                            break;
                        }
                        if (!ajouterAuPanier(produit, quantite, restaurant.getId())) {
                            System.out.println("Impossible d'ajouter le produit :");
                            System.out.println("Tous les produits d'une même commande doivent provenir d'un unique restaurant. Restaurant actuel : #" + restaurant.getId());
                        }
                        System.out.println("Produit ajouté au panier.");
                    } catch (PersistenceException ex) {
                        System.out.println("Erreur de persistence lors de la récupération de la liste des produits.");
                        Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
                case 2: { // Passer a la commande
                    passerALaCommande(panier);
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
        try {
            String nom = Saisie.lireChaine("Nom : ");
            String prenom = Saisie.lireChaine("Prénom : ");
            String email = Saisie.lireEmailAvecVerification(serviceMetier);
            if (null == email) {
                return;
            }
            String adresse = Saisie.lireChaine("Adresse de livraison : ");

            boolean cree;
            try {
                cree = serviceMetier.inscrireClient(new Client(nom, prenom, email, adresse));
            } catch (DuplicateEmailException ex) {
                cree = false;
                System.out.println("Ce mail est déjà utilisé !");
            } catch (IllegalUserInfoException ex) {
                cree = false;
                System.out.println(ex.getMessage());
            } catch (NotFoundException ex) {
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
        } catch (PersistenceException ex) {
            System.out.println("Erreur de persistence lors de l'inscription.");
            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void connexion() {
        try {
            System.out.println("Connexion :");
            String email = Saisie.lireChaine("Email : ").toLowerCase();
            identite = serviceMetier.recupererClient(email);
            if (null == identite) {
                System.out.println("La connexion a échoué.");
            } else {
                System.out.println("Vous êtes connecté !");
            }
        } catch (PersistenceException ex) {
            System.out.println("Erreur de persistence lors de la récupération du client.");
            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
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
                        try {
                            System.out.println("Ne rentrez rien pour ne pas modifier un champ.");
                            String nom = Saisie.lireChaine("Nom : ");
                            String prenom = Saisie.lireChaine("Prénom : ");
                            String email = Saisie.lireEmailAvecVerification(serviceMetier);
                            if (null == email) {
                                return;
                            }
                            String adresse = Saisie.lireChaine("Adresse de livraison : ");

                            try {
                                serviceMetier.modifierClient(identite, nom, prenom, email, adresse);
                            } catch (DuplicateEmailException ex) {
                                System.out.println("Votre compte n'a pas pu être mis à jour, car cet email est déjà utilisé.");
                                afficherIdentite();
                                break;
                            } catch (NotFoundException ex) {
                                System.out.println("Votre compte n'a pas pu être mis à jour, car votre adresse n'est pas reconnue.");
                                afficherIdentite();
                                break;
                            }
                        } catch (PersistenceException ex) {
                            System.out.println("Erreur de persistence lors de la modification du client.");
                            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
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
                    + identite.getPrenom() + " " + identite.getNom() + " <" + identite.getMail() + ">"
            );
        }
        if (!panier.isEmpty()) {
            System.out.println("Panier actuel : ");
            for (ProduitCommande produitCommande : panier) {
                System.out.println("  - " + produitCommande);
            }
        }
    }

    private boolean ajouterAuPanier(Produit produit, int quantite, Long idRestaurant) {
        if (panier.isEmpty()) {
            panierIdRestaurant = idRestaurant;
        } else {
            if (!Objects.equals(idRestaurant, panierIdRestaurant)) {
                return false;
            }
        }
        for (ProduitCommande pc : panier) {
            if (Objects.equals(pc.getProduit().getId(), produit.getId())) {
                pc.setQuantity(pc.getQuantity() + quantite);
                return true;
            }
        }
        panier.add(new ProduitCommande(produit, quantite));
        return true;
    }

    private void viderPanier() {
        this.panier.clear();
        this.panierIdRestaurant = -1L;
    }

    private void passerALaCommande(List<ProduitCommande> listeProduits) throws BackToHomeException {
        while (true) {
            afficherIdentite();
            if (null == identite) { // Non connecté
                System.out.println("Vous devez vous connecter avant de commander.");
                int choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                    "M'identifier",
                    "Vider mon panier et retourner à l'accueil",
                    "Retour"
                });
                switch (choix) {
                    case 1: { // M'identifier
                        gestionCompte();
                        break;
                    }
                    case 2: { // Vider mon panier et retourner à l'accueil
                        viderPanier();
                        throw new BackToHomeException();
                    }
                    case 3: { // Retour
                        return;
                    }
                }
            } else { // Connecté
                int choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                    "Payer par carte bancaire",
                    "Retour"
                });
                switch (choix) {
                    case 1: { // Payer par carte bancaire
                        try {
                            Commande commande = serviceMetier.creerCommande(identite, listeProduits);
                            System.out.println("Votre commande est validée, et est en cours de livraison !");
                            System.out.println("C'est " + commande.getLivreur().getIdentifiant() + " qui vous l'amène !");
                            panier.clear();
                            throw new BackToHomeException();

                        } catch (CommandeMalFormeeException ex) {
                            System.out.println("Impossible de créer la commande :");
                            System.out.println(ex.getMessage());
                        } catch (AucunLivreurDisponibleException ex) {
                            //TODO : si il n'y a pas de livreur, on fait quoi? 
                            System.out.println("Pas de livreurs disponibles !");
                        } catch (OverDailyLimitException ex) {
                            System.out.println("Quota Google Maps dépassé.");
                        } catch (PersistenceException ex) {
                            System.out.println("Erreur de persistence lors de la création de la commande.");
                            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ServeurOccupeException ex) {
                            System.out.println("Impossible de commander, le serveur Gustat'IF est victime de son succès.");
                            System.out.println("Veuillez réessayer dans quelques minutes :)");
                        }
                    }
                    case 2: { // Retour
                        return;
                    }
                }
            }
        }
    }

    void voirToutesMesCommandes() {
        if (null == identite) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }

        afficherIdentite();
        if (identite.getCommandes().isEmpty()) {
            System.out.println("Vous n'avez passé aucune commande.");
        } else {
            System.out.println("Voici l'historique de vos commandes :");
            List<Commande> listeCommandes = identite.getCommandes();
            for (Commande listeCommande : listeCommandes) {
                System.out.println(listeCommande);
            }
        }
    }
}
