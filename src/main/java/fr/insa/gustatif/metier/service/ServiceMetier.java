package fr.insa.gustatif.metier.service;

import com.google.maps.model.LatLng;
import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.dao.DroneDAO;
import fr.insa.gustatif.dao.ClientDAO;
import fr.insa.gustatif.dao.CommandeDAO;
import fr.insa.gustatif.dao.CyclisteDAO;
import fr.insa.gustatif.dao.GestionnaireDAO;
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
import fr.insa.gustatif.metier.modele.Gestionnaire;
import fr.insa.gustatif.metier.modele.Produit;
import fr.insa.gustatif.metier.modele.ProduitCommande;
import fr.insa.gustatif.metier.modele.Restaurant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

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
    public boolean inscrireClient(Client client) throws DuplicateEmailException, IllegalUserInfoException, BadLocationException {
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
            // TODO: Le top serait de permettre des clients avec des adresses foireuses

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

    /**
     *
     * TODO: Tester ce qu'il se passe au niveau du client si la modif est
     * refusée
     *
     * @param client
     * @param nom
     * @param prenom
     * @param adresse
     * @param email
     * @return
     * @throws BadLocationException
     * @throws fr.insa.gustatif.exceptions.DuplicateEmailException
     * @throws fr.insa.gustatif.exceptions.IllegalUserInfoException
     */
    public boolean modifierClient(Client client, String nom, String prenom, String email, String adresse)
            throws DuplicateEmailException, BadLocationException {
        // Récupère les coordonnées
        LatLng coords = ServiceTechnique.getLatLng(client.getAdresse());

        ClientDAO clientDAO = new ClientDAO();

        JpaUtil.ouvrirTransaction();
        try {
            client.setLatitudeLongitude(coords);
            clientDAO.modifierClient(client, nom, prenom, email, adresse);
            JpaUtil.validerTransaction();
            return true;
        } catch (PersistenceException e) {
            JpaUtil.annulerTransaction();
            return false;
        } catch (DuplicateEmailException ex) {
            JpaUtil.annulerTransaction();
            throw ex;
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
        } catch (PersistenceException ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    /**
     * public void ajouterAuPanier(Client client, Produit produit, int quantite)
     *
     * TODO: Géré par l'IHM !! NOUS, Seulement commande
     *
     * @param client
     * @param produit
     * @param quantite
     */
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

    /**
     * TODO: Inutile car attribut de restaurant
     *
     * @param idRestaurant
     * @return
     */
    public List<Produit> recupererProduitsFromRestaurant(Long idRestaurant) {
        RestaurantDAO restaurantDao = new RestaurantDAO();
        List<Produit> liste = null;
        try {
            Restaurant restaurant = restaurantDao.findById(idRestaurant);
            liste = restaurant.getProduits();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return liste; // TODO: gerer erreurs
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
     * @TODO: Tous ces creer => hop dans la créationDemo
     */
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

    public Commande creerCommande(Client client ,List<ProduitCommande> listeProduit) throws Exception{
        JpaUtil.ouvrirTransaction();
        CommandeDAO commandeDAO = new CommandeDAO();
        ClientDAO clientDAO = new ClientDAO();
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        Commande commande = new Commande(client, new Date(), null, listeProduit);
        long idRestaurant = restaurantDAO.getRestaurantIdByProduit(listeProduit.get(0).getId());
        commandeDAO.setRestaurant(idRestaurant, commande);  //avant creer la commande verifie si chaque produit apartient au premier produit du panier
        for (ProduitCommande produitCommande : listeProduit) {
            if(restaurantDAO.getRestaurantIdByProduit(produitCommande.getProduit().getId()) != idRestaurant){
                throw new Exception("Les produits de cette commande n'appartiennent pas au même restaurant !");
            }
        }
        commandeDAO.creerCommande(commande);
        clientDAO.ajouterCommande(client, commande);
        JpaUtil.validerTransaction();
        return commande;
    }

    /**
     * TODO: A VOIR SI UTILE TODO: vérifier le return
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

    public List<Commande> recupererCommandesEnCoursParDrones() {
        CommandeDAO commandeDAO = new CommandeDAO();
        return commandeDAO.recupererCommandesEnCoursParDrones();
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

    /**
     * TODO: NE PAS FAIRE, SEUL PAIEMENT A LA COMMANDE TODO: PAS CETTE GESTION
     * PRECISE DE L'ETAT: soit en cours de livraison, soit livrée
     */
    /**
     * @param commande
     */
    public void validerCommande(Commande commande) {
        JpaUtil.ouvrirTransaction();
        CommandeDAO commandeDAO = new CommandeDAO();
        commandeDAO.validerCommande(commande);
        LivreurDAO livreurDAO = new LivreurDAO();
        livreurDAO.terminerCommandeEnCours(commande.getLivreur());
        JpaUtil.validerTransaction();
    }

    public Cycliste recupererCycliste(String email) {
        CyclisteDAO cyclisteDAO = new CyclisteDAO();
        try {
            return cyclisteDAO.findByEmail(email);
        } catch (PersistenceException ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Gestionnaire recupererGestionnaire(String email) {
        GestionnaireDAO gestionnaireDAO = new GestionnaireDAO();
        try {
            return gestionnaireDAO.findByEmail(email);
        } catch (PersistenceException ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void genererComptesFictifs() throws PersistenceException {
        JpaUtil.ouvrirTransaction();

        final int NB_CYCLISTES = 15;
        final int NB_DRONES = 10;
        final int NB_GESTIONNAIRES = 3;

        // Récupère les coordonnées du départ IF
        LatLng coordsIF;
        try {
            coordsIF = ServiceTechnique.getLatLng("Département Informatique, INSA Lyon, Villeurbanne");
        } catch (BadLocationException ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
            coordsIF = new LatLng(45.78126, 4.87221);
        }

        // Création des cyclistes
        System.out.println("Création des cyclistes :");
        CyclisteDAO cyclisteDAO = new CyclisteDAO();
        for (int i = 0; i < NB_CYCLISTES; ++i) {
            String nom = ServiceTechnique.GenererString(true);
            String prenom = ServiceTechnique.GenererString(true);
            Integer capaciteMax = 20 + (int) (Math.random() * 20);
            Cycliste c = new Cycliste(nom, prenom, nom.toLowerCase() + "@gustatif.fr", capaciteMax, true, coordsIF.lat, coordsIF.lng);
            cyclisteDAO.creerCycliste(c);
            System.out.println("  - " + c);
        }

        // Création des drônes
        System.out.println("Création des drônes :");
        DroneDAO droneDAO = new DroneDAO();
        for (int i = 0; i < NB_DRONES; ++i) {
            Integer vitesse = 20 + (int) (Math.random() * 20);
            Integer capaciteMax = 20 + (int) (Math.random() * 20);
            Drone d = new Drone(vitesse, capaciteMax, true, coordsIF.lat, coordsIF.lng);
            droneDAO.creerDrone(d);
            System.out.println("  - " + d);
        }

        // Création des gestionnaires
        System.out.println("Création des gestionnaires :");
        GestionnaireDAO gestionnaireDAO = new GestionnaireDAO();
        for (int i = 0; i < NB_GESTIONNAIRES; ++i) {
            String nom = ServiceTechnique.GenererString(true);
            String prenom = ServiceTechnique.GenererString(true);
            Gestionnaire g = new Gestionnaire(nom, prenom, nom.toLowerCase() + "@gustatif.fr");
            gestionnaireDAO.creerGestionnaire(g);
            System.out.println("  - " + g);
        }

        JpaUtil.validerTransaction();
    }

    public List<Livreur> recupererLivreurs() {
        LivreurDAO livreurDAO = new LivreurDAO();
        try {
            return livreurDAO.findAll();
        } catch (PersistenceException ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
}
