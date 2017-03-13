package fr.insa.gustatif.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.Client;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

public class ClientDAO {

    /**
     * Crée un client en vérifiant que le mail est unique.
     * @param client
     * @return true si le client a été créé, sinon false (le mail est déjà utilisé)
     * @throws java.lang.Exception
     */
    public boolean creerClient(Client client) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();

        // Vérifie l'unicité de l'email du client
        if (existWithMail(client.getMail())) {
            return false;
        }

        em.persist(client);
        return true;
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

    public boolean existWithMail(String mail) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query emailQuery = em.createQuery("select c from Client c where c.mail = :mail");
        emailQuery.setParameter("mail", mail);
        try {
            Client client = (Client) emailQuery.getSingleResult();
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
}
