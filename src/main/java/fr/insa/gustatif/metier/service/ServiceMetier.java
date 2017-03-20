package fr.insa.gustatif.metier.service;

import com.google.maps.model.LatLng;
import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.dao.DroneDAO;
import fr.insa.gustatif.dao.CyclisteDAO;
import fr.insa.gustatif.dao.ClientDAO;
import fr.insa.gustatif.dao.CommandeDAO;
import fr.insa.gustatif.dao.LivreurDAO;
import fr.insa.gustatif.dao.ProduitDAO;
import fr.insa.gustatif.dao.RestaurantDAO;
import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
import fr.insa.gustatif.metier.modele.Drone;
import fr.insa.gustatif.metier.modele.Cycliste;
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
import javax.persistence.RollbackException;

/**
 * TODO: Annuler une commande
 */
public class ServiceMetier {

    ServiceTechnique serviceTechnique;

    public ServiceMetier() {
        this.serviceTechnique = new ServiceTechnique();
    }

    /**
     * Crée un client en vérifiant que le mail est unique.
     *
     * @param client Le client à enregistrer dans la BDD
     * @return true si le client a été créé, sinon false (mail déjà utilisé)
     */
    public boolean creerClient(Client client) throws DuplicateEmailException, IllegalUserInfoException {
        JpaUtil.ouvrirTransaction();

        // TODO: A tester
        LatLng coords = GeoTest.getLatLng(client.getAdresse());
        client.setLatitudeLongitude(coords);

        try {
            ClientDAO clientDAO = new ClientDAO();
            try {
                clientDAO.creerClient(client);
                
                // Le client a été créé
                serviceTechnique.EnvoyerMail(client.getMail(),
                        "Bienvenue chez Gustat'IF",
                        "Bonjour " + client.getPrenom() + "," + "\n"
                        + "Nous vous confirmons votre inscription au service Gustat'IF. "
                        + "Votre numéro de client est : " + client.getId() + "."
                );
                JpaUtil.validerTransaction();
                return true;
            } catch (DuplicateEmailException | IllegalUserInfoException e) {
                throw e;
            }
        } catch (RollbackException e) {
            serviceTechnique.EnvoyerMail(client.getMail(),
                    "Votre inscription chez Gustat'IF",
                    "Bonjour " + client.getPrenom() + "," + "\n"
                    + "Votre inscription au service Gustat'IF a malencontreusement échoué... "
                    + "Merci de recommencer ultérieusement."
            );
            JpaUtil.annulerTransaction();
            return false;
        }
    }

    /**
     * TODO: vérifier le return
     *
     * @param idClient
     * @return la liste de tous les clients
     */
    public Client recupererClient(Long idClient) {
        ClientDAO clientDAO = new ClientDAO();
        try {
            return clientDAO.findById(idClient);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * TODO: vérifier le return
     *
     * @param mail
     * @return la liste de tous les clients
     */
    public Client recupererClient(String mail) {
        ClientDAO clientDAO = new ClientDAO();
        try {
            return clientDAO.findByEmail(mail);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * null pour ne pas modifier
     * @param client
     * @param nom
     * @param prenom
     * @param email
     * @param adresse
     * @return 
     */
    public boolean modifierClient(Client client, String nom, String prenom, String email, String adresse) {
        JpaUtil.ouvrirTransaction();
        ClientDAO clientDAO = new ClientDAO();
        try {
            if (clientDAO.modifierClient(client, nom, prenom, email, adresse)) {
                JpaUtil.annulerTransaction();
                return false;
            }
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
            JpaUtil.annulerTransaction();
            return false;
        }
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

    public void ajouterAuPanier(Client client, Produit produit) {
        JpaUtil.ouvrirTransaction();
        ClientDAO clientDAO = new ClientDAO();
        clientDAO.ajouterAuPanier(client, produit);
        JpaUtil.validerTransaction();
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
    
    public List<Produit> recupererProduitsFromRestaurant(Long idRestaurant){
        RestaurantDAO restaurantDao = new RestaurantDAO();
        List<Produit> liste = null ;
        try {
            Restaurant restaurant = restaurantDao.findById(idRestaurant);
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
     * @param idRestaurant
     * @return
     */
    public Restaurant recupererRestaurant(Long idRestaurant) {
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        try {
            return restaurantDAO.findById(idRestaurant);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    /**
     * TODO: vérifier le return
     *
     * @return
     * @throws java.lang.Exception
     */
    public List<Restaurant> recupererRestaurantsTriesAlpha() throws Exception {
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        try {
            return restaurantDAO.findAllSortedByName();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public void creerDrone(Drone drone) {
        JpaUtil.ouvrirTransaction();

        DroneDAO droneDAO = new DroneDAO();
        droneDAO.creerDrone(drone);

        JpaUtil.validerTransaction();
    }

    public boolean creerCycliste(Cycliste cycliste) {
        JpaUtil.ouvrirTransaction();

        try {
            CyclisteDAO cyclisteDAO = new CyclisteDAO();
            cyclisteDAO.creerCycliste(cycliste);

            JpaUtil.validerTransaction();
            return true;
        } catch (Exception ex) {
            System.err.println("La création du cycliste a échoué !");
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
            JpaUtil.annulerTransaction();
            return false;
        }
    }

    /**
     * TODO: vérifier le return
     *
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
     * @param idCommande
     * @return
     */
    public Commande recupererCommande(Long idCommande) {
        CommandeDAO commandeDAO = new CommandeDAO();
        try {
            return commandeDAO.findById(idCommande);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
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

    public Produit recupererProduit(long idProduit) {
        ProduitDAO produitDAO = new ProduitDAO();
        try {
            return produitDAO.findById(idProduit);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
