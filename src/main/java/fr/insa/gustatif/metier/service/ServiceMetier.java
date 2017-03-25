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
import fr.insa.gustatif.exceptions.IllegalCommandException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
import fr.insa.gustatif.exceptions.LivreurNotDisponibleException;
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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        JpaUtil.creerEntityManager();

        final Runnable envoyerMailSucces = () -> {
            serviceTechnique.envoyerMail(client.getMail(),
                    "Bienvenue chez Gustat'IF",
                    "Bonjour " + client.getPrenom() + "," + "\n"
                    + "Nous vous confirmons votre inscription au service Gustat'IF. "
                    + "Votre numéro de client est : " + client.getId() + "."
            );
        };
        final Runnable envoyerMailEchec = () -> {
            serviceTechnique.envoyerMail(client.getMail(),
                    "Votre inscription chez Gustat'IF",
                    "Bonjour " + client.getPrenom() + "," + "\n"
                    + "Votre inscription au service Gustat'IF a malencontreusement échoué... "
                    + "Merci de recommencer ultérieusement."
            );
        };

        // Vérifie si le mail est valide
        if (!serviceTechnique.verifierMail(client.getMail())) {
            throw new IllegalUserInfoException("Le mail n'est pas valide.");
        }

        try {
            // Récupère les coordonnées
            LatLng coords = ServiceTechnique.getLatLng(client.getAdresse());

            // Persiste le client
            JpaUtil.ouvrirTransaction();
            ClientDAO clientDAO = new ClientDAO();
            client.setLatitudeLongitude(coords);
            clientDAO.creerClient(client);

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
        } finally {
            JpaUtil.fermerEntityManager();
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
     */
    public boolean modifierClient(Client client, String nom, String prenom, String email, String adresse)
            throws DuplicateEmailException, BadLocationException {
        // Récupère les coordonnées
        LatLng coords = ServiceTechnique.getLatLng(client.getAdresse());

        JpaUtil.creerEntityManager();
        JpaUtil.ouvrirTransaction();

        ClientDAO clientDAO = new ClientDAO();
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
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * TODO: vérifier le return
     *
     * @param idClient
     * @return la liste de tous les clients
     */
    public Client recupererClient(Long idClient) {
        try {
            JpaUtil.creerEntityManager();
            ClientDAO clientDAO = new ClientDAO();
            return clientDAO.findById(idClient);
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * TODO: vérifier le return
     *
     * @param mail
     * @return la liste de tous les clients
     */
    public Client recupererClient(String mail) {
        JpaUtil.creerEntityManager();
        ClientDAO clientDAO = new ClientDAO();
        try {
            return clientDAO.findByEmail(mail);
        } catch (PersistenceException ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            JpaUtil.fermerEntityManager();
        }
        return null;
    }

    /**
     * TODO: vérifier le return
     *
     * @return la liste de tous les clients
     */
    public List<Client> recupererClients() {
        JpaUtil.creerEntityManager();
        ClientDAO clientDAO = new ClientDAO();
        try {
            return clientDAO.findAll();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            JpaUtil.fermerEntityManager();
        }
        return new ArrayList<>();
    }

    /**
     * TODO: vérifier le return
     *
     * @param idRestaurant
     * @return
     */
    public Restaurant recupererRestaurant(Long idRestaurant) {
        JpaUtil.creerEntityManager();
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        try {
            return restaurantDAO.findById(idRestaurant);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            JpaUtil.fermerEntityManager();
        }
        return null;
    }

    /**
     * TODO: vérifier le return
     *
     * @return
     */
    public List<Restaurant> recupererRestaurants() {
        JpaUtil.creerEntityManager();
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        try {
            return restaurantDAO.findAll();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            JpaUtil.fermerEntityManager();
        }
        return new ArrayList<>();
    }

    /**
     * TODO: vérifier le return
     *
     * @return
     */
    public List<Livreur> recupererLivreurs() {
        JpaUtil.creerEntityManager();
        LivreurDAO livreurDAO = new LivreurDAO();
        try {
            return livreurDAO.findAll();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            JpaUtil.fermerEntityManager();
        }
        return new ArrayList<>();
    }

    public Livreur recupererLivreur(long id) {
        JpaUtil.creerEntityManager();
        LivreurDAO livreurDAO = new LivreurDAO();
        try {
            return livreurDAO.findById(id);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            JpaUtil.fermerEntityManager();
        }
        return null;
    }

    public boolean modifierLivreur(Livreur livreur) {
        JpaUtil.creerEntityManager();
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
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Vérifie la validité d'une commande, c'est-à-dire si tous les produits
     * proviennent du même restaurant.
     *
     * @param panier Le panier à valider
     * @return L'ID du restaurant qui vend ces produits
     * @throws fr.insa.gustatif.exceptions.IllegalCommandException Si la
     * commande n'est pas valide. L'exception contient le message d'erreur avec
     * les détails.
     */
    public Restaurant validerPanier(List<ProduitCommande> panier) throws IllegalCommandException {
        if (panier.isEmpty()) {
            throw new IllegalCommandException("Une commande doit comporter au moins un produit !");
        }
        try {
            JpaUtil.creerEntityManager();

            RestaurantDAO restaurantDAO = new RestaurantDAO();

            Long idRestaurant = -1L;
            for (ProduitCommande pc : panier) {
                Long id = restaurantDAO.getRestaurantIdByProduit(pc.getProduit().getId());
                if (idRestaurant < 0) {
                    idRestaurant = id;
                } else if (pc.getQuantity() <= 0) {
                    throw new IllegalCommandException("La quantité de chaque produit doit être strictement positive !");
                } else if (!Objects.equals(id, idRestaurant)) {
                    throw new IllegalCommandException("Les produits de cette commande n'appartiennent pas tous au même restaurant !");
                }
            }
            return restaurantDAO.findById(idRestaurant);
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Vérifie la commande
     *
     * @param client
     * @param listeProduits
     * @return
     * @throws IllegalCommandException
     */
    public Commande creerCommande(Client client, List<ProduitCommande> listeProduits) throws IllegalCommandException {
        // Vérifie la validité de la commande
        Restaurant resto = validerPanier(listeProduits);
        if (null == resto) {
            throw new IllegalCommandException("Le panier est invalide.");
        }

        try {
            JpaUtil.creerEntityManager();
            JpaUtil.ouvrirTransaction();

            Commande commande = new Commande(client, new Date(), null, listeProduits, resto);
            CommandeDAO commandeDAO = new CommandeDAO();
            commandeDAO.creerCommande(commande);

            ClientDAO clientDAO = new ClientDAO();
            clientDAO.ajouterCommande(client, commande);

            // TODO: Assigner la commande à un livreur
            JpaUtil.validerTransaction();
            return commande;
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }
    
    //TODO : qu'est-ce qui se passe si le livreur a des commandes en cours? on lui assigne aussi? et Difference entre getDisponible et 
    public boolean assignerLivreur(Commande commande) throws LivreurNotDisponibleException{
        JpaUtil.creerEntityManager();
        JpaUtil.ouvrirTransaction();
        
        CommandeDAO commandeDAO = new CommandeDAO();
        LivreurDAO livreurDAO = new LivreurDAO();
        List<Livreur> livreursDispo = livreurDAO.findAll();
        Livreur livreurAssigne = null;
        double distance = 0.;
        double temps;
        double tempsMin = -1.;
        for (Livreur livreur : livreursDispo) {
            if(livreur.getDisponible() && livreur.getCapaciteMax()>commande.getPoids()){
                try{
                    if(livreur instanceof Drone){
                        //du livreur au restaurant
                        distance += ServiceTechnique.getFlightDistanceInKm(new LatLng(livreur.getLatitude(), livreur.getLongitude()),
                                                                        new LatLng(commande.getRestaurant().getLatitude(), commande.getRestaurant().getLongitude()));
                        //du restaurant au client
                        distance += ServiceTechnique.getFlightDistanceInKm(new LatLng(commande.getRestaurant().getLatitude(), commande.getRestaurant().getLongitude()),
                                                                        new LatLng(commande.getClient().getLatitude(), commande.getClient().getLongitude()));

                        temps = distance * ((Drone) livreur).getVitesse();
                    }else{
                        //du livreur au client passant par restaurant
                        temps = ServiceTechnique.getTripDurationByBicycleInMinute(new LatLng(livreur.getLatitude(), livreur.getLongitude()),
                                new LatLng(commande.getClient().getLatitude(),commande.getClient().getLongitude()),
                                new LatLng(commande.getRestaurant().getLatitude(), commande.getRestaurant().getLongitude()));
                    }
                } catch (BadLocationException ex){
                    Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
                    JpaUtil.annulerTransaction();
                    return false;
                }
                if(tempsMin < 0 || tempsMin > temps){
                    tempsMin = temps;
                    livreurAssigne = livreur;
                }
            }
        }
        //TODO : si le livreur est null marche pas!
        if(livreurAssigne != null){
            commande.setLivreur(livreurAssigne);
            livreurAssigne.setCommandeEnCours(commande);
            commandeDAO.modifierCommande(commande);
            livreurDAO.modifierLivreur(livreurAssigne);
            JpaUtil.validerTransaction();
            return true;
        }else{
            try {
                JpaUtil.annulerTransaction();
                throw new LivreurNotDisponibleException();
            } finally {
                JpaUtil.fermerEntityManager();
            }
        }
    }

    /**
     * TODO: A VOIR SI UTILE TODO: vérifier le return
     *
     * @param idCommande
     * @return
     */
    public Commande recupererCommande(Long idCommande) {
        JpaUtil.creerEntityManager();
        CommandeDAO commandeDAO = new CommandeDAO();
        try {
            return commandeDAO.findById(idCommande);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * TODO: vérifier le return
     *
     * @return
     */
    public List<Commande> recupererCommandes() {
        JpaUtil.creerEntityManager();
        CommandeDAO commandeDAO = new CommandeDAO();
        try {
            return commandeDAO.findAll();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            JpaUtil.fermerEntityManager();
        }
        return new ArrayList<>();
    }

    public List<Commande> recupererCommandesEnCoursParDrones() {
        try {
            JpaUtil.creerEntityManager();
            CommandeDAO commandeDAO = new CommandeDAO();
            return commandeDAO.recupererCommandesEnCoursParDrones();
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    public Produit recupererProduit(long idProduit) {
        JpaUtil.creerEntityManager();
        ProduitDAO produitDAO = new ProduitDAO();
        try {
            return produitDAO.findById(idProduit);
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            JpaUtil.fermerEntityManager();
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
        try {
            JpaUtil.creerEntityManager();
            JpaUtil.ouvrirTransaction();
            CommandeDAO commandeDAO = new CommandeDAO();
            commandeDAO.validerCommande(commande);
            LivreurDAO livreurDAO = new LivreurDAO();
            livreurDAO.terminerCommandeEnCours(commande.getLivreur());
            JpaUtil.validerTransaction();
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    public Cycliste recupererCycliste(String email) {
        JpaUtil.creerEntityManager();
        CyclisteDAO cyclisteDAO = new CyclisteDAO();
        try {
            return cyclisteDAO.findByEmail(email);
        } catch (PersistenceException ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            JpaUtil.fermerEntityManager();
        }
        return null;
    }

    public Gestionnaire recupererGestionnaire(String email) {
        JpaUtil.creerEntityManager();
        GestionnaireDAO gestionnaireDAO = new GestionnaireDAO();
        try {
            return gestionnaireDAO.findByEmail(email);
        } catch (PersistenceException ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            JpaUtil.fermerEntityManager();
        }
        return null;
    }

    public void genererComptesFictifs() throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
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
            try {
                cyclisteDAO.creerCycliste(new Cycliste("cyc", "liste", "cyc@gustatif.fr", 200, true, coordsIF.lat, coordsIF.lng));
            } catch (DuplicateEmailException ex) {
                // Le cycliste existe déjà
            }
            for (int i = 0; i < NB_CYCLISTES; ++i) {
                try {
                    String nom = ServiceTechnique.genererString(true);
                    String prenom = ServiceTechnique.genererString(true);
                    Integer capaciteMax = 20 + (int) (Math.random() * 20);
                    Cycliste c = new Cycliste(nom, prenom, nom.toLowerCase() + "@gustatif.fr", capaciteMax, true, coordsIF.lat, coordsIF.lng);
                    cyclisteDAO.creerCycliste(c);
                    System.out.println("  - " + c);
                } catch (DuplicateEmailException ex) {
                    i--;
                }
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
            try {
                gestionnaireDAO.creerGestionnaire(new Gestionnaire("gege", "stionaire", "gege@gustatif.fr"));
            } catch (DuplicateEmailException ex) {
                // Le gestionnaire existe déjà
            }
            for (int i = 0; i < NB_GESTIONNAIRES; ++i) {
                try {
                    String nom = ServiceTechnique.genererString(true);
                    String prenom = ServiceTechnique.genererString(true);
                    Gestionnaire g = new Gestionnaire(nom, prenom, nom.toLowerCase() + "@gustatif.fr");
                    gestionnaireDAO.creerGestionnaire(g);
                    System.out.println("  - " + g);
                } catch (DuplicateEmailException ex) {
                    i--;
                }
            }

            JpaUtil.validerTransaction();
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }
}
