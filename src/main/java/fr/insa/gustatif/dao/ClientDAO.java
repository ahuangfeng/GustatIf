package fr.insa.gustatif.dao;

import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
import javax.persistence.EntityManager;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Commande;
import javax.persistence.PersistenceException;

public class ClientDAO implements BasicDAO<Client>, EmailDAO<Client> {

    @Override
    public void creer(Client client) {
        throw new UnsupportedOperationException("Utiliser la méthode creerClient() pour créer un client.");
    }
    
    /**
     * Crée un client en vérifiant que le mail est unique.
     *
     * @param client
     * @throws fr.insa.gustatif.exceptions.DuplicateEmailException
     * @throws fr.insa.gustatif.exceptions.IllegalUserInfoException
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
     * @param client
     * @param nom
     * @param prenom
     * @param email
     * @param adresse
     * @throws DuplicateEmailException
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

    public void ajouterCommande(Client client, Commande commande) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        client.addCommande(commande);
        em.merge(client);
    }
}
