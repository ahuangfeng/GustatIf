package fr.insa.gustatif.metier.service;

import com.google.maps.model.LatLng;
import fr.insa.gustatif.dao.ClientDAO;
import fr.insa.gustatif.dao.CommandeDAO;
import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.dao.LivreurDAO;
import fr.insa.gustatif.dao.ProduitDAO;
import fr.insa.gustatif.dao.RestaurantDAO;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Livreur;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.Produit;
import fr.insa.gustatif.metier.modele.Restaurant;
import fr.insa.gustatif.util.GeoTest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ServiceMetier {

    /**
     * Crée un client en vérifiant que le mail est unique.
     *
     * @param client Le client à enregistrer dans la BDD
     * @return true si le client a été créé, sinon false (mail déjà utilisé)
     * @throws Exception
     */
    public boolean creerClient(Client client) throws Exception {
        JpaUtil.ouvrirTransaction();

        // TODO: A tester
        LatLng coords = GeoTest.getLatLng(client.getAdresse());
        client.setLatitudeLongitude(coords);

        ClientDAO clientDAO = new ClientDAO();
        clientDAO.creerClient(client);

        JpaUtil.validerTransaction();
        return true;
    }

    /**
     * TODO: vérifier le return
     *
     * @return la liste de tous les clients
     */
    public List<Client> recupererClients() {
        ClientDAO clientDAO = new ClientDAO();
        try {
            return clientDAO.findAll();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    public void creerProduit(Produit produit) {
        JpaUtil.ouvrirTransaction();

        ProduitDAO produitDAO = new ProduitDAO();
        produitDAO.creerProduit(produit);

        JpaUtil.validerTransaction();
    }
    
    public Produit getProduit(long id){
        ProduitDAO produitDAO = new ProduitDAO();
        Produit res = null;
        try {
            return res = produitDAO.findById(id);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    /**
     * TODO: vérifier le return
     *
     * @return
     */
    public List<Produit> recupererProduits() {
        ProduitDAO produitDAO = new ProduitDAO();
        try {
            return produitDAO.findAll();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
    
    public List<Produit> recupererProduitsFromRestaurant(long id){
        RestaurantDAO restaurantDao = new RestaurantDAO();
        List<Produit> liste = null ;
        try {
            Restaurant restaurant = restaurantDao.findById(id);
            liste = restaurant.getProduits();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return liste;   //gerer erreurs 
    }

    public void creerRestaurant(Restaurant restaurant) {
        JpaUtil.ouvrirTransaction();

        RestaurantDAO restaurantDAO = new RestaurantDAO();
        restaurantDAO.creerRestaurant(restaurant);

        JpaUtil.validerTransaction();
    }

    /**
     * TODO: vérifier le return
     *
     * @return
     */
    public List<Restaurant> recupererRestaurants() {
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        try {
            return restaurantDAO.findAll();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
    
    public void creerLivreur(Livreur livreur) {
        JpaUtil.ouvrirTransaction();
        
        LivreurDAO livreurDAO = new LivreurDAO();
        livreurDAO.creerLivreur(livreur);
        
        JpaUtil.validerTransaction();
    }
    
    /**
     * TODO: vérifier le return
     * @return 
     */
    public List<Livreur> recupererLivreur() {
        LivreurDAO livreurDAO = new LivreurDAO();
        try {
            return livreurDAO.findAll();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
    
    public void creerCommande(Commande commande) {
        JpaUtil.ouvrirTransaction();

        CommandeDAO commandeDAO = new CommandeDAO();
        commandeDAO.creerCommande(commande);

        JpaUtil.validerTransaction();
    }

    /**
     * TODO: vérifier le return
     *
     * @return
     */
    public List<Commande> recupererCommandes() {
        CommandeDAO commandeDAO = new CommandeDAO();
        try {
            return commandeDAO.findAll();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

}
