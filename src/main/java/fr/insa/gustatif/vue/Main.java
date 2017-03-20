package fr.insa.gustatif.vue;

import fr.insa.gustatif.dao.CommandeDAO;
import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.dao.LivreurDAO;
import fr.insa.gustatif.dao.ProduitCommandeDAO;
import fr.insa.gustatif.dao.ProduitDAO;
import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws DuplicateEmailException, IllegalUserInfoException {
        JpaUtil.init();
        JpaUtil.creerEntityManager();

        // Tests
        ServiceMetier sm = new ServiceMetier();

        final List<Produit> produitsDB = sm.recupererProduits();
        final List<Client> clients = sm.recupererClients();
        
        // Test Panier
        Client clientPanier = clients.get((int) (Math.random() * clients.size()));
        Produit produitPanier = produitsDB.get((int) (Math.random() * produitsDB.size()));
        sm.ajouterAuPanier(clientPanier, produitPanier,5);

        // Tests Client
        String nomClient = ServiceTechnique.GenererString();
        String prenomClient = ServiceTechnique.GenererString();
        //sm.creerClient(new Client(nomClient, prenomClient, nomClient + "." + prenomClient + "@client.fr", "Villeurbanne"));
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
        List<ProduitCommande> produits = new ArrayList<>();
        while ((Math.random() > 0.2 || produits.size() <= 0) && produits.size() < 10) {
            int id = (int) (Math.random() * produitsDB.size());
            int qu = (int) (Math.random() * 10) + 1;
            produits.add(new ProduitCommande(produitsDB.get(id), qu));
        }
        
        Client clientCommande = clients.get((int) (Math.random() * clients.size()));
        System.out.println("Commande par " + clientCommande.getMail() + " :");
        for (ProduitCommande produitCommande : produits) {
            System.out.println(produitCommande);
        }
        Commande commande = new Commande(clientCommande, new Date(), null, produits);
        //sm.creerCommande(commande);
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
        List<Produit> liste = sm.recupererProduitsFromRestaurant(1L);
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
        

        //Modifier Commande
//        CommandeDAO commandedao = new CommandeDAO();
//        try {
//            Commande cm = dao.findById(12002);
//            System.out.println(cm.toString());
//            cm.setClient(cl);
//            sm.modifierCommande(1152, cm);
//            System.out.println(cm.toString());
//        } catch (Exception ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        //test Retirer produitCommande d'une commande
        //TODO : Finir
        CommandeDAO commandeDao = new CommandeDAO();
        try {
            System.out.println(commandeDao.findById(1202).toString());
            
            //modif
            sm.retirerProduitDeCommande(1202, 82);
            System.out.println(commandeDao.findById(1202).toString());
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        //verification de la persistance
//        CommandeDAO commandeDao = new CommandeDAO();
//        try {
//            System.out.println(commandeDao.findById(1202).toString());
//        } catch (Exception ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }



        //TestModifier Livreur
        LivreurDAO livreurDAO = new LivreurDAO();
        try {
            //test Cycliste
            Cycliste livreur = (Cycliste)livreurDAO.findById(1206); //faut que les ID soient de cycliste
            System.out.println(livreur.toString());
            livreur.setCapaciteMax(80);
            livreur.setLatitude(30.2546);
            livreur.setNom("Alex");
            sm.modifierLivreur(livreur);
            System.out.println(livreurDAO.findById(1206));
            
            //test Drone
            Drone drone = (Drone) livreurDAO.findById(1409); //faut que les ID soient de drone
            System.out.println(drone.toString());
            drone.setVitesse(100);
            sm.modifierLivreur(drone);
            System.out.println(livreurDAO.findById(1409));
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        JpaUtil.fermerEntityManager();
        JpaUtil.destroy();
    }
}
