package fr.insa.gustatif.vue;

import fr.insa.gustatif.exceptions.BadLocationException;
import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.Produit;
import fr.insa.gustatif.metier.modele.ProduitCommande;
import fr.insa.gustatif.metier.modele.Restaurant;
import fr.insa.gustatif.metier.service.ServiceMetier;
import fr.insa.gustatif.util.Saisie;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class SimulationPublic {

    Client identite;
    ServiceMetier serviceMetier;
    List<ProduitCommande> panier;

    public SimulationPublic() {
        this.identite = null;
        this.serviceMetier = new ServiceMetier();
        this.panier = new ArrayList<>();
    }

    public void run() {
        accueil();
    }

    private void accueil() {
        int choix = -1;
        while (choix != 5) {
            System.out.println("Accueil de Gustat'IF.");
            afficherIdentite();
            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Gestion compte / Connexion / Inscription",
                "Voir toutes mes commandes",
                "Restaurants",
                "Contact",
                "Quitter"
            });
            switch (choix) {
                case 1: { // Gestion compte / Connexion / Inscription OU Déconnexion
                    gestionCompte();
                    break;
                }
                case 2:{
                    if(identite != null){
                        voirToutesCommandes();
                    }else{
                        System.out.println("Pas connecté!");
                    }
                    break;
                }
                case 3: { // Restaurants
                    restaurants();
                    break;
                }
                case 4: { // Contact
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
                restaurants = serviceMetier.recupererRestaurants();
                restaurants.sort((Restaurant r1, Restaurant r2) -> r1.getDenomination().compareToIgnoreCase(r2.getDenomination()));
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
                try {
                    details_restaurant(resto);
                } catch (Exception ex) {
                    Logger.getLogger(SimulationPublic.class.getName()).log(Level.SEVERE, null, ex);
                }
                    break;
                }
            }
        }
    }

    private void details_restaurant(Restaurant restaurant){
        int choix = -1;
        while (choix != 3) {
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
                "Passer à la commande",
                "Retour"
            });
            switch (choix) {
                case 1: { // Ajouter un produit au panier
                    Integer idProduit = Saisie.lireInteger("Rentrez l'#ID du produit : ");
                    Integer quantite = Saisie.lireInteger("Rentrez la quantité : ");
                    Produit produit = serviceMetier.recupererProduit(idProduit.longValue());
                    if (produit == null) {
                        System.out.println("L'#ID est invalide.");
                        break;
                    }
                    panier.add(new ProduitCommande(produit, quantite)); //TODO: gere si le produit exists!
//                    ajouterAuPanier(produit, quantite); 
                    break;
                }
                case 2 : { try {
                    //Passer a la commande
                    passerALaCommande(panier);
                } catch (Exception ex) {
                    Logger.getLogger(SimulationPublic.class.getName()).log(Level.SEVERE, null, ex);
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

                        try {
                            serviceMetier.modifierClient(identite, nom, prenom, email, adresse);
                        } catch (DuplicateEmailException ex) {
                            System.out.println("Votre compte n'a pas pu être mis à jour, car cet email est déjà utilisé.");
                            afficherIdentite();
                            break;
                        } catch (BadLocationException ex) {
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
                    + identite.getPrenom() + " " + identite.getNom() + "<" + identite.getMail() + ">"
            );
            System.out.println("Panier actuel : ");
            for (ProduitCommande produitCommande : this.panier) {
                System.out.println("    "+produitCommande.toString());
            }
        }
    }
    /**
     * TODO: Ajouter au panier n'existe plus!
     * @param produit
     * @param quantite 
     */
    private void ajouterAuPanier(Produit produit, int quantite) {
        identite.ajouterAuPanier(new ProduitCommande(produit, quantite));
    }

    private void passerALaCommande(List<ProduitCommande> listeProduits){  //TODO: verifier les exception et concretiser
        Commande commandeActuel;
        try {
            commandeActuel = serviceMetier.creerCommande(identite,listeProduits);//c'est creerCommande qui donne un exception
            System.out.println("Commande Créée.");
            this.panier.clear();
            int choix = -1;
            afficherIdentite();
            System.out.println("Commande \""+commandeActuel.getId()+"\" :");
            System.out.println("  -> " + commandeActuel.toString());

            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Payer avec Carte bancaire",
                "Payer en espèces",
                "Retour"
            });
            switch (choix) { //TODO les deux payer sont pareil!
                case 1: { // Payer avec carte
                    System.out.println("Commande "+commandeActuel.getId()+" payé avec carte bancaire.");
                    break;
                }
                case 2 : { //Payer en especes
                    System.out.println("Commande "+commandeActuel.getId()+" avec paiement à la livraison.");
                    break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SimulationPublic.class.getName()).log(Level.SEVERE, null, ex);
        }
        accueil();
    }
    
    void voirToutesCommandes(){
        List<Commande> listeCommandes = identite.getCommandes();
        if(listeCommandes!=null){
            for (Commande listeCommande : listeCommandes) {
                System.out.println(listeCommande.toString());
            }
        }
    }
}
