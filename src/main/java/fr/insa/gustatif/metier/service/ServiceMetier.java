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
import fr.insa.gustatif.exceptions.BadLocationException;
import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
import fr.insa.gustatif.metier.modele.Drone;
import fr.insa.gustatif.metier.modele.Cycliste;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Livreur;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.Produit;
import fr.insa.gustatif.metier.modele.ProduitCommande;
import fr.insa.gustatif.metier.modele.Restaurant;
import fr.insa.gustatif.util.GeoTest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
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
     * @throws fr.insa.gustatif.exceptions.DuplicateEmailException
     * @throws fr.insa.gustatif.exceptions.IllegalUserInfoException
     * @throws fr.insa.gustatif.exceptions.BadLocationException
     */
    public boolean creerClient(Client client) throws DuplicateEmailException, IllegalUserInfoException, BadLocationException {
        final Runnable envoyerMailSucces = () -> {
            serviceTechnique.EnvoyerMail(client.getMail(),
                    "Bienvenue chez Gustat'IF",
                    "Bonjour " + client.getPrenom() + "," + "\n"
                    + "Nous vous confirmons votre inscription au service Gustat'IF. "
                    + "Votre numéro de client est : " + client.getId() + "."
            );
        };
        final Runnable envoyerMailEchec = () -> {
            serviceTechnique.EnvoyerMail(client.getMail(),
                    "Votre inscription chez Gustat'IF",
                    "Bonjour " + client.getPrenom() + "," + "\n"
                    + "Votre inscription au service Gustat'IF a malencontreusement échoué... "
                    + "Merci de recommencer ultérieusement."
            );
        };

        // Vérifie si le mail est valide
        if (!serviceTechnique.VerifierMail(client.getMail())) {
            throw new IllegalUserInfoException("Le mail n'est pas valide.");
        }

        JpaUtil.ouvrirTransaction();
        ClientDAO clientDAO = new ClientDAO();
        try {
            clientDAO.creerClient(client);

            // Récupère les coordonnées
            LatLng coords = ServiceTechnique.getLatLng(client.getAdresse());
            client.setLatitudeLongitude(coords);

            // Le client a été créé
            envoyerMailSucces.run();
            JpaUtil.validerTransaction();
            return true;
        } catch (DuplicateEmailException | IllegalUserInfoException e) {
            JpaUtil.annulerTransaction();
            throw e;
        } catch (BadLocationException e) {
            envoyerMailEchec.run();
            JpaUtil.annulerTransaction();
            throw e;
        } catch (PersistenceException e) {
            envoyerMailEchec.run();
            JpaUtil.annulerTransaction();
            return false;
        }
    }

    public boolean modifierClient(Client client) throws BadLocationException {
        // Récupère les coordonnées
        LatLng coords = ServiceTechnique.getLatLng(client.getAdresse());

        ClientDAO clientDAO = new ClientDAO();

        JpaUtil.ouvrirTransaction();
        try {
            client.setLatitudeLongitude(coords);
            clientDAO.modifierClient(client);
            JpaUtil.validerTransaction();
            return true;
        } catch (PersistenceException e) {
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
        return clientDAO.findById(idClient);
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
        } catch (NonUniqueResultException ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PersistenceException ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * vide pour ne pas modifier
     *
     * @param client
     * @param nom
     * @param prenom
     * @param email
     * @param adresse
     * @throws fr.insa.gustatif.exceptions.DuplicateEmailException
     */
    public void modifierClient(Client client, String nom, String prenom, String email, String adresse) throws DuplicateEmailException {
        JpaUtil.ouvrirTransaction();
        ClientDAO clientDAO = new ClientDAO();
        try {
            clientDAO.modifierClient(client, nom, prenom, email, adresse);
        } catch (DuplicateEmailException ex) {
            JpaUtil.annulerTransaction();
            throw ex;
        }
        JpaUtil.validerTransaction();
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

    public Client recupererClientsById(long id) {
        ClientDAO clientDAO = new ClientDAO();
        try {
            return clientDAO.findById(id);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void ajouterAuPanier(Client client, Produit produit, int quantite) {
        JpaUtil.ouvrirTransaction();
        ClientDAO clientDAO = new ClientDAO();
        clientDAO.ajouterAuPanier(client, produit, quantite);
        JpaUtil.validerTransaction();
    }

    public void creerProduit(Produit produit) {
        JpaUtil.ouvrirTransaction();

        ProduitDAO produitDAO = new ProduitDAO();
        produitDAO.creerProduit(produit);

        JpaUtil.validerTransaction();
    }

    public Produit getProduit(long id) {
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

    public List<Produit> recupererProduitsFromRestaurant(Long idRestaurant) {
        RestaurantDAO restaurantDao = new RestaurantDAO();
        List<Produit> liste = null;
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

    public Livreur recupererLivreur(long id) {
        LivreurDAO livreurDAO = new LivreurDAO();
        try {
            return livreurDAO.findById(id);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean modifierLivreur(Livreur livreur) {
        JpaUtil.ouvrirTransaction();
        LivreurDAO livreurDAO = new LivreurDAO();
        try {
            livreurDAO.modifierLivreur(livreur);
            JpaUtil.validerTransaction();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
            JpaUtil.annulerTransaction();
            return false;
        }
    }

    public void panierToCommande(Client client) {
        JpaUtil.ouvrirTransaction();

        CommandeDAO commandeDAO = new CommandeDAO();
        ClientDAO clientDAO = new ClientDAO();
        commandeDAO.creerCommande(new Commande(client, new Date() , null, client.getPanier()));
        clientDAO.viderPanier(client);
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

    public boolean modifierCommande(long id, Commande commande) {
        JpaUtil.ouvrirTransaction();
        CommandeDAO commandeDAO = new CommandeDAO();
        try {
            commandeDAO.modifierCommande(id, commande);
            JpaUtil.validerTransaction();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
            JpaUtil.annulerTransaction();
            return false;
        }
    }

    public boolean retirerProduitDeCommande(long idCommande, long idProduit) {
        JpaUtil.ouvrirTransaction();
        boolean enleve = false;
        CommandeDAO commandeDAO = new CommandeDAO();
        try {
            Commande com = commandeDAO.findById(idCommande);
            List<ProduitCommande> produitsCommande = com.getProduits();
            for (ProduitCommande prod : produitsCommande) {
                if (prod.getProduit().getId() == idProduit) {
                    produitsCommande.remove(prod);
                    enleve = true;
                }
            }
            com.setProduits(produitsCommande);

            commandeDAO.modifierCommande(idCommande, com);
            if (enleve) {
                JpaUtil.validerTransaction();
            } else {
                throw new Exception("ID du produit n'est pas trouvé dans la commande. ");
            }
            return true;
        } catch (Exception ex) {
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
    
    public boolean payerCommandeALaLivraison(Commande commande){ 
        JpaUtil.ouvrirTransaction();

        try {
            CommandeDAO commandeDAO = new CommandeDAO();
            commandeDAO.payerALaLivraison(commande.getId());
            JpaUtil.validerTransaction();
            return true;
        } catch (Exception ex) {
            System.err.println("Le paiement à la livraison a échoué !");
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
            JpaUtil.annulerTransaction();
            return false;
        }
    }
    
    public boolean payer(Commande commande){
        JpaUtil.ouvrirTransaction();

        try {
            CommandeDAO commandeDAO = new CommandeDAO();
            commandeDAO.payer(commande.getId());
            JpaUtil.validerTransaction();
            return true;
        } catch (Exception ex) {
            System.err.println("Le paiement sur le site web a échoué !");
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
            JpaUtil.annulerTransaction();
            return false;
        }
    }
    
    public void commandeLivree(Commande commande){
        JpaUtil.ouvrirTransaction();
        CommandeDAO commandeDAO = new CommandeDAO();
        commandeDAO.livraisonComplete(commande);
        JpaUtil.validerTransaction();
    }
    
    public void livraisonEnCours(Commande commande){
        JpaUtil.ouvrirTransaction();
        CommandeDAO commandeDAO = new CommandeDAO();
        commandeDAO.livraisonEnCours(commande);
        JpaUtil.validerTransaction();
    }
    
}
