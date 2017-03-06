package fr.insa.gustatif.metier.service;

import fr.insa.gustatif.dao.ClientDAO;
import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.dao.RestaurantDAO;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Restaurant;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ServiceMetier {
    
    public void creerClient(Client client) {
        JpaUtil.ouvrirTransaction();
        
        ClientDAO clientDAO = new ClientDAO();
        clientDAO.creerClient(client);
        
        JpaUtil.validerTransaction();
    }
    
    /**
     * TODO: vérifier le return
     * @return 
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
    
     public void creerRestaurant(Restaurant restaurant) {
        JpaUtil.ouvrirTransaction();
        
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        restaurantDAO.creerRestaurant(restaurant);
        
        JpaUtil.validerTransaction();
    }
    
    /**
     * TODO: vérifier le return
     * @return 
     */
    public List<Restaurant> recupererRestaurant() {
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        try {
            return restaurantDAO.findAll();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
}
