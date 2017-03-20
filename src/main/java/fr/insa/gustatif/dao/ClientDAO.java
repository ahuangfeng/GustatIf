package fr.insa.gustatif.dao;

import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Produit;
import fr.insa.gustatif.metier.modele.ProduitCommande;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

public class ClientDAO {

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

    public boolean modifierClient(Client client) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();

        if (existWithMail(client.getMail())) {
            em.merge(client);
            return true;
        }
        return false;
    }

    public Client findById(long id) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        return em.find(Client.class, id);
    }

    public Client findByEmail(String mail) throws NonUniqueResultException, PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query emailQuery = em.createQuery("select c from Client c where c.mail = :mail");
        emailQuery.setParameter("mail", mail);
        try {
            return (Client) emailQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean existWithMail(String mail) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query emailQuery = em.createQuery("select c from Client c where c.mail = :mail");
        emailQuery.setParameter("mail", mail);
        try {
            emailQuery.getSingleResult();
        } catch (NoResultException e) {
            return false;
        } catch (NonUniqueResultException e) {
            return true;
        }
        return true;
    }

    public List<Client> findAll() throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        List<Client> clients = null;
        try {
            Query q = em.createQuery("SELECT c FROM Client c");
            clients = (List<Client>) q.getResultList();
        } catch (Exception e) {
            throw e;
        }
        return clients;
    }

    public void ajouterAuPanier(Client client, Produit produit, int quantite) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        client.ajouterAuPanier(new ProduitCommande(produit, quantite));
        em.merge(client);
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

        // Vérifie l'unicité de l'email du client et la validité des informations
        if (existWithMail(email)) {
            throw new DuplicateEmailException(email);
        }

        if (!nom.isEmpty()) {
            client.setNom(nom);
        }
        if (!prenom.isEmpty()) {
            client.setPrenom(prenom);
        }
        if (!email.isEmpty()) {
            client.setMail(email);
        }
        if (!adresse.isEmpty()) {
            client.setAdresse(adresse);
        }

        em.merge(client);
    }

    public void viderPanier(Client client) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        client.viderPanier();
        em.merge(client);
    }
}
