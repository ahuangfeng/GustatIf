package fr.insa.gustatif.vue;

import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Cycliste;
import fr.insa.gustatif.metier.modele.Drone;
import fr.insa.gustatif.metier.modele.Livreur;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.Produit;
import fr.insa.gustatif.metier.modele.ProduitCommande;
import fr.insa.gustatif.metier.modele.Restaurant;
import fr.insa.gustatif.metier.service.ServiceMetier;
import fr.insa.gustatif.metier.service.ServiceTechnique;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class Main {

    public static void main(String[] args) {
        JpaUtil.init();
        JpaUtil.creerEntityManager();

        // Tests
        ServiceMetier sm = new ServiceMetier();

        // Tests Client
        String nomClient = ServiceTechnique.GenererString();
        String prenomClient = ServiceTechnique.GenererString();
        sm.creerClient(new Client(nomClient, prenomClient, nomClient + "." + prenomClient + "@client.fr", "Villeurbanne"));
        for (Client result : sm.recupererClients()) {
            System.out.println(result);
        }
        // Tests Produits
        //sm.creerProduit(new Produit("Gros cookie", "Un gros gros gros cookie", 1.4, 2.1));
        for (Produit result : sm.recupererProduits()) {
            System.out.println(result);
        }
        // Tests Restaurants
        //sm.creerRestaurant(new Restaurant("Nom","Description","adresse"));
        for (Restaurant res : sm.recupererRestaurants()) {
            System.out.println(res);
        }
        
        // Tests Commandes
        final List<Produit> produitsDB = sm.recupererProduits();
        List<ProduitCommande> produits = new ArrayList<>();
        while ((Math.random() > 0.2 || produits.size() <= 0) && produits.size() < 10) {
            int id = (int) (Math.random() * produitsDB.size());
            int qu = (int) (Math.random() * 10) + 1;
            produits.add(new ProduitCommande(produitsDB.get(id), qu));
        }
        
        List<Client> clients = sm.recupererClients();
        Client clientCommande = clients.get((int) (Math.random() * clients.size()));
        System.out.println("Commande par " + clientCommande.getMail() + " :");
        for (ProduitCommande produitCommande : produits) {
            System.out.println(produitCommande);
        }
        Commande commande = new Commande(clientCommande, new Date(), null, produits);
        sm.creerCommande(commande);
        for (Commande res : sm.recupererCommandes()) {
            System.out.println(res);
        }

        // Tests Livreurs
        sm.creerDrone(new Drone(20, 30, true, 12.325, 14.2115));
        
        String nom = ServiceTechnique.GenererString();
        String prenom = ServiceTechnique.GenererString();
        sm.creerCycliste(new Cycliste(nom, prenom, nom + "." + prenom + "@gustatif.com", 30, true, 1.230, 5.256666));
        
        for (Livreur result : sm.recupererLivreur()) {
            System.out.println(result);
        }
        
        
        //Service GetProduit
        //Produit prod = sm.getProduit(53);
        //System.out.println(prod.toString());
        
        
        //Liste de produit par id de restaurant
        List<Produit> liste = sm.recupererProduitsFromRestaurant(1);
        for (Produit produit : liste) {
            System.out.println(produit.toString());
        }
        
        //modifier client
        Client cl = sm.recupererClientsById(124);
        System.out.println(cl.toString());
//        cl.setNom("NUEVOOO NOOMM");
//        System.out.println(cl.toString());
//        cl.setAdresse("por ahi");
//        cl.setMail("asdfsd@ffff.fr");
//        sm.modifierClient(cl);
//        Client cl1 = sm.recupererClientsById(124);
//        System.out.println(cl.toString());
        
        
        
        
        JpaUtil.fermerEntityManager();
        JpaUtil.destroy();
    }
}
