package fr.insa.gustatif.dao;

import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
import javax.persistence.EntityManager;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Commande;
import javax.persistence.PersistenceException;

/**
 * DAO de Client
 */
public class ClientDAO implements BasicDAO<Client>, EmailDAO<Client> {

    /**
     * NE PAS UTILISER CETTE METHODE, UTILISER creerClient()
     *
     * @param client Non utilisé.
     */
    @Override
    public void creer(Client client) {
        throw new UnsupportedOperationException("Utiliser la méthode creerClient() pour créer un client.");
    }

    /**
     * NE PAS UTILISER CETTE METHODE, UTILISER modifierClient()
     *
     * @param client Non utilisé.
     */
    @Override
    public boolean modifier(Client client, long id) {
        throw new UnsupportedOperationException("Utiliser la méthode modifierClient() pour modifier un client.");
    }

    /**
     * Crée un client en vérifiant que le mail est unique.
     *
     * @param client Le client à persister
     * @throws DuplicateEmailException Si le mail est déjà utilisé.
     * @throws IllegalUserInfoException Si les informations du livreur ne sont
     * pas valides.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public void creerClient(Client client) throws DuplicateEmailException, IllegalUserInfoException, PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();

        // Vérifie l'unicité de l'email du client
        if (existWithMail(client.getMail())) {
            throw new DuplicateEmailException(client.getMail());
        }

        // Vérifie la validité des informations
        if (client.getNom().isEmpty()) {
            throw new IllegalUserInfoException("Le nom ne peut pas être vide.");
        }
        if (client.getPrenom().isEmpty()) {
            throw new IllegalUserInfoException("Le prénom ne peut pas être vide.");
        }
        if (client.getAdresse().isEmpty()) {
            throw new IllegalUserInfoException("L'adresse ne peut pas être vide.");
        }

        em.persist(client);
    }

    /**
     *
     * Modifie les données d'un client. Pour ne pas modifier une valeur, laisser
     * le champ à null. L'adresse est validée par l'API Google Maps.
     *
     * @param client Le client à mettre à jour
     * @param nom Le nouveau nom, ou null
     * @param prenom Le nouveau prénom, ou null
     * @param email Le nouvel email, ou null
     * @param adresse La nouvelle adresse, ou null
     * @throws DuplicateEmailException Si le mail est déjà utilisé.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public void modifierClient(Client client, String nom, String prenom, String email, String adresse) throws DuplicateEmailException, PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();

        // Vérifie l'unicité de l'email du client
        if (existWithMail(email)) {
            throw new DuplicateEmailException(email);
        }

        if (null != nom && !nom.isEmpty()) {
            client.setNom(nom);
        }
        if (null != prenom && !prenom.isEmpty()) {
            client.setPrenom(prenom);
        }
        if (null != email && !email.isEmpty()) {
            client.setMail(email);
        }
        if (null != adresse && !adresse.isEmpty()) {
            client.setAdresse(adresse);
        }

        em.merge(client);
    }

    /**
     * Ajoute une commande au client.
     *
     * @param client Le client à qui ajouter la commande.
     * @param commande La commande à ajouter
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public void ajouterCommande(Client client, Commande commande) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        client.addCommande(commande);
        em.merge(client);
    }
}
