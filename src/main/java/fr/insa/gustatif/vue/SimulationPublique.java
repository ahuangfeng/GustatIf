package fr.insa.gustatif.vue;

import com.google.maps.errors.NotFoundException;
import com.google.maps.errors.OverDailyLimitException;
import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.CommandeMalFormeeException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
import fr.insa.gustatif.exceptions.AucunLivreurDisponibleException;
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

/** TODO : si l'utilisateur se déconnecte, le panier se vide ou pas?
 *
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
        accueil();
    }

    private void accueil() {
        int choix = -1;
        while (choix != 4) {
            System.out.println("Accueil de Gustat'IF.");
            afficherIdentite();
            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Gestion compte / Connexion / Inscription",
                // TODO: Gestion du panier
                "Voir toutes mes commandes",
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
                case 3: { // Restaurants
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
            afficherIdentite();
            System.out.println("Liste des restaurants :");
            List<Restaurant> restaurants;
            try {
                restaurants = serviceMetier.recupererRestaurants();
                restaurants.sort((Restaurant r1, Restaurant r2) -> r1.getDenomination().compareToIgnoreCase(r2.getDenomination()));
                for (Restaurant restaurant : restaurants) {
                    System.out.println("  - " + restaurant);
                }
            } catch (Exception ex) {
                System.out.println("Erreur lors de la récupération de la liste des restaurants.");
                Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
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
                    try {
                        details_restaurant(resto);
                    } catch (Exception ex) {
                        Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
            }
        }
    }

    private void details_restaurant(Restaurant restaurant) throws BackToHomeException {
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
                        System.out.println("Tous les produits d'une même commande doivent provenir d'un unique restaurant. Restaurant actuel : #"+restaurant.getId());
                    }
                    System.out.println("Produit ajouté au panier.");
                    break;
                }
                case 2: { // Passer a la commande
                    try {
                        passerALaCommande(panier);
                    } catch (Exception ex) {
                        Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
    }

    private void connexion() {
        System.out.println("Connexion :");
        String email = Saisie.lireChaine("Email : ").toLowerCase();
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
                            if (email.isEmpty() || null == serviceMetier.recupererClient(email)) {
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
        if (!this.panier.isEmpty()) {
            System.out.println("Panier actuel : ");
            for (ProduitCommande produitCommande : this.panier) {
                System.out.println("  - " + produitCommande);
            }
        }
    }

    /**
     *
     * @param produit
     * @param quantite
     */
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
                            
                        } catch (PersistenceException ex) {
                            System.out.println("Erreur critique lors de la création de la commande.");
                            Logger.getLogger(SimulationPublique.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (CommandeMalFormeeException ex) {
                            System.out.println("Impossible de créer la commande :");
                            System.out.println(ex.getMessage());
                        } catch (AucunLivreurDisponibleException ex) {
                            //TODO : si il n'y a pas de livreur, on fait quoi? 
                            System.out.println("Pas de livreurs disponibles !");
                        } catch (OverDailyLimitException ex) {
                            System.out.println("Quota Google Maps dépassé.");
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
