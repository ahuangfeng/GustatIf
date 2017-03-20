package fr.insa.gustatif.dao;

import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Produit;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

public class ClientDAO {

    /**
     * Crée un client en vérifiant que le mail est unique.
     *
     * @param client
     * @throws fr.insa.gustatif.exceptions.DuplicateEmailException
     * @throws fr.insa.gustatif.exceptions.IllegalUserInfoException
     */
    public void creerClient(Client client) throws DuplicateEmailException, IllegalUserInfoException {
        EntityManager em = JpaUtil.obtenirEntityManager();

        // Vérifie l'unicité de l'email du client
        if (existWithMail(client.getMail())) {
            throw new DuplicateEmailException(client.getMail());
        }

        // Vérifie la validité des informations
        if (client.getNom().isEmpty() || client.getPrenom().isEmpty() || client.getAdresse().isEmpty()) {
            throw new IllegalUserInfoException();
        }

        em.persist(client);
    }

    public Client findById(long id) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Client client = null;
        try {
            client = em.find(Client.class, id);
        } catch (Exception e) {
            throw e;
        }
        return client;
    }

    public Client findByEmail(String mail) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query emailQuery = em.createQuery("select c from Client c where c.mail = :mail");
        emailQuery.setParameter("mail", mail);
        return (Client) emailQuery.getSingleResult();
    }

    public boolean existWithMail(String mail) {
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

    public List<Client> findAll() throws Exception {
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

    public void ajouterAuPanier(Client client, Produit produit) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        client.ajouterAuPanier(produit);
        em.merge(client);
    }

    public boolean modifierClient(Client client, String nom, String prenom, String email, String adresse) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();

        // Vérifie l'unicité de l'email du client, et de la validité des informations
        if ((null != email && existWithMail(email))
                || null != nom && nom.isEmpty()
                || null != prenom && prenom.isEmpty()
                || null != adresse && adresse.isEmpty()) {
            return false;
        }

        if (null != nom) {
            client.setNom(nom);
        }
        if (null != prenom) {
            client.setPrenom(prenom);
        }
        if (null != email) {
            client.setMail(email);
        }
        if (null != adresse) {
            client.setAdresse(adresse);
        }

        em.merge(client);
        return true;
    }
}
