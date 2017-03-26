package fr.insa.gustatif.metier.service;

import com.google.maps.errors.NotFoundException;
import com.google.maps.errors.OverDailyLimitException;
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
import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.CommandeMalFormeeException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
import fr.insa.gustatif.exceptions.AucunLivreurDisponibleException;
import fr.insa.gustatif.metier.modele.Drone;
import fr.insa.gustatif.metier.modele.Cycliste;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Livreur;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.Gestionnaire;
import fr.insa.gustatif.metier.modele.Produit;
import fr.insa.gustatif.metier.modele.ProduitCommande;
import fr.insa.gustatif.metier.modele.Restaurant;
import fr.insa.gustatif.util.GeoTest;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;

/**
 * Cette classe contient tous les services métiers de Gustat'IF.
 */
public class ServiceMetier {

    ServiceTechnique serviceTechnique;

    public ServiceMetier() {
        this.serviceTechnique = new ServiceTechnique();
    }

    /**
     * Crée une commande à partir d'un panier. Assigne le livreur avec le
     * meilleur temps de livraison estimé, et ajoute la commande au client. Si
     * la commande est livrée par un cycliste, celui en est informé par mail.
     *
     * @param client Le client passant cette commande.
     * @param listeProduits La liste des produits de la commande (le panier).
     * @return La commande créée.
     * @throws CommandeMalFormeeException Si la commande n'est pas valide. Pour
     * cela, utiliser la méthode validerPanier()
     * @throws AucunLivreurDisponibleException Si aucun livreur n'est disponible
     * pour livrer la commande
     * @throws OverDailyLimitException Si le quota Google Maps est atteint
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public Commande creerCommande(Client client, List<ProduitCommande> listeProduits) throws CommandeMalFormeeException, AucunLivreurDisponibleException, OverDailyLimitException, PersistenceException {
        // Vérifie la validité de la commande
        Restaurant resto = validerPanier(listeProduits);
        if (null == resto) {
            throw new CommandeMalFormeeException("Le panier est invalide.");
        }

        try {
            JpaUtil.creerEntityManager();
            JpaUtil.ouvrirTransaction();

            // Crée la commande
            Commande commande = new Commande(client, new Date(), null, listeProduits, resto);

            // Récupère les livreurs disponibles
            Map<Double, Livreur> livreurs = serviceTechnique.recupererLivreursParTemps(commande);

            // Essaie d'assigner un livreur
            LivreurDAO livreurDAO = new LivreurDAO();
            while (null == commande.getLivreur() && !livreurs.isEmpty()) {
                for (Iterator<Map.Entry<Double, Livreur>> it = livreurs.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<Double, Livreur> entry = it.next();
                    Double tempsEstime = entry.getKey();
                    Livreur livreur = entry.getValue();

                    // Si le livreur est dispo, on le réserve
                    livreurDAO.rafraichir(livreur);
                    if (livreur.getDisponible()) {
                        livreur.setDisponible(false);

                        // C'est notre livreur
                        // Persiste la commande
                        CommandeDAO commandeDAO = new CommandeDAO();
                        commande.setLivreur(livreur);
                        commande.setTempsEstime(tempsEstime);
                        commandeDAO.creer(commande);

                        // Ajoute la commande au client
                        ClientDAO clientDAO = new ClientDAO();
                        clientDAO.ajouterCommande(client, commande);

                        // Assigne la commande au livreur
                        livreur.setCommandeEnCours(commande);
                        livreurDAO.modifier(livreur, livreur.getId());

                        JpaUtil.validerTransaction();

                        if (livreur instanceof Cycliste) {
                            Cycliste c = (Cycliste) livreur;

                            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.FRANCE);
                            String dateCommande = df.format(commande.getDateDeCommande());

                            String infosLivreur = c.getPrenom() + " " + c.getNom().toUpperCase() + " (#" + c.getId() + ", " + c.getIdentifiant() + ")";

                            String corpsMail = "Bonjour " + c.getPrenom() + "," + "\n"
                                    + "\n"
                                    + "Merci d'effectuer cette livraison dès maintenant, en respectant le code de la route ;-)" + "\n"
                                    + "\n"
                                    + "Le Chef\n"
                                    + "\n"
                                    + "Détails de la livraison :" + "\n"
                                    + "  - Date / heure : " + dateCommande + "\n"
                                    + "  - Livreur : " + infosLivreur + "\n"
                                    + "  - Restaurant : " + commande.getRestaurant().getDenomination() + "\n"
                                    + "      -> " + commande.getRestaurant().getAdresse() + "\n"
                                    + "  - Client : " + "\n"
                                    + "      -> " + client.getPrenom() + " " + client.getNom().toUpperCase() + "\n"
                                    + "      -> " + client.getAdresse() + "\n"
                                    + "\n"
                                    + "Commande :" + "\n";

                            Double prixTotal = 0.;
                            for (ProduitCommande produit : commande.getProduits()) {
                                Integer q = produit.getQuantity();
                                String d = produit.getProduit().getDenomination();
                                Double p = produit.getProduit().getPrix();

                                corpsMail += "  - " + q + " " + d + " : " + q + " x " + p + " € \n";

                                prixTotal += p * q.doubleValue();
                            }

                            corpsMail += "\n" + "TOTAL : " + prixTotal + " €";

                            String sujetMail = "Livraison n°" + commande.getId() + " à effectuer";
                            serviceTechnique.envoyerMail(c.getMail(), sujetMail, corpsMail);
                        }

                        return commande;
                    } else { // Si le livreur n'est pas dispo, on l'enlève
                        it.remove();
                    }
                }
            }

            // On a pas réussi à assigner un livreur
            throw new AucunLivreurDisponibleException();

        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Génère des cyclistes, drônes et gestionnaires aléatoirement, pour la
     * simulation. Les comptes cyc@gustatif.fr (cycliste) et gege@gustatif.fr
     * (gestionnaire) sont systématiquement créés.
     *
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public void genererComptesFictifs() throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            JpaUtil.ouvrirTransaction();

            final int NB_CYCLISTES = 15;
            final int NB_DRONES = 10;
            final int NB_GESTIONNAIRES = 2;
            final int CAPACITE_MOY_CYCLISTE = 4000;
            final int CAPACITE_ECART_CYCLISTE = 500;
            final int CAPACITE_MOY_DRONE = 1000;
            final int CAPACITE_ECART_DRONE = 200;
            final int VITESSE_MOY_DRONE = 10;
            final int VITESSE_ECART_DRONE = 5;

            // Récupère les coordonnées du départ IF
            LatLng coordsIF;
            try {
                coordsIF = GeoTest.getLatLng("Département Informatique, INSA Lyon, Villeurbanne");
            } catch (NotFoundException ex) {
                Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
                coordsIF = new LatLng(45.78126, 4.87221);
            }

            // Création des cyclistes
            System.out.println("Création des cyclistes :");
            CyclisteDAO cyclisteDAO = new CyclisteDAO();
            try {
                cyclisteDAO.creerCycliste(new Cycliste("cyc", "liste", "cyc@gustatif.fr", 200, true,
                        coordsIF.lat + Math.random() / 10., coordsIF.lng + Math.random() / 10.)
                );
            } catch (DuplicateEmailException ex) {
                // Le cycliste existe déjà
            }
            for (int i = 0; i < NB_CYCLISTES; ++i) {
                try {
                    String nom = ServiceTechnique.genererString(true);
                    String prenom = ServiceTechnique.genererString(true);
                    Integer capaciteMax = CAPACITE_MOY_CYCLISTE + (int) (Math.random() * CAPACITE_ECART_CYCLISTE);
                    Cycliste c = new Cycliste(nom, prenom, nom.toLowerCase() + "@gustatif.fr", capaciteMax, true,
                            coordsIF.lat + Math.random() / 10., coordsIF.lng + Math.random() / 10.
                    );
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
                Integer vitesse = VITESSE_MOY_DRONE + (int) (Math.random() * VITESSE_ECART_DRONE);
                Integer capaciteMax = CAPACITE_MOY_DRONE + (int) (Math.random() * CAPACITE_ECART_DRONE);
                Drone d = new Drone(vitesse, capaciteMax, true,
                        coordsIF.lat + Math.random() / 10., coordsIF.lng + Math.random() / 10.
                );
                droneDAO.creer(d);
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
            JpaUtil.annulerTransaction();
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Crée un client en vérifiant que le mail est unique. Vérifie également les
     * données du client, et géolocalise le client avec son adresse.
     *
     * @param client Le client à enregistrer dans la BDD
     * @return true si le client a été créé, sinon false (mail déjà utilisé)
     * @throws DuplicateEmailException Si le mail est déjà utilisé.
     * @throws IllegalUserInfoException Si les informations de l'utilisateur
     * sont invalides
     * @throws NotFoundException Si l'adresse n'est pas reconnue par Google Maps
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public boolean inscrireClient(Client client) throws DuplicateEmailException, IllegalUserInfoException, NotFoundException, PersistenceException {
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
            LatLng coords = GeoTest.getLatLng(client.getAdresse());

            // Persiste le client
            JpaUtil.creerEntityManager();
            JpaUtil.ouvrirTransaction();
            ClientDAO clientDAO = new ClientDAO();
            client.setLatitudeLongitude(coords);
            clientDAO.creerClient(client);

            // Le client a été créé
            envoyerMailSucces.run();
            JpaUtil.validerTransaction();
            return true;
        } catch (DuplicateEmailException | IllegalUserInfoException e) {
            throw e;
        } catch (NotFoundException e) {
            envoyerMailEchec.run();
            throw e;
        } catch (PersistenceException e) {
            envoyerMailEchec.run();
            return false;
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Modifie les données d'un client. Pour ne pas modifier une valeur, laisser
     * le champ à null. L'adresse est validée par l'API Google Maps.
     *
     * @param client Le client à mettre à jour
     * @param nom Le nouveau nom, ou null
     * @param prenom Le nouveau prénom, ou null
     * @param email Le nouvel email, ou null
     * @param adresse La nouvelle adresse, ou null
     * @throws DuplicateEmailException Si le mail est déjà utilisé.
     * @throws NotFoundException Si l'adresse n'est pas reconnue par Google Maps
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public void modifierClient(Client client, String nom, String prenom, String email, String adresse)
            throws DuplicateEmailException, NotFoundException, PersistenceException {
        // Récupère les coordonnées
        LatLng coords = GeoTest.getLatLng(client.getAdresse());

        try {
            JpaUtil.creerEntityManager();
            JpaUtil.ouvrirTransaction();

            ClientDAO clientDAO = new ClientDAO();
            client.setLatitudeLongitude(coords);
            clientDAO.modifierClient(client, nom, prenom, email, adresse);

            JpaUtil.validerTransaction();
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Récupère le client ayant l'ID id, ou null s'il n'existe pas.
     *
     * @param id L'ID du client à récupérer
     * @return Le client ayant l'ID id, ou null
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public Client recupererClient(Long id) throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            ClientDAO clientDAO = new ClientDAO();
            return clientDAO.findById(id);
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Récupère le client ayant le mail donné en paramètre, ou null s'il
     * n'existe pas.
     *
     * @param mail Le mail du client à récupérer.
     * @return Le client ayant le mail donné, ou null
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public Client recupererClient(String mail) throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            ClientDAO clientDAO = new ClientDAO();
            return clientDAO.findByEmail(mail);
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Récupère tous les clients.
     *
     * @return la liste de tous les clients
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public List<Client> recupererClients() throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            ClientDAO clientDAO = new ClientDAO();
            return clientDAO.findAll();
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Récupère la commande ayant l'ID id, ou null si elle n'existe pas.
     *
     * @param id L'ID de la commande à récupérer
     * @return La commande ayant l'ID id, ou null
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public Commande recupererCommande(Long id) throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            CommandeDAO commandeDAO = new CommandeDAO();
            return commandeDAO.findById(id);
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Récupère toutes les commandes.
     *
     * @return la liste de toutes les commandes
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public List<Commande> recupererCommandes() throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            CommandeDAO commandeDAO = new CommandeDAO();
            return commandeDAO.findAll();
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Récupère les commandes répondant aux filtres.
     *
     * @param uniquementEnCours si true, uniquement les commandes en cours de
     * livraison, sinon toutes les commandes
     * @param inclureCyclistes si false, enlève les commandes assignées à des
     * cyclistes
     * @param inclureDrones si false, enlève les commandes assignées à des
     * drônes
     * @return la liste de toutes les commandes en cours de livraison par de
     * drônes
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public List<Commande> recupererCommandesFiltre(boolean uniquementEnCours, boolean inclureCyclistes, boolean inclureDrones) throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            CommandeDAO commandeDAO = new CommandeDAO();

            List<Commande> resultat = commandeDAO.recupererCommandesFiltre(uniquementEnCours);

            for (Iterator<Commande> it = resultat.iterator(); it.hasNext();) {
                Commande commande = it.next();
                if ((!inclureCyclistes && (null == commande.getLivreur() || (commande.getLivreur() instanceof Cycliste)))
                        || (!inclureDrones && (null == commande.getLivreur() || (commande.getLivreur() instanceof Drone)))) {
                    it.remove();
                }
            }

            return resultat;

        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Récupère le cycliste ayant le mail donné en paramètre, ou null s'il
     * n'existe pas.
     *
     * @param email Le mail du cycliste à récupérer.
     * @return Le cycliste ayant le mail donné, ou null
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public Cycliste recupererCycliste(String email) throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            CyclisteDAO cyclisteDAO = new CyclisteDAO();
            return cyclisteDAO.findByEmail(email);
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Récupère le gestionnaire ayant le mail donné en paramètre, ou null s'il
     * n'existe pas.
     *
     * @param email Le mail du gestionnaire à récupérer.
     * @return Le gestionnaire ayant le mail donné, ou null
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public Gestionnaire recupererGestionnaire(String email) throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            GestionnaireDAO gestionnaireDAO = new GestionnaireDAO();
            return gestionnaireDAO.findByEmail(email);
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     *
     * Récupère le livreur ayant l'ID id, ou null s'il n'existe pas.
     *
     * @param id L'ID du livreur à récupérer
     * @return Le livreur ayant l'ID id, ou null
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public Livreur recupererLivreur(long id) throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            LivreurDAO livreurDAO = new LivreurDAO();
            return livreurDAO.findById(id);
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Récupère tous les livreurs.
     *
     * @return la liste de tous les livreurs
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public List<Livreur> recupererLivreurs() throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            LivreurDAO livreurDAO = new LivreurDAO();
            return livreurDAO.findAll();
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Récupère le produit ayant l'ID id, ou null s'il n'existe pas.
     *
     * @param id L'ID du produit à récupérer
     * @return Le produit ayant l'ID id, ou null
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public Produit recupererProduit(long id) throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            ProduitDAO produitDAO = new ProduitDAO();
            return produitDAO.findById(id);
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Récupère le restaurant ayant l'ID id, ou null s'il n'existe pas.
     *
     * @param id L'ID du restaurant à récupérer
     * @return Le restaurant ayant l'ID id, ou null
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public Restaurant recupererRestaurant(Long id) throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            RestaurantDAO restaurantDAO = new RestaurantDAO();
            return restaurantDAO.findById(id);
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Récupère tous les restaurants.
     *
     * @return la liste de tous les restaurants
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public List<Restaurant> recupererRestaurants() throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            RestaurantDAO restaurantDAO = new RestaurantDAO();
            return restaurantDAO.findAll();
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Marque une commande comme étant terminée. Met à jour la date de fin de
     * livraison.
     *
     * @param commande La commande à terminer.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public void terminerCommande(Commande commande) throws PersistenceException {
        try {
            JpaUtil.creerEntityManager();
            JpaUtil.ouvrirTransaction();

            CommandeDAO commandeDAO = new CommandeDAO();
            commandeDAO.validerCommande(commande);

            LivreurDAO livreurDAO = new LivreurDAO();
            livreurDAO.terminerCommandeEnCours(commande.getLivreur());

            JpaUtil.validerTransaction();
        } finally {
            JpaUtil.annulerTransaction();
            JpaUtil.fermerEntityManager();
        }
    }

    /**
     * Vérifie la validité d'une commande, c'est-à-dire si tous les produits
     * proviennent du même restaurant, et si la commande comporte au moins un
     * produit.
     *
     * @param panier Le panier à valider
     * @return L'ID du restaurant qui vend ces produits
     * @throws CommandeMalFormeeException Si la commande n'est pas valide.
     * L'exception contient le message d'erreur avec les détails.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public Restaurant validerPanier(List<ProduitCommande> panier) throws CommandeMalFormeeException, PersistenceException {
        if (panier.isEmpty()) {
            throw new CommandeMalFormeeException("Une commande doit comporter au moins un produit !");
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
                    throw new CommandeMalFormeeException("La quantité de chaque produit doit être strictement positive !");
                } else if (!Objects.equals(id, idRestaurant)) {
                    throw new CommandeMalFormeeException("Les produits de cette commande n'appartiennent pas tous au même restaurant !");
                }
            }
            return restaurantDAO.findById(idRestaurant);
        } finally {
            JpaUtil.fermerEntityManager();
        }
    }
}
